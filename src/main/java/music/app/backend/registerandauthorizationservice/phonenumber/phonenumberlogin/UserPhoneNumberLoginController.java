package music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogin;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.reactivex.Single;
import music.app.backend.BaseConstants;
import music.app.backend.registerandauthorizationservice.phonenumber.UserPhoneAuthenticationRegistrationLogoutConstants;
import music.app.backend.responses.ResponseDataEncapsulate;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * @apiNote List of Endpoints: <br>
 *  1. Login using permanent token: /MusicApp/Login/Token
 *  2. Login using password: /MusicApp/Login/Password
 */
@Validated
@Controller(BaseConstants.URLS.CONTROLLER_BASE_PATH)
public class UserPhoneNumberLoginController {

    @Inject
    UserLoginPhoneNumberPersistenceDAO userLoginPhoneNumberDAO;

    @Post(uri = UserPhoneAuthenticationRegistrationLogoutConstants.URLS.CONTROLLER_LOGIN_TOKEN_PATH,
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public Single<ResponseDataEncapsulate>
    authenticateUsingPermanentToken(@Valid @Body UserLoginEntityTokenBased tokenBased,
                                    HttpHeaders httpHeaders) {
        return userLoginPhoneNumberDAO.obtainSessionToken(tokenBased);
    }

    @Post(uri = UserPhoneAuthenticationRegistrationLogoutConstants.URLS.CONTROLLER_LOGIN_PASSWORD_PATH,
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public Single<ResponseDataEncapsulate>
    authenticateUsingPassword(@Valid @Body UserLoginPasswordBased passwordBased,
                              HttpHeaders httpHeaders) {
        return userLoginPhoneNumberDAO.authenticateUsingPassword(passwordBased);
    }
}