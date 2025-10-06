package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // 👈 Игнорируем неожиданные поля
public class UserInfoResponse {
    private UserData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserData {
        private String id;
        private UserProfile profile;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserProfile {
        private String name;
        private String email;
        // можешь добавить любые другие поля, которые возвращает API
    }
}
