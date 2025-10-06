package tests;

import api.AccountApiSteps;
import helpers.WithLogin;
import io.qameta.allure.*;
import io.restassured.response.Response;
import models.LoginResponse;
import models.UserInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.logevents.SelenideLogger.step;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpecs.responseSpec;
import static specs.LoginSpec.authSpec;

@Epic("API")
@Feature("Авторизация")
@Owner("oPalushina")
@Tag("api")
public class AuthApiTests {

    LoginResponse loginResponse = AccountApiSteps.loginWithApi();

    @WithLogin
    @Test
    @Story("Проверка метода логина")
    @DisplayName("Проверка метода авторизации")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Проверяет, что метод авторизации возвращает корректный статус и заполняет поля userID и userName")
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
    @Story("Получение информации о пользователе")
    @DisplayName("Проверка авторизации и получение информации о пользователе через API")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Проверяет, что после авторизации GET /user возвращает корректные данные о пользователе")
    void getUserInfoViaApiTest() {
        Response response = step("GET /user — получаем информацию о пользователе", () ->
                authSpec(loginResponse)
                        .get("/user")
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Проверяем отдельные значения в ответе (через path)", () -> {
            assertThat(response.jsonPath().getString("data.id")).isEqualTo(loginResponse.getUserID());
            assertThat(response.jsonPath().getString("data.profile.name")).isEqualTo(loginResponse.getUserName());
        });

        step("Проверяем корректную десериализацию тела ответа в модель", () -> {
            UserInfoResponse userInfo = response.as(UserInfoResponse.class);

            assertThat(userInfo.getData().getId()).isEqualTo(loginResponse.getUserID());
            assertThat(userInfo.getData().getProfile().getName()).isEqualTo(loginResponse.getUserName());
            assertThat(userInfo.getData().getProfile()).isNotNull();

            System.out.println("✅ Пользователь авторизован через модель: " + userInfo.getData().getProfile().getName());
        });
    }
}
