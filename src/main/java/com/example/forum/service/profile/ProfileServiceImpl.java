package com.example.forum.service.profile;

import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.follow.FollowUserDTO;
import com.example.forum.dto.profile.*;
import com.example.forum.mapper.profile.ProfileMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.follow.FollowRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.repository.profile.ProfileRepository;
import com.example.forum.repository.user.UserRepository;
import com.example.forum.security.JwtTokenProvider;
import com.example.forum.service.S3Service;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.profile.ProfileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final AuthValidator userValidator;
    private final UserRepository userRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final PostRepository postRepository;
    private final ProfileValidator profileValidator;
    private final ProfileRepository profileRepository;
    private final FollowRepository followRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final S3Service s3Service;

    @Value("${app.default-profile-image}")
    private String defaultProfileImageUrl;

    @Override
    public ProfileResponseDTO getProfile(String targetUsername, String loginUsername) {

        User user = userValidator.validateUserByUsername(targetUsername);
        boolean isMe = loginUsername != null && loginUsername.equals(targetUsername);
        int postCount = postRepository.countByAuthor(user);

        List<Post> posts;

        if (isMe) {
            // See my profile
            posts = postRepository.findAllByAuthorOrderByCreatedAtDesc(user);
        } else {

            // See another person's profile
            User currentUser = loginUsername != null
                    ? userRepository.findByUsername(loginUsername).orElse(null)
                    : null;

            List<Community> sharedCommunities = currentUser != null
                    ? communityMemberRepository.findByUser(currentUser)
                    .stream()
                    .map(cm -> cm.getCommunity())
                    .toList()
                    : List.of();


            posts = postRepository.findVisiblePostsForViewer(user, sharedCommunities);
        }

        // Followers
        List<FollowUserDTO> followers = followRepository.findAllByFollowing(user)
                .stream()
                .map(f -> new FollowUserDTO(f.getFollower()))
                .toList();

        // Followings
        List<FollowUserDTO> followings = followRepository.findAllByFollower(user)
                .stream()
                .map(f -> new FollowUserDTO(f.getFollowing()))
                .toList();

        ProfileResponseDTO dto = ProfileMapper.toDTO(user, isMe, postCount, followers, followings);
        return dto;
    }

    /**
     * Update related methods
     */

    // Nickname
    @Override
    public void updateNickname(String username, NicknameUpdateDTO dto) {

        User user = userValidator.validateUserByUsername(username);
        Profile profile = profileValidator.getProfileByUser(user);

        profile.setNickname(dto.getNickname());
        profileRepository.save(profile);
    }

    // Username
    @Override
    public LoginResponseDTO updateUsername(String currUsername, UsernameUpdateDTO dto) {

        userValidator.validateUniqueUsername(dto.getUsername());

        User user = userValidator.validateUserByUsername(currUsername);
        user.setUsername(dto.getUsername());
        userRepository.save(user);

        String newToken = jwtTokenProvider.generateToken(user.getUsername());

        return new LoginResponseDTO(newToken, user.getUsername());
    }

    // Bio
    @Override
    public void updateBio(String username, BioUpdateDTO dto) {

        User user = userValidator.validateUserByUsername(username);
        Profile profile = profileValidator.getProfileByUser(user);

        profile.setBio(dto.getBio());
        profileRepository.save(profile);
    }

    // Image
    @Override
    public void updateProfileImage(String username, ProfileImageUpdateDTO dto) {

        User user = userValidator.validateUserByUsername(username);
        Profile profile = profileValidator.getProfileByUser(user);

        if (profile.getImageUrl() != null && !profile.getImageUrl().equals(defaultProfileImageUrl))
            s3Service.delete(profile.getImageUrl());

        String newImageUrl = s3Service.upload(dto.getImage());

        profile.setImageUrl(newImageUrl);
        profile.setImagePositionX(dto.getPositionX());
        profile.setImagePositionY(dto.getPositionY());
        profileRepository.save(profile);
    }

//    @Override
//    public ProfileResponseDTO getProfileByPublicId(String publicId) {
//
////        Profile profile = profileRepository.findByPublicId(publicId)
////                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
////
////        return ProfileMapper.toDto(profile);
//        return null;
//    }
}
