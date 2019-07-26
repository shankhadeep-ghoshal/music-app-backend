package music.app.backend.responses;

public interface ErrorResponseEncapsulate<T> extends ResponseDataEncapsulate {
    T getException();
}