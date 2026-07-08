package org.example.comicbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Stories")
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "slug")
    private String slug;

    @Column(name = "author")
    private String author;

    @Column(name = "description")
    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "status")
    private String status;

    @Column(name = "view_count")
    private Integer viewCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "story")
    private List<Chapter> chapters;

    @JsonIgnore
    @OneToMany(mappedBy = "story")
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "story")
    private List<Favorite> favorites;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Story_Category",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}