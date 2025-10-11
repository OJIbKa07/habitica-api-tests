package api;

import configs.AuthConfigProvider;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import models.LoginResponse;

import static helpers.allure.report.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.given;
import static specs.BaseSpecs.requestSpec;
import static specs.BaseSpecs.responseSpec;

@Slf4j
public class AccountApiSteps {

    public static LoginResponse loginWithApi() {
        log.info("🔐 Выполняем авторизацию в Habitica API...");

        Response response = given()
                .filter(withCustomTemplates())
                .spec(requestSpec)
                .header("x-api-user", AuthConfigProvider.authConfig.user_id())
                .header("x-api-key", AuthConfigProvider.authConfig.api_token())
                .header("x-client", AuthConfigProvider.authConfig.username())
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
            loginResponse.setToken(AuthConfigProvider.authConfig.api_token());
            loginResponse.setExpires("never");

            log.info("Авторизация успешна. Пользователь: {}", loginResponse.getUserName());
        } else {
            throw new RuntimeException("Авторизация не удалась: " + response.asString());
        }

        return loginResponse;
    }
}
