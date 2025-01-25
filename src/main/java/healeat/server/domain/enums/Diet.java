package healeat.server.domain.enums;

import java.util.Arrays;

public enum Diet {

    NONE(null),

    LOSE_WEIGHT("체중 감량"),
    MAINTAIN("건강 유지");

    private final String description;

    Diet(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Diet getByDescription(String description) {
        return Arrays.stream(Diet.values())
                .filter(a -> a.getDescription().equals(description))
                .findFirst()
                .orElse(Diet.NONE);
    }
}
