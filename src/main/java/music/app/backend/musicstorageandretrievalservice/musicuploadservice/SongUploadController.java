package music.app.backend.musicstorageandretrievalservice.musicuploadservice;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.validation.Validated;
import io.reactivex.Single;
import music.app.backend.BaseConstants;
import music.app.backend.musicstorageandretrievalservice.SongEntity;
import music.app.backend.responses.ResponseDataEncapsulate;

import javax.inject.Inject;
import javax.validation.Valid;

@Validated
@Controller(BaseConstants.URLS.CONTROLLER_BASE_PATH)
public class SongUploadController {
    @Inject MusicUploadDAO<ResponseDataEncapsulate> musicUploadDAO;

    @Post(uri = "/Upload/Song/Metadata")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Single<ResponseDataEncapsulate> uploadSongMetadata(@Valid SongEntity songEntity,
                                                              HttpHeaders httpHeaders) {
        return musicUploadDAO.uploadMetaDataOne(songEntity, httpHeaders);
    }

    @Post(uri = "/Upload/Song/Binary")
    @Consumes(value = MediaType.MULTIPART_FORM_DATA)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Single<ResponseDataEncapsulate> uploadSong(CompletedFileUpload uploadedFile,
                                                      HttpHeaders httpHeaders) {
        return musicUploadDAO.uploadFileDataOne(uploadedFile, httpHeaders);
    }
}