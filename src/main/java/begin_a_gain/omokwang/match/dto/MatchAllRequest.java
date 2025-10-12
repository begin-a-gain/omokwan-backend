package begin_a_gain.omokwang.match.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchAllRequest {
    private Boolean joinable;
    private Long categoryId;
    private String search;
    private int pageNumber;
    private int pageSize;
}
