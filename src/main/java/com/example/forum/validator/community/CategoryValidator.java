package com.example.forum.validator.community;

import com.example.forum.exception.common.BadRequestException;
import com.example.forum.exception.community.CategoryNotFoundException;
import com.example.forum.exception.community.InvalidCategoryForThisCommunity;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.repository.community.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public Category validateCategoryById(Long id) {

        if (id == null)
            throw new BadRequestException("Category must be provided for community posts.");

        return categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
    }

    public Category validateCategoryByName(Community community, String categoryName) {

        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new BadRequestException("Category name must be provided.");
        }

        return categoryRepository.findByCommunityAndName(community, categoryName)
                .orElseThrow(CategoryNotFoundException::new);
    }

    public Category validateExistingCategoryInCommunity(Long categoryId, Community community) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if (!category.getCommunity().getId().equals(community.getId()))
            throw new InvalidCategoryForThisCommunity();

        return category;
    }
}
