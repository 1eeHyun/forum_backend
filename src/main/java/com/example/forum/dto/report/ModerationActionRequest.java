package com.example.forum.dto.report;

import com.example.forum.model.report.ModerationActionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModerationActionRequest {

    @NotNull
    private ModerationActionType action;

    @Size(max = 1000)
    private String note;
}
