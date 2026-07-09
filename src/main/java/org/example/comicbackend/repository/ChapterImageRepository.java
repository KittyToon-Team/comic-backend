package org.example.comicbackend.repository;

import org.example.comicbackend.entity.ChapterImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChapterImageRepository extends JpaRepository<ChapterImage, Integer> {

    @Transactional
    void deleteByChapterId(Integer chapterId);

    List<ChapterImage> findByChapterIdOrderByImageOrderAsc(Integer chapterId);

}
