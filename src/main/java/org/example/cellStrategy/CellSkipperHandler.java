package org.example.cellStrategy;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Enum.SkipperType;
import org.example.configuration.AppConfig;
import org.example.entity.CellSkipperEntity;
import org.example.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CellSkipperHandler {
    private final AppConfig appConfig;
    private Map<Integer, List<CellSkipperEntity>> cellSkipperEntities;
    private Set<Integer> mines;

    @Inject
    public void init() {
        var skipperEntities = appConfig.getEntities();
        this.mines  = appConfig.getMines();
        var skipperPosMap = new HashMap<Integer, List<CellSkipperEntity>>();
        for (var entity : skipperEntities) {
            var existingEntities = skipperPosMap.getOrDefault(entity.getSource(), new ArrayList<>());
            existingEntities.add(entity);
            skipperPosMap.put(entity.getSource(), existingEntities);
        }
        this.cellSkipperEntities = skipperPosMap;
    }

    public Integer getNext(Player player, Integer currPos, Integer stepCount,
                           BiConsumer<Integer, Player> removeExistingPLayer) {
        var newPos = stepCount + currPos;
        var didEncounterObstacle = false;
        if (mines.contains(newPos)) {
            log.info("{} rolled a {} and move from {} to {} and encountered a mine",
                    player.getName(), stepCount, currPos, newPos);
            player.activateMineMoveBlocker();
            return newPos;
        }
        while(cellSkipperEntities.containsKey(newPos)) {
            didEncounterObstacle = true;
            removeExistingPLayer.accept(newPos, player);
            var cellSkipperAtNewPos= cellSkipperEntities.get(newPos);
            var firstCellSkipper = cellSkipperAtNewPos.stream().findFirst();
            if (firstCellSkipper.isPresent()) {
                logMessage(player, firstCellSkipper.get(),stepCount);
                var skipper = firstCellSkipper.get();
                newPos = skipper.getDestination();
            } else {
                break;
            }
        }
        if (!didEncounterObstacle) {
            log.info("{} rolled a {} and move from {} to {}", player.getName(), stepCount, currPos, newPos);
        }
        return newPos;
    }

    private void logMessage(Player player, CellSkipperEntity skipper, Integer stepCount) {
        var skipperType = SkipperType.fromString(skipper.getType());
        switch (skipperType) {
            case SNAKE -> log.info("{} rolled a {} and bitten by snake from {} to {}", player.getName(),
                    stepCount, skipper.getSource(),
                    skipper.getDestination());
            case LADDER -> log.info("{} rolled a {} and climbed ladder from {} to {}", player.getName(),
                    stepCount, skipper.getSource(),
                    skipper.getDestination());
            case CROCODILE -> log.info("{} rolled a {} and bitten by crocodile from {} to {}", player.getName(),
                    stepCount, skipper.getSource(),
                    skipper.getDestination()) ;
        }
    }
}
