package com.example.forum.controller.profile;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.dto.profile.BioUpdateDTO;
import com.example.forum.dto.profile.NicknameUpdateDTO;
import com.example.forum.dto.profile.ProfileResponseDTO;
import com.example.forum.dto.profile.UsernameUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Profile", description = "Profile related API")
public interface ProfileApiDocs {

    @Operation(
            summary = "Retrieve a user's profile",
            description = "Retrieves a users profile, it detects if the profile is my profile."
    )
    ResponseEntity<CommonResponse<ProfileResponseDTO>> getProfile(String username, UserDetails userDetails);

    @Operation(
            summary = "Retrieve a user's posts",
            description = "Retrieves a users posts to show on their profile."
    )
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getProfilePosts(String username, String sort, int page, int size, UserDetails userDetails);

    @Operation(
            summary = "Update user's nickname",
            description = "Update user's nickname."
    )
    ResponseEntity<CommonResponse<Void>> updateNickname(String targetUsername, UserDetails userDetails, NicknameUpdateDTO dto);

    @Operation(
            summary = "Update user's username",
            description = "Update user's username."
    )
    ResponseEntity<CommonResponse<LoginResponseDTO>> updateUsername(String targetUsername, UserDetails userDetails, UsernameUpdateDTO dto);

    @Operation(
            summary = "Update user's bio",
            description = "Update user's bio."
    )
    ResponseEntity<CommonResponse<Void>> updateBio(String targetUsername, UserDetails userDetails, BioUpdateDTO dto);

    @Operation(
            summary = "Update user's profile image",
            description = "Update user's profile image."
    )
    ResponseEntity<CommonResponse<Void>> updateProfileImage(String targetUsername, UserDetails userDetails,
                                                            MultipartFile image,
                                                            Double positionX,
                                                            Double positionY);

    ResponseEntity<CommonResponse<ProfileResponseDTO>> getProfileByPublicId(String publicId);
}
