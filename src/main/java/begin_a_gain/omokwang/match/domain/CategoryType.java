package begin_a_gain.omokwang.match.domain;

import java.util.Arrays;
import java.util.List;

public enum CategoryType {
    EXERCISE("1", "운동", "U+1F4AA"),
    HEALTH("2", "건강", "U+1F3C3 U+200D U+2640 U+FE0F"),
    HOBBY("3", "취미", "U+1F308"),
    LIFESTYLE("4", "생활", "U+1FAE7"),
    STUDY("5", "공부", "U+1F4DD"),
    PRACTICE("6", "연습", "U+1F501"),
    KNOWLEDGE("7", "시사/교양", "U+1F5DE"),
    INSTRUMENT("8", "악기", "U+1F3B5"),
    IMPROVEMENT("9", "자기계발", "U+1F4DA"),
    DIET("10", "다이어트", "U+1F957"),
    READING("11", "독서", "U+1F4D5");

    private final String code;
    private final String category;
    private final String emoji;

    CategoryType(String code, String category, String emoji) {
        this.code = code;
        this.category = category;
        this.emoji = emoji;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public String getEmoji() {
        return emoji;
    }

    public static List<Category> getCategoryList() {
        return Arrays.stream(values())
                .map(x -> Category.builder()
                        .code(x.getCode())
                        .category(x.getCategory())
                        .emoji(x.getEmoji())
                        .build()).toList();
    }

    public static boolean isValidCategory(String code) {
        return Arrays.stream(values()).anyMatch(x -> x.getCode().equals(code));
    }
}
