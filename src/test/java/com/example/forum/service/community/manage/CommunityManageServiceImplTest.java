package com.example.forum.service.community.manage;

import com.example.forum.dto.community.CategoryRequestDTO;
import com.example.forum.dto.community.CommunityRuleResponseDTO;
import com.example.forum.dto.image.ImageUploadRequestDTO;
import com.example.forum.exception.auth.ForbiddenException;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityRule;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CategoryRepository;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.community.CommunityRuleRepository;
import com.example.forum.service.auth.RedisService;
import com.example.forum.service.common.S3Service;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CategoryValidator;
import com.example.forum.validator.community.CommunityRuleValidator;
import com.example.forum.validator.community.CommunityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityManageService Tests")
class CommunityManageServiceImplTest {

    private static final Long COMMUNITY_ID = 1L;
    private static final String USERNAME = "testuser";

    @InjectMocks
    private CommunityManageServiceImpl communityManageService;

    @Mock private AuthValidator authValidator;
    @Mock private CommunityValidator communityValidator;
    @Mock private CommunityRuleValidator communityRuleValidator;
    @Mock private CategoryValidator categoryValidator;

    @Mock private CommunityRepository communityRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private CommunityRuleRepository communityRuleRepository;

    @Mock private RedisService redisService;
    @Mock private S3Service s3Service;

    private User user;
    private Community community;

    @BeforeEach
    void setUp() {
        user = User.builder().username(USERNAME).build();
        community = Community.builder().id(COMMUNITY_ID).categories(new HashSet<>()).build();
    }

    @Test
    @DisplayName("Add category - success when user is manager")
    void addCategory_success() {
        // given
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Notice");
        dto.setDescription("Community updates");

        when(authValidator.validateUserByUsername(USERNAME)).thenReturn(user);
        when(communityValidator.validateExistingCommunity(COMMUNITY_ID)).thenReturn(community);

        // when
        communityManageService.addCategory(COMMUNITY_ID, dto, USERNAME);

        // then
        verify(categoryRepository).save(any(Category.class));
        assertEquals(1, community.getCategories().size());
    }

    @Test
    @DisplayName("Add category - throws ForbiddenException if user is not manager")
    void addCategory_forbidden() {
        // given
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("General");
        dto.setDescription("Discussion");

        when(authValidator.validateUserByUsername(USERNAME)).thenReturn(user);
        when(communityValidator.validateExistingCommunity(COMMUNITY_ID)).thenReturn(community);
        doThrow(new ForbiddenException("Not a manager"))
                .when(communityValidator).validateManagerPermission(user, community);

        // when & then
        assertThrows(ForbiddenException.class, () ->
                communityManageService.addCategory(COMMUNITY_ID, dto, USERNAME));
    }

    @Test
    @DisplayName("Get rules - should return sorted list by createdAt")
    void getRules_sortedByCreatedAt() {
        // given
        CommunityRule rule1 = CommunityRule.builder().title("Rule B").createdAt(LocalDateTime.now().minusDays(1)).build();
        CommunityRule rule2 = CommunityRule.builder().title("Rule A").createdAt(LocalDateTime.now().minusDays(2)).build();
        Set<CommunityRule> rules = new HashSet<>(List.of(rule1, rule2));

        Community communityWithRules = Community.builder()
                .id(COMMUNITY_ID)
                .rules(rules)
                .build();

        when(communityValidator.validateExistingCommunity(COMMUNITY_ID)).thenReturn(communityWithRules);

        // when
        List<CommunityRuleResponseDTO> result = communityManageService.getRules(COMMUNITY_ID);

        // then
        assertEquals(2, result.size());
        assertEquals("Rule A", result.get(0).getTitle());
        assertEquals("Rule B", result.get(1).getTitle());
    }

    @Test
    @DisplayName("Update profile image - success when image uploaded and previous image deleted")
    void updateProfileImage_success() {
        // given
        MultipartFile image = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[10]);
        ImageUploadRequestDTO dto = new ImageUploadRequestDTO();
        dto.setImage(image);
        dto.setPositionX(0.3);
        dto.setPositionY(0.5);

        community.setProfileImageUrl("old_url.jpg");

        when(authValidator.validateUserByUsername(USERNAME)).thenReturn(user);
        when(communityValidator.validateExistingCommunity(COMMUNITY_ID)).thenReturn(community);
        when(s3Service.upload(image)).thenReturn("new_url.jpg");

        // when
        communityManageService.updateProfileImage(USERNAME, COMMUNITY_ID, dto);

        // then
        verify(s3Service).delete("old_url.jpg");
        verify(s3Service).upload(image);
        verify(communityRepository).save(community);

        assertEquals("new_url.jpg", community.getProfileImageUrl());
        assertEquals(0.3, community.getProfileImagePositionX());
        assertEquals(0.5, community.getProfileImagePositionY());
    }

    @Test
    @DisplayName("Update profile image - throws ForbiddenException when user is not manager")
    void updateProfileImage_forbidden() {
        // given
        MultipartFile image = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[10]);
        ImageUploadRequestDTO dto = new ImageUploadRequestDTO();
        dto.setImage(image);
        dto.setPositionX(0.0);
        dto.setPositionY(0.0);

        when(authValidator.validateUserByUsername(USERNAME)).thenReturn(user);
        when(communityValidator.validateExistingCommunity(COMMUNITY_ID)).thenReturn(community);
        doThrow(new ForbiddenException("Not a manager"))
                .when(communityValidator).validateManagerPermission(user, community);

        // when & then
        assertThrows(ForbiddenException.class, () ->
                communityManageService.updateProfileImage(USERNAME, COMMUNITY_ID, dto));
    }
}
