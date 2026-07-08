package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.LoginRequest;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setCreatedAt(new Date());
        user.setRole("USER");
        User saved = userRepository.save(user);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
    }
}
