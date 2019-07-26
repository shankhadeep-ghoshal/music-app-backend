package music.app.backend;

import java.security.SecureRandom;
import java.util.Base64;

public class UtilityClass {

    public static String generateClientToken(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }


}
