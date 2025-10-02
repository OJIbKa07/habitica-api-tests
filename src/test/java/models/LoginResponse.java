package models;

import lombok.Data;

@Data
public class LoginResponse {
    private int statusCode;
    private String userID;
    private String userName;
    private String token;
    private String expires;
}
