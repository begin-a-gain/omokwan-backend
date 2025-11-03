package begin_a_gain.omokwang.match.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchAllRequest {
    private Boolean joinable;
    private List<Long> categories;
    private String search;
    private int pageNumber;
    private int pageSize;
}
