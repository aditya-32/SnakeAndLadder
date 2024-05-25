package org.example.entity;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Value
@Builder
public class Dice {
    @Builder.Default
    String id = UUID.randomUUID().toString();
    Integer size;

    public Integer rollDice() {
        return ThreadLocalRandom.current().nextInt(1, size+1);
    }
}
