package test_data;

import java.util.concurrent.ThreadLocalRandom;

public enum TaskType {
    TODO("todo"),
    HABIT("habit"),
    DAILY("daily"),
    REWARD("reward");

    private final String type;

    TaskType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TaskType getRandomType() {
        TaskType[] values = TaskType.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(values.length);
        return values[randomIndex];
    }
}
