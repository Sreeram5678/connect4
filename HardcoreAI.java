// HardcoreAI class - implements advanced minimax algorithm with alpha-beta pruning
// This AI is much more challenging and looks ahead multiple moves

public class HardcoreAI implements AI {

    private static final int MAX_DEPTH = 6; // How many moves ahead to look
    private static final int WIN_SCORE = 1000000;
    private static final int LOSE_SCORE = -1000000;

    public int getBestMove(GameBoard board) {
        int bestCol = -1;
        int bestScore = Integer.MIN_VALUE;
        int cols = board.getCols();

        // Try each possible move
        for (int col = 0; col < cols; col++) {
            if (!board.isValidMove(col)) {
                continue;
            }

            // Create test board for this move
            GameBoard testBoard = new GameBoard(board);
            int row = testBoard.makeMove(col, 2); // CPU is player 2

            if (row != -1) {
                // Use minimax to evaluate this move
                int score = minimax(testBoard, MAX_DEPTH - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);

                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
        }

        return bestCol;
    }

    private int minimax(GameBoard board, int depth, boolean isMaximizing, int alpha, int beta) {
        // Check for terminal states
        int winner = checkGameEnd(board);
        if (winner == 2) { // CPU wins
            return WIN_SCORE + depth; // Prefer faster wins
        } else if (winner == 1) { // Player wins
            return LOSE_SCORE - depth; // Prefer delaying losses
        } else if (winner == 0) { // Draw
            return 0;
        }

        // Maximum depth reached, evaluate position
        if (depth == 0) {
            return evaluatePosition(board);
        }

        int player = isMaximizing ? 2 : 1; // CPU maximizes, player minimizes
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Try all possible moves
        for (int col = 0; col < board.getCols(); col++) {
            if (!board.isValidMove(col)) {
                continue;
            }

            GameBoard testBoard = new GameBoard(board);
            testBoard.makeMove(col, player);

            int score = minimax(testBoard, depth - 1, !isMaximizing, alpha, beta);

            if (isMaximizing) {
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, bestScore);
            } else {
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, bestScore);
            }

            // Alpha-beta pruning
            if (beta <= alpha) {
                break;
            }
        }

        return bestScore;
    }

    private int checkGameEnd(GameBoard board) {
        // Check if someone has won
        int[][] gameBoard = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();

        // Check horizontal, vertical, and diagonal wins
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (gameBoard[row][col] != 0) {
                    int player = gameBoard[row][col];
                    if (checkWinDirection(gameBoard, row, col, 0, 1, player, rows, cols) ||
                        checkWinDirection(gameBoard, row, col, 1, 0, player, rows, cols) ||
                        checkWinDirection(gameBoard, row, col, 1, 1, player, rows, cols) ||
                        checkWinDirection(gameBoard, row, col, 1, -1, player, rows, cols)) {
                        return player;
                    }
                }
            }
        }

        // Check for draw
        if (board.isBoardFull()) {
            return 0; // Draw
        }

        return -1; // Game not ended
    }

    private boolean checkWinDirection(int[][] board, int row, int col, int deltaRow, int deltaCol, int player, int rows, int cols) {
        int count = 1;
        // Count in positive direction
        int r = row + deltaRow;
        int c = col + deltaCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r += deltaRow;
            c += deltaCol;
        }
        // Count in negative direction
        r = row - deltaRow;
        c = col - deltaCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r -= deltaRow;
            c -= deltaCol;
        }
        return count >= 4;
    }

    private int evaluatePosition(GameBoard board) {
        int score = 0;
        int[][] gameBoard = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();

        // Evaluate center column preference (CPU pieces)
        int centerCol = cols / 2;
        for (int row = 0; row < rows; row++) {
            if (gameBoard[row][centerCol] == 2) {
                score += 30; // CPU center control
            } else if (gameBoard[row][centerCol] == 1) {
                score -= 30; // Player center control
            }
        }

        // Evaluate all possible windows (4 consecutive positions)
        score += evaluateWindows(gameBoard, rows, cols, 2); // CPU windows
        score -= evaluateWindows(gameBoard, rows, cols, 1); // Player windows (negative)

        // Evaluate threats and blocking
        score += evaluateThreats(board, 2); // CPU threats
        score -= evaluateThreats(board, 1) * 2; // Player threats (more important to block)

        return score;
    }

    private int evaluateWindows(int[][] board, int rows, int cols, int player) {
        int score = 0;

        // Horizontal windows
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols - 3; col++) {
                int window[] = {board[row][col], board[row][col+1], board[row][col+2], board[row][col+3]};
                score += evaluateWindow(window, player);
            }
        }

        // Vertical windows
        for (int row = 0; row < rows - 3; row++) {
            for (int col = 0; col < cols; col++) {
                int window[] = {board[row][col], board[row+1][col], board[row+2][col], board[row+3][col]};
                score += evaluateWindow(window, player);
            }
        }

        // Diagonal windows (positive slope)
        for (int row = 0; row < rows - 3; row++) {
            for (int col = 0; col < cols - 3; col++) {
                int window[] = {board[row][col], board[row+1][col+1], board[row+2][col+2], board[row+3][col+3]};
                score += evaluateWindow(window, player);
            }
        }

        // Diagonal windows (negative slope)
        for (int row = 0; row < rows - 3; row++) {
            for (int col = 3; col < cols; col++) {
                int window[] = {board[row][col], board[row+1][col-1], board[row+2][col-2], board[row+3][col-3]};
                score += evaluateWindow(window, player);
            }
        }

        return score;
    }

    private int evaluateWindow(int[] window, int player) {
        int score = 0;
        int playerCount = 0;
        int opponentCount = 0;
        int emptyCount = 0;

        for (int piece : window) {
            if (piece == player) {
                playerCount++;
            } else if (piece == 0) {
                emptyCount++;
            } else {
                opponentCount++;
            }
        }

        if (playerCount == 4) {
            score += 1000; // Win
        } else if (playerCount == 3 && emptyCount == 1) {
            score += 100; // Three in a row with space
        } else if (playerCount == 2 && emptyCount == 2) {
            score += 20; // Two in a row with spaces
        }

        // Bonus for center positions
        if (window.length >= 3 && window[1] == player && window[2] == player) {
            score += 10;
        }

        return score;
    }

    private int evaluateThreats(GameBoard board, int player) {
        int threatScore = 0;

        // Check for immediate winning moves
        for (int col = 0; col < board.getCols(); col++) {
            if (board.isValidMove(col)) {
                GameBoard testBoard = new GameBoard(board);
                int row = testBoard.makeMove(col, player);
                if (row != -1 && testBoard.checkWin(row, col, player)) {
                    threatScore += 1000; // Immediate win threat
                }
            }
        }

        // Evaluate potential threats (3 in a row)
        int[][] gameBoard = board.getBoard();
        threatScore += countThreeInRow(gameBoard, board.getRows(), board.getCols(), player) * 50;

        return threatScore;
    }

    private int countThreeInRow(int[][] board, int rows, int cols, int player) {
        int count = 0;

        // Check all directions for 3 in a row
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (board[row][col] == player) {
                    // Horizontal
                    if (col + 2 < cols &&
                        board[row][col+1] == player &&
                        board[row][col+2] == player) {
                        count++;
                    }
                    // Vertical
                    if (row + 2 < rows &&
                        board[row+1][col] == player &&
                        board[row+2][col] == player) {
                        count++;
                    }
                    // Diagonal \
                    if (row + 2 < rows && col + 2 < cols &&
                        board[row+1][col+1] == player &&
                        board[row+2][col+2] == player) {
                        count++;
                    }
                    // Diagonal /
                    if (row + 2 < rows && col - 2 >= 0 &&
                        board[row+1][col-1] == player &&
                        board[row+2][col-2] == player) {
                        count++;
                    }
                }
            }
        }

        return count;
    }
}
