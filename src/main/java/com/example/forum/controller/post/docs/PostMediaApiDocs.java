package com.example.forum.controller.post.docs;

import com.example.forum.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Post Media", description = "Post media related API")
public interface PostMediaApiDocs {

    @Operation(
            summary = "Upload file (image/video/etc)",
            description = "Uploads a media file (image or video) and returns the URL of the stored file.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Media file to upload",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "File uploaded successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CommonResponse<String>> uploadFile(
            @RequestParam("file") MultipartFile file
    );
}
