package tests;

import api.AccountApiSteps;
import helpers.WithLogin;
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

@Epic("API")
@Feature("Task Management")
@Owner("oPalushina")
@Tag("api")
public class TaskApiTests {

    LoginResponse loginResponse = AccountApiSteps.loginWithApi();
    RandomUtils faker = new RandomUtils();
    TaskRequest task;
    String taskText, taskType;

    @WithLogin
    @Test
    @Story("Create Task")
    @DisplayName("Create a new task")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies the ability to create a new task via POST /tasks/user")
    void createTaskTest() {
        taskText = "Task: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        Response response = step("Send POST /tasks/user to create a task", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().response()
        );

        step("Verify the task is created with correct text", () -> {
            assertThat(response.jsonPath().getString("data.text")).isEqualTo(taskText);
        });

        step("Deserialize response and check model fields", () -> {
            TaskResponse taskResponse = response.as(TaskResponse.class);
            assertThat(taskResponse.getData().getText()).isEqualTo(taskText);
            assertThat(taskResponse.getData().getType()).isEqualTo(taskType);
            assertThat(taskResponse.getData().getId()).isNotBlank();
        });
    }

    @WithLogin
    @Test
    @Story("Get Task List")
    @DisplayName("Get all user tasks")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that GET /tasks/user returns a non-empty task list")
    void getAllTasksTest() {
        Response response = step("Send GET /tasks/user to retrieve task list", () ->
                authSpec(loginResponse)
                        .get("/tasks/user")
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Verify the task list is not empty", () -> {
            assertThat(response.jsonPath().getList("data")).isNotEmpty();
        });

        step("Deserialize task list and verify data", () -> {
            TaskListResponse taskList = response.as(TaskListResponse.class);
            assertThat(taskList.getData()).isNotEmpty();
            assertThat(taskList.getData().get(0).getText()).isNotBlank();
        });
    }

    @WithLogin
    @Test
    @Story("Delete Task")
    @DisplayName("Delete a user task")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that DELETE /tasks/{id} correctly deletes a task")
    void deleteTaskTest() {
        taskText = "Task: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        String taskId = step("Create a task via POST /tasks/user", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        step("Delete the task via DELETE /tasks/{id}", () -> {
            authSpec(loginResponse)
                    .delete("/tasks/" + taskId)
                    .then()
                    .spec(responseSpec(200));
        });

        step("Verify the task is not present in the list", () -> {
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
    @Story("Update Task")
    @DisplayName("Update a user task")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies the ability to update a task via PUT /tasks/{id}")
    void updateTaskTest() {
        taskText = "Task: " + faker.getTitle();
        taskType = TaskType.getRandomType().getType();
        task = new TaskRequest(taskText, taskType);

        String taskId = step("Create a task via POST /tasks/user", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        String updatedText = taskText + " - updated";
        TaskRequest updatedTask = new TaskRequest(updatedText, taskType);

        step("Update the task via PUT /tasks/{id}", () -> {
            authSpec(loginResponse)
                    .body(updatedTask)
                    .put("/tasks/" + taskId)
                    .then()
                    .spec(responseSpec(200));
        });

        step("Verify the task is updated in the list", () -> {
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
    @Story("Complete Todo Task")
    @DisplayName("Complete a Todo task")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that POST /tasks/{id}/score/up correctly completes a Todo task")
    void completeTodoTaskTest() {
        String taskText = "Todo: " + faker.getTitle();
        TaskRequest task = new TaskRequest(taskText, TODO.getType());

        String taskId = step("Create a Todo task via POST /tasks/user", () ->
                authSpec(loginResponse)
                        .body(task)
                        .post("/tasks/user")
                        .then()
                        .spec(responseSpec(201))
                        .extract().jsonPath().getString("data.id")
        );

        Response scoreResponse = step("Complete the task via POST /tasks/{id}/score/up", () ->
                authSpec(loginResponse)
                        .post("/tasks/{id}/score/up", taskId)
                        .then()
                        .spec(responseSpec(200))
                        .extract().response()
        );

        step("Verify the task is marked as completed", () -> {
            assertThat(scoreResponse.jsonPath().getBoolean("success")).isTrue();
        });

        step("Deserialize response and verify success flag", () -> {
            TaskResponse taskResponse = scoreResponse.as(TaskResponse.class);
            assertThat(taskResponse.isSuccess()).isTrue();
        });
    }
}
