package com.example.forum.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;

public record MarkAsReadRequest(
        @Schema(description = "ID of the last read message", example = "128")
        Long lastReadMessageId
) {}
