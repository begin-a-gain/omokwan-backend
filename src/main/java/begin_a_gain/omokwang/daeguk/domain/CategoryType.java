package begin_a_gain.omokwang.daeguk.domain;

import java.util.Arrays;
import java.util.List;

public enum CategoryType {
    EXERCISE("1", "운동"),
    HEALTH("2", "건강"),
    HOBBY("3", "취미"),
    LIFESTYLE("4", "생활"),
    STUDY("5", "공부"),
    PRACTICE("6", "연습"),
    KNOWLEDGE("7", "시사/교양"),
    INSTRUMENT("8", "악기"),
    IMPROVEMENT("9", "자기계발"),
    DIET("10", "다이어트"),
    READING("11", "독서");

    private final String code;
    private final String category;

    CategoryType(String code, String category) {
        this.code = code;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public static List<Category> getCategoryList() {
        return Arrays.stream(values())
                .map(x -> Category.builder()
                        .code(x.getCode())
                        .category(x.getCategory())
                        .build()).toList();
    }

    public static boolean isValidCategory(String code) {
        return Arrays.stream(values()).anyMatch(x -> x.getCode().equals(code));
    }
}
