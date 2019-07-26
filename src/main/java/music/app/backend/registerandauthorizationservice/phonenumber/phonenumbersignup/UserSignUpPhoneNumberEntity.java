package music.app.backend.registerandauthorizationservice.phonenumber.phonenumbersignup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import music.app.backend.Mongoable;
import music.app.backend.registerandauthorizationservice.phonenumber.UserPhoneAuthenticationRegistrationLogoutConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Introspected
public class UserSignUpPhoneNumberEntity implements Mongoable {
    @NotNull @NotBlank @Size(min = 1, max = 3) private String countryCode;
    @NotNull @NotBlank @Size(min = 4, max = 10) private String phoneNumber;
    @NotNull @NotBlank @Size(min = 8, max = 64)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\"';,!@#$%^&+=])(?=\\S+$).{8,}$")
    private String password;

    @Override
    public Map<String, Object> makeEntityMapForMongo() {
        return Collections.unmodifiableMap(new HashMap<String, Object>() {{
            put("phoneNumber", countryCode + "-" + phoneNumber);
            put("keyCan", hashPasswordUsingArgon(password.getBytes(StandardCharsets.UTF_8)));
        }});
    }

    public static String hashPasswordUsingArgon(byte[] password) {
        return jargon2Hasher()
                .type(UserPhoneAuthenticationRegistrationLogoutConstants.Argon2HyperParams.ARGON2_TYPE)
                .memoryCost(UserPhoneAuthenticationRegistrationLogoutConstants.Argon2HyperParams.ARGON_MEMORY_COST)
                .timeCost(UserPhoneAuthenticationRegistrationLogoutConstants.Argon2HyperParams.ARGON_NUM_PASS_MEMORY)
                .parallelism(UserPhoneAuthenticationRegistrationLogoutConstants.Argon2HyperParams.ARGON_PARALLELISM)
                .saltLength(UserPhoneAuthenticationRegistrationLogoutConstants.Argon2HyperParams.ARGON_SALT_LENGTH)
                .hashLength(UserPhoneAuthenticationRegistrationLogoutConstants.Argon2HyperParams.ARGON_HASH_LENGTH)
                .password(password)
                .encodedHash();
    }
}