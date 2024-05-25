package org.example.simulator;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.example.Enum.MovementStrategyEnum;
import org.example.configuration.AppConfig;
import org.example.entity.Dice;
import org.example.entity.Player;
import org.example.strategy.MovementStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DiceRoller {
    private final AppConfig appConfig;
    private final List<Dice> diceList;
    private final Map<MovementStrategyEnum, MovementStrategy> enumMovementStrategyMap;

    @Inject
    DiceRoller(AppConfig config, Map<MovementStrategyEnum, MovementStrategy> map) {
        this.appConfig = config;
        this.diceList = createDiceList(appConfig);
        this.enumMovementStrategyMap = map;
    }

    private List<Dice> createDiceList(AppConfig appConfig) {
        var dices = appConfig.getDices();
        var listBuilder = ImmutableList.<Dice>builder();
        for (var diceSize : dices) {
            var dice = Dice.builder().size(diceSize).build();
            listBuilder.add(dice);
        }
        return listBuilder.build();
    }

    public Integer rollDice(Player player) {
        if (MapUtils.isNotEmpty(appConfig.getDiceRolls())) {
            var predefinedDiceRolls = appConfig.getDiceRolls().get(player.getName());
            if (CollectionUtils.isNotEmpty(predefinedDiceRolls)) {
                var firstElement = predefinedDiceRolls.get(0);
                predefinedDiceRolls.remove(firstElement);
                return firstElement;
            }
        }
        var strategy = appConfig.getMovementStrategy();
        var strategyEnum = MovementStrategyEnum.fromString(strategy);
        var movementStrategy = enumMovementStrategyMap.get(strategyEnum);
        return movementStrategy.executeStrategy(diceList);
    }
}
