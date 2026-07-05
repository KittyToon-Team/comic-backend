package org.example.comicbackend.controller;

import org.example.comicbackend.entity.Chapter;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.repository.ChapterRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class ChapterController {

    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;

    public ChapterController(StoryRepository storyRepository, ChapterRepository chapterRepository) {
        this.storyRepository = storyRepository;
        this.chapterRepository = chapterRepository;
    }

    @GetMapping("/stories/{storyId}/chapters")
    public List<Chapter> getChaptersByStory(@PathVariable Integer storyId) {
        Optional<Story> story = storyRepository.findById(storyId);
        return story.map(chapterRepository::findByStoryOrderByChapterNumberAsc).orElseGet(List::of);
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Integer id) {
        return chapterRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
