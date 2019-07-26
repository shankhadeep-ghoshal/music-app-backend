package music.app.backend.responses;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public abstract class ErrorResponse<T> extends ResponseBasicDetails implements ErrorResponseEncapsulate<T> {
    private final T exception;

    public ErrorResponse(int responseCode, String message, T exception) {
        super(responseCode, message);
        this.exception = exception;
    }
}