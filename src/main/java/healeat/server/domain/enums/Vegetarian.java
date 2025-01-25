package healeat.server.domain.enums;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthInfoHandler;

import java.util.Arrays;

public enum Vegetarian {

    NONE(null),

    FLEXI("플렉시테리언"),
    POLO_PESCET("폴로-페스코"),
    PESCET("페스코"),
    POLO("폴로"),

    LACTO_OVO("락토-오보"), // VEGAN과 동일
    LACTO("락토"), // VEGAN과 동일
    OVO("오보"), // VEGAN과 동일
    VEGAN("비건");

    private final String description;

    Vegetarian(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Vegetarian getByDescription(String description) {
        return Arrays.stream(Vegetarian.values())
                .filter(a -> a.getDescription().equals(description))
                .findFirst()
                .orElse(Vegetarian.NONE);
    }
}