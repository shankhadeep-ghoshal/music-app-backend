package music.app.backend.registerandauthorizationservice.verification;

import io.reactivex.Single;
import music.app.backend.Mongoable;
import music.app.backend.responses.ResponseDataEncapsulate;

public interface Verification<T extends ResponseDataEncapsulate, V, W extends Mongoable> {
    Single<T> sendVerification(V userSignUpEntity);
    Single<T> confirmPhoneNumberAndSignUp(W userSignUpEntity);
}