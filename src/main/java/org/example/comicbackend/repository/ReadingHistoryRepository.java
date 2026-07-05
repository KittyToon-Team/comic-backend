package org.example.comicbackend.repository;

import org.example.comicbackend.entity.ReadingHistory;
import org.example.comicbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Integer> {
    Optional<ReadingHistory> findByUserIdAndStoryId(Integer userId, Integer storyId);

    List<ReadingHistory> findByUserOrderByLastReadAtDesc(User user);
}
