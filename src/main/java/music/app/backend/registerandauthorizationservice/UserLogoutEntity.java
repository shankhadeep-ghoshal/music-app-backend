package music.app.backend.registerandauthorizationservice;

public interface UserLogoutEntity<T> {
    T getKeyData();
    String getUserPermanentToken();
}