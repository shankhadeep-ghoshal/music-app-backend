package music.app.backend.registerandauthorizationservice;

import io.reactivex.Single;
import music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogin.UserLoginEntityTokenBased;
import music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogin.UserLoginPasswordBased;
import music.app.backend.responses.ResponseDataEncapsulate;

public interface UserLoginDAO<T extends ResponseDataEncapsulate> {
    Single<T> obtainSessionToken(UserLoginEntityTokenBased tokenBased);
    Single<T> authenticateUsingPassword(UserLoginPasswordBased passwordBased);
}