package org.example.comicbackend.controller.admin;

import org.example.comicbackend.controller.dto.ChapterRequest;
import org.example.comicbackend.entity.Chapter;
import org.example.comicbackend.entity.ChapterImage;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.repository.ChapterRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin/chapters")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class AdminChapterController {

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;

    public AdminChapterController(ChapterRepository chapterRepository, StoryRepository storyRepository) {
        this.chapterRepository = chapterRepository;
        this.storyRepository = storyRepository;
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Chapter>> getByStory(@PathVariable Integer storyId) {
        return storyRepository.findById(storyId)
                .map(story -> ResponseEntity.ok(chapterRepository.findByStoryOrderByChapterNumberAsc(story)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chapter> getById(@PathVariable Integer id) {
        return chapterRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Chapter> create(@RequestBody ChapterRequest request) {
        Story story = storyRepository.findById(request.getStoryId()).orElse(null);
        if (story == null) {
            return ResponseEntity.badRequest().build();
        }

        Chapter chapter = new Chapter();
        chapter.setStory(story);
        chapter.setChapterNumber(request.getChapterNumber());
        chapter.setTitle(request.getTitle());
        chapter.setAccessType(request.getAccessType() != null ? request.getAccessType() : 0);
        chapter.setViewCount(0);
        chapter.setCreatedAt(new Date());
        chapter.setImages(buildImages(chapter, request.getImageUrls()));

        Chapter saved = chapterRepository.save(chapter);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> update(@PathVariable Integer id, @RequestBody ChapterRequest request) {
        return chapterRepository.findById(id)
                .map(chapter -> {
                    chapter.setChapterNumber(request.getChapterNumber());
                    chapter.setTitle(request.getTitle());
                    chapter.setAccessType(request.getAccessType() != null ? request.getAccessType() : 0);

                    // Xóa toàn bộ ảnh cũ, thay bằng danh sách ảnh mới theo đúng thứ tự
                    chapter.getImages().clear();
                    chapter.getImages().addAll(buildImages(chapter, request.getImageUrls()));

                    return ResponseEntity.ok(chapterRepository.save(chapter));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!chapterRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        chapterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private List<ChapterImage> buildImages(Chapter chapter, List<String> urls) {
        List<ChapterImage> images = new ArrayList<>();
        if (urls == null) return images;
        for (int i = 0; i < urls.size(); i++) {
            ChapterImage img = new ChapterImage();
            img.setChapter(chapter);
            img.setImageUrl(urls.get(i));
            img.setImageOrder(i + 1);
            images.add(img);
        }
        return images;
    }
}
