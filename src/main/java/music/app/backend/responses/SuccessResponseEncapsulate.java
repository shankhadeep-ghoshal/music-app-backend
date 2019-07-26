package music.app.backend.responses;

public interface SuccessResponseEncapsulate<T extends Object> {
    T getData();
}
