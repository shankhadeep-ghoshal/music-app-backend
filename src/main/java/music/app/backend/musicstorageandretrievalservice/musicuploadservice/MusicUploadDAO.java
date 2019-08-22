package music.app.backend.musicstorageandretrievalservice.musicuploadservice;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.reactivex.Single;
import music.app.backend.Mongoable;
import music.app.backend.responses.ResponseDataEncapsulate;

public interface MusicUploadDAO<T extends ResponseDataEncapsulate> {
    Single<T> uploadMetaDataOne(Mongoable mongoable, HttpHeaders httpHeaders);
    Single<T> uploadFileDataOne(CompletedFileUpload completedFileUpload, HttpHeaders httpHeaders);
}