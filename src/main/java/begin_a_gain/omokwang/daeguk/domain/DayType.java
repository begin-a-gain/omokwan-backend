package begin_a_gain.omokwang.daeguk.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DayType {
    MONDAY(1, "WEEKDAY"),
    TUESDAY(2, "WEEKDAY"),
    WEDNESDAY(3, "WEEKDAY"),
    THURSDAY(4, "WEEKDAY"),
    FRIDAY(5, "WEEKDAY"),
    SATURDAY(6, "WEEKEND"),
    SUNDAY(7, "WEEKEND"),
    WEEKDAYS(8, "GROUP"),
    WEEKENDS(9, "GROUP"),
    EVERYDAY(10, "GROUP");

    private final int code;
    private final String category;

    private static final Map<Integer, DayType> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(DayType::getCode, Function.identity()));


    public int getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public static DayType fromCode(int code) {
        return CODE_MAP.get(code);
    }

    public enum Category {
        WEEKDAY, WEEKEND, GROUP
    }
}
