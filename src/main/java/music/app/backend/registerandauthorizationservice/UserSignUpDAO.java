package music.app.backend.registerandauthorizationservice;


import io.reactivex.Single;
import music.app.backend.Mongoable;
import music.app.backend.responses.ResponseDataEncapsulate;

public interface UserSignUpDAO<T extends ResponseDataEncapsulate, V extends Mongoable> {
    Single<T> signUp(V data);
    Single<T> sendVerification(V signUpEntity);
//    ResponseDataEncapsulate sendSignUpLoginToken(T userId);
}