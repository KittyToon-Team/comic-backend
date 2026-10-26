package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.FavoriteRequest;
import org.example.comicbackend.entity.Favorite;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.FavoriteRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.example.comicbackend.repository.UserRepository;
import org.example.comicbackend.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class FavoriteController {

    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final FavoriteRepository favoriteRepository;

    public FavoriteController(UserRepository userRepository, StoryRepository storyRepository,
                              FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @PostMapping("/favorites")
    public ResponseEntity<?> toggleFavorite(@RequestBody FavoriteRequest request) {
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập để yêu thích truyện!"));

        Optional<Story> storyOpt = storyRepository.findById(request.getStoryId());
        if (storyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Truyện không tồn tại!");
        }

        Optional<Favorite> existing = favoriteRepository.findByUserAndStory(currentUser, storyOpt.get());
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return ResponseEntity.ok(Map.of("message", "Đã xóa khỏi yêu thích", "favorited", false));
        }

        Favorite favorite = new Favorite();
        favorite.setUser(currentUser);
        favorite.setStory(storyOpt.get());
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Đã thêm vào yêu thích", "favorited", true));
    }

    @GetMapping("/users/{userId}/favorites")
    public ResponseEntity<?> getFavorites(@PathVariable Integer userId) {
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập!"));

        // Chống IDOR: Chỉ cho phép người dùng xem dữ liệu của chính mình, trừ khi là ADMIN
        if (!currentUser.getId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem danh sách yêu thích của người khác!");
        }

        Optional<User> targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Người dùng không tồn tại!");
        }

        List<Map<String, Object>> result = favoriteRepository.findByUser(targetUser.get()).stream()
                .map(fav -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", fav.getId());
                    map.put("createdAt", fav.getCreatedAt());
                    if (fav.getStory() != null) {
                        map.put("story", Map.of(
                                "id", fav.getStory().getId(),
                                "title", fav.getStory().getTitle() != null ? fav.getStory().getTitle() : "",
                                "coverImageUrl", fav.getStory().getCoverImageUrl() != null ? fav.getStory().getCoverImageUrl() : "",
                                "slug", fav.getStory().getSlug() != null ? fav.getStory().getSlug() : ""
                        ));
                    }
                    return map;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
