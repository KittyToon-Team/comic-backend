package org.example.comicbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Chapters")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chapter_number")
    private BigDecimal chapterNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "access_type")
    private Integer accessType;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "story_id")
    @JsonIgnoreProperties({"chapters", "hibernateLazyInitializer", "handler"})
    private Story story;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("imageOrder ASC")
    private List<ChapterImage> images;

    @JsonIgnore
    @OneToMany(mappedBy = "lastChapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingHistory> readingHistories;
}