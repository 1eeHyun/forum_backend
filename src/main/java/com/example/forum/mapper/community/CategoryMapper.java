package com.example.forum.mapper.community;

import com.example.forum.dto.community.CategoryResponseDTO;
import com.example.forum.model.community.Category;

public class CategoryMapper {

    public static CategoryResponseDTO toDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
