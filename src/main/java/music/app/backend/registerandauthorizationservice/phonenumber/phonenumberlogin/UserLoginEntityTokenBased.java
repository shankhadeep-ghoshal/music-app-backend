package music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import music.app.backend.registerandauthorizationservice.UserLoginEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Introspected
public class UserLoginEntityTokenBased implements UserLoginEntity<String> {
    @NotNull
    @NotBlank
    @Size(min = 5, max = 13, message = "Phone number must length must be between 4 and 10")
    private String phoneNumber;

    @NotNull @NotBlank private String userPermanentToken;

    @Override
    public String getKeyField() {
        return userPermanentToken;
    }
}