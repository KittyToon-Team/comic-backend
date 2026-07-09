package org.example.comicbackend.controller.admin;

import org.example.comicbackend.entity.Chapter;
import org.example.comicbackend.entity.ChapterImage;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.repository.ChapterImageRepository;
import org.example.comicbackend.repository.ChapterRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminChapterController {

    @Autowired private ChapterRepository chapterRepository;
    @Autowired private StoryRepository storyRepository;
    @Autowired private ChapterImageRepository chapterImageRepository;

    @GetMapping("/stories/{storyId}/chapters")
    public ResponseEntity<?> getChaptersByStory(@PathVariable Integer storyId) {
        List<Chapter> chapters = chapterRepository.findByStory_IdOrderByChapterNumberDesc(storyId);
        return ResponseEntity.ok(chapters);
    }

    @PostMapping("/chapters")
    public ResponseEntity<?> createChapter(@RequestBody Map<String, Object> payload) {
        Integer storyId = (Integer) payload.get("storyId");
        Double chapterNumber = Double.parseDouble(payload.get("chapterNumber").toString());
        String title = (String) payload.get("title");
        Integer accessType = (Integer) payload.get("accessType");
        List<String> imageUrls = (List<String>) payload.get("imageUrls");

        if (chapterRepository.existsByStory_IdAndChapterNumber(storyId, java.math.BigDecimal.valueOf(chapterNumber))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Chương số " + chapterNumber + " đã tồn tại trong bộ truyện này!"));
        }

        Story story = storyRepository.findById(storyId).orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        Chapter chapter = new Chapter();
        chapter.setStory(story);
        chapter.setChapterNumber(java.math.BigDecimal.valueOf(chapterNumber));
        chapter.setTitle(title);
        chapter.setAccessType(accessType);
        chapter.setViewCount(0);
        chapter.setCreatedAt(new Date());
        Chapter savedChapter = chapterRepository.save(chapter);

        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                ChapterImage img = new ChapterImage();
                img.setChapter(savedChapter);
                img.setImageUrl(imageUrls.get(i));
                img.setImageOrder(i + 1);
                chapterImageRepository.save(img);
            }
        }

        return ResponseEntity.ok(Map.of("message", "Tạo chương mới thành công!"));
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable Integer id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương truyện"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", chapter.getId());
        result.put("chapterNumber", chapter.getChapterNumber());
        result.put("title", chapter.getTitle());
        result.put("accessType", chapter.getAccessType());
        result.put("story", Map.of("id", chapter.getStory().getId()));

        List<ChapterImage> images = chapterImageRepository.findByChapterIdOrderByImageOrderAsc(id);
        result.put("images", images);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/chapters/{id}")
    public ResponseEntity<?> updateChapter(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương để cập nhật"));

        Double chapterNumber = Double.parseDouble(payload.get("chapterNumber").toString());
        String title = (String) payload.get("title");
        Integer accessType = (Integer) payload.get("accessType");
        List<String> imageUrls = (List<String>) payload.get("imageUrls");

        chapter.setChapterNumber(java.math.BigDecimal.valueOf(chapterNumber));
        chapter.setTitle(title);
        chapter.setAccessType(accessType);
        chapterRepository.save(chapter);

        chapterImageRepository.deleteByChapterId(id);

        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                ChapterImage img = new ChapterImage();
                img.setChapter(chapter);
                img.setImageUrl(imageUrls.get(i));
                img.setImageOrder(i + 1);
                chapterImageRepository.save(img);
            }
        }

        return ResponseEntity.ok(Map.of("message", "Cập nhật chương thành công!"));
    }
}