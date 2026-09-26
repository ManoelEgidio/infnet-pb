package br.com.manoelegidio.tp4.taskmanager.service;

import br.com.manoelegidio.tp4.taskmanager.domain.exception.ResourceNotFoundException;
import br.com.manoelegidio.tp4.taskmanager.domain.model.Category;
import br.com.manoelegidio.tp4.taskmanager.dto.CategoryDTO;
import br.com.manoelegidio.tp4.taskmanager.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDTO createCategory(String name, String description, String colorCode) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + name);
        }
        Category category = new Category(name, description, colorCode);
        return CategoryDTO.fromEntity(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Category getCategoryEntityById(Long id) {
        if (id == null) return null;
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + id));
    }
}
