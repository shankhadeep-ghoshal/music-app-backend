package music.app.backend.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SuccessResponseConcrete<T> extends SuccessResponse<T> {

    public SuccessResponseConcrete(int responseCode, String message) {
        super(responseCode, message);
    }

    public SuccessResponseConcrete(int responseCode, String message, T  data) {
        super(responseCode, message, data);
    }
}