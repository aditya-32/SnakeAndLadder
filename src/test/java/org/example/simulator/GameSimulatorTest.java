package org.example.simulator;

import com.google.inject.Guice;
import org.example.ApplicationModule;
import org.example.Enum.SkipperType;
import org.example.configuration.AppConfig;
import org.example.configuration.YamlParser;
import org.example.exceptions.InvalidGameConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameSimulatorTest {
    private static final String FILE_NAME = "application-test.yaml";

    private GameSimulator simulator;
    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        appConfig = YamlParser.parse(FILE_NAME);
    }

    @Test
    void testGameDataValidity_SnakeAndLadderSameStartPoint() {
        var snakeOpt = appConfig.getEntities().stream()
                .filter(entity -> SkipperType.SNAKE.getName().equals(entity.getType()))
                .findFirst();
        var ladderOpt = appConfig.getEntities().stream()
                .filter(entity -> SkipperType.LADDER.getName().equals(entity.getType()))
                .findFirst();
        if (snakeOpt.isPresent() && ladderOpt.isPresent()) {
            ladderOpt.get().setSource(snakeOpt.get().getSource());
        }
        var injector = Guice.createInjector(new ApplicationModule(appConfig));
        simulator = injector.getInstance(GameSimulator.class);
        Assertions.assertThrows(InvalidGameConfigException.class, () -> simulator.simulate(),
                "Snake and ladder cannot have same start point");
    }

    @Test
    void testGameDataValidity_SnakeValidity() {
        var snakeOpt = appConfig.getEntities().stream()
                .filter(entity -> SkipperType.SNAKE.getName().equals(entity.getType()))
                .findFirst();
        snakeOpt.ifPresent(value -> value.setDestination(value.getSource() + 1));
        var injector = Guice.createInjector(new ApplicationModule(appConfig));
        simulator = injector.getInstance(GameSimulator.class);
        Assertions.assertThrows(InvalidGameConfigException.class, () -> simulator.simulate(),
                "Snake can only take you to a cell lower than current one");
    }

    @Test
    void testGameDataValidity_LadderValidity() {
        var ladderOpt = appConfig.getEntities().stream()
                .filter(entity -> SkipperType.LADDER.getName().equals(entity.getType()))
                .findFirst();
        ladderOpt.ifPresent(value -> value.setSource(value.getDestination() + 1));
        var injector = Guice.createInjector(new ApplicationModule(appConfig));
        simulator = injector.getInstance(GameSimulator.class);
        Assertions.assertThrows(InvalidGameConfigException.class, () -> simulator.simulate(),
                "Ladder can only take you to a cell greater than current one");
    }


    @Test
    void testPlayerWithStaringPoint() {
        var injector = Guice.createInjector(new ApplicationModule(appConfig));
        simulator = injector.getInstance(GameSimulator.class);
        Assertions.assertDoesNotThrow(() -> simulator.simulate());
    }

    @Test
    void testPlayerWithPreDefinedDiceRolls() {
        for (var player : appConfig.getPlayers()) {
            player.setCurrentPos(1);
        }
        var injector = Guice.createInjector(new ApplicationModule(appConfig));
        simulator = injector.getInstance(GameSimulator.class);
        var winner = simulator.simulate();
        Assertions.assertEquals(winner.getName(), "Gaurav");
    }


    @Test
    void testPlayerWithCrocodileEncounter() {
        for (var player : appConfig.getPlayers()) {
            player.setCurrentPos(1);
        }
        var injector = Guice.createInjector(new ApplicationModule(appConfig));
        simulator = injector.getInstance(GameSimulator.class);
        var winner = simulator.simulate();
    }

    /**
     *  testing mine encounter for Sagar as in the diceRolls manual dice rolls lead him to mine
     */
    @Test
    void testPlayerWithMineEncounter() {
        for (var player : appConfig.getPlayers()) {
            player.setCurrentPos(1);
        }
        var player = appConfig.getPlayers().get(1);
        var mockerPLayer = Mockito.spy(player);
        appConfig.getPlayers().remove(player);
        appConfig.getPlayers().add(mockerPLayer);
        var injector = Guice.createInjector(new ApplicationModule(appConfig));
        simulator = injector.getInstance(GameSimulator.class);
        simulator.simulate();
        Mockito.verify(mockerPLayer).activateMineMoveBlocker();
    }
}