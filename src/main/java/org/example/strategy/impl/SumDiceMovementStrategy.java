package org.example.strategy.impl;

import com.google.inject.Inject;
import lombok.NoArgsConstructor;
import org.example.entity.Dice;
import org.example.strategy.MovementStrategy;
import java.util.List;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class SumDiceMovementStrategy implements MovementStrategy {
    @Override
    public Integer executeStrategy(List<Dice> diceList) {
        var diceRollResult = diceList.stream().map(Dice::rollDice).toList();
        return diceRollResult.stream().mapToInt(Integer::intValue).sum();
    }
}
