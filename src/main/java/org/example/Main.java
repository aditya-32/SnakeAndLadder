package org.example;

import com.google.inject.Guice;
import org.example.configuration.YamlParser;
import org.example.simulator.GameSimulator;

public class Main {
    public static void main(String[] args) {
        var appConfig = YamlParser.parse("application.yaml");
        var injector = Guice.createInjector(new ApplicationModule(appConfig));
        var gameSimulator = injector.getInstance(GameSimulator.class);
        gameSimulator.simulate();
    }
}