package music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogout;

import com.mongodb.client.result.UpdateResult;
import io.micronaut.runtime.context.scope.ThreadLocal;
import io.reactivex.Maybe;
import io.reactivex.Single;
import music.app.backend.GlobalPersistenceConfig;
import music.app.backend.registerandauthorizationservice.UserLogoutDAO;
import music.app.backend.responses.ErrorResponsesConcrete;
import music.app.backend.responses.ResponseDataEncapsulate;
import music.app.backend.responses.SuccessResponseConcrete;
import org.bson.Document;

@ThreadLocal
public class UserPhoneNumberLogoutPersistenceDAO extends GlobalPersistenceConfig
        implements UserLogoutDAO<ResponseDataEncapsulate, UserPhoneNumberLogoutEntity> {

    public Single<ResponseDataEncapsulate>
    performLogout(UserPhoneNumberLogoutEntity userPhoneNumberLogoutEntity) {
        return Single.fromPublisher(userCollection
                .updateOne(new Document("userId",
                                userPhoneNumberLogoutEntity.getUserId())
                                .append("userTokenPerm",
                                        userPhoneNumberLogoutEntity.getUserPermanentToken()),
                        new Document("$set", new Document("userTokenPerm", ""))))
                .filter(UpdateResult::wasAcknowledged)
                .map(updateResult ->
                        (ResponseDataEncapsulate) new SuccessResponseConcrete(ResponseCodes.SUCCESS,
                                ResponseMessage.SUCCESS_MESSAGE))
                .onErrorReturn(throwable -> new ErrorResponsesConcrete<>(ResponseCodes.ERROR_MONGO,
                        ResponseMessage.ERROR_MESSAGE_MONGO,
                        throwable))
                .flatMap(responseDataEncapsulate -> Maybe.fromFuture(stringRedisUsersSessionTokenCache
                        .get(userPhoneNumberLogoutEntity.getUserPermanentToken()))
                        .filter(s -> s != null &&!s.isEmpty())
                        .map(s -> stringRedisUsersSessionTokenCache
                                .del(userPhoneNumberLogoutEntity.getUserPermanentToken()))
                        .map(longRedisFuture ->
                                (ResponseDataEncapsulate) new SuccessResponseConcrete(ResponseCodes.SUCCESS,
                                        ResponseMessage.SUCCESS_MESSAGE)))
                .toSingle();
    }

    private static class ResponseCodes {
        private static final int SUCCESS = 200;
        private static final int ERROR_REDIS = 501;
        private static final int ERROR_MONGO = 502;
    }

    private static class ResponseMessage {
        private static final String SUCCESS_MESSAGE = "Successfully Logged out";
        private static final String ERROR_MESSAGE_REDIS = "Redis Error";
        private static final String ERROR_MESSAGE_MONGO = "Mongo Error";
    }
}