package org.example.comicbackend.controller.dto;

public class FavoriteRequest {
    private Integer userId;
    private Integer storyId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStoryId() {
        return storyId;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }
}
