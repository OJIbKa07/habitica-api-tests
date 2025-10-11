package tests;

import api.AccountApiSteps;
import helpers.allure.annotations.Layer;
import helpers.allure.annotations.Microservice;
import helpers.login.WithLogin;
import io.qameta.allure.*;
import io.restassured.response.Response;
import models.LoginResponse;
import models.TaskListResponse;
import models.TaskRequest;
import models.TaskResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test_data.TaskType;
import utils.RandomUtils;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpecs.responseSpec;
import static specs.LoginSpec.authSpec;
import static test_data.TaskType.TODO;

@Owner("oPalushina")
@Layer("api")
@Microservice("TaskService")
@Tag("api")
@Epic("Задачи")
@Feature("Работа с задачами (API)")
@Story("CRUD операций задач")
@DisplayName("API: Работа с задачами")
public class TaskApiTests {

    LoginResponse loginResponse = AccountApiSteps.loginWithApi();
    RandomUtils faker = new RandomUtils();
    TaskRequest task;
    String taskText, taskType;

    @WithLogin
    @Test
    @Story("Создание задачи")
    @DisplayName("Создание новой задачи")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Проверяет возможность создания новой задачи через POST /tasks/user")
    void createTaskTest() {
        taskText = "Задача: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        Response response = step("Отправляем POST /tasks/user для создания задачи", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().response()
        );

        step("Проверяем, что задача создана с корректным текстом", () -> {
            assertThat(response.jsonPath().getString("data.text")).isEqualTo(taskText);
        });

        step("Десериализуем ответ и проверяем поля модели", () -> {
            TaskResponse taskResponse = response.as(TaskResponse.class);
            assertThat(taskResponse.getData().getText()).isEqualTo(taskText);
            assertThat(taskResponse.getData().getType()).isEqualTo(taskType);
            assertThat(taskResponse.getData().getId()).isNotBlank();
        });
    }

    @WithLogin
    @Test
    @Story("Получение списка задач")
    @DisplayName("Получение всех задач пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что GET /tasks/user возвращает непустой список задач")
    void getAllTasksTest() {
        Response response = step("Отправляем GET /tasks/user для получения списка задач", () ->
                authSpec(loginResponse)
                        .get("/tasks/user")
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Проверяем, что список задач не пуст", () -> {
            assertThat(response.jsonPath().getList("data")).isNotEmpty();
        });

        step("Десериализуем список задач и проверяем данные", () -> {
            TaskListResponse taskList = response.as(TaskListResponse.class);
            assertThat(taskList.getData()).isNotEmpty();
            assertThat(taskList.getData().get(0).getText()).isNotBlank();
        });
    }

    @WithLogin
    @Test
    @Story("Удаление задачи")
    @DisplayName("Удаление задачи пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что DELETE /tasks/{id} корректно удаляет задачу")
    void deleteTaskTest() {
        taskText = "Задача: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        String taskId = step("Создаём задачу через POST /tasks/user", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        step("Удаляем задачу через DELETE /tasks/{id}", () -> {
            authSpec(loginResponse)
                    .delete("/tasks/" + taskId)
                    .then()
                    .spec(responseSpec(200));
        });

        step("Проверяем, что задача отсутствует в списке", () -> {
            Response allTasks = authSpec(loginResponse)
                    .get("/tasks/user")
                    .then()
                    .spec(responseSpec(200))
                    .extract().response();

            TaskListResponse tasks = allTasks.as(TaskListResponse.class);
            assertThat(tasks.getData().stream().map(TaskResponse.TaskData::getId))
                    .doesNotContain(taskId);
        });
    }

    @WithLogin
    @Test
    @Story("Обновление задачи")
    @DisplayName("Обновление задачи пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет возможность обновления задачи через PUT /tasks/{id}")
    void updateTaskTest() {
        taskText = "Задача: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        String taskId = step("Создаём задачу через POST /tasks/user", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        String updatedText = taskText + " — обновлено";
        TaskRequest updatedTask = new TaskRequest(updatedText, taskType);

        step("Обновляем задачу через PUT /tasks/{id}", () -> {
            authSpec(loginResponse)
                    .body(updatedTask)
                    .put("/tasks/" + taskId)
                    .then()
                    .spec(responseSpec(200));
        });

        step("Проверяем, что задача обновлена в списке", () -> {
            Response allTasks = authSpec(loginResponse)
                    .get("/tasks/user")
                    .then()
                    .spec(responseSpec(200))
                    .extract().response();

            TaskListResponse tasks = allTasks.as(TaskListResponse.class);
            assertThat(tasks.getData().stream().map(TaskResponse.TaskData::getText))
                    .contains(updatedText);
        });
    }

    @WithLogin
    @Test
    @Story("Выполнение задачи Todo")
    @DisplayName("Выполнение задачи Todo")
    @Severity(SeverityLevel.NORMAL)
    @Description("Проверяет, что POST /tasks/{id}/score/up корректно выполняет задачу Todo")
    void completeTodoTaskTest() {
        String taskText = "Todo: " + faker.getTitle();
        TaskRequest task = new TaskRequest(taskText, TODO.getType());

        String taskId = step("Создаём задачу Todo через POST /tasks/user", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        Response scoreResponse = step("Выполняем задачу через POST /tasks/{id}/score/up", () ->
                authSpec(loginResponse)
                        .post("/tasks/{id}/score/up", taskId)
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Проверяем, что задача помечена как выполненная", () -> {
            assertThat(scoreResponse.jsonPath().getBoolean("success")).isTrue();
        });

        step("Десериализуем ответ и проверяем флаг успеха", () -> {
            TaskResponse taskResponse = scoreResponse.as(TaskResponse.class);
            assertThat(taskResponse.isSuccess()).isTrue();
        });
    }
}
