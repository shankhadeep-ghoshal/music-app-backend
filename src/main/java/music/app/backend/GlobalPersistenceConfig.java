package music.app.backend;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SslOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.context.scope.ThreadLocal;
import io.reactivex.subscribers.TestSubscriber;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ThreadLocal
public abstract class GlobalPersistenceConfig {
    protected MongoCollection<Document> userCollection;
    protected MongoCollection<Document> loginAndLogoutCollection;

    // REDIS
    protected RedisAsyncCommands<String, String> stringRedisUsersSessionTokenCache;

    // Mongo Database
    protected MongoDatabase usersDatabase;

    @Inject
    MongoClient mongoClient;

    @Value("${redis.servers.users.uri}")
    private String redisUriUserDetails;

    @Value("${mongo-database.collection-name-user}")
    private String userCollectionName;

    @Value("${mongo-database.collection-name-login-and-logout}")
    private String loginAndLogoutCollectionName;

    @Value("${mongo-database.name}")
    private String databaseName;

    @PostConstruct
    void InitDb() {
        StatefulRedisConnection<String, String> stringStatefulRedisConnection = initRedisConnection(redisUriUserDetails);

        usersDatabase = mongoClient.getDatabase(databaseName);
        stringRedisUsersSessionTokenCache = stringStatefulRedisConnection.async();

        userCollection = usersDatabase.getCollection(userCollectionName);
        loginAndLogoutCollection = usersDatabase.getCollection(loginAndLogoutCollectionName);

        userCollection.createIndex(Indexes.ascending("userId"),
                new IndexOptions().unique(true))
                .subscribe(new TestSubscriber<>());
        loginAndLogoutCollection.createIndex(Indexes.ascending("userId"),
                new IndexOptions().unique(true))
                .subscribe(new TestSubscriber<>());
    }

    private static StatefulRedisConnection<String, String>
    initRedisConnection(String redisUriUserDetails) {
        RedisClient redisClient = RedisClient.create(redisUriUserDetails);
        
        redisClient.setOptions(ClientOptions.builder()
                .sslOptions(SslOptions.builder()
                        .jdkSslProvider()
                        .build())
                .timeoutOptions(TimeoutOptions.builder()
                        .fixedTimeout(Duration.of(30, ChronoUnit.SECONDS))
                        .build())
                .autoReconnect(true)
                .build());

        return redisClient.connect();
    }
}