package org.example.comicbackend.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoryRequest {
    private String title;
    private String author;
    private String description;
    private String coverImageUrl;
    private String status;
    private List<Integer> categoryIds;
}
