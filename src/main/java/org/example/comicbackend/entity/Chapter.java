package org.example.comicbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;

    @OneToMany(mappedBy = "chapter")
    private List<ChapterImage> images;

    @OneToMany(mappedBy = "lastChapter")
    private List<ReadingHistory> readingHistories;
}