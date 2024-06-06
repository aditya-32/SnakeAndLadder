package org.example.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.entity.CellSkipperEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Getter
@RequiredArgsConstructor
public enum SkipperType {
    SNAKE("snake", (cellSkipperEntity, boardSize) ->
            cellSkipperEntity.getSource() > cellSkipperEntity.getDestination()
                    && cellSkipperEntity.getSource() <= boardSize * boardSize
                    && cellSkipperEntity.getDestination() <= boardSize * boardSize
                    && cellSkipperEntity.getSource() >= 1
                    && cellSkipperEntity.getDestination() >= 1),
    LADDER("ladder", (cellSkipperEntity, boardSize) ->
            cellSkipperEntity.getSource() < cellSkipperEntity.getDestination()
                    && cellSkipperEntity.getSource() <= boardSize * boardSize
                    && cellSkipperEntity.getDestination() <= boardSize * boardSize
                    && cellSkipperEntity.getSource() >= 1
                    && cellSkipperEntity.getDestination() >= 1),
    CROCODILE("crocodile", (cellSkipperEntity, boardSize) ->
            cellSkipperEntity.getSource() - cellSkipperEntity.getDestination() == 5
                    && cellSkipperEntity.getSource() <= boardSize * boardSize
                    && cellSkipperEntity.getDestination() <= boardSize * boardSize
                    && cellSkipperEntity.getSource() >= 1
                    && cellSkipperEntity.getDestination() >= 1);
    private final String name;
    private final BiFunction<CellSkipperEntity, Integer, Boolean> validator;

    private static final Map<String, SkipperType> REVERSE_MAP= new HashMap<>();

    static {
        for (var myEnum : SkipperType.values()) {
            REVERSE_MAP.put(myEnum.getName(), myEnum);
        }
    }

    public static SkipperType fromString(String name) {
        return REVERSE_MAP.get(name);
    }
}
