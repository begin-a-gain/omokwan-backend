package begin_a_gain.omokwang.daeguk.domain;

import begin_a_gain.omokwang.common.converter.IntegerListJsonConverter;
import begin_a_gain.omokwang.user.dto.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Daeguk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_id", nullable = false)
    private User createId;

    private String name;

    @Convert(converter = IntegerListJsonConverter.class) // JSON 변환기 사용
    @Column(columnDefinition = "JSON")
    private List<Integer> dayType;

    private int maxParticipants;

    private String category;

    private boolean isPublic;

    private int password;

    @Column(name = "daeguk_code", nullable = false, unique = true)
    private String daegukCode;

}
