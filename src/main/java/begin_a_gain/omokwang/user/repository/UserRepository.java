package begin_a_gain.omokwang.user.repository;

import begin_a_gain.omokwang.user.dto.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    User findByRefreshToken(String refreshToken);

    // 리프레시 토큰 업데이트 메서드
    @Modifying
    @Query("UPDATE User u SET u.refreshToken = :refreshToken WHERE u.id = :id")
    void updateRefreshToken(@Param("id") Long id, @Param("refreshToken") String refreshToken);
}