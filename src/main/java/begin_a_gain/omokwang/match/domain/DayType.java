package begin_a_gain.omokwang.match.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DayType {
    MONDAY(1, "WEEKDAY"),
    TUESDAY(2, "WEEKDAY"),
    WEDNESDAY(3, "WEEKDAY"),
    THURSDAY(4, "WEEKDAY"),
    FRIDAY(5, "WEEKDAY"),
    SATURDAY(6, "WEEKEND"),
    SUNDAY(7, "WEEKEND"),
    WEEKDAYS(8, "GROUP", Arrays.asList(1, 2, 3, 4, 5)),
    WEEKENDS(9, "GROUP", Arrays.asList(6, 7)),
    EVERYDAY(10, "GROUP", Arrays.asList(1, 2, 3, 4, 5, 6, 7));

    private final int code;
    private final String category;
    private final List<Integer> days;

    DayType(int code, String category) {
        this.code = code;
        this.category = category;
        this.days = Collections.singletonList(code);
    }

    DayType(int code, String category, List<Integer> days) {
        this.code = code;
        this.category = category;
        this.days = days;
    }

    public int getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public List<Integer> getDays() {
        return days;
    }

    public static List<Integer> expandDays(List<Integer> dayTypes) {
        return dayTypes.stream()
                .flatMap(x -> fromCode(x).getDays().stream())
                .distinct()
                .sorted()
                .toList();
    }

    public static DayType fromCode(int code) {
        return Arrays.stream(values())
                .filter(day -> day.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid code: " + code));
    }
}
