package com.example.forum.validator.community;

import com.example.forum.exception.common.BadRequestException;
import com.example.forum.exception.community.CategoryNotFoundException;
import com.example.forum.exception.community.InvalidCategoryForThisCommunity;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.repository.community.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryValidator Tests")
class CategoryValidatorTest {

    @InjectMocks
    private CategoryValidator categoryValidator;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Should throw BadRequestException when category ID is null")
    void validateCategoryById_nullId_throwsBadRequestException() {
        assertThrows(BadRequestException.class, () -> {
            categoryValidator.validateCategoryById(null);
        });
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category does not exist")
    void validateCategoryById_notFound_throwsCategoryNotFoundException() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryValidator.validateCategoryById(id);
        });
    }

    @Test
    @DisplayName("Should return category when it exists")
    void validateCategoryById_success() {
        Long id = 1L;
        Category category = Category.builder().id(id).build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        Category result = categoryValidator.validateCategoryById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("Should return category when it belongs to the given community")
    void validateExistingCategoryInCommunity_success() {
        Long categoryId = 1L;
        Long communityId = 100L;

        Community community = Community.builder().id(communityId).build();
        Category category = Category.builder()
                .id(categoryId)
                .community(community)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category result = categoryValidator.validateExistingCategoryInCommunity(categoryId, community);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
    }

    @Test
    @DisplayName("Should throw InvalidCategoryForThisCommunity when category belongs to a different community")
    void validateExistingCategoryInCommunity_wrongCommunity_throwsException() {
        Long categoryId = 1L;
        Community correctCommunity = Community.builder().id(1L).build();
        Community wrongCommunity = Community.builder().id(2L).build();

        Category category = Category.builder()
                .id(categoryId)
                .community(wrongCommunity)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(InvalidCategoryForThisCommunity.class, () -> {
            categoryValidator.validateExistingCategoryInCommunity(categoryId, correctCommunity);
        });
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category is not found in community validation")
    void validateExistingCategoryInCommunity_notFound_throwsException() {
        Long categoryId = 1L;
        Community community = Community.builder().id(1L).build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryValidator.validateExistingCategoryInCommunity(categoryId, community);
        });
    }
}
