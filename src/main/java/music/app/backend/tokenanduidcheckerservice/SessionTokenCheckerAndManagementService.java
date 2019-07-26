package music.app.backend.tokenanduidcheckerservice;

import io.reactivex.Single;
import music.app.backend.GlobalPersistenceConfig;

import javax.inject.Singleton;

@Singleton
public class SessionTokenCheckerAndManagementService extends GlobalPersistenceConfig {

    public Single<Boolean> doesTokenExistsInRedis(String key, String value) {
        return Single.fromFuture(stringRedisUsersSessionTokenCache.get(key))
                .map(s -> s!=null && !s.isEmpty() && s.equals(value));
    }

    public Single<Boolean> putTokenInRedis(String key, String value, long evictDuration) {
        return Single.fromFuture(stringRedisUsersSessionTokenCache.setex(key,evictDuration,value))
                .map(s -> s != null && !s.isEmpty() && s.equals("OK"));
    }

    public Single<Boolean> removeTokenInRedis(String key) {
        return Single.fromFuture(stringRedisUsersSessionTokenCache.del(key))
                .map(aLong -> aLong!=null && aLong > 0L);
    }
}