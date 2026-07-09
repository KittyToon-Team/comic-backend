package org.example.comicbackend.repository;

import org.example.comicbackend.entity.Chapter;
import org.example.comicbackend.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByStoryOrderByChapterNumberAsc(Story story);

    List<Chapter> findByStory_IdOrderByChapterNumberDesc(Integer storyId);

    boolean existsByStory_IdAndChapterNumber(Integer storyId, BigDecimal chapterNumber);
}
