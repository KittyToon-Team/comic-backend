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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        Optional<Story> storyOpt = storyRepository.findById(request.getStoryId());
        Optional<Chapter> chapterOpt = chapterRepository.findById(request.getLastChapterId());
        if (userOpt.isEmpty() || storyOpt.isEmpty() || chapterOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User, Story hoặc Chapter không tồn tại");
        }

        ReadingHistory history = new ReadingHistory();
        history.setUser(userOpt.get());
        history.setStory(storyOpt.get());
        history.setLastChapter(chapterOpt.get());
        history.setLastReadAt(LocalDateTime.now());
        ReadingHistory saved = readingHistoryRepository.save(history);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/reading-history")
    public List<ReadingHistory> getReadingHistory(@PathVariable Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(readingHistoryRepository::findByUserOrderByLastReadAtDesc).orElseGet(List::of);
    }
}
