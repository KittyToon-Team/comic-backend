package org.example.comicbackend.controller.dto.Response;

import java.math.BigDecimal;
import java.util.List;

public class ChapterDetailResponse {
    private Integer id;
    private BigDecimal chapterNumber;
    private String title;
    private List<String> images;      // danh sách URL ảnh, đã sắp thứ tự

    private Integer storyId;
    private String storyTitle;

    private Integer prevChapterId;
    private BigDecimal prevChapterNumber;

    private Integer nextChapterId;
    private BigDecimal nextChapterNumber;

    public ChapterDetailResponse() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public BigDecimal getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(BigDecimal chapterNumber) { this.chapterNumber = chapterNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public Integer getStoryId() { return storyId; }
    public void setStoryId(Integer storyId) { this.storyId = storyId; }
    public String getStoryTitle() { return storyTitle; }
    public void setStoryTitle(String storyTitle) { this.storyTitle = storyTitle; }
    public Integer getPrevChapterId() { return prevChapterId; }
    public void setPrevChapterId(Integer prevChapterId) { this.prevChapterId = prevChapterId; }
    public BigDecimal getPrevChapterNumber() { return prevChapterNumber; }
    public void setPrevChapterNumber(BigDecimal prevChapterNumber) { this.prevChapterNumber = prevChapterNumber; }
    public Integer getNextChapterId() { return nextChapterId; }
    public void setNextChapterId(Integer nextChapterId) { this.nextChapterId = nextChapterId; }
    public BigDecimal getNextChapterNumber() { return nextChapterNumber; }
    public void setNextChapterNumber(BigDecimal nextChapterNumber) { this.nextChapterNumber = nextChapterNumber; }
}