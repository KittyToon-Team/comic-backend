package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.ReadingHistoryRequest;
import org.example.comicbackend.entity.Chapter;
import org.example.comicbackend.entity.ReadingHistory;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.ChapterRepository;
import org.example.comicbackend.repository.ReadingHistoryRepository;
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
public class ReadingHistoryController {

    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ReadingHistoryRepository readingHistoryRepository;

    public ReadingHistoryController(UserRepository userRepository, StoryRepository storyRepository,
                                    ChapterRepository chapterRepository,
                                    ReadingHistoryRepository readingHistoryRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.chapterRepository = chapterRepository;
        this.readingHistoryRepository = readingHistoryRepository;
    }

    @PostMapping("/reading-history")
    public ResponseEntity<?> saveReadingHistory(@RequestBody ReadingHistoryRequest request) {
        // Lấy user từ SecurityContext thay vì tin userId client gửi lên
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập để lưu lịch sử đọc!"));

        Optional<Story> storyOpt = storyRepository.findById(request.getStoryId());
        Optional<Chapter> chapterOpt = chapterRepository.findById(request.getLastChapterId());
        if (storyOpt.isEmpty() || chapterOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Truyện hoặc Chương không tồn tại!");
        }

        ReadingHistory history = new ReadingHistory();
        history.setUser(currentUser);
        history.setStory(storyOpt.get());
        history.setLastChapter(chapterOpt.get());
        history.setLastReadAt(LocalDateTime.now());
        ReadingHistory saved = readingHistoryRepository.save(history);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/reading-history")
    public ResponseEntity<?> getReadingHistory(@PathVariable Integer userId) {
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập!"));

        // Chống IDOR: Chỉ người dùng đó hoặc Admin mới có quyền xem lịch sử đọc
        if (!currentUser.getId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem lịch sử đọc của người khác!");
        }

        Optional<User> targetUser = userRepository.findById(userId);
        if (targetUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Người dùng không tồn tại!");
        }

        List<Map<String, Object>> result = readingHistoryRepository.findByUserOrderByLastReadAtDesc(targetUser.get()).stream()
                .map(history -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", history.getId());
                    map.put("lastReadAt", history.getLastReadAt());
                    if (history.getStory() != null) {
                        map.put("story", Map.of(
                                "id", history.getStory().getId(),
                                "title", history.getStory().getTitle() != null ? history.getStory().getTitle() : "",
                                "coverImageUrl", history.getStory().getCoverImageUrl() != null ? history.getStory().getCoverImageUrl() : "",
                                "slug", history.getStory().getSlug() != null ? history.getStory().getSlug() : ""
                        ));
                    }
                    if (history.getLastChapter() != null) {
                        map.put("chapter", Map.of(
                                "id", history.getLastChapter().getId(),
                                "chapterNumber", history.getLastChapter().getChapterNumber(),
                                "title", history.getLastChapter().getTitle() != null ? history.getLastChapter().getTitle() : ""
                        ));
                    }
                    return map;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
