package music.app.backend.musicstorageandretrievalservice.musicuploadservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import music.app.backend.Mongoable;
import music.app.backend.UtilityClass;
import music.app.backend.musicstorageandretrievalservice.MusicStorageMongoConfig;
import music.app.backend.musicstorageandretrievalservice.SongEntity;
import music.app.backend.responses.ErrorResponsesConcrete;
import music.app.backend.responses.ResponseDataEncapsulate;
import music.app.backend.responses.SuccessResponseConcrete;
import org.bson.Document;

import java.io.IOException;

/**
 * @apiNote Headers Fields:
 * <ul>
 *  <li>
 *     X-Key-Field - This is the key that is used to get the temporary token. <br>This can
 *     be the phone number or the permanent token that lasts between successive sign ins. </li>
 *  <li>
 *     X-Token-Field - This the temporary session token that is refreshed every 5 minutes
 *  </li>
 *  <li>
 *     X-User-Id - The permanent user Id created during sign-up. This is created only once
 *  </li>
 *  <li>
 *     X-Song-Id - This is a unique ID for a song created just before meta data caching in the
 *     server.
 *  </li>
 * </ul>
 */
@Prototype
public class SongUploadDAO extends MusicStorageMongoConfig
        implements MusicUploadDAO<ResponseDataEncapsulate> {

    private static final int SONG_ID_LENGTH = 16;
    private static final int SONG_ENTRY_CACHE_LIFE_DURATION = 1000;

    private static final String DEFAULT_PLAYLIST_NAME = "_def";
    private static final String[] SUPPORTED_AUDIO_FORMATS = new String[]{"mp3","flac"};

    private static final Single<ResponseDataEncapsulate> NOT_LOGGED_IN_SINGLE =
            Single.just(new ErrorResponsesConcrete<>(400,
            "Not Logged In",
            "Please Log In"));
    private static final Single<ResponseDataEncapsulate> NOT_FOUND_SINGLE =
            Single.just(new ErrorResponsesConcrete<>(404,
                    "Song Not Found",
                    "Please Upload Again"));

    private static final Function<Document, SingleSource<? extends ResponseDataEncapsulate>>
            documentSingleSourceFunction = document ->
            Single.just((ResponseDataEncapsulate)
                    new SuccessResponseConcrete<>(200,
                            "Uploaded",
                            "Song Uploaded"));

    @Override
    public Single<ResponseDataEncapsulate> uploadMetaDataOne(Mongoable mongoableSingleSongEntity,
                                                             HttpHeaders httpHeaders) {
        return doesUserSessionTokenExist(httpHeaders)
                .flatMap(aBoolean -> aBoolean ?
                        putSongsMetaDataToCache(mongoableSingleSongEntity) :
                        NOT_LOGGED_IN_SINGLE);
    }

    @Override
    public Single<ResponseDataEncapsulate> uploadFileDataOne(CompletedFileUpload completedFileUpload,
                                                             HttpHeaders httpHeaders) {
        return doesUserSessionTokenExist(httpHeaders)
                .flatMap(aBoolean -> aBoolean ?
                        uploadFileToS3(completedFileUpload, httpHeaders) :
                        NOT_LOGGED_IN_SINGLE);
    }

    private Single<Boolean> doesUserSessionTokenExist(HttpHeaders httpHeaders) {
        String userKeyValue = httpHeaders.get("X-Key-Field");
        String userTokenOfKey = httpHeaders.get("X-Token-Field");

        return sessionTokenCheckerAndManagementService
                .doesTokenExistsInRedis(userKeyValue, userTokenOfKey);
    }

    private Single<ResponseDataEncapsulate>
    putSongsMetaDataToCache(Mongoable mongoableSingleSongEntity) {
        SongEntity songEntity = (SongEntity) mongoableSingleSongEntity;
        String songId = UtilityClass.generateClientToken(SONG_ID_LENGTH);

        songEntity.setId(songId);
        try {
            String jsonSerializedSongEntity = new ObjectMapper().writeValueAsString(songEntity);

            return Single.fromFuture(songsUploadMetaDataCache.setex(songId,
                    SONG_ENTRY_CACHE_LIFE_DURATION,
                    jsonSerializedSongEntity))
                    .map(s -> s != null && !s.isEmpty() && s.equals("OK") ?
                            new SuccessResponseConcrete<>(200,
                                    "songId",
                                    songId) :
                            new ErrorResponsesConcrete<>(500,
                                    "errorMessage",
                                    "Something went wrong"));
        } catch (JsonProcessingException e) {
            return Single.just(new ErrorResponsesConcrete<>(500,
                    "errorMessage",
                    "JSONP exception"));
        }
    }

    private Single<ResponseDataEncapsulate>
    uploadFileToS3(CompletedFileUpload completedFileUpload, HttpHeaders httpHeaders) {
        String songId = httpHeaders.get("X-Song-Id");
        String userId = httpHeaders.get("X-User-Id");

        return Single.fromFuture(songsUploadMetaDataCache.get(songId))
                .flatMap(s -> s != null && !s.isEmpty() ?
                        persistSongToS3(s, userId, completedFileUpload) :
                        NOT_FOUND_SINGLE);
    }

    private Single<ResponseDataEncapsulate>
    persistSongToS3(String s, String userId, CompletedFileUpload completedFileUpload) {
        try {
            SongEntity songEntity = new ObjectMapper().readValue(s, SongEntity.class);
            String[] filenameParts = completedFileUpload.getFilename().trim().split("\\.");

            if (checkIfFileSafeToUpload(filenameParts)) {
                return s3Management.putDataToS3(userId,
                        songEntity.getId(),
                        completedFileUpload.getBytes())
                        .flatMap(aBoolean -> {
                            if (aBoolean) {
                                if (songEntity.getPlaylistId() == null ||
                                        songEntity.getPlaylistId().isEmpty()) {
                                    songEntity.setPlaylistId(DEFAULT_PLAYLIST_NAME);
                                    return persistSongMetaDataToMongo(playlistsWithSongsCollection,
                                            DEFAULT_PLAYLIST_NAME,
                                            userId,
                                            songEntity);
                                } else {
                                    return persistSongMetaDataToMongo(playlistsWithSongsCollection,
                                            songEntity.getPlaylistId(),
                                            userId,
                                            songEntity);
                                }
                            } else {
                                return Single.just((ResponseDataEncapsulate)
                                        new ErrorResponsesConcrete<>(401,
                                                "File Not Uploaded",
                                                "Please Try Again"));
                            }
                        });
            } else {
                return Single.just(new ErrorResponsesConcrete<>(403,
                "Wrong file type or format",
                "please upload only mp3 or flac files"));
            }
        } catch (IOException e) {
            return Single.just(new ErrorResponsesConcrete<>(500,
            "JSONP Exception",
            e.getMessage()));
        }
    }

    private static Single<ResponseDataEncapsulate>
    persistSongMetaDataToMongo(MongoCollection<Document> mongoCollection,
                               String defaultPlaylistName,
                               String userId,
                               SongEntity songEntity) {
        return Single.fromPublisher(mongoCollection
                .findOneAndUpdate(Filters.eq("userId", userId),
                        new Document("$addToSet",
                                new Document(defaultPlaylistName,
                                        new Document(songEntity.makeEntityMapForMongo()))),
                        new FindOneAndUpdateOptions()
                                .upsert(true)))
                .flatMap(SongUploadDAO.documentSingleSourceFunction);
    }

    private static boolean checkIfFileSafeToUpload(final String[] filenameParts) {
        if (checkFileExtensionCounts(filenameParts)) {
            return checkFileExtension(filenameParts[1]);
        } else {
            return false;
        }
    }

    private static boolean checkFileExtensionCounts(final String[] filenameParts) {
        int dotCount = 0;

        for (int i = filenameParts.length - 1; i >= 0; i--) {
            if (filenameParts[i].contains(".")) {
                ++dotCount;
            }

            if (dotCount > 1) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkFileExtension(String filenamePart) {
        if (filenamePart.equals(SUPPORTED_AUDIO_FORMATS[0])) {
            return true;
        } else {
            return filenamePart.equals(SUPPORTED_AUDIO_FORMATS[1]);
        }
    }
}