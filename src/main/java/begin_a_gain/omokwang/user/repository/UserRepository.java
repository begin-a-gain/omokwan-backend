package begin_a_gain.omokwang.user.repository;

import begin_a_gain.omokwang.user.dto.User;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialId(Long socialId);

    User findByRefreshToken(String refreshToken);

    Optional<User> findBySocialIdAndPlatform(Long socialId, String platform);

    // 리프레시 토큰 업데이트 메서드
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.refreshToken = :refreshToken WHERE u.socialId = :socialId")
    void updateRefreshToken(@Param("socialId") Long socialId, @Param("refreshToken") String refreshToken);
}