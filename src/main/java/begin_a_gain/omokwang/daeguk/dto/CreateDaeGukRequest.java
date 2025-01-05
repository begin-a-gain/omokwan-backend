package begin_a_gain.omokwang.daeguk.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class CreateDaeGukRequest {
    private String name;

    private List<Integer> dayType;

    private int maxParticipants;

    private String category;

    private boolean isPublic;

    private String password;

    private String daegukCode;
}
