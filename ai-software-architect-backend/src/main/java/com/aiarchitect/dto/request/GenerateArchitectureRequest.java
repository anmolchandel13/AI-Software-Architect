package com.aiarchitect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.aiarchitect.util.AppConstants.MAX_IDEA_LENGTH;
import static com.aiarchitect.util.AppConstants.MIN_IDEA_LENGTH;

/**
 * Data Transfer Object for the "generate architecture" request.
 *
 * Contains the user's plain-English project idea.
 * The idea must be between 10 and 2000 characters — too short means
 * the AI won't have enough context, too long risks exceeding token limits.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateArchitectureRequest {

    @NotBlank(message = "Project idea is required")
    @Size(min = MIN_IDEA_LENGTH, max = MAX_IDEA_LENGTH,
            message = "Project idea must be between " + MIN_IDEA_LENGTH + " and " + MAX_IDEA_LENGTH + " characters")
    private String projectIdea;
}
