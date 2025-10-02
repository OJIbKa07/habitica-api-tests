package tests;

import api.AccountApiSteps;
import helpers.WithLogin;
import io.restassured.response.Response;
import models.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.logevents.SelenideLogger.step;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpecs.responseSpec;
import static specs.LoginSpec.authSpec;


public class AuthApiTests {

    @Test
    @DisplayName("Проверка метода авторизации")
    void loginMethodTest() {
        LoginResponse loginResponse = AccountApiSteps.loginWithApi();

        step("Проверяем, что логин прошёл успешно", () -> {
            assertThat(loginResponse.getStatusCode()).isEqualTo(200);
            assertThat(loginResponse.getUserID()).isNotNull();
            assertThat(loginResponse.getUserName()).isNotNull();
            System.out.println("✅ Авторизация через метод успешна: " + loginResponse.getUserName());
        });
    }

    @WithLogin
    @Test
    @DisplayName("Проверка авторизации и получение информации о пользователе через API")
    void getUserInfoViaApiTest() {
        LoginResponse loginResponse = AccountApiSteps.loginWithApi();

        Response response = step("GET /user — получаем информацию о пользователе", () ->
                authSpec(loginResponse)
                        .get("/user")
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Проверяем, что пользователь авторизован и данные совпадают с логином", () -> {
            assertThat(response.jsonPath().getString("data.id")).isEqualTo(loginResponse.getUserID());
            assertThat(response.jsonPath().getString("data.profile.name")).isEqualTo(loginResponse.getUserName());
            System.out.println("✅ Пользователь авторизован через API: " + loginResponse.getUserName());
        });
    }
}
