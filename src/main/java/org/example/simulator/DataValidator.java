package org.example.simulator;

import com.google.inject.Inject;
import lombok.NoArgsConstructor;
import org.example.Enum.SkipperType;
import org.example.configuration.AppConfig;
import org.example.exceptions.InvalidGameConfigException;

@NoArgsConstructor(onConstructor = @__(@Inject))
public class DataValidator {
    private static final String ERROR_MSG = "The InputData is invalid for %s %d %d";
    public void validateGameConfig(AppConfig appConfig) throws InvalidGameConfigException {
        var cellSkippers = appConfig.getEntities();
        var boardSize = appConfig.getBoardSize();
        for (var cellSkipperEntity : cellSkippers) {
            var type = cellSkipperEntity.getType();
            var cellSkipperEnum = SkipperType.fromString(type);
            var dataValidator = cellSkipperEnum.getValidator();
            if (!dataValidator.apply(cellSkipperEntity, boardSize)) {
                throw new InvalidGameConfigException(String.format(ERROR_MSG, type,
                        cellSkipperEntity.getSource(),
                        cellSkipperEntity.getDestination()));
            }
        }
    }
}
