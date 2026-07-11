package org.example.comicbackend.controller;

import org.example.comicbackend.entity.Chapter;
import org.example.comicbackend.entity.ChapterImage;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.repository.ChapterImageRepository;
import org.example.comicbackend.repository.ChapterRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class ChapterController {

    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterImageRepository chapterImageRepository;

    public ChapterController(StoryRepository storyRepository,
                             ChapterRepository chapterRepository,
                             ChapterImageRepository chapterImageRepository) {
        this.storyRepository = storyRepository;
        this.chapterRepository = chapterRepository;
        this.chapterImageRepository = chapterImageRepository;
    }

    @GetMapping("/stories/{storyId}/chapters")
    public List<Chapter> getChaptersByStory(@PathVariable Integer storyId) {
        Optional<Story> story = storyRepository.findById(storyId);
        return story.map(chapterRepository::findByStoryOrderByChapterNumberAsc).orElseGet(List::of);
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable Integer id) {
        Optional<Chapter> chapterOpt = chapterRepository.findById(id);
        if (chapterOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Chapter chapter = chapterOpt.get();
        Integer storyId = chapter.getStory().getId();

        chapter.setViewCount((chapter.getViewCount() == null ? 0 : chapter.getViewCount()) + 1);
        chapterRepository.save(chapter);

        List<ChapterImage> images = chapterImageRepository.findByChapterIdOrderByImageOrderAsc(id);
        List<String> imageUrls = images.stream()
                .map(ChapterImage::getImageUrl)
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("id", chapter.getId());
        data.put("chapterNumber", chapter.getChapterNumber());
        data.put("title", chapter.getTitle());
        data.put("accessType", chapter.getAccessType());
        data.put("viewCount", chapter.getViewCount());
        data.put("images", imageUrls);
        data.put("storyId", storyId);
        data.put("storyTitle", chapter.getStory().getTitle());

        chapterRepository.findFirstByStory_IdAndChapterNumberLessThanOrderByChapterNumberDesc(
                storyId, chapter.getChapterNumber()
        ).ifPresent(prev -> {
            data.put("prevChapterId", prev.getId());
            data.put("prevChapterNumber", prev.getChapterNumber());
        });

        chapterRepository.findFirstByStory_IdAndChapterNumberGreaterThanOrderByChapterNumberAsc(
                storyId, chapter.getChapterNumber()
        ).ifPresent(next -> {
            data.put("nextChapterId", next.getId());
            data.put("nextChapterNumber", next.getChapterNumber());
        });

        return ResponseEntity.ok(data);
    }
}