package org.example;

import static com.google.inject.Scopes.SINGLETON;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import lombok.RequiredArgsConstructor;
import org.example.Enum.MovementStrategyEnum;
import org.example.configuration.AppConfig;
import org.example.simulator.DataValidator;
import org.example.simulator.DiceRoller;
import org.example.simulator.GameSimulator;
import org.example.strategy.MovementStrategy;
import org.example.strategy.impl.MaxDiceMovementStrategy;
import org.example.strategy.impl.MinDiceMovementStrategy;
import org.example.strategy.impl.SumDiceMovementStrategy;


@RequiredArgsConstructor
public class ApplicationModule extends AbstractModule {
    private final AppConfig appConfig;

    @Override
    public void configure() {
        binder().disableCircularProxies();
        binder().requireExplicitBindings();
        binder().requireExactBindingAnnotations();
        binder().requireAtInjectOnConstructors();

        binderMovementStrategy(MovementStrategyEnum.MAX, MaxDiceMovementStrategy.class);
        binderMovementStrategy(MovementStrategyEnum.MIN, MinDiceMovementStrategy.class);
        binderMovementStrategy(MovementStrategyEnum.SUM, SumDiceMovementStrategy.class);

        bind(AppConfig.class).toInstance(appConfig);
        bind(DiceRoller.class).in(SINGLETON);
        bind(GameSimulator.class).in(SINGLETON);
        bind(DataValidator.class).in(SINGLETON);
    }

    private void binderMovementStrategy(MovementStrategyEnum strategyEnum, Class<? extends MovementStrategy> klass) {
        bind(klass).in(SINGLETON);
        getMapBinder().addBinding(strategyEnum).to(klass).in(SINGLETON);
    }

    private MapBinder<MovementStrategyEnum, MovementStrategy> getMapBinder() {
        return MapBinder.newMapBinder(binder(), MovementStrategyEnum.class, MovementStrategy.class);
    }
}
