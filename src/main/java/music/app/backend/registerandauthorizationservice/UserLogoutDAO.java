package music.app.backend.registerandauthorizationservice;

import io.reactivex.Single;
import music.app.backend.responses.ResponseDataEncapsulate;

public interface UserLogoutDAO<T extends ResponseDataEncapsulate, V extends UserLogoutEntity> {
    Single<T> performLogout(V logoutEntity);
}