package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("POST İstekleri Testleri")
class PostTests extends BaseTest {

    // TEST 1 — Yeni Gönderi Oluştırma 
    @Test
    @DisplayName("POST /posts → New Comment - 201")
    void createPost_shouldReturn201WithCreatedPost() {

        // Request body 
        Map<String, Object> yeniGonderi = new HashMap<>();
        yeniGonderi.put("title",  "Test Post");
        yeniGonderi.put("body",   "Body Text");
        yeniGonderi.put("userId", 1);

        given()
            .spec(requestSpec)
            .body(yeniGonderi)
        .when()
            .post("/posts")
        .then()
            .statusCode(201) // Status code kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS)) // Yanıt süresi kontrolü
            .body("id",     notNullValue()) // Response body değer kontrolleri                              
            .body("title",  equalTo("Test Post"))
            .body("body",   equalTo("Body Text"))
            .body("userId", equalTo(1));
    }

    // TEST 2 — Yeni Yorum Oluşturma 
    @Test
    @DisplayName("POST /comments → New comment - 201")
    void createComment_shouldReturn201WithCreatedComment() {

        Map<String, Object> newComment = new HashMap<>();
        newComment.put("postId", 1);
        newComment.put("name",   "Auto Test Comment");
        newComment.put("email",  "testuser@example.com");
        newComment.put("body",   "Body text");

        Response response =
            given()
                .spec(requestSpec)
                .body(newComment)
            .when()
                .post("/comments")
            .then()
                .statusCode(201) // Status code kontrolü
                .time(lessThan(MAX_RESPONSE_TIME_MS)) // Yanıt süresi kontrolü
                .body("postId", equalTo(1)) // Response body değer kontrolleri
                .body("name",   equalTo("Auto Test Comment"))
                .body("email",  equalTo("testuser@example.com"))
                .body("body",   not(emptyOrNullString()))
                .extract().response();

        int resultId = response.jsonPath().getInt("id");
        assertThat("Yorum ID pozitif olmalı", resultId, greaterThan(0));
    }

    // TEST 3 — Yeni Todo Oluşturma
    @Test
    @DisplayName("POST /todos → New todo - 201")
    void createTodo_shouldReturn201AndBeIncomplete() {

        Map<String, Object> newTodo = new HashMap<>();
        newTodo.put("title",     "Complete Tests");
        newTodo.put("completed", false);
        newTodo.put("userId",    5);

        given()
            .spec(requestSpec)
            .body(newTodo)
        .when()
            .post("/todos")
        .then()
            // 1) Status code kontrolü
            .statusCode(201)

            // 2) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS))

            // 3) Response body değer kontrolleri
            .body("id",        notNullValue())
            .body("title",     equalTo("Complete Tests"))
            .body("completed", equalTo(false))  
            .body("userId",    equalTo(5));
    }

    // TEST 4 — Yeni Kullanıcı Oluşturma
    @Test
    @DisplayName("POST /users → New user - 201")
    void createUser_shouldReturn201WithCreatedUser() {

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", "Test User");
        newUser.put("username", "testuser");
        newUser.put("email", "testuser@example.com");

        given()
            .spec(requestSpec)
            .body(newUser)
        .when()
            .post("/users")
        .then()
            // 1) Status code kontrolü
            .statusCode(201)

            // 2) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS))

            // 3) Response body değer kontrolleri
            .body("id", notNullValue())
            .body("name", equalTo("Test User"))
            .body("email", equalTo("testuser@example.com"));
    }

    // TEST 5 — Yeni Albüm Oluşturma
    @Test
    @DisplayName("POST /albums → New album - 201")
    void createAlbum_shouldReturn201WithCreatedAlbum() {

        Map<String, Object> newAlbum = new HashMap<>();
        newAlbum.put("userId", 1);
        newAlbum.put("title", "My Test Album");

        given()
            .spec(requestSpec)
            .body(newAlbum)
        .when()
            .post("/albums")
        .then()
            .statusCode(201) // Status code kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS)) // Yanıt süresi kontrolü
            .body("id", notNullValue()) // Response body değer kontrolleri
            .body("userId", equalTo(1))
            .body("title", equalTo("My Test Album"));
    }

    // TEST 6 — Yeni Fotoğraf Oluşturma
    @Test
    @DisplayName("POST /photos → New photo - 201")
    void createPhoto_shouldReturn201WithCreatedPhoto() {

        Map<String, Object> newPhoto = new HashMap<>();
        newPhoto.put("albumId", 1);
        newPhoto.put("title", "Test Photo");
        newPhoto.put("url", "https://via.placeholder.com/600/92c952");
        newPhoto.put("thumbnailUrl", "https://via.placeholder.com/150/92c952");

        given()
            .spec(requestSpec)
            .body(newPhoto)
        .when()
            .post("/photos")
        .then()
            .statusCode(201) // Status code kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS)) // Yanıt süresi kontrolü
            .body("id", notNullValue()) // Response body değer kontrolleri
            .body("albumId", equalTo(1))
            .body("title", equalTo("Test Photo"));
    }

    // TEST 7 — String Body İle Gönderi Oluşturma
    @Test
    @DisplayName("POST /posts → String body - 201")
    void createPostWithStringBody_shouldReturn201() {

        String jsonBody = "{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }";

        given()
            .spec(requestSpec)
            .body(jsonBody)
        .when()
            .post("/posts")
        .then()
            .statusCode(201) // Status code kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS)) // Yanıt süresi kontrolü
            .body("id", notNullValue()) // Response body değer kontrolleri
            .body("title", equalTo("foo"))
            .body("body", equalTo("bar"));
    }
}
