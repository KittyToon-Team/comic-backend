package org.example.comicbackend.controller.admin;

import org.example.comicbackend.controller.dto.StoryRequest;
import org.example.comicbackend.entity.Category;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.repository.CategoryRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/admin/stories")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AdminStoryController {

    private final StoryRepository storyRepository;
    private final CategoryRepository categoryRepository;

    public AdminStoryController(StoryRepository storyRepository, CategoryRepository categoryRepository) {
        this.storyRepository = storyRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Story> getAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return storyRepository.findByTitleContainingIgnoreCase(keyword);
        }
        return storyRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Story> getById(@PathVariable Integer id) {
        return storyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Story> create(@RequestBody StoryRequest request) {
        Story story = new Story();
        applyRequest(story, request);
        story.setCreatedAt(new Date());
        story.setUpdatedAt(new Date());
        story.setViewCount(0);
        Story saved = storyRepository.save(story);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Story> update(@PathVariable Integer id, @RequestBody StoryRequest request) {
        return storyRepository.findById(id)
                .map(story -> {
                    applyRequest(story, request);
                    story.setUpdatedAt(new Date());
                    return ResponseEntity.ok(storyRepository.save(story));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!storyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        storyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void applyRequest(Story story, StoryRequest request) {
        story.setTitle(request.getTitle());
        if (story.getSlug() == null || !story.getTitle().equals(request.getTitle())) {
            story.setSlug(toSlug(request.getTitle()));
        }
        story.setAuthor(request.getAuthor());
        story.setDescription(request.getDescription());
        story.setCoverImageUrl(request.getCoverImageUrl());
        story.setStatus(request.getStatus() != null ? request.getStatus() : "Đang ra");

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categoryList = categoryRepository.findAllById(request.getCategoryIds());

            story.setCategories(categoryList);

            for (Category cat : categoryList) {
                if (cat.getStories() == null) {
                    cat.setStories(new ArrayList<>());
                }
                if (!cat.getStories().contains(story)) {
                    cat.getStories().add(story);
                }
            }
        } else {
            story.setCategories(new ArrayList<>());
        }
    }

    private String toSlug(String title) {
        if (title == null) return null;
        String noAccent = Normalizer.normalize(title, Normalizer.Form.NFD);
        noAccent = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(noAccent).replaceAll("");
        String slug = noAccent.toLowerCase()
                .replace("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
        return slug + "-" + (System.currentTimeMillis() % 100000);
    }
}