package org.example.comicbackend.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ChapterRequest {
    private Integer storyId;
    private BigDecimal chapterNumber;
    private String title;
    private Integer accessType; // 0 = public, 1 = cần đăng nhập
    private List<String> imageUrls; // thứ tự trong list quyết định image_order
}
