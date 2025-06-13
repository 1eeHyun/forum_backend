package com.example.forum.controller.community.docs.manage;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityRuleRequestDTO;
import com.example.forum.dto.community.CommunityRuleResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Community Rule", description = "Community Rules related API")
public interface CommunityRuleApiDocs {

    @Operation(
            summary = "Add a new rule",
            description = "Adds a new rule to the specified community.",
            parameters = {
                    @Parameter(name = "communityId", description = "ID of the community", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Title and content of the rule",
                    content = @Content(schema = @Schema(implementation = CommunityRuleRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Rule successfully created"),
                    @ApiResponse(responseCode = "404", description = "Community not found")
            }
    )
    @PostMapping
    ResponseEntity<CommonResponse<Void>> addRule(
            @PathVariable Long communityId,
            @RequestBody CommunityRuleRequestDTO request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Update an existing rule",
            description = "Updates the title and/or content of a rule in the community.",
            parameters = {
                    @Parameter(name = "communityId", description = "ID of the community", in = ParameterIn.PATH),
                    @Parameter(name = "ruleId", description = "ID of the rule to update", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CommunityRuleRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rule updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Rule or community not found")
            }
    )
    @PutMapping("/{ruleId}")
    ResponseEntity<CommonResponse<Void>> updateRule(
            @PathVariable Long communityId,
            @PathVariable Long ruleId,
            @RequestBody CommunityRuleRequestDTO request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Delete a rule",
            description = "Deletes a specific rule from the community.",
            parameters = {
                    @Parameter(name = "communityId", description = "ID of the community", in = ParameterIn.PATH),
                    @Parameter(name = "ruleId", description = "ID of the rule to delete", in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rule deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Rule or community not found")
            }
    )
    @DeleteMapping("/{ruleId}")
    ResponseEntity<CommonResponse<Void>> deleteRule(
            @PathVariable Long communityId,
            @PathVariable Long ruleId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Get all rules in a community",
            description = "Returns a list of all rules belonging to the specified community.",
            parameters = {
                    @Parameter(name = "communityId", description = "ID of the community", in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of rules",
                            content = @Content(schema = @Schema(implementation = CommunityRuleResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Community not found")
            }
    )
    @GetMapping
    ResponseEntity<CommonResponse<List<CommunityRuleResponseDTO>>> getRulesByCommunity(
            @PathVariable Long communityId
    );
}
