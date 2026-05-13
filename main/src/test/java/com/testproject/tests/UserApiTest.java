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

/**
 * /users endpoint'i için regresyon testleri.
 *
 * <p>Kapsanan senaryolar:</p>
 * <ul>
 *   <li>Tüm kullanıcıların listelenmesi ve alanların doğrulanması</li>
 *   <li>Tekil kullanıcı getirilmesi ve iç içe nesne doğrulama</li>
 *   <li>Email formatının doğrulanması</li>
 *   <li>Tüm kullanıcıların benzersiz email'e sahip olduğunun kontrolü</li>
 * </ul>
 */
@DisplayName("User API Testleri")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserApiTest extends BaseTest {

    // ── GET /users ───────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("GET /users → 200 döner ve 10 kullanıcı bulunur")
    void getAllUsers_shouldReturn200AndTenUsers() {

        given()
            .spec(requestSpec)
        .when()
            .get(TestConfig.Endpoints.USERS)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("$",    hasSize(10))
            .body("id",   everyItem(notNullValue()))
            .body("name", everyItem(not(emptyOrNullString())))
            .body("email",everyItem(not(emptyOrNullString())));
    }

    @Test
    @Order(2)
    @DisplayName("GET /users → tüm email adresleri benzersizdir")
    void getAllUsers_emailsShouldBeUnique() {

        Response response = given()
                .spec(requestSpec)
            .when()
                .get(TestConfig.Endpoints.USERS)
            .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();

        List<String> emails = response.jsonPath().getList("email");
        long uniqueCount = emails.stream().distinct().count();

        assertThat(uniqueCount)
            .as("Her kullanıcının email adresi benzersiz olmalıdır")
            .isEqualTo(emails.size());
    }

    // ── GET /users/{id} ──────────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("GET /users/1 → iç içe address nesnesi doğru gelir")
    void getUserById_shouldReturnNestedAddressObject() {

        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get(TestConfig.Endpoints.USER_BY_ID)
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id",               equalTo(1))
            .body("name",             equalTo("Leanne Graham"))
            .body("username",         equalTo("Bret"))
            .body("email",            equalTo("Sincere@april.biz"))
            // İç içe adres nesnesi kontrolü
            .body("address.city",     not(emptyOrNullString()))
            .body("address.zipcode",  not(emptyOrNullString()))
            // İç içe geo koordinatları
            .body("address.geo.lat",  not(emptyOrNullString()))
            .body("address.geo.lng",  not(emptyOrNullString()))
            // Şirket bilgisi
            .body("company.name",     not(emptyOrNullString()));
    }

    @Test
    @Order(4)
    @DisplayName("GET /users → tüm kullanıcılar geçerli website alanına sahiptir")
    void getAllUsers_shouldHaveWebsiteField() {

        Response response = given()
                .spec(requestSpec)
            .when()
                .get(TestConfig.Endpoints.USERS)
            .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> users = response.jsonPath().getList("$");

        assertThat(users).allSatisfy(user -> {
            assertThat(user).containsKey("website");
            assertThat((String) user.get("website")).isNotBlank();
            assertThat((String) user.get("phone")).isNotBlank();
        });
    }

    // ── GET /users/{id} - Negatif ─────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("GET /users/9999 → 404 döner (var olmayan kullanıcı)")
    void getUserById_nonExistent_shouldReturn404() {

        given()
            .spec(requestSpec)
            .pathParam("id", 9999)
        .when()
            .get(TestConfig.Endpoints.USER_BY_ID)
        .then()
            .time(lessThan(TestConfig.MAX_RESPONSE_TIME_MS))
            .statusCode(404);
    }
}
