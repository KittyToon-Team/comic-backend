package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.FavoriteRequest;
import org.example.comicbackend.entity.Favorite;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.FavoriteRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.example.comicbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        Optional<Story> storyOpt = storyRepository.findById(request.getStoryId());
        if (userOpt.isEmpty() || storyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User hoặc Story không tồn tại");
        }

        Optional<Favorite> existing = favoriteRepository.findByUserAndStory(userOpt.get(), storyOpt.get());
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return ResponseEntity.ok("Đã xóa khỏi yêu thích");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(userOpt.get());
        favorite.setStory(storyOpt.get());
        favorite.setCreatedAt(new Date());
        favoriteRepository.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đã thêm vào yêu thích");
    }

    @GetMapping("/users/{userId}/favorites")
    public List<Favorite> getFavorites(@PathVariable Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(favoriteRepository::findByUser).orElseGet(List::of);
    }
}
