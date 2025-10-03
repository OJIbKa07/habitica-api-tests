package tests;

import api.AccountApiSteps;
import helpers.WithLogin;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import models.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.logevents.SelenideLogger.step;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpecs.responseSpec;
import static specs.LoginSpec.authSpec;

@Epic("Api")
@Feature("Авторизация")
@Tag("api")
public class AuthApiTests {
    LoginResponse loginResponse = AccountApiSteps.loginWithApi();

    @WithLogin
    @Test
    @DisplayName("Проверка метода авторизации")
    @Severity(SeverityLevel.BLOCKER)
    void loginMethodTest() {

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
    @Severity(SeverityLevel.BLOCKER)
    void getUserInfoViaApiTest() {
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
