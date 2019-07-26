package music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import music.app.backend.registerandauthorizationservice.UserLogoutEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class UserPhoneNumberLogoutEntity implements UserLogoutEntity<String> {
    @NotNull @NotBlank private String userId;

    @NotNull @NotBlank private String userPermanentToken;

    @Override
    public String getKeyData() {
        return userId;
    }
}