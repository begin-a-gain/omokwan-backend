package begin_a_gain.omokwang.notification.dto;

public enum NotificationFilter {
    ALL,
    UNREAD;

    public static NotificationFilter from(String rawFilter) {
        if (rawFilter == null || rawFilter.isBlank()) {
            return ALL;
        }

        return switch (rawFilter.toLowerCase()) {
            case "all" -> ALL;
            case "unread" -> UNREAD;
            default -> throw new IllegalArgumentException("Invalid filter: " + rawFilter);
        };
    }
}
