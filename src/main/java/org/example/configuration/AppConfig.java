package org.example.configuration;

import lombok.Data;
import org.example.entity.Ladder;
import org.example.entity.Player;
import org.example.entity.Snake;
import java.util.List;
import java.util.Map;

@Data
public class AppConfig {
    Integer boardSize;
    List<Player> players;
    List<Snake> snakes;
    List<Ladder> ladders;
    List<Integer> mines;
    List<Integer> crocodiles;
    List<Integer> dices;
    Map<String, List<Integer>> diceRolls;
    String movementStrategy;
}
