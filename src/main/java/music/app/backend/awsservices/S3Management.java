package music.app.backend.awsservices;

import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.context.scope.ThreadLocal;
import io.reactivex.Single;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;

@ThreadLocal
public class S3Management {

    @Value("${aws.s3.access-key}") private String accessKey;
    @Value("${aws.s3.secret-access-key}") private String secretKey;
    @Value("${aws.s3.session-token}") private String sessionToken;
    @Value("${aws.s3.bucket-name}") private String bucketName;

    private S3AsyncClient s3AsyncClient;

    @PostConstruct
    public void connectToS3() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        SdkAsyncHttpClient sdkAsyncHttpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .maxPendingConnectionAcquires(10_000)
                .build();
        s3AsyncClient = S3AsyncClient.builder()
                .httpClient(sdkAsyncHttpClient)
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    public Single<ByteBuffer> getDataFromS3(String userId, String songId) {
        return Single.fromFuture(s3AsyncClient.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(userId+"/"+songId)
                .build(), AsyncResponseTransformer.toBytes()))
                .map(BytesWrapper::asByteBuffer);
    }

    public Single<Boolean> putDataToS3(String userId, String songId, byte[] fileBytes) {
        return Single.fromFuture(s3AsyncClient.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(userId+"/"+songId)
                        .acl(ObjectCannedACL.PRIVATE)
                        .build(),
                AsyncRequestBody.fromBytes(fileBytes)))
                .map(putObjectResponse -> putObjectResponse.sdkHttpResponse().isSuccessful());
    }

    public Single<Boolean> deleteDataFromS3(String s3FileKey, String fileName) {
        return Single.fromFuture(s3AsyncClient.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder()
                        .objects(ObjectIdentifier.builder()
                                .key(s3FileKey+"/"+fileName)
                                .build())
                        .build())
                .build()))
        .map(deleteObjectsResponse -> deleteObjectsResponse.sdkHttpResponse().isSuccessful());
    }
}