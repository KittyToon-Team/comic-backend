package org.example.comicbackend.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private Integer id;
    private String username;
    private String displayName;
    private String email;
    private String role;
    private String avatarUrl;
}
