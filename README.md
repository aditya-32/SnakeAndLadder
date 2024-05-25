# Snake And Ladder


Snake And Ladder Application performs the Game using Random dice movements
for Given Number of Players until someone wins

**PROJECT SETUP**

To install correct version of maven and jdk execute below command 
in root folder

```shell
sk env install
```


**_Rules_** **_for the Game_**:
1. Snake always takes you to the cell where its tail is, and has to be a number less than where you are at currently. 
2. Ladder takes you up (strictly).
3. Crocodile, which takes you exactly 5 steps back.
4. Mine which holds you for 2 turns.
3. If a player (A) comes to a cell where another player (B) is placed already, the previously placed player (B) has to start again from 1. 


For Input Format please refer to ```application.yaml``` 

Predefined Test cases are provided in class```GameSimulatorTest.class```

For Custom Use case please feel free to udpate ```application-test.yaml```
file to test as per need.


**_Note:_**
```diceRolls``` field in ```AppConfig.class``` is provided to manually 
make the player execute predefined dice rolls. Once the ```diceRolls``` list for a 
particular player is exhausted then the dice roll algorithm falls back to its
default strategy type mentioned in ```.yaml``` file (```MAX```, ```MIN```, ```SUM```)
