package org.example.simulator;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.example.cellStrategy.CellSkipperHandler;
import org.example.configuration.AppConfig;
import org.example.entity.Player;
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

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class GameSimulator {
    private static final Logger logger = LoggerFactory.getLogger(GameSimulator.class);

    private final AppConfig appConfig;
    private final DiceRoller diceRoller;
    private final DataValidator validator;
    private final CellSkipperHandler cellSkipperHandler;

    private Integer winPos;
    private Integer startPos;
    private Queue<Player> players = new LinkedList<>();
    private Map<Integer, Set<Player>> posToPlayerMap = new HashMap<>();

    private void init() throws InvalidGameConfigException {
        validator.validateGameConfig(appConfig);
        Integer boardSize = appConfig.getBoardSize();
        this.winPos = boardSize * boardSize;
        this.startPos = 1;
        this.players = new LinkedList<>(appConfig.getPlayers());
        this.posToPlayerMap = extractPlayPositionMap(players);
    }

    private Map<Integer, Set<Player>> extractPlayPositionMap(Queue<Player> players) {
        var positionMap = new HashMap<Integer, Set<Player>>();
        for (int i = startPos; i <= winPos; i++) {
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
        newPos = cellSkipperHandler.getNext(player, newPos);
        removeExistingPlayer(newPos, player);
        if (!didEncounterObstacle) {
            logger.info("{} rolled a {} and move from {} to {}", player.getName(), stepCount, currPos, newPos);
        }
        adPlayerToNewPosition(newPos, player);
        return newPos;
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
        if (posToPlayerMap.containsKey(pos) && pos != startPos) {
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
