package org.example.comicbackend.repository;

import org.example.comicbackend.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Integer> {
    List<Story> findByTitleContainingIgnoreCase(String keyword);

    List<Story> findByAuthorContainingIgnoreCase(String author);
}
