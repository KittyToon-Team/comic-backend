package org.example.comicbackend.controller;

import org.example.comicbackend.controller.dto.Response.ChapterDetailResponse;
import org.example.comicbackend.entity.Chapter;
import org.example.comicbackend.service.ChapterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/stories/{storyId}/chapters")
    public List<Chapter> getChaptersByStory(@PathVariable Integer storyId) {
        return chapterService.getChaptersByStory(storyId);
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<ChapterDetailResponse> getChapterById(@PathVariable Integer id) {
        ChapterDetailResponse response = chapterService.getChapterDetail(id);
        return ResponseEntity.ok(response);
    }
}
