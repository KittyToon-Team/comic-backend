package org.example.comicbackend.controller.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDetailResponse {
    private Integer id;
    private BigDecimal chapterNumber;
    private String title;
    private Integer accessType;
    private Integer viewCount;
    private boolean hasAccess;
    private String message;
    private List<String> images;

    private Integer storyId;
    private String storyTitle;

    private Integer prevChapterId;
    private BigDecimal prevChapterNumber;

    private Integer nextChapterId;
    private BigDecimal nextChapterNumber;
}
