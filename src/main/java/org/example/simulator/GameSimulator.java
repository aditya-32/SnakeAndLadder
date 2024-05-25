package org.example.simulator;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.example.configuration.AppConfig;
import org.example.entity.Ladder;
import org.example.entity.Player;
import org.example.entity.Snake;
import org.example.exceptions.InvalidGameConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class GameSimulator {
    private static final Logger logger = LoggerFactory.getLogger(GameSimulator.class);

    private final AppConfig appConfig;
    private final DiceRoller diceRoller;
    private final DataValidator validator;

    private Integer winPos;
    private Queue<Player> players = new LinkedList<>();
    private Set<Integer> mines;
    private Set<Integer> crocodiles;
    private Map<Integer, Set<Player>> posToPlayerMap = new HashMap<>();
    private Map<Integer, Snake> snakeMap = new HashMap<>();
    private Map<Integer, Ladder> ladderMap = new HashMap<>();

    private void init() throws InvalidGameConfigException {
        validator.validateGameConfig(appConfig);
        Integer boardSize = appConfig.getBoardSize();
        this.winPos = boardSize * boardSize;
        this.players = new LinkedList<>(appConfig.getPlayers());
        this.posToPlayerMap = extractPlayPositionMap(players);
        this.crocodiles = new HashSet<>(appConfig.getCrocodiles());
        this.mines = new HashSet<>(appConfig.getMines());
        var snakes = appConfig.getSnakes();
        this.snakeMap = snakes.stream().collect(Collectors.toMap(
                Snake::getSource,
                Function.identity()
        ));
        var ladders = appConfig.getLadders();
        this.ladderMap = ladders.stream().collect(Collectors.toMap(
                Ladder::getSource,
                Function.identity()
        ));
    }

    private Map<Integer, Set<Player>> extractPlayPositionMap(Queue<Player> players) {
        var positionMap = new HashMap<Integer, Set<Player>>();
        for (int i = 1; i <= winPos; i++) {
            positionMap.put(i, new HashSet<>());
        }
        for (var player : players) {
            var playerList = positionMap.get(player.getCurrentPos());
            playerList.add(player);
        }
        return positionMap;
    }

    public Player simulate() {
        init();
        Player winner = null;
        if (CollectionUtils.isEmpty(players)) {
            logger.info("No Players Found in Game");
            return winner;
        }
        while (true) {
            if (players.size() < 2) {
                logger.info("Atleast 2 Players needed");
                break;
            }
            var currPlayer = players.remove();
            players.remove(currPlayer);
            var nextPosition = geNextMove(currPlayer);
            currPlayer.setCurrentPos(nextPosition);
            if (Objects.equals(nextPosition, winPos)) {
                logger.info("Winner Found {}", currPlayer.getName());
                winner = currPlayer;
                break;
            } else {
                players.add(currPlayer);
            }
        }
        logger.info("Game Over");
        return winner;
    }

    private Integer geNextMove(Player player) {
        var currPos = player.getCurrentPos();
        if (!player.canMove()) {
            logger.info("{} cannot move as he encountered Mine recently", player.getName());
            return currPos;
        }
        var stepCount = diceRoller.rollDice(player);
        var newPos = currPos + stepCount;
        if (newPos > winPos) {
            return currPos;
        }
        removePlayerFromOldPosition(currPos, player);
        var didEncounterObstacle = false;
        while (isPlayerInDanger(newPos, player)) {
            if (snakeMap.containsKey(newPos)) {
                didEncounterObstacle = true;
                var snake = snakeMap.get(newPos);
                var updatedPos = snake.getDestination();
                logger.info("{} rolled a {} and bitten by snake at {} and moved from {} to {}",
                        player.getName(), stepCount, newPos, newPos, updatedPos);
                newPos = updatedPos;
            } else if (ladderMap.containsKey(newPos)) {
                didEncounterObstacle = true;
                var ladder = ladderMap.get(newPos);
                var updatedPos = ladder.getDestination();
                logger.info("{} rolled a {} and climbed the ladder at {} and moved from {} to {}",
                        player.getName(), stepCount, newPos, newPos, updatedPos);
                newPos = updatedPos;
            } else if (crocodiles.contains(newPos)) {
                didEncounterObstacle = true;
                var updatedPos = newPos - 5;
                logger.info("{} rolled a {} and bitten the crocodile at {} and moved from {} to {}",
                        player.getName(), stepCount, newPos, newPos, updatedPos);
                newPos = updatedPos;
            } else if (mines.contains(newPos)) {
                didEncounterObstacle = true;
                player.activateMineMoveBlocker();
                logger.info("{} rolled a {} and encountered a mine at {} and did not move",
                        player.getName(), stepCount, newPos);
            }
            removeExistingPlayer(newPos, player);
        }
        if (!didEncounterObstacle) {
            logger.info("{} rolled a {} and move from {} to {}", player.getName(), stepCount, currPos, newPos);
        }
        adPlayerToNewPosition(newPos, player);
        return newPos;
    }

    private boolean isPlayerInDanger(int newPos, Player player) {
        return snakeMap.containsKey(newPos)
                || ladderMap.containsKey(newPos)
                || crocodiles.contains(newPos)
                || (mines.contains(newPos) && player.getMineTracker() != 2);
    }

    private void adPlayerToNewPosition(int pos, Player player) {
        removeExistingPlayer(pos, player);
        var playerList = posToPlayerMap.get(pos);
        playerList.add(player);
    }

    private void removePlayerFromOldPosition(int pos, Player player) {
        var playerList = posToPlayerMap.get(pos);
        playerList.remove(player);
    }

    private void removeExistingPlayer(int pos, Player player) {
        if (posToPlayerMap.containsKey(pos) && pos != 1) {
            var exitingPLayers = posToPlayerMap.get(pos);
            for (var ply : exitingPLayers) {
                if (ply.getId() != player.getId()) {
                    logger.info("{} starting again due to {} pos {}", ply.getName(), player.getName(), pos);
                    ply.startAgain();
                    var positionPlayerList = posToPlayerMap.get(ply.getCurrentPos());
                    positionPlayerList.add(player);
                }
            }
            var refreshList = new HashSet<Player>();
            refreshList.add(player);
            posToPlayerMap.put(pos, refreshList);
        }
    }
}
