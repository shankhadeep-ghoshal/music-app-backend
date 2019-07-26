package music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import music.app.backend.registerandauthorizationservice.UserLoginEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.nio.charset.StandardCharsets;

import static com.kosprov.jargon2.api.Jargon2.jargon2Verifier;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Introspected
public class UserLoginPasswordBased implements UserLoginEntity<String> {
    @NotNull
    @NotBlank
    @Size(min = 5, max = 13, message = "Phone number must length must be between 4 and 10")
    private String phoneNumber;

    @NotNull @NotBlank @Size(min = 8, max = 64)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\"';,!@#$%^&+=])(?=\\S+$).{8,}$")
    private String password;

    public boolean matchPassword(String hash) {
        return jargon2Verifier()
                .hash(hash)
                .password(password.getBytes(StandardCharsets.UTF_8))
                .verifyEncoded();
    }

    @Override
    public String getKeyField() {
        return phoneNumber;
    }
}