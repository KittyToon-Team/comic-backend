package org.example.comicbackend.controller.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
    private String newPassword;
}
