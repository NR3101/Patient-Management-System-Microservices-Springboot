import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientWithValidToken() {
        // 1.Arrange
        String loginPayload = """
                {
                    "email" : "testuser@test.com",
                    "password" : "password123"
                }
                """;

        // 2.Act and 3.Assert
        String token = RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");


        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue());
    }
}
