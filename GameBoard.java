// GameBoard class - handles game logic and graph representation
// Each game state is stored as a node in a graph structure

import java.util.ArrayList;

public class GameBoard {
    private int rows;
    private int cols;
    private int[][] board;
    private ArrayList<GameState> stateGraph; // Graph representation of game states
    
    public GameBoard() {
        this.rows = 6;
        this.cols = 7;
        board = new int[rows][cols];
        stateGraph = new ArrayList<GameState>();
        // Initialize graph with starting state
        int[][] startBoard = copyBoard();
        GameState startState = new GameState(startBoard, 0, -1);
        stateGraph.add(startState);
    }
    
    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        board = new int[rows][cols];
        stateGraph = new ArrayList<GameState>();
        // Initialize graph with starting state
        int[][] startBoard = copyBoard();
        GameState startState = new GameState(startBoard, 0, -1);
        stateGraph.add(startState);
    }
    
    // Copy constructor for creating board copies
    public GameBoard(GameBoard other) {
        this.rows = other.rows;
        this.cols = other.cols;
        this.board = copyBoard(other.board);
        this.stateGraph = new ArrayList<GameState>();
        // Copy all states
        for (int i = 0; i < other.stateGraph.size(); i++) {
            stateGraph.add(other.stateGraph.get(i));
        }
    }
    
    public int makeMove(int col, int player) {
        if (!isValidMove(col)) {
            return -1;
        }
        
        // Find the first empty row in the column
        int row = -1;
        for (int r = rows - 1; r >= 0; r--) {
            if (board[r][col] == 0) {
                row = r;
                break;
            }
        }
        
        if (row == -1) {
            return -1;
        }
        
        board[row][col] = player;
        
        // Add new state to graph
        int[][] newBoard = copyBoard();
        GameState newState = new GameState(newBoard, player, col);
        stateGraph.add(newState);
        
        // Connect to previous state (graph representation)
        if (stateGraph.size() > 1) {
            int prevIndex = stateGraph.size() - 2;
            GameState prevState = stateGraph.get(prevIndex);
            prevState.addNextState(newState);
        }
        
        return row;
    }
    
    public boolean isValidMove(int col) {
        if (col < 0 || col >= cols) {
            return false;
        }
        return board[0][col] == 0;
    }
    
    public boolean checkWin(int row, int col, int player) {
        // Check horizontal
        if (checkDirection(row, col, 0, 1, player) >= 4) {
            return true;
        }
        // Check vertical
        if (checkDirection(row, col, 1, 0, player) >= 4) {
            return true;
        }
        // Check diagonal (top-left to bottom-right)
        if (checkDirection(row, col, 1, 1, player) >= 4) {
            return true;
        }
        // Check diagonal (top-right to bottom-left)
        if (checkDirection(row, col, 1, -1, player) >= 4) {
            return true;
        }
        
        return false;
    }
    
    private int checkDirection(int row, int col, int deltaRow, int deltaCol, int player) {
        int count = 1;
        
        // Check in positive direction
        int r = row + deltaRow;
        int c = col + deltaCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r = r + deltaRow;
            c = c + deltaCol;
        }
        
        // Check in negative direction
        r = row - deltaRow;
        c = col - deltaCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r = r - deltaRow;
            c = c - deltaCol;
        }
        
        return count;
    }
    
    public boolean isBoardFull() {
        for (int col = 0; col < cols; col++) {
            if (board[0][col] == 0) {
                return false;
            }
        }
        return true;
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    public int[][] copyBoard() {
        return copyBoard(this.board);
    }
    
    private int[][] copyBoard(int[][] source) {
        int[][] copy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copy[i][j] = source[i][j];
            }
        }
        return copy;
    }
    
    public ArrayList<GameState> getStateGraph() {
        return stateGraph;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
}
