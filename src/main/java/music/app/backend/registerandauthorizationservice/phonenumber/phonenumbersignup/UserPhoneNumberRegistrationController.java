package music.app.backend.registerandauthorizationservice.phonenumber.phonenumbersignup;

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
 * @apiNote List of Endpoints:
 * 1. Sign up request using phone number: /MusicApp/SignUp
 * 2. Phone Number Verification: /MusicApp/SignUp/Confirmation
 */
@Validated
@Controller(BaseConstants.URLS.CONTROLLER_BASE_PATH)
public class UserPhoneNumberRegistrationController {

    @Inject
    UserSignUpPhoneNumberPersistenceDAO userSignUpPhoneNumberDAO;

    @Post(uri = UserPhoneAuthenticationRegistrationLogoutConstants.URLS.CONTROLLER_SIGN_UP_PATH,
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public Single<ResponseDataEncapsulate>
    signUpForPhoneVerification(@Valid @Body UserSignUpPhoneNumberEntity phoneNumber,
                               HttpHeaders httpHeaders) {
        return userSignUpPhoneNumberDAO.sendVerification(phoneNumber);
    }

    @Post(uri = UserPhoneAuthenticationRegistrationLogoutConstants.URLS.CONTROLLER_SIGN_UP_PATH
            +
            UserPhoneAuthenticationRegistrationLogoutConstants.URLS.CONTROLLER_SIGN_UP_VERIFICATION_CODE_PARAM,
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public Single<ResponseDataEncapsulate>
    sendUserSignUpConfirmation(@Valid @Body UserAccountStateSignUpEntity verificationData,
                               HttpHeaders httpHeaders) {
        return userSignUpPhoneNumberDAO.signUp(verificationData);
    }
}