package api;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import models.LoginResponse;

import static io.restassured.RestAssured.given;
import static specs.BaseSpecs.requestSpec;
import static specs.BaseSpecs.responseSpec;
import static test_data.AuthData.*;

@Slf4j
public class AccountApiSteps {

    public static LoginResponse loginWithApi() {
        log.info("🔐 Выполняем авторизацию в Habitica API...");

        Response response = given()
                .spec(requestSpec)
                .header("x-api-user", HABITICA_USER_ID)
                .header("x-api-key", HABITICA_API_TOKEN)
                .header("x-client", HABITICA_USERNAME)
                .get("/user")
                .then()
                .spec(responseSpec(200))
                .extract().response();
        System.out.println("📦 Full response: " + response.asString());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setStatusCode(response.getStatusCode());

        if (response.statusCode() == 200) {
            loginResponse.setUserID(response.jsonPath().getString("data.id"));
            loginResponse.setUserName(response.jsonPath().getString("data.profile.name"));
            loginResponse.setToken(HABITICA_API_TOKEN);
            loginResponse.setExpires("never");

            log.info("Авторизация успешна. Пользователь: {}", loginResponse.getUserName());
        } else {
            throw new RuntimeException("Авторизация не удалась: " + response.asString());
        }

        return loginResponse;
    }
}
