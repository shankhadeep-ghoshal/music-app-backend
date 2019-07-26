package music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Introspected
class UserLoginTokenResponseEntity {
    @NotNull @NotBlank private String sessionToken;
    @NotNull private Long expirationDuration;
}