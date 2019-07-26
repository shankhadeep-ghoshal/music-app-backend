package music.app.backend.responses;

public class ErrorResponsesConcrete<T> extends ErrorResponse<T> {

    public ErrorResponsesConcrete(int responseCode, String message, T exception) {
        super(responseCode, message, exception);
    }
}