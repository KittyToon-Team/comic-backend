package org.example.comicbackend.controller.admin;

import org.example.comicbackend.controller.dto.UpdateRoleRequest;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AdminUserController {

    // Tài khoản admin duy nhất (super admin) của hệ thống - không được phép đổi vai trò
    private static final String SUPER_ADMIN_USERNAME = "admin";

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Lấy danh sách tài khoản, có thể tìm kiếm theo username/display_name/email.
     * Password không bao giờ được trả về (đã đánh dấu WRITE_ONLY trên entity User).
     */
    @GetMapping
    public List<User> getAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return userRepository
                    .findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            keyword, keyword, keyword);
        }
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Đổi vai trò (role) của một tài khoản.
     * - Tài khoản admin duy nhất (username = "admin") không bao giờ được đổi vai trò.
     * - Các tài khoản admin khác có thể bị hạ xuống USER.
     * - Tài khoản USER có thể được nâng lên ADMIN.
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody UpdateRoleRequest request) {
        String newRole = request.getRole();
        if (newRole == null || (!newRole.equalsIgnoreCase("ADMIN") && !newRole.equalsIgnoreCase("USER"))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vai trò không hợp lệ. Chỉ chấp nhận ADMIN hoặc USER"));
        }
        newRole = newRole.toUpperCase();

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        if (SUPER_ADMIN_USERNAME.equalsIgnoreCase(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Không thể thay đổi vai trò của tài khoản admin duy nhất của hệ thống"));
        }

        user.setRole(newRole);
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }
}
