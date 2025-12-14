// GameState class - represents a node in the game state graph
// This is used for graph representation of the game

import java.util.ArrayList;

public class GameState {
    private int[][] state;
    private int player;
    private int moveCol;
    private ArrayList<GameState> nextStates; // List of next possible states
    
    public GameState(int[][] state, int player, int moveCol) {
        this.state = state;
        this.player = player;
        this.moveCol = moveCol;
        this.nextStates = new ArrayList<GameState>();
    }
    
    public void addNextState(GameState next) {
        nextStates.add(next);
    }
    
    public ArrayList<GameState> getNextStates() {
        return nextStates;
    }
    
    public int[][] getState() {
        return state;
    }
    
    public int getPlayer() {
        return player;
    }
    
    public int getMoveCol() {
        return moveCol;
    }
}

