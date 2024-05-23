package logalyzes.server.utils.logger;

public enum LOG_LEVEL {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
    FATAL(4);

    private final int value;

    LOG_LEVEL(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LOG_LEVEL fromInt(int i) {
        for (LOG_LEVEL level : LOG_LEVEL.values()) {
            if (level.getValue() == i) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid LOG_LEVEL value: " + i);
    }

    @Override
    public String toString() {
        return name();
    }
}
