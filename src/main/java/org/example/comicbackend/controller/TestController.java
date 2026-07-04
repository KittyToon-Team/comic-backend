package org.example.comicbackend.controller; // Nhớ đổi tên package cho khớp máy bạn

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class TestController {

    @GetMapping("/api/test")
    public String testConnection() {
        return "Xin chào! Kết nối giữa Vue 3 và Spring Boot đã thành công rực rỡ!";
    }
}