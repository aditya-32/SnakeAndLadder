package org.example.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum MovementStrategyEnum {
    MAX("max"),
    MIN("min"),
    SUM("sum");

    private final String name;
    private static final Map<String, MovementStrategyEnum> REVERSE_MAP= new HashMap<>();

    static {
        for (var myEnum : MovementStrategyEnum.values()) {
            REVERSE_MAP.put(myEnum.getName(), myEnum);
        }
    }

    public static MovementStrategyEnum fromString(String name) {
        return REVERSE_MAP.get(name);
    }
}
