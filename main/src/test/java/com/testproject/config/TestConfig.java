package com.testproject.config;

public final class TestConfig {

    private TestConfig() {
    }

    /** Test edilen API'nin base URL'i */
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    /** Kabul edilebilir maksimum yanıt süresi (milisaniye) */
    public static final long MAX_RESPONSE_TIME_MS = 3000L;

    /** API endpoint'leri */
    public static final class Endpoints {
        public static final String POSTS   = "/posts";
        public static final String POST_BY_ID = "/posts/{id}";
        public static final String USERS   = "/users";
        public static final String USER_BY_ID = "/users/{id}";
        public static final String COMMENTS = "/comments";
    }
}
