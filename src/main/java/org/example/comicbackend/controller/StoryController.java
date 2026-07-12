package org.example.comicbackend.controller;

import org.example.comicbackend.entity.Category;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.repository.CategoryRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class StoryController {

    private final StoryRepository storyRepository;
    private final CategoryRepository categoryRepository;

    public StoryController(StoryRepository storyRepository, CategoryRepository categoryRepository) {
        this.storyRepository = storyRepository;
        this.categoryRepository = categoryRepository;
    }

    private boolean isStoryVisible(Story story) {
        if (story.getCategories() == null || story.getCategories().isEmpty()) {
            return true;
        }
        for (Category cat : story.getCategories()) {
            if (Boolean.TRUE.equals(cat.getIsHidden())) {
                return false;
            }
        }
        return true;
    }

    @GetMapping("/stories")
    public List<Story> getAllStories() {
        return storyRepository.findAll().stream()
                .filter(this::isStoryVisible)
                .collect(Collectors.toList());
    }

    @GetMapping("/stories/{id}")
    public ResponseEntity<Story> getStoryById(@PathVariable Integer id) {
        return storyRepository.findById(id)
                .filter(this::isStoryVisible)
                .map(story -> {
                    story.setViewCount((story.getViewCount() == null ? 0 : story.getViewCount()) + 1);
                    storyRepository.save(story);
                    return ResponseEntity.ok(story);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stories/search")
    public List<Story> searchStories(@RequestParam String keyword) {
        return storyRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .filter(this::isStoryVisible)
                .collect(Collectors.toList());
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(cat -> !Boolean.TRUE.equals(cat.getIsHidden()))
                .collect(Collectors.toList());
    }
}
