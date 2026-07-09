package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.CommentRequest;
import org.example.comicbackend.entity.Comment;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.CommentRepository;
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
public class CommentController {

    private final StoryRepository storyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentController(StoryRepository storyRepository, CommentRepository commentRepository,
                             UserRepository userRepository) {
        this.storyRepository = storyRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/stories/{storyId}/comments")
    public List<Comment> getComments(@PathVariable Integer storyId) {
        Optional<Story> story = storyRepository.findById(storyId);
        return story.map(commentRepository::findByStoryOrderByCreatedAtDesc).orElseGet(List::of);
    }

    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        Optional<Story> storyOpt = storyRepository.findById(request.getStoryId());
        if (userOpt.isEmpty() || storyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User hoặc Story không tồn tại");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(userOpt.get());
        comment.setStory(storyOpt.get());

        Comment saved = commentRepository.save(comment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
