package tests;

import api.AccountApiSteps;
import helpers.WithLogin;
import io.restassured.response.Response;
import models.LoginResponse;
import models.TaskRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test_data.TaskType;
import utils.RandomUtils;


import static com.codeborne.selenide.logevents.SelenideLogger.step;
import static specs.BaseSpecs.responseSpec;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.LoginSpec.authSpec;
import static test_data.TaskType.TODO;

public class TaskApiTests {

    LoginResponse loginResponse = AccountApiSteps.loginWithApi();
    RandomUtils faker = new RandomUtils();
    TaskRequest task;
    String taskText, taskType;


    @WithLogin
    @Test
    @DisplayName("Создание новой задачи")
    void createTaskTest() {
        taskText = "Задача: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        Response response = step("POST /tasks/user — создаём задачу", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().response()
        );

        step("Проверяем, что задача создана", () -> {
            assertThat(response.jsonPath().getString("data.text")).isEqualTo(taskText);
            System.out.println("✅ Создана задача: " + taskText);
        });
    }

    @WithLogin
    @Test
    @DisplayName("Получение всех задач пользователя")
    void getAllTasksTest() {

        Response response = step("GET /tasks/user — получаем список задач", () ->
                authSpec(loginResponse)
                        .get("/tasks/user")
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Проверяем, что список задач не пуст", () -> {
            assertThat(response.jsonPath().getList("data")).isNotEmpty();
            System.out.println("📋 Найдено задач: " + response.jsonPath().getList("data").size());
        });
    }

    @WithLogin
    @Test
    @DisplayName("Удаление задачи пользователя")
    void deleteTaskTest() {
        taskText = "Задача: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        String taskId = step("POST /tasks/user — создаём задачу", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        System.out.println("🆕 Создана задача с ID: " + taskId);

        step("DELETE /tasks/{id} — удаляем задачу", () -> {
            authSpec(loginResponse)
                    .delete("/tasks/" + taskId)
                    .then()
                    .spec(responseSpec(200));
        });

        step("GET /tasks/user — проверяем, что задача удалена", () -> {
            Response allTasks = authSpec(loginResponse)
                    .get("/tasks/user")
                    .then()
                    .spec(responseSpec(200))
                    .extract().response();

            String responseBody = allTasks.asString();
            assertThat(responseBody).doesNotContain(taskId);
            assertThat(responseBody).doesNotContain(taskText);
            System.out.println("✅ Задача успешно удалена: " + taskId + " (" + taskText + ")");
        });
    }

    @WithLogin
    @Test
    @DisplayName("Обновление задачи пользователя")
    void updateTaskTest() {
        taskText = "Задача: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        String taskId = step("POST /tasks/user — создаём задачу", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        System.out.println("🆕 Создана задача с ID: " + taskId);

        String updatedText = taskText + " — обновлено";
        TaskRequest updatedTask = new TaskRequest(updatedText, taskType);

        step("PUT /tasks/{id} — обновляем задачу", () -> {
            authSpec(loginResponse)
                    .body(updatedTask)
                    .put("/tasks/" + taskId)
                    .then()
                    .spec(responseSpec(200));
        });

        step("GET /tasks/user — проверяем обновлённую задачу", () -> {
            Response allTasks = authSpec(loginResponse)
                    .get("/tasks/user")
                    .then()
                    .spec(responseSpec(200))
                    .extract().response();

            String responseBody = allTasks.asString();
            assertThat(responseBody).contains(taskId);
            assertThat(responseBody).contains(updatedText);
            System.out.println("✅ Задача успешно обновлена: " + taskId + " (" + updatedText + ")");
        });
    }

    @WithLogin
    @Test
    @DisplayName("Выполнение задачи Todo")
    void completeTodoTaskTest() {
        String taskText = "Todo: " + faker.getTitle();
        TaskRequest task = new TaskRequest(taskText, TODO.getType());

        String taskId = step("POST /tasks/user — создаём задачу Todo", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        System.out.println("🆕 Создана задача Todo с ID: " + taskId);

        Response scoreResponse = step("POST /tasks/{id}/score/up — выполняем задачу Todo", () ->
                authSpec(loginResponse)
                        .post("/tasks/{id}/score/up", taskId)
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Проверяем, что задача выполнена", () -> {
            assertThat(scoreResponse.jsonPath().getBoolean("success")).isTrue();
            System.out.println("✅ Задача Todo выполнена: " + taskId + " (" + taskText + ")");
        });
    }
}
