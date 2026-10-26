package org.example.comicbackend.service;

import org.example.comicbackend.controller.dto.Response.ChapterDetailResponse;
import org.example.comicbackend.entity.Chapter;
import org.example.comicbackend.entity.ChapterImage;
import org.example.comicbackend.entity.Story;
import org.example.comicbackend.entity.User;
import org.example.comicbackend.repository.ChapterImageRepository;
import org.example.comicbackend.repository.ChapterRepository;
import org.example.comicbackend.repository.StoryRepository;
import org.example.comicbackend.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChapterService {

    public static final int ACCESS_PUBLIC = 0;
    public static final int ACCESS_LOGIN_REQUIRED = 1;
    public static final int ACCESS_PAID = 2;

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;
    private final ChapterImageRepository chapterImageRepository;

    public ChapterService(ChapterRepository chapterRepository,
                          StoryRepository storyRepository,
                          ChapterImageRepository chapterImageRepository) {
        this.chapterRepository = chapterRepository;
        this.storyRepository = storyRepository;
        this.chapterImageRepository = chapterImageRepository;
    }

    public List<Chapter> getChaptersByStory(Integer storyId) {
        Optional<Story> story = storyRepository.findById(storyId);
        return story.map(chapterRepository::findByStoryOrderByChapterNumberAsc).orElseGet(List::of);
    }

    @Transactional
    public ChapterDetailResponse getChapterDetail(Integer id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương truyện"));

        Integer storyId = chapter.getStory().getId();
        String storyTitle = chapter.getStory().getTitle();

        Optional<User> currentUserOpt = SecurityUtils.getCurrentUser();
        boolean isAdmin = SecurityUtils.isAdmin();

        int accessType = chapter.getAccessType() != null ? chapter.getAccessType() : ACCESS_PUBLIC;
        boolean hasAccess = false;
        String message = null;

        if (isAdmin) {
            hasAccess = true;
        } else if (accessType == ACCESS_PUBLIC) {
            hasAccess = true;
        } else if (accessType == ACCESS_LOGIN_REQUIRED) {
            if (currentUserOpt.isPresent()) {
                hasAccess = true;
            } else {
                hasAccess = false;
                message = "Chương này yêu cầu đăng nhập tài khoản để đọc.";
            }
        } else if (accessType == ACCESS_PAID) {
            if (currentUserOpt.isEmpty()) {
                hasAccess = false;
                message = "Chương trả phí. Vui lòng đăng nhập tài khoản.";
            } else {
                // Hiện tại hệ thống mua chương bằng Point đang trong quá trình chuẩn bị phát triển.
                // Mặc định từ chối người dùng thông thường chưa sở hữu chương.
                hasAccess = false;
                message = "Chương trả phí. Bạn cần mua chương để tiếp tục đọc.";
            }
        }

        List<String> imageUrls;
        if (hasAccess) {
            // Tăng view count khi người dùng có quyền đọc hợp lệ
            chapter.setViewCount((chapter.getViewCount() == null ? 0 : chapter.getViewCount()) + 1);
            chapterRepository.save(chapter);

            List<ChapterImage> images = chapterImageRepository.findByChapterIdOrderByImageOrderAsc(id);
            imageUrls = images.stream()
                    .map(ChapterImage::getImageUrl)
                    .collect(Collectors.toList());
        } else {
            // BẢO MẬT: Tuyệt đối không trả URL ảnh hoặc file khi không có quyền đọc
            imageUrls = Collections.emptyList();
        }

        ChapterDetailResponse response = ChapterDetailResponse.builder()
                .id(chapter.getId())
                .chapterNumber(chapter.getChapterNumber())
                .title(chapter.getTitle())
                .accessType(accessType)
                .viewCount(chapter.getViewCount())
                .hasAccess(hasAccess)
                .message(message)
                .images(imageUrls)
                .storyId(storyId)
                .storyTitle(storyTitle)
                .build();

        chapterRepository.findFirstByStory_IdAndChapterNumberLessThanOrderByChapterNumberDesc(
                storyId, chapter.getChapterNumber()
        ).ifPresent(prev -> {
            response.setPrevChapterId(prev.getId());
            response.setPrevChapterNumber(prev.getChapterNumber());
        });

        chapterRepository.findFirstByStory_IdAndChapterNumberGreaterThanOrderByChapterNumberAsc(
                storyId, chapter.getChapterNumber()
        ).ifPresent(next -> {
            response.setNextChapterId(next.getId());
            response.setNextChapterNumber(next.getChapterNumber());
        });

        return response;
    }
}
