package org.example.comicbackend.repository;

import org.example.comicbackend.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Integer> {

    List<Story> findByAuthorContainingIgnoreCase(String author);

    @Query("SELECT DISTINCT s FROM Story s LEFT JOIN FETCH s.categories")
    List<Story> findAll();

    @Query("SELECT DISTINCT s FROM Story s LEFT JOIN FETCH s.categories WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Story> findByTitleContainingIgnoreCase(@Param("keyword") String keyword);

}
