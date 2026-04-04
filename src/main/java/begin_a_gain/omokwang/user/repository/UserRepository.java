package begin_a_gain.omokwang.user.repository;

import begin_a_gain.omokwang.user.dto.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialId(Long socialId);

    User findByRefreshToken(String refreshToken);

    boolean existsByNickname(String nickname);

    Optional<User> findBySocialIdAndPlatform(Long socialId, String platform);

    boolean existsByIdAndNicknameIsNotNull(Long id);

    @Query("SELECT CASE WHEN u.nickname IS NULL THEN false ELSE true END FROM User u WHERE u.socialId = :socialId")
    boolean existsNicknameBySocialId(@Param("socialId") Long socialId);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.nickname IS NOT NULL
              AND (:keyword IS NULL OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND NOT EXISTS (
                  SELECT 1
                  FROM MatchParticipant mp
                  WHERE mp.match.id = :matchId
                    AND mp.user.id = u.id
                    AND (
                        mp.kickedDate IS NOT NULL
                        OR mp.leaveDate IS NULL
                    )
              )
              AND (
                  :cursorNickname IS NULL
                  OR u.nickname > :cursorNickname
                  OR (u.nickname = :cursorNickname AND u.id > :cursorUserId)
              )
            ORDER BY u.nickname ASC, u.id ASC
            """)
    List<User> findUsersByNicknameWithCursor(
            @Param("matchId") Long matchId,
            @Param("keyword") String keyword,
            @Param("cursorNickname") String cursorNickname,
            @Param("cursorUserId") Long cursorUserId,
            Pageable pageable
    );

}
