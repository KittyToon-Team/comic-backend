package org.example.comicbackend.controller.dto;

public class UpdateRoleRequest {
    private String role; // "ADMIN" hoặc "USER"

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
