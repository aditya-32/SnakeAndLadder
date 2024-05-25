package org.example.strategy;

import org.example.entity.Dice;
import java.util.List;

public interface MovementStrategy {
    Integer executeStrategy(List<Dice> diceList);
}
