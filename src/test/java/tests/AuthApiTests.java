package tests;

import api.AccountApiSteps;
import helpers.login.WithLogin;
import io.qameta.allure.*;
import io.restassured.response.Response;
import models.LoginResponse;
import models.UserInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import helpers.allure.annotations.Layer;
import helpers.allure.annotations.Microservice;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpecs.responseSpec;
import static specs.LoginSpec.authSpec;

@Owner("oPalushina")
@Layer("api")
@Microservice("UserService")
@Tag("api")
@Epic("Пользователи")
@Feature("Авторизация (API)")
@Story("Авторизация пользователя в системе")
@DisplayName("API: Авторизация в системе")
public class AuthApiTests {

    LoginResponse loginResponse = AccountApiSteps.loginWithApi();

    @WithLogin
    @Test
    @Story("Проверка метода логина")
    @DisplayName("Проверка метода авторизации")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Проверяет, что метод авторизации возвращает корректный статус и заполняет поля userID и userName")
    void loginMethodTest() {
        step("Проверяем, что метод логина вернул код 200", () -> {
            assertThat(loginResponse.getStatusCode()).isEqualTo(200);
        });

        step("Проверяем, что поле userID заполнено", () -> {
            assertThat(loginResponse.getUserID()).isNotNull();
        });

        step("Проверяем, что поле userName заполнено", () -> {
            assertThat(loginResponse.getUserName()).isNotNull();
        });
    }

    @WithLogin
    @Test
    @Story("Получение информации о пользователе")
    @DisplayName("Проверка авторизации и получение информации о пользователе через API")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Проверяет, что после авторизации GET /user возвращает корректные данные о пользователе")
    void getUserInfoViaApiTest() {
        Response response = step("Отправляем GET /user для получения информации о пользователе", () ->
                authSpec(loginResponse)
                        .get("/user")
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Проверяем значения полей в JSON-ответе через JsonPath", () -> {
            assertThat(response.jsonPath().getString("data.id")).isEqualTo(loginResponse.getUserID());
            assertThat(response.jsonPath().getString("data.profile.name")).isEqualTo(loginResponse.getUserName());
        });

        step("Десериализуем тело ответа в модель UserInfoResponse и проверяем поля", () -> {
            UserInfoResponse userInfo = response.as(UserInfoResponse.class);

            assertThat(userInfo.getData().getId())
                    .as("ID пользователя должен совпадать с ID из loginResponse")
                    .isEqualTo(loginResponse.getUserID());

            assertThat(userInfo.getData().getProfile().getName())
                    .as("Имя пользователя должно совпадать с loginResponse")
                    .isEqualTo(loginResponse.getUserName());

            assertThat(userInfo.getData().getProfile())
                    .as("Профиль пользователя не должен быть null")
                    .isNotNull();
        });
    }
}
