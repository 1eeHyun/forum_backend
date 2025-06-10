package com.example.forum.controller.profile.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.dto.profile.BioUpdateDTO;
import com.example.forum.dto.profile.NicknameUpdateDTO;
import com.example.forum.dto.profile.ProfileResponseDTO;
import com.example.forum.dto.profile.UsernameUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Profile", description = "Profile related API")
public interface ProfileApiDocs {

    @Operation(
            summary = "Get user profile",
            description = "Retrieves profile information of the specified user. If the logged-in user is viewing their own profile, all posts are returned. Otherwise, only public or shared community posts are shown.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{username}")
    ResponseEntity<CommonResponse<ProfileResponseDTO>> getProfile(
            @Parameter(description = "Username of the profile to retrieve", required = true)
            @PathVariable String username,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Get posts by user profile",
            description = "Retrieves a paginated and sorted list of posts for the specified user. If the logged-in user is the same as the profile owner, all posts are shown. Otherwise, only public or shared-community posts are shown.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Posts retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{username}/posts")
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getProfilePosts(
            @Parameter(description = "Username of the profile", required = true)
            @PathVariable String username,

            @Parameter(description = "Sort order: top, newest, or oldest", required = true, example = "newest")
            @RequestParam String sort,

            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam int page,

            @Parameter(description = "Number of posts per page", example = "10")
            @RequestParam int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Update nickname",
            description = "Updates the nickname of the currently logged-in user. Only the user themselves can change their nickname.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New nickname value",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NicknameUpdateDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nickname updated successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid nickname or already taken", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is not the owner of the profile", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PostMapping("/{targetUsername}/nickname")
    ResponseEntity<CommonResponse<Void>> updateNickname(
            @Parameter(description = "Username of the profile to update nickname", required = true)
            @PathVariable String targetUsername,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @RequestBody NicknameUpdateDTO dto
    );

    @Operation(
            summary = "Update username",
            description = "Updates the login username of the currently authenticated user. Returns a new JWT token along with the updated username.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New username value",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsernameUpdateDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Username updated successfully", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Invalid username or already taken", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user is not the owner of the account", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PostMapping("/{targetUsername}/username")
    ResponseEntity<CommonResponse<LoginResponseDTO>> updateUsername(
            @Parameter(description = "Current username (must match authenticated user)", required = true)
            @PathVariable String targetUsername,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @RequestBody UsernameUpdateDTO dto
    );


    @Operation(
            summary = "Update user bio",
            description = "Updates the bio of the authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New bio text to be updated",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BioUpdateDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid bio", content = @Content)
            }
    )
    @PostMapping("/{targetUsername}/bio")
    ResponseEntity<CommonResponse<Void>> updateBio(
            @Parameter(description = "Username of the profile", required = true)
            @PathVariable String targetUsername,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @RequestBody BioUpdateDTO dto
    );

    @Operation(
            summary = "Update profile image",
            description = "Updates the profile image of the logged-in user along with image position coordinates (X, Y).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Multipart form containing image file and position",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile image updated successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid file or coordinates",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - not the owner of the profile",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content
                    )
            }
    )
    @PostMapping(value = "/{targetUsername}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CommonResponse<Void>> updateProfileImage(
            @Parameter(description = "Target username", required = true)
            @PathVariable String targetUsername,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "Image file to upload", required = true)
            @RequestParam MultipartFile image,

            @Parameter(description = "X position for image centering", example = "50.0", required = true)
            @RequestParam Double positionX,

            @Parameter(description = "Y position for image centering", example = "50.0", required = true)
            @RequestParam Double positionY
    );


    @Operation(
            summary = "Get profile by public ID",
            description = "Retrieves a user's profile using their public ID (if applicable).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/public/{publicId}")
    ResponseEntity<CommonResponse<ProfileResponseDTO>> getProfileByPublicId(
            @Parameter(description = "Public ID of the user", required = true)
            @PathVariable String publicId
    );

}
