package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.AuthResponse;
import org.example.comicbackend.controller.dto.ChangePasswordRequest;
import org.example.comicbackend.controller.dto.ForgotPasswordRequest;
import org.example.comicbackend.controller.dto.LoginRequest;
import org.example.comicbackend.controller.dto.RegisterRequest;
import org.example.comicbackend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // Vô hiệu hóa endpoint đổi mật khẩu không xác thực để bảo vệ an toàn tài khoản
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "message", "Tính năng thiết lập lại mật khẩu tự động đang tạm thời bị khóa vì lý do bảo mật. Vui lòng liên hệ ban quản trị để được hỗ trợ xác minh danh tính."
        ));
    }
}
