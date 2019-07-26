package music.app.backend.registerandauthorizationservice.phonenumber;

import com.kosprov.jargon2.api.Jargon2;

public class UserPhoneAuthenticationRegistrationLogoutConstants {

    public static class URLS {
        // Sign Up
        public static final String CONTROLLER_SIGN_UP_PATH = "/SignUp";
        public static final String CONTROLLER_SIGN_UP_VERIFICATION_CODE_PARAM = "/Confirmation";

        // Login
        public static final String CONTROLLER_LOGIN_TOKEN_PATH = "/Login/Token";
        public static final String CONTROLLER_LOGIN_PASSWORD_PATH = "/Login/Password";

        // Logout
        public static final String CONTROLLER_LOGOUT_PATH = "/Logout";
        public static final String CONTROLLER_LOGOUT_PATH_PHONE = "/Phone";
    }

    public static class Argon2HyperParams {
        // Argon2 metadata
        public static final Jargon2.Type ARGON2_TYPE = Jargon2.Type.ARGON2id;
        public static final int ARGON_MEMORY_COST = 65536;
        public static final int ARGON_NUM_PASS_MEMORY = 6;
        public static final int ARGON_PARALLELISM = 4;
        public static final int ARGON_SALT_LENGTH = 128;
        public static final int ARGON_HASH_LENGTH = 128;
    }

    public static final int PERMANENT_SESSION_TOKEN_LENGTH = 1024;
    public static final int TEMPORARY_SESSION_TOKEN_LENGTH = 64;

    // REDIS metadata
    public static final long EVICTION_DURATION = 300L;
}