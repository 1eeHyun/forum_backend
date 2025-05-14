package com.example.forum.controller.profile;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.profile.ProfileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "Profile", description = "Profile related API")
public interface ProfileApiDocs {

    @Operation(
            summary = "Get my profile",
            description = "Retrieve a users profile, it detects if the profile is my profile."
    )
    ResponseEntity<CommonResponse<ProfileResponseDTO>> getProfile(String username, UserDetails userDetails);
}
