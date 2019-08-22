package music.app.backend.musicstorageandretrievalservice;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.micronaut.context.annotation.Value;
import io.reactivex.subscribers.TestSubscriber;
import music.app.backend.GlobalPersistenceConfig;
import music.app.backend.awsservices.S3Management;
import music.app.backend.tokenanduidcheckerservice.SessionTokenCheckerAndManagementService;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public abstract class MusicStorageMongoConfig extends GlobalPersistenceConfig {
    protected MongoCollection<Document> playlistsWithSongsCollection;
    protected RedisAsyncCommands<String, String> songsUploadMetaDataCache;

    @Inject protected SessionTokenCheckerAndManagementService sessionTokenCheckerAndManagementService;
    @Inject protected S3Management s3Management;

    @Inject @Named("${songs-upload}")
    protected StatefulRedisConnection<String, String> songsUploadMetaDataCacheConnection;

    @Value("${mongo-database.collection-playlists-with-song}")
    private String playlistWithSongCollectionName;

    @PostConstruct
    void initCollections() {
        playlistsWithSongsCollection = usersDatabase.getCollection(playlistWithSongCollectionName);
        songsUploadMetaDataCache = songsUploadMetaDataCacheConnection.async();

        playlistsWithSongsCollection.createIndex(Indexes.ascending("userId"),
                new IndexOptions().unique(true))
                .subscribe(new TestSubscriber<>());
    }
}