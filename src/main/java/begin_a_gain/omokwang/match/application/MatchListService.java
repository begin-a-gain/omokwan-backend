package begin_a_gain.omokwang.match.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.match.dto.MatchAllRequest;
import begin_a_gain.omokwang.match.dto.MatchAllResponse;
import begin_a_gain.omokwang.match.dto.MatchQuery;
import begin_a_gain.omokwang.match.repository.MatchListRepository;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchListService {

    private final MatchListRepository matchRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MatchAllResponse> findAllMatch(MatchAllRequest request) {
        var query = convertToQuery(request);
        return matchRepository.findMatches(query);
    }

    private MatchQuery convertToQuery(MatchAllRequest request) {
        return MatchQuery.builder()
                .joinable(request.getJoinable())
                .categoryId(request.getCategoryId())
                .search(request.getSearch())
                .pageNumber(request.getPageNumber())
                .pageSize(request.getPageSize())
                .userId(getUserId())
                .build();
    }

    private Long getUserId() {
        var socialId = SecurityUtil.getCurrentUserSocialId();
        var user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + socialId));
        return user.getId();
    }

}
