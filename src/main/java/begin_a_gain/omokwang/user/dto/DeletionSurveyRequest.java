package begin_a_gain.omokwang.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "Deletion Survey Request")
public record DeletionSurveyRequest(

        @Schema(description = "List of selected reasons for account deletion")
        @NotEmpty
        List<DeletionReason> reasons,

        @Schema(description = "Required only if the 'OTHER' option is selected.",
                maxLength = 500)
        @Size(max = 500)
        String otherReason
) {
}
