package specs;

import io.restassured.specification.RequestSpecification;
import models.LoginResponse;

import static io.restassured.RestAssured.given;
import static specs.BaseSpecs.requestSpec;

public class LoginSpec {
    public static RequestSpecification authSpec(LoginResponse loginResponse) {
        return given()
                .spec(requestSpec)
                .header("x-api-user", loginResponse.getUserID())
                .header("x-api-key", loginResponse.getToken())
                .header("x-client", "qa-habitica-tests");
    }
}
