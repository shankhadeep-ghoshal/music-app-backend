package music.app.backend.responses;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
@Setter
public abstract class SuccessResponse<T> extends ResponseBasicDetails implements SuccessResponseEncapsulate<T> {
    T data;

    public SuccessResponse() {
        this.data = null;
    }

    public SuccessResponse(int responseCode, String message, T data) {
        super(responseCode, message);
        this.data = data;
    }

    public SuccessResponse(int responseCode, String message) {
        super(responseCode, message);
    }
}