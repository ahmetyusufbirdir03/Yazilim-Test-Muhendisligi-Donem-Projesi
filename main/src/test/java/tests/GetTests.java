package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("GET İstekleri Testleri")
class GetTests extends BaseTest {
    
    // TEST 1 — Gönderi Getir 
    @Test
    @DisplayName("GET /posts/1")
    void getSinglePost_shouldReturnCorrectPost() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            // 1) Status code kontrolü
            .statusCode(200)

            // 2) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS))

            // 3) Response body değer kontrolleri
            .body("id",     equalTo(1))
            .body("userId", equalTo(1))
            .body("title",  not(emptyOrNullString()))
            .body("body",   not(emptyOrNullString()));
    }

    // TEST 2 — Tüm Gönderileri Listele
    @Test
    @DisplayName("GET /posts")
    void getAllPosts_shouldReturn100Posts() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .statusCode(200) // Status code kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS)) // Yanıt süresi kontrolü
            .body("",         hasSize(100))   // Response body değer kontrolleri  
            .body("[0].id",   equalTo(1))            
            .body("[0].userId", greaterThan(0))       
            .body("[99].id",  equalTo(100));          
    }

    // TEST 3 — Kullanıcı Todo Listesi
    @Test
    @DisplayName("GET /users/1/todos")
    void getUserTodos_shouldReturnTodosForUser1() {
        Response response =
            given()
                .spec(requestSpec)
            .when()
                .get("/users/1/todos")
            .then()
                .statusCode(200) // Status code kontrolü
                .time(lessThan(MAX_RESPONSE_TIME_MS)) // Yanıt süresi kontrolü
                .body("",not(empty())) // Response body değer kontrolleri     
                .body("userId",everyItem(equalTo(1))) 
                .body("title",everyItem(not(emptyOrNullString())))
                .body("completed",hasItem(true))       
                .extract().response();

        int toplamTodo = response.jsonPath().getList("$").size();
        assert toplamTodo > 0 : "Todo listesi boş olmamalı";
    }
}
