package org.example.comicbackend.repository;

import org.example.comicbackend.entity.Favorite;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Optional<Favorite> findByUserAndStory(User user, Story story);

    List<Favorite> findByUser(User user);
}
