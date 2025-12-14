// AI interface for Connect 4 AI implementations
public interface AI {
    /**
     * Returns the best column to play for the CPU
     * @param board The current game board
     * @return The column number (0-indexed) to play in
     */
    int getBestMove(GameBoard board);
}
