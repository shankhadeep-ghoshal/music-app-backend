package music.app.backend.registerandauthorizationservice.phonenumber.phonenumbersignup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import music.app.backend.Mongoable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Introspected
public class UserAccountStateSignUpEntity implements Mongoable {
    @NotNull @NotBlank
    @Size(min = 5, max = 13, message = "Phone number must length must be between 4 and 10")
    private String phoneNumber;

    @NotNull @NotBlank
    @Size(min = 6, max = 6, message = "Code length must exactly be equal to 6")
    private String verificationCode;

    @JsonIgnore private Boolean verifiedAccount = Boolean.FALSE;

    public UserAccountStateSignUpEntity(String phoneNumber, String verificationCode) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
        this.verifiedAccount = Boolean.TRUE;
    }

    @Override
    public Map<String, Object> makeEntityMapForMongo() {
        HashMap<String, Object> returnMap = new HashMap<String, Object>() {{
            put("phoneNumber", phoneNumber);
            put("verificationCode", verificationCode);
            put("verifiedAccount", verifiedAccount);
        }};

        return Collections.unmodifiableMap(returnMap);
    }
}