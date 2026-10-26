package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.CommentRequest;
import org.example.comicbackend.entity.Comment;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.CommentRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.example.comicbackend.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class CommentController {

    private final StoryRepository storyRepository;
    private final CommentRepository commentRepository;

    public CommentController(StoryRepository storyRepository, CommentRepository commentRepository) {
        this.storyRepository = storyRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/stories/{storyId}/comments")
    public List<Comment> getComments(@PathVariable Integer storyId) {
        Optional<Story> story = storyRepository.findById(storyId);
        return story.map(commentRepository::findByStoryOrderByCreatedAtDesc).orElseGet(List::of);
    }

    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest request) {
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập để bình luận!"));

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nội dung bình luận không được để trống!");
        }

        Optional<Story> storyOpt = storyRepository.findById(request.getStoryId());
        if (storyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Truyện không tồn tại!");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent().trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(currentUser); // Chống IDOR: Gán bình luận cho người dùng đang xác thực
        comment.setStory(storyOpt.get());

        Comment saved = commentRepository.save(comment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
