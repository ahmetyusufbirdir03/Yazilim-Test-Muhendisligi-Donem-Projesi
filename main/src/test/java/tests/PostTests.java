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
}
