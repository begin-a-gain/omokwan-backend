package begin_a_gain.omokwang.user.repository;

import begin_a_gain.omokwang.user.dto.User;
import java.util.Optional;
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
}
