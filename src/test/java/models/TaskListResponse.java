package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskListResponse {
    private boolean success;
    private List<TaskResponse.TaskData> data;
}
