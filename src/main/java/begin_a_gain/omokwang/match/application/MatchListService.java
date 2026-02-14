package begin_a_gain.omokwang.match.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.match.controller.MatchAllControllerResponse;
import begin_a_gain.omokwang.match.dto.MatchAllRequest;
import begin_a_gain.omokwang.match.dto.MatchQuery;
import begin_a_gain.omokwang.match.repository.MatchListRepository;
import begin_a_gain.omokwang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchListService {

    private final MatchListRepository matchRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MatchAllControllerResponse findAllMatch(MatchAllRequest request) {
        var query = convertToQuery(request);

        var matcheList = matchRepository.findMatches(query);

        boolean hasNext = matcheList.size() > query.getPageSize();
        if (hasNext) {
            matcheList.remove(matcheList.size() - 1);
        }
        return MatchAllControllerResponse.builder()
                .matchList(matcheList)
                .hasNext(hasNext)
                .build();
    }

    private MatchQuery convertToQuery(MatchAllRequest request) {
        return MatchQuery.builder()
                .joinable(request.getJoinable())
                .categories(request.getCategories())
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
