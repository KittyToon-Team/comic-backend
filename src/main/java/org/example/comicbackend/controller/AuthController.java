package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.ForgotPasswordRequest;
import org.example.comicbackend.controller.dto.LoginRequest;
import org.example.comicbackend.controller.dto.RegisterRequest;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setRole("USER");
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User saved = userRepository.save(user);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String account = loginRequest.getEmail(); // This could be username or email
        Optional<User> userOpt = account.contains("@") 
                ? userRepository.findByEmail(account) 
                : userRepository.findByUsername(account);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String rawPassword = loginRequest.getPassword();
            String dbPassword = user.getPassword();
            
            // Check BCrypt, if fails, check plaintext (for existing manual DB records)
            boolean isMatch = passwordEncoder.matches(rawPassword, dbPassword);
            if (!isMatch && rawPassword.equals(dbPassword)) {
                isMatch = true;
                // Auto upgrade to hashed password for future logins
                user.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(user);
            }

            if (isMatch) {
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không đúng!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên đăng nhập đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setDisplayName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole("USER");

        User saved = userRepository.save(user);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email không tồn tại trong hệ thống");
        }
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}
