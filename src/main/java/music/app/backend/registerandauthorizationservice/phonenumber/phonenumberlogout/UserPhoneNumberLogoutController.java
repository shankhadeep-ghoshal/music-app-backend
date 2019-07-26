package music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogout;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.reactivex.Single;
import music.app.backend.BaseConstants;
import music.app.backend.registerandauthorizationservice.phonenumber.UserPhoneAuthenticationRegistrationLogoutConstants;
import music.app.backend.responses.ResponseDataEncapsulate;

import javax.inject.Inject;
import javax.validation.Valid;

@Validated
@Controller(BaseConstants.URLS.CONTROLLER_BASE_PATH)
public class UserPhoneNumberLogoutController {
    @Inject
    UserPhoneNumberLogoutPersistenceDAO userPhoneNumberLogoutDAO;

    @Post(uri = UserPhoneAuthenticationRegistrationLogoutConstants.URLS.CONTROLLER_LOGOUT_PATH
            +
            UserPhoneAuthenticationRegistrationLogoutConstants.URLS.CONTROLLER_LOGOUT_PATH_PHONE,
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public Single<ResponseDataEncapsulate>
    logoutPhoneNumberUser(@Valid UserPhoneNumberLogoutEntity userPhoneNumberLogoutEntity) {
        return userPhoneNumberLogoutDAO.performLogout(userPhoneNumberLogoutEntity);
    }
}