package com.testproject.base;

import com.testproject.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import static org.hamcrest.Matchers.lessThan;

/**
 * Tüm test sınıflarının kalıtım aldığı temel sınıf.
 *
 * <p>Rest Assured'un ortak konfigürasyonunu (base URL, header'lar,
 * log filtreleri, yanıt şartnamesi) tek bir yerde tanımlar.
 * Her test sınıfı bu ayarları otomatik olarak miras alır.</p>
 */
public abstract class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;

    @BeforeAll
    static void setupRestAssured() {

        // İstek şartnamesi: tüm testlerde ortak olan header ve ayarlar
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(TestConfig.BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())   // her isteği logla
                .addFilter(new ResponseLoggingFilter())  // her yanıtı logla
                .build();

        // Yanıt şartnamesi: tüm testlerde ortak beklentiler
        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(TestConfig.MAX_RESPONSE_TIME_MS))
                .build();

        RestAssured.requestSpecification  = requestSpec;
        RestAssured.responseSpecification = responseSpec;
    }
}
