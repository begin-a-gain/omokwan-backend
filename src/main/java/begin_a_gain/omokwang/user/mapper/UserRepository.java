import begin_a_gain.omokwang.user.dto.UserDto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserDto, Long> {

    Optional<UserDto> findById(Long id);

    UserDto findByRefreshToken(String refreshToken);

    // User 업데이트는 save() 메서드를 이용하면 됩니다.
    // void update(User user);  // save()가 자동으로 사용됨


    // 리프레시 토큰 업데이트 메서드
    @Query("UPDATE User u SET u.refreshToken = :refreshToken WHERE u.id = :id")
    void updateRefreshToken(@Param("id") Long id, @Param("refreshToken") String refreshToken);
}