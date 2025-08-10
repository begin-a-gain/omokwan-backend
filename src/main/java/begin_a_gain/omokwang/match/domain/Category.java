package begin_a_gain.omokwang.match.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "대국 카테고리")
public class Category {
    @Schema(description = "대국 카테고리 코드", example = "1")
    private String code;

    @Schema(description = "대국 카테고리", example = "운동")
    private String category;

    @Schema(description = "대국 카테고리 이모티콘", example = "U+1F4AA")
    private String emoji;


}
