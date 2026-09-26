package br.com.manoelegidio.tp3.taskmanager.controller;

import br.com.manoelegidio.tp3.taskmanager.dto.CategoryDTO;
import br.com.manoelegidio.tp3.taskmanager.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String description = payload.get("description");
        String colorCode = payload.get("colorCode");

        CategoryDTO created = categoryService.createCategory(name, description, colorCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
