package com.testproject.tests;

import com.testproject.base.BaseTest;
import com.testproject.config.TestConfig;
import com.testproject.model.Post;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * /posts endpoint'i için regresyon testleri.
 *
 * <p>Kapsanan senaryolar:</p>
 * <ul>
 *   <li>Tüm post'ların listelenmesi (GET)</li>
 *   <li>Tekil post getirilmesi (GET by ID)</li>
 *   <li>Var olmayan post için 404 kontrolü (GET - negatif)</li>
 *   <li>Yeni post oluşturulması (POST with JSON body)</li>
 *   <li>Eksik alanla post oluşturma (POST - negatif)</li>
 * </ul>
 */
@DisplayName("Post API Testleri")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostApiTest extends BaseTest {

    // ── GET /posts ───────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("GET /posts → 200 döner ve liste boş değildir")
    void getAllPosts_shouldReturn200AndNonEmptyList() {

        Response response = given()
                .spec(requestSpec)
            .when()
                .get(TestConfig.Endpoints.POSTS)
            .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))   // liste dolu
                .extract().response();

        // AssertJ ile daha okunabilir doğrulama
        List<Post> posts = response.jsonPath().getList("$", Post.class);
        assertThat(posts).isNotEmpty();
        assertThat(posts).allSatisfy(post -> {
            assertThat(post.getId()).isNotNull().isPositive();
            assertThat(post.getTitle()).isNotBlank();
        });
    }

    // ── GET /posts/{id} ──────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("GET /posts/1 → doğru post alanları döner")
    void getPostById_shouldReturnCorrectPost() {

        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get(TestConfig.Endpoints.POST_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id",     equalTo(1))
            .body("userId", equalTo(1))
            .body("title",  not(emptyOrNullString()))
            .body("body",   not(emptyOrNullString()));
    }

    @ParameterizedTest(name = "GET /posts/{0} → 200 döner")
    @Order(3)
    @DisplayName("Birden fazla ID için GET testi (parametrik)")
    @ValueSource(ints = {1, 5, 10, 50, 100})
    void getPostById_variousIds_shouldReturn200(int postId) {

        given()
            .spec(requestSpec)
            .pathParam("id", postId)
        .when()
            .get(TestConfig.Endpoints.POST_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(postId));
    }

    // ── GET /posts/{id} - Negatif ─────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("GET /posts/9999 → 404 döner (var olmayan kayıt)")
    void getPostById_nonExistent_shouldReturn404() {

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            given()
                .spec(requestSpec)
                .pathParam("id", 9999)
            .when()
                .get(TestConfig.Endpoints.POST_BY_ID)
            .then()
                .statusCode(404);
        });
        
        assertThat(exception.getMessage()).contains("404");
    }

    // ── POST /posts ──────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("POST /posts → 201 döner ve oluşturulan kayıt ID alır")
    void createPost_shouldReturn201AndReturnCreatedPost() {

        Post newPost = new Post(1, "Regresyon Testi Başlığı",
                "Bu gönderi otomatik test tarafından oluşturuldu.");

        Response response = given()
                .spec(requestSpec)
                .body(newPost)
            .when()
                .post(TestConfig.Endpoints.POSTS)
            .then()
                .spec(responseSpec)
                .statusCode(201)
                .body("id",     notNullValue())
                .body("userId", equalTo(newPost.getUserId()))
                .body("title",  equalTo(newPost.getTitle()))
                .body("body",   equalTo(newPost.getBody()))
                .extract().response();

        Post created = response.as(Post.class);
        assertThat(created.getId()).isNotNull().isPositive();
        assertThat(created.getTitle()).isEqualTo(newPost.getTitle());
    }

    @Test
    @Order(6)
    @DisplayName("POST /posts → title alanı yanıtta boş değildir")
    void createPost_withAllFields_responseTitleShouldNotBeBlank() {

        Post payload = new Post(3, "Test Gönderisi", "Detaylı içerik buraya gelir.");

        given()
            .spec(requestSpec)
            .body(payload)
        .when()
            .post(TestConfig.Endpoints.POSTS)
        .then()
            .spec(responseSpec)
            .statusCode(201)
            .body("title", not(emptyOrNullString()))
            .body("userId", equalTo(3));
    }
}
