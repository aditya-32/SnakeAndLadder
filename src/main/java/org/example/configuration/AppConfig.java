package org.example.configuration;

import lombok.Data;
import org.example.entity.CellSkipperEntity;
import org.example.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class AppConfig {
    private Integer boardSize;
    private List<Player> players;
    private List<CellSkipperEntity> entities;
    private Set<Integer> mines;
    private List<Integer> dices;
    private Map<String, List<Integer>> diceRolls;
    private String movementStrategy;
}
