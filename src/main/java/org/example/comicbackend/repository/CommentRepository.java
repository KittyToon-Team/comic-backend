package org.example.comicbackend.repository;

import org.example.comicbackend.entity.Comment;
import org.example.comicbackend.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByStoryOrderByCreatedAtDesc(Story story);
}
