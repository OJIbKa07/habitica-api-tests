package api;

import configs.AuthConfig;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import models.LoginResponse;
import org.aeonbits.owner.ConfigFactory;

import static helpers.allure.report.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.given;
import static specs.BaseSpecs.requestSpec;
import static specs.BaseSpecs.responseSpec;

@Slf4j
public class AccountApiSteps {
    public static final AuthConfig authConfig = ConfigFactory.create(AuthConfig.class, System.getProperties());

    public static LoginResponse loginWithApi() {
        log.info("Выполняем авторизацию в Habitica API...");

        Response response = given()
                .filter(withCustomTemplates())
                .spec(requestSpec)
                .header("x-api-user", authConfig.user_id())
                .header("x-api-key", authConfig.api_token())
                .header("x-client", authConfig.username())
                .get("/user")
                .then()
                .spec(responseSpec(200))
                .extract().response();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setStatusCode(response.getStatusCode());

        if (response.statusCode() == 200) {
            loginResponse.setUserID(response.jsonPath().getString("data.id"));
            loginResponse.setUserName(response.jsonPath().getString("data.profile.name"));
            loginResponse.setToken(authConfig.api_token());
            loginResponse.setExpires("never");

            log.info("Авторизация успешна. Пользователь: {}", loginResponse.getUserName());
        } else {
            throw new RuntimeException("Авторизация не удалась: " + response.asString());
        }

        return loginResponse;
    }
}
