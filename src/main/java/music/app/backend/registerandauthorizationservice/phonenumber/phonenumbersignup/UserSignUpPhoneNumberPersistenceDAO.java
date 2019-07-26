package music.app.backend.registerandauthorizationservice.phonenumber.phonenumbersignup;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.runtime.context.scope.ThreadLocal;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import music.app.backend.GlobalPersistenceConfig;
import music.app.backend.Mongoable;
import music.app.backend.UtilityClass;
import music.app.backend.registerandauthorizationservice.UserSignUpDAO;
import music.app.backend.registerandauthorizationservice.UserSignUpSuccessInfoResponseEntity;
import music.app.backend.responses.ErrorResponsesConcrete;
import music.app.backend.responses.ResponseDataEncapsulate;
import music.app.backend.responses.SuccessResponseConcrete;
import org.bson.Document;
import org.reactivestreams.Publisher;

import java.util.Objects;
import java.util.Random;

import static music.app.backend.registerandauthorizationservice.phonenumber.UserPhoneAuthenticationRegistrationLogoutConstants.PERMANENT_SESSION_TOKEN_LENGTH;
import static music.app.backend.registerandauthorizationservice.phonenumber.UserPhoneAuthenticationRegistrationLogoutConstants.TEMPORARY_SESSION_TOKEN_LENGTH;

@ThreadLocal
public class UserSignUpPhoneNumberPersistenceDAO extends GlobalPersistenceConfig
        implements UserSignUpDAO<ResponseDataEncapsulate, Mongoable> {

    @Override
    public Single<ResponseDataEncapsulate> signUp(Mongoable data) {
        return signUpDelegate((UserAccountStateSignUpEntity) data);
    }

    @Override
    public Single<ResponseDataEncapsulate>
    sendVerification(Mongoable userSignUpPhoneNumberEntity) {
        return sendVerificationDelegate((UserSignUpPhoneNumberEntity) userSignUpPhoneNumberEntity);
    }

    private Single<ResponseDataEncapsulate>
    sendVerificationDelegate(UserSignUpPhoneNumberEntity signUpPhoneNumberEntity) {
        String verificationNumber =
                String.valueOf(10000 + new Random().nextInt(989980) + 1);

        return Single.fromPublisher(userCollection
                .insertOne(new Document(signUpPhoneNumberEntity.makeEntityMapForMongo())
                        .append("verificationCode",verificationNumber)
                        .append("verifiedAccount", Boolean.FALSE)))
                .map(success -> (ResponseDataEncapsulate)
                        new SuccessResponseConcrete<>(ResponseCode.SUCCESS,
                        "verificationCode",
                        verificationNumber))
                .onErrorReturn(throwable -> new ErrorResponsesConcrete<>(ResponseCode.SERVER_ERROR,
                        ResponseMessage.SERVER_ERROR,
                        throwable.getMessage()));
    }

    private Single<ResponseDataEncapsulate> signUpDelegate(UserAccountStateSignUpEntity data) {
        return Single
                .fromPublisher(userCollection
                        .find(new Document("phoneNumber", data.getPhoneNumber()))
                        .first())
                .subscribeOn(Schedulers.io())
                .filter(Objects::nonNull)
                .filter(document -> document.getString("verificationCode")
                        .equals(data.getVerificationCode()))
                .toSingle()
                .flatMap(document -> Single
                        .fromPublisher(setVerifiedFieldAsTrue(data,
                                userCollection))
                        .flatMap(document1 -> {
                            String userTokenPermanent =
                                    UtilityClass.generateClientToken(PERMANENT_SESSION_TOKEN_LENGTH);
                            String userId =
                                    UtilityClass.generateClientToken(TEMPORARY_SESSION_TOKEN_LENGTH);

                            return handleUserIdCreationAndFirstTimePermTokenGenerate(data,
                                    userTokenPermanent,
                                    userId,
                                    userCollection);
                        }))
                .onErrorReturn(throwable -> new ErrorResponsesConcrete<>(ResponseCode
                        .WRONG_VERIFICATION_CODE,
                        ResponseMessage.WRONG_VERIFICATION_CODE,
                        throwable));
    }

    private static Single<ResponseDataEncapsulate>
    handleUserIdCreationAndFirstTimePermTokenGenerate(UserAccountStateSignUpEntity data,
                                                      String userTokenPermanent,
                                                      String userId,
                                                      MongoCollection<Document> userCollection) {
        return Single.fromPublisher(setUserIdAndFirstTimePermToken(data,
                userTokenPermanent,
                userId,
                userCollection))
                .flatMap(document2 -> Single
                        .just((ResponseDataEncapsulate)
                                new SuccessResponseConcrete<>(ResponseCode.SUCCESS,
                                "Message",
                                        new UserSignUpSuccessInfoResponseEntity(userTokenPermanent,
                                                userId))))
                .onErrorReturn(throwable ->
                        new ErrorResponsesConcrete<>(ResponseCode.SERVER_ERROR,
                                ResponseMessage.SERVER_ERROR,
                                "Internal Server Error"));
    }

    private static Publisher<UpdateResult>
    setUserIdAndFirstTimePermToken(UserAccountStateSignUpEntity data,
                                   String userTokenPermanent,
                                   String userId,
                                   MongoCollection<Document> phoneVerificationMongoCollection) {
        return phoneVerificationMongoCollection
                .updateOne(Filters.eq("phoneNumber", data.getPhoneNumber()),
                new Document("$set", new Document("userId", userId)
                        .append("userTokenPerm", userTokenPermanent)),
                new UpdateOptions().upsert(true));
    }

    private static Publisher<UpdateResult>
    setVerifiedFieldAsTrue(UserAccountStateSignUpEntity data,
                           MongoCollection<Document> phoneVerificationMongoCollection) {
        return phoneVerificationMongoCollection
                .updateOne(Filters.eq("phoneNumber", data.getPhoneNumber()),
                new Document("$set", new Document("verifiedAccount", Boolean.TRUE)
                        .append("verificationCode", "")),
                new UpdateOptions().upsert(true));
    }

    private static class ResponseCode {
        private static final int SUCCESS = 200;
        private static final int SERVER_ERROR = 500;
        private static final int WRONG_VERIFICATION_CODE = 403;
    }

    private static class ResponseMessage {
        private static final String SERVER_ERROR = "Something went wrong. Please try again.";
        private static final String WRONG_VERIFICATION_CODE = "Wrong Verification Code Entered";
    }
}