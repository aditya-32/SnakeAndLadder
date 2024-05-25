package org.example.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class Player {
    private UUID id = UUID.randomUUID();
    private String name;
    private Integer currentPos;
    private Integer mineTracker;

    public Player() {
        this.mineTracker = 0;
    }

    public void startAgain(){
        this.currentPos = 1;
    }

    public void activateMineMoveBlocker () {
        this.mineTracker = 2;
    }

    public boolean canMove() {
        if (mineTracker > 0) {
            mineTracker--;
            return false;
        }
        return true;
    }
}
