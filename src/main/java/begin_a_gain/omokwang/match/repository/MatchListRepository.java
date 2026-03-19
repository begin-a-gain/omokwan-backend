package begin_a_gain.omokwang.match.repository;

import begin_a_gain.omokwang.match.domain.JoinableStatus;
import begin_a_gain.omokwang.match.dto.MatchAllResponse;
import begin_a_gain.omokwang.match.dto.MatchQuery;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchListRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<MatchAllResponse> findMatches(MatchQuery query) {

        String sql = """
                SELECT
                  info.id,
                  ANY_VALUE(info.category)         AS category,
                  ANY_VALUE(info.name)             AS name,
                  ANY_VALUE(info.is_public)        AS is_public,
                  MAX(info.create_date)            AS create_date,
                  ANY_VALUE(info.max_participants) AS max_participants,
                
                  COUNT(CASE WHEN p.leave_date IS NULL THEN 1 END) AS participants,
                
                  ANY_VALUE(u.nickname)            AS host_name,
                
                  EXISTS (
                      SELECT 1
                      FROM match_participant AS my
                      WHERE my.match_id = info.id
                        AND my.user_id   = :userId
                        AND my.leave_date IS NULL
                  ) AS already_joined,
                
                  EXISTS (
                      SELECT 1
                      FROM match_participant AS my
                      WHERE my.match_id = info.id
                        AND my.user_id   = :userId
                        AND my.leave_date IS NOT NULL
                  ) AS was_kicked
                
                FROM match_info AS info
                JOIN user AS u
                  ON u.id = info.create_id
                LEFT JOIN match_participant AS p
                  ON p.match_id = info.id
                
                WHERE ( :categoriesEmpty = TRUE OR info.category IN (:categories) )
                  AND (
                       :search IS NULL
                    OR LOWER(info.name)      LIKE CONCAT('%', LOWER(:search), '%')
                    OR CAST(info.id AS CHAR) LIKE CONCAT('%', :search, '%')
                    OR LOWER(u.nickname)     LIKE CONCAT('%', LOWER(:search), '%')
                  )
                
                GROUP BY info.id
                
                HAVING (
                  :joinable IS NULL
                  OR (:joinable = TRUE
                      AND COUNT(CASE WHEN p.leave_date IS NULL THEN 1 END) < MAX(info.max_participants)
                      AND already_joined = 0
                      AND was_kicked = 0
                  )
                  OR (:joinable = FALSE
                      AND (
                           COUNT(CASE WHEN p.leave_date IS NULL THEN 1 END) >= MAX(info.max_participants)
                        OR already_joined = 1
                        OR was_kicked = 1
                      )
                  )
                )
                
                ORDER BY create_date DESC, info.id DESC
                LIMIT :limitPlusOne OFFSET :offset
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("joinable", query.getJoinable());

        List<Long> categories = query.getCategories();
        boolean categoriesEmpty = (categories == null || categories.isEmpty());
        params.put("categoriesEmpty", categoriesEmpty);
        params.put("categories", categoriesEmpty ? List.of(-1L) : categories);

        params.put("search", (query.getSearch() == null || query.getSearch().isBlank()) ? null : query.getSearch());

        params.put("offset", query.getPageNumber() * query.getPageSize());
        params.put("limitPlusOne", query.getPageSize() + 1);
        params.put("userId", query.getUserId());

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            int participants = rs.getInt("participants");
            int maxParticipants = rs.getInt("max_participants");

            return MatchAllResponse.builder()
                    .matchId(rs.getLong("id"))
                    .categoryId(rs.getLong("category"))
                    .name(rs.getString("name"))
                    .hostName(rs.getString("host_name"))
                    .ongoingDays(getOngoingDays(rs.getDate("create_date").toLocalDate()))
                    .participants(participants)
                    .maxParticipants(maxParticipants)
                    .isPublic(rs.getBoolean("is_public"))
                    .joinable(getJoinableStatus(
                            rs.getBoolean("already_joined"),
                            rs.getBoolean("was_kicked"),
                            participants >= maxParticipants
                    ))
                    .build();
        });
    }

    private int getOngoingDays(LocalDate createDate) {
        var today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        long daysBetween = ChronoUnit.DAYS.between(createDate, today) + 1;
        return (int) daysBetween;
    }

    private JoinableStatus getJoinableStatus(boolean alreadyJoined, boolean wasKicked, boolean isFull) {
        if (alreadyJoined) {
            return JoinableStatus.IN_PROGRESS;
        }
        if (wasKicked) {
            return JoinableStatus.KICKED;
        }
        if (isFull) {
            return JoinableStatus.FULL;
        }
        return JoinableStatus.JOINABLE;
    }
}
