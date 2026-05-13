package com.testproject.tests;

import com.testproject.base.BaseTest;
import com.testproject.config.TestConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Comment API Testleri")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentApiTest extends BaseTest {

    @Test
    @Order(1)
    @DisplayName("GET /comments → 200 döner ve yorum listesi boş değildir")
    void getAllComments_shouldReturn200AndNonEmptyList() {

        given()
            .spec(requestSpec)
        .when()
            .get(TestConfig.Endpoints.COMMENTS)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("$",     hasSize(greaterThan(0)))
            .body("email", everyItem(not(emptyOrNullString())));
    }

    @Test
    @Order(2)
    @DisplayName("GET /comments?postId=1 → yalnızca postId=1 yorumları gelir")
    void getCommentsByPostId_shouldReturnOnlyMatchingComments() {

        int targetPostId = 1;

        Response response = given()
                .spec(requestSpec)
                .queryParam("postId", targetPostId)
            .when()
                .get(TestConfig.Endpoints.COMMENTS)
            .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .extract().response();

        List<Integer> postIds = response.jsonPath().getList("postId");

        assertThat(postIds)
            .as("Dönen tüm yorumlar postId=" + targetPostId + " olmalıdır")
            .allMatch(id -> id == targetPostId);
    }

    @Test
    @Order(3)
    @DisplayName("GET /comments → tüm yorumların email alanı '@' içerir")
    void getAllComments_emailsShouldContainAtSign() {

        Response response = given()
                .spec(requestSpec)
            .when()
                .get(TestConfig.Endpoints.COMMENTS)
            .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();

        List<String> emails = response.jsonPath().getList("email");

        assertThat(emails).allSatisfy(email ->
            assertThat(email)
                .as("Email adresi '@' karakteri içermelidir: %s", email)
                .contains("@")
        );
    }

    @Test
    @Order(4)
    @DisplayName("GET /comments → her yorumda zorunlu alanlar mevcuttur")
    void getAllComments_shouldHaveRequiredFields() {

        Response response = given()
                .spec(requestSpec)
            .when()
                .get(TestConfig.Endpoints.COMMENTS)
            .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> comments = response.jsonPath().getList("$");

        assertThat(comments).allSatisfy(comment -> {
            assertThat(comment).containsKeys("id", "postId", "name", "email", "body");
            assertThat((String) comment.get("body")).isNotBlank();
            assertThat((String) comment.get("name")).isNotBlank();
        });
    }
}
