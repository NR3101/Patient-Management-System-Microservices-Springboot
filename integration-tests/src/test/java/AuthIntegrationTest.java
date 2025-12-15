import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOKWithValidToken() {
        // 1.Arrange
        String loginPayload = """
                {
                    "email" : "testuser@test.com",
                    "password" : "password123"
                }
                """;

        // 2.Act and 3.Assert
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorizedOnInvalidLogin() {
        // 1.Arrange
        String loginPayload = """
                {
                    "email" : "testuser@test.com",
                    "password" : "wrongpassword"
                }
                """;

        // 2.Act and 3.Assert
        RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }
}
