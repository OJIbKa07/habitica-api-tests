package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResponse {
    private boolean success;
    private TaskData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TaskData {
        private String id;
        private String text;
        private String type;
        private String createdAt;
        private String updatedAt;
        private boolean completed;
    }
}
