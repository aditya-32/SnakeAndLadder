package org.example.cellStrategy;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.example.configuration.AppConfig;
import org.example.entity.CellSkipperEntity;
import org.example.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        }
        this.cellSkipperEntities = skipperPosMap;
    }

    public Integer getNext(Player player, Integer newPos) {
        if (mines.contains(newPos)) {
            player.activateMineMoveBlocker();
            return newPos;
        }
        while(cellSkipperEntities.containsKey(newPos)) {
            var cellSkipperAtNewPos= cellSkipperEntities.get(newPos);
            var firstCellSkipper = cellSkipperAtNewPos.stream().findFirst();
            if (firstCellSkipper.isPresent()) {
                var skipper = firstCellSkipper.get();
                newPos = skipper.getDestination();
            } else {
                break;
            }
        }
        return newPos;
    }
}
