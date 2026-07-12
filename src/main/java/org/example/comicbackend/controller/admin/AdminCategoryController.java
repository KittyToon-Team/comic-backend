package org.example.comicbackend.controller.admin;

import org.example.comicbackend.controller.dto.CategoryRequest;
import org.example.comicbackend.entity.Category;
import org.example.comicbackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(origins = "*")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tên thể loại không được để trống!"));
        }

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");

        categoryRepository.save(category);
        return ResponseEntity.ok(Map.of("message", "Thêm thể loại mới thành công!"));
    }

    @PutMapping("/{id}/toggle-visibility")
    public ResponseEntity<?> toggleVisibility(@PathVariable Integer id) {
        return categoryRepository.findById(id).map(category -> {
            category.setIsHidden(category.getIsHidden() == null ? true : !category.getIsHidden());
            categoryRepository.save(category);
            String status = category.getIsHidden() ? "đã bị ẩn" : "đã hiển thị lại";
            return ResponseEntity.ok(Map.of("message", "Thể loại " + status + " thành công!", "isHidden", category.getIsHidden()));
        }).orElse(ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy thể loại.")));
    }
}