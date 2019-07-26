package music.app.backend.registerandauthorizationservice.verification;

import io.reactivex.Single;
import music.app.backend.registerandauthorizationservice.phonenumber.phonenumbersignup.UserAccountStateSignUpEntity;
import music.app.backend.responses.ResponseDataEncapsulate;

import javax.inject.Singleton;

@Singleton
public class PhoneVerificationService implements
        Verification<ResponseDataEncapsulate,
                String,
                UserAccountStateSignUpEntity> {

    @Override
    public Single<ResponseDataEncapsulate> sendVerification(String phoneNumber) {
        return null;
/*
        // Call to remote phone number verification code sending service
        String verificationNumber =
                String.valueOf(100000 + new Random().nextInt(999991) + 1);
        UserAccountStateSignUpEntity userAccountStateSignUpEntity =
                new UserAccountStateSignUpEntity(userId, verificationNumber, Boolean.FALSE);

        return Single.fromPublisher(phoneVerificationMongoCollection
                .insertOne(new Document(userAccountStateSignUpEntity.makeEntityMapForMongo())))
                .map(success -> (ResponseDataEncapsulate) new SuccessResponseConcrete<>(200,
                         "verificationCode",
                         userAccountStateSignUpEntity.getVerificationCode()))
                .onErrorReturn(throwable -> new ErrorResponsesConcrete<>(400,
                        "Something Went Wrong",
                        throwable.getMessage()));
*/
    }

    @Override
    public Single<ResponseDataEncapsulate>
    confirmPhoneNumberAndSignUp(UserAccountStateSignUpEntity userSignUpEntity) {
        return null;
        /*return Single
                .fromPublisher(phoneVerificationMongoCollection
                        .find(new Document("userId", userSignUpEntity.getUserId()))
                        .first())
                .filter(Objects::nonNull)
                .toSingle()
                .flatMap(document -> Single.fromPublisher(phoneVerificationMongoCollection
                        .updateOne(Filters.eq("userId", userSignUpEntity.getUserId()),
                                new Document("$set", new Document("verifiedAccount", Boolean.TRUE)),
                                new UpdateOptions().upsert(true)))
                .flatMap(document1 -> Single
                        .just((ResponseDataEncapsulate)new SuccessResponseConcrete<>(200,
                        "Message", "Created"))))
                .onErrorReturn(throwable -> new ErrorResponsesConcrete<>(400,
                        "Something Went Wrong,",
                        throwable.getMessage()));*/
    }
}