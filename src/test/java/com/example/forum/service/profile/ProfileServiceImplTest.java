package com.example.forum.service.profile;

import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.profile.*;
import com.example.forum.exception.auth.DuplicateUsernameException;
import com.example.forum.exception.auth.UserNotFoundException;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.follow.FollowRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.repository.profile.ProfileRepository;
import com.example.forum.repository.user.UserRepository;
import com.example.forum.security.JwtTokenProvider;
import com.example.forum.service.common.S3Service;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.profile.ProfileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ProfileServiceImplTest {

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Mock private AuthValidator authValidator;
    @Mock private UserRepository userRepository;
    @Mock private CommunityMemberRepository communityMemberRepository;
    @Mock private PostRepository postRepository;
    @Mock private ProfileValidator profileValidator;
    @Mock private ProfileRepository profileRepository;
    @Mock private FollowRepository followRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private S3Service s3Service;

    private User user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        profile = Profile.builder()
                .nickname("johnny")
                .bio("Hello world")
                .imageUrl("old.png")
                .imagePositionX(0.5)
                .imagePositionY(0.5)
                .build();

        user = User.builder()
                .id(1L)
                .username("john")
                .profile(profile)
                .build();

        profile.setUser(user);
    }

    @Nested
    @DisplayName("updateNickname()")
    class UpdateNicknameTest {

        @Test
        @DisplayName("Success - update nickname")
        void update_nickname_success() {
            NicknameUpdateDTO dto = new NicknameUpdateDTO();
            dto.setNickname("newNick");

            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(profileValidator.getProfileByUser(user)).thenReturn(profile);

            profileService.updateNickname("john", dto);

            assertThat(profile.getNickname()).isEqualTo("newNick");
            verify(profileRepository).save(profile);
        }

        @Test
        @DisplayName("Failure - user not found")
        void update_nickname_user_not_found() {
            NicknameUpdateDTO dto = new NicknameUpdateDTO();
            dto.setNickname("fail");

            when(authValidator.validateUserByUsername("john")).thenThrow(UserNotFoundException.class);

            assertThrows(UserNotFoundException.class, () ->
                    profileService.updateNickname("john", dto));
        }
    }

    @Nested
    @DisplayName("updateUsername()")
    class UpdateUsernameTest {

        @Test
        @DisplayName("Success - update username and return new token")
        void update_username_success() {
            UsernameUpdateDTO dto = new UsernameUpdateDTO();
            dto.setUsername("john_new");

            doNothing().when(authValidator).validateUniqueUsername("john_new");
            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(jwtTokenProvider.generateToken("john_new")).thenReturn("token123");

            LoginResponseDTO response = profileService.updateUsername("john", dto);

            assertThat(response.getUsername()).isEqualTo("john_new");
            assertThat(response.getToken()).isEqualTo("token123");
        }

        @Test
        @DisplayName("Failure - duplicate username")
        void update_username_duplicate() {
            UsernameUpdateDTO dto = new UsernameUpdateDTO();
            dto.setUsername("existing");

            doThrow(new DuplicateUsernameException()).when(authValidator).validateUniqueUsername("existing");

            assertThrows(DuplicateUsernameException.class, () ->
                    profileService.updateUsername("john", dto));
        }
    }

    @Nested
    @DisplayName("updateBio()")
    class UpdateBioTest {

        @Test
        @DisplayName("Success - update bio")
        void update_bio_success() {
            BioUpdateDTO dto = new BioUpdateDTO();
            dto.setBio("updated bio");

            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(profileValidator.getProfileByUser(user)).thenReturn(profile);

            profileService.updateBio("john", dto);

            assertThat(profile.getBio()).isEqualTo("updated bio");
        }

        @Test
        @DisplayName("Failure - user not found")
        void update_bio_user_not_found() {
            BioUpdateDTO dto = new BioUpdateDTO();
            dto.setBio("fail");

            when(authValidator.validateUserByUsername("john")).thenThrow(UserNotFoundException.class);

            assertThrows(UserNotFoundException.class, () ->
                    profileService.updateBio("john", dto));
        }
    }

    @Nested
    @DisplayName("updateProfileImage()")
    class UpdateProfileImageTest {

        @Test
        @DisplayName("Success - update profile image")
        void update_profile_image_success() {
            ProfileImageUpdateDTO dto = new ProfileImageUpdateDTO();
            MultipartFile mockFile = mock(MultipartFile.class);
            dto.setImage(mockFile);
            dto.setPositionX(0.1);
            dto.setPositionY(0.2);

            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(profileValidator.getProfileByUser(user)).thenReturn(profile);
            when(s3Service.upload(mockFile)).thenReturn("newImage.jpg");

            profileService.updateProfileImage("john", dto);

            assertThat(profile.getImageUrl()).isEqualTo("newImage.jpg");
            assertThat(profile.getImagePositionX()).isEqualTo(0.1);
            assertThat(profile.getImagePositionY()).isEqualTo(0.2);
            verify(profileRepository).save(profile);
        }

        @Test
        @DisplayName("Failure - user not found")
        void update_profile_image_user_not_found() {
            ProfileImageUpdateDTO dto = new ProfileImageUpdateDTO();
            dto.setImage(mock(MultipartFile.class));

            when(authValidator.validateUserByUsername("john")).thenThrow(UserNotFoundException.class);

            assertThrows(UserNotFoundException.class, () ->
                    profileService.updateProfileImage("john", dto));
        }
    }

    @Nested
    @DisplayName("getProfile()")
    class GetProfileTest {

        @Test
        @DisplayName("Success - get my profile")
        void get_my_profile() {
            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(postRepository.findAllByAuthorOrderByCreatedAtDesc(user)).thenReturn(Collections.emptyList());
            when(followRepository.findAllByFollowing(user)).thenReturn(Collections.emptyList());
            when(followRepository.findAllByFollower(user)).thenReturn(Collections.emptyList());

            ProfileResponseDTO dto = profileService.getProfile("john", "john");

            assertThat(dto.getIsMe()).isTrue();
            assertThat(dto.getUsername()).isEqualTo("john");
        }

        @Test
        @DisplayName("Success - get another user's profile")
        void get_other_profile() {
            User other = User.builder().id(2L).username("jane").profile(profile).build();
            profile.setUser(other);

            when(authValidator.validateUserByUsername("jane")).thenReturn(other);
            when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
            when(communityMemberRepository.findByUser(user)).thenReturn(Collections.emptyList());
            when(postRepository.findVisiblePostsForViewer(other, Collections.emptyList())).thenReturn(Collections.emptyList());
            when(followRepository.findAllByFollowing(other)).thenReturn(Collections.emptyList());
            when(followRepository.findAllByFollower(other)).thenReturn(Collections.emptyList());

            ProfileResponseDTO dto = profileService.getProfile("jane", "john");

            assertThat(dto.getIsMe()).isFalse();
            assertThat(dto.getUsername()).isEqualTo("jane");
        }

        @Test
        @DisplayName("Failure - target user not found")
        void get_profile_user_not_found() {
            when(authValidator.validateUserByUsername("unknown")).thenThrow(UserNotFoundException.class);

            assertThrows(UserNotFoundException.class, () ->
                    profileService.getProfile("unknown", "john"));
        }
    }
}
