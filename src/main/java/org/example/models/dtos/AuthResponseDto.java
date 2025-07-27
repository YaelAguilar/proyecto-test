package org.example.models.dtos;

public class AuthResponseDto {
    private String message;
    private String token;
    private int userId;
    private String userRole;
    private Integer companyId; // Para proveedores

    public AuthResponseDto(String message, String token, int userId, String userRole) {
        this.message = message;
        this.token = token;
        this.userId = userId;
        this.userRole = userRole;
    }

    public AuthResponseDto(String message, String token, int userId, String userRole, Integer companyId) {
        this.message = message;
        this.token = token;
        this.userId = userId;
        this.userRole = userRole;
        this.companyId = companyId;
    }

    public String getMessage() { return message; }
    public String getToken() { return token; }
    public int getUserId() { return userId; }
    public String getUserRole() { return userRole; }
    public Integer getCompanyId() { return companyId; }
}