package music.app.backend.registerandauthorizationservice.phonenumber.phonenumberlogin;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import music.app.backend.GlobalPersistenceConfig;
import music.app.backend.UtilityClass;
import music.app.backend.registerandauthorizationservice.UserLoginDAO;
import music.app.backend.registerandauthorizationservice.UserLoginEntity;
import music.app.backend.responses.ErrorResponsesConcrete;
import music.app.backend.responses.ResponseDataEncapsulate;
import music.app.backend.responses.SuccessResponseConcrete;
import org.bson.Document;

import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;

import static com.kosprov.jargon2.api.Jargon2.jargon2Verifier;
import static music.app.backend.registerandauthorizationservice.phonenumber.UserPhoneAuthenticationRegistrationLogoutConstants.*;

@Singleton
public class UserLoginPhoneNumberPersistenceDAO extends GlobalPersistenceConfig
        implements UserLoginDAO<ResponseDataEncapsulate> {

    @Override
    public Single<ResponseDataEncapsulate>
    obtainSessionToken(UserLoginEntityTokenBased tokenBased) {
        final String sessionToken = UtilityClass
                .generateClientToken(TEMPORARY_SESSION_TOKEN_LENGTH);

        return handleSessionKeyPlacementInRedis(tokenBased,
                sessionToken,
                stringRedisUsersSessionTokenCache);
    }

    @Override
    public Single<ResponseDataEncapsulate>
    authenticateUsingPassword(UserLoginPasswordBased passwordBased) {
        final String sessionToken = UtilityClass
                .generateClientToken(TEMPORARY_SESSION_TOKEN_LENGTH);

        return Single
                .fromPublisher(userCollection.find(new Document("phoneNumber",
                passwordBased.getPhoneNumber())).first())
                .subscribeOn(Schedulers.io())
                .flatMap(document -> {
                    if(jargon2Verifier()
                            .hash(document.getString("keyCan"))
                            .password(passwordBased
                                    .getPassword()
                                    .getBytes(StandardCharsets.UTF_8))
                            .verifyEncoded()) {
                        if (document.getBoolean("verifiedAccount")) {
                            // get the userPermanentToken and send it back to the client and ask the
                            // client to use that to get a session token
                            String userPermanentToken = document.getString("userTokenPerm");
                            String userId = document.getString("userId");

                            if (userPermanentToken != null && !userPermanentToken.isEmpty()) {
                                UserLoginEntityTokenBased loginEntityUidAndPermToken =
                                        new UserLoginEntityTokenBased(userId,
                                                userPermanentToken);

                                return handleSessionKeyPlacementInRedis(loginEntityUidAndPermToken,
                                        sessionToken,
                                        stringRedisUsersSessionTokenCache);
                            } else {
                                String newPermanentToken =
                                        UtilityClass.generateClientToken(PERMANENT_SESSION_TOKEN_LENGTH);

                                return Single.fromPublisher(userCollection
                                        .updateOne(Filters.eq("userId", userId),
                                        new Document("$set",
                                                new Document("userTokenPerm", newPermanentToken)),
                                        new UpdateOptions().upsert(true)))
                                        .filter(UpdateResult::wasAcknowledged)
                                        .map(document1 ->
                                                new SuccessResponseConcrete<>(ResponseCodes
                                                        .PERMANENT_TOKEN_CREATED,
                                                        ResponseMessage.TOKEN_PERMANENT_CREATED,
                                                        newPermanentToken))
                                        .toSingle();
                            }
                        } else {
                            return Single
                                    .just(new ErrorResponsesConcrete<>(ResponseCodes
                                            .WRONG_CREDENTIALS_LOGIN,
                                            ResponseMessage.WRONG_LOGIN_CREDENTIALS,
                                            ""));
                        }
                    } else {
                        return Single.just(new ErrorResponsesConcrete<>(ResponseCodes
                                .UNVERIFIED_USER,
                                ResponseMessage.UNVERIFIED_USER,
                                ""));
                    }
                })
                .onErrorReturn(throwable -> {
                    if (throwable.getLocalizedMessage().contains("Publisher is empty")) {
                        return new ErrorResponsesConcrete<>(ResponseCodes
                                .WRONG_CREDENTIALS_LOGIN,
                                ResponseMessage.WRONG_LOGIN_CREDENTIALS,
                                "");
                    } else {
                        return new ErrorResponsesConcrete<>(ResponseCodes.SERVER_ERROR,
                                ResponseMessage.SERVER_ERROR,
                                throwable);
                    }
                });
    }

    private static Single<ResponseDataEncapsulate>
    handleSessionKeyPlacementInRedis(UserLoginEntity userLoginEntity,
                                     String sessionToken,
                                     RedisAsyncCommands<String, String> stringRedisCommands) {
        return Single.fromFuture(stringRedisCommands
                .get((String)(userLoginEntity.getKeyField())))
                .subscribeOn(Schedulers.io())
                .flatMap(s -> Single.just((ResponseDataEncapsulate)
                        new SuccessResponseConcrete<>(ResponseCodes.TOKEN_ALREADY_PRESENT,
                                ResponseMessage.TOKEN_ALREADY_PRESENT,
                                new UserLoginTokenResponseEntity())))
                .onErrorResumeNext(throwable ->
                        setSessionTokenInRedisAndRespond(stringRedisCommands,
                        sessionToken,
                        (String)(userLoginEntity.getKeyField())));
    }

    private static Single<ResponseDataEncapsulate>
    setSessionTokenInRedisAndRespond(RedisAsyncCommands<String, String> stringStringRedisAsyncCommands,
                                     String sessionToken,
                                     String phoneNumber) {
        return Single.fromFuture(stringStringRedisAsyncCommands
                .setex(phoneNumber,
                        EVICTION_DURATION,
                        sessionToken))
                .map(s1 -> new SuccessResponseConcrete<>(ResponseCodes.SUCCESS,
                        ResponseMessage.TOKEN_REGISTERED_MESSAGE,
                        new UserLoginTokenResponseEntity(sessionToken,
                                EVICTION_DURATION + 10L)));
    }

    private static class ResponseCodes {
        private static final int SUCCESS = 200;
        private static final int SERVER_ERROR = 500;
        private static final int PERMANENT_TOKEN_CREATED = 201;
        private static final int TOKEN_ALREADY_PRESENT = 300;
        private static final int WRONG_CREDENTIALS_LOGIN = 403;
        private static final int UNVERIFIED_USER = 405;
    }

    private static class ResponseMessage {
        private static final String SERVER_ERROR = "Something went wrong. Please try again.";
        private static final String TOKEN_REGISTERED_MESSAGE = "Token Registered";
        private static final String TOKEN_ALREADY_PRESENT = "Token Already Present";
        private static final String TOKEN_PERMANENT_CREATED = "Permanent token created";
        private static final String WRONG_LOGIN_CREDENTIALS = "Wrong login credentials entered";
        private static final String UNVERIFIED_USER = "Please verify your phone number";
    }
}