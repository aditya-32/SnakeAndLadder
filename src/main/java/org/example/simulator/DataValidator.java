package org.example.simulator;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.example.configuration.AppConfig;
import org.example.entity.Ladder;
import org.example.entity.Snake;
import org.example.exceptions.InvalidGameConfigException;
import java.util.stream.Collectors;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class DataValidator {
    public void validateGameConfig(AppConfig appConfig) throws InvalidGameConfigException {
        var snake = appConfig.getSnakes();
        var ladders = appConfig.getLadders();
        var snakeStartSet = snake.stream().map(Snake::getSource).collect(Collectors.toSet());
        var ladderStartSet = ladders.stream().map(Ladder::getSource).collect(Collectors.toSet());
        var commonStartPoints = Sets.intersection(snakeStartSet, ladderStartSet);
        if (CollectionUtils.isNotEmpty(commonStartPoints)) {
            throw  new InvalidGameConfigException("Snake and ladder cannot have same start point");
        }
        var lastCell = appConfig.getBoardSize() * appConfig.getBoardSize();
        for (var snk : snake) {
            if (snk.getSource() < 0 || snk.getSource() > lastCell) {
                throw new InvalidGameConfigException("Snake can have cells within limit " + 1 + " - "+lastCell);
            }
            if (snk.getSource() < snk.getDestination()) {
                throw new InvalidGameConfigException("Snake can only take you to a cell lower than current one");
            }
        }
        for (var ldr : ladders) {
            if (ldr.getSource() < 0 || ldr.getSource() > lastCell) {
                throw new InvalidGameConfigException("Ladder can have cells within limit " + 1 + " - "+lastCell);
            }
            if (ldr.getSource() > ldr.getDestination()) {
                throw new InvalidGameConfigException("Ladder can only take you to a cell greater than current one");
            }
        }
        for (var crocodile : appConfig.getCrocodiles()) {
            if (crocodile <= 5) {
                throw new InvalidGameConfigException("Crocodiles should be present after 5th Cell as they take you 5 steps back");
            }
        }
    }
}
