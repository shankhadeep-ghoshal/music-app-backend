package music.app.backend.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class ResponseBasicDetails implements ResponseDataEncapsulate {
    private final int responseCode;
    private final String message;

    public ResponseBasicDetails() {
        this.responseCode = -1;
        this.message = "";
    }
}