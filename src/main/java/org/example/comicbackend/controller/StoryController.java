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

    @GetMapping("/stories")
    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    @GetMapping("/stories/{id}")
    public ResponseEntity<Story> getStoryById(@PathVariable Integer id) {
        return storyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stories/search")
    public List<Story> searchStories(@RequestParam String keyword) {
        return storyRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
