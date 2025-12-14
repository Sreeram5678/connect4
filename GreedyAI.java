// GreedyAI class - implements greedy algorithm for CPU moves
// Greedy algorithm always picks the move that gives immediate best score

public class GreedyAI {
    
    public int getBestMove(GameBoard board) {
        int cols = board.getCols();
        int bestCol = -1;
        int bestScore = -999999;

        // Priority 1: Block player when they can win immediately
        for (int col = 0; col < cols; col++) {
            if (!board.isValidMove(col)) {
                continue;
            }
            if (checkBlockPlayer(board, col)) {
                return col;
            }
        }

        // Priority 2: CPU tries to win greedily - check if CPU can win immediately
        for (int col = 0; col < cols; col++) {
            if (!board.isValidMove(col)) {
                continue;
            }
            GameBoard testBoard = new GameBoard(board);
            int row = testBoard.makeMove(col, 2);
            if (row != -1 && testBoard.checkWin(row, col, 2)) {
                return col;
            }
        }

        // Priority 3: Evaluate greedy offensive moves - CPU tries to create winning positions
        for (int col = 0; col < cols; col++) {
            if (!board.isValidMove(col)) {
                continue;
            }

            GameBoard testBoard = new GameBoard(board);
            int row = testBoard.makeMove(col, 2);

            if (row == -1) {
                continue;
            }

            int score = evaluateMove(testBoard, row, col);

            // Bonus for creating threats (3 in a row)
            if (hasThreeInRow(testBoard, row, col, 2)) {
                score = score + 150;
            }

            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }
        }

        // Fallback: pick first valid column
        if (bestCol == -1) {
            for (int col = 0; col < cols; col++) {
                if (board.isValidMove(col)) {
                    return col;
                }
            }
        }

        return bestCol;
    }
    
    private int findTwoInRowThreat(GameBoard board) {
        // Find if player has 2 in a row that could become 3 or 4
        int rows = board.getRows();
        int cols = board.getCols();
        int[][] gameBoard = board.getBoard();
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (gameBoard[row][col] == 1) {
                    // Check horizontal 2-in-a-row
                    int blockCol = checkHorizontalTwoThreat(board, row, col);
                    if (blockCol != -1) {
                        return blockCol;
                    }
                    // Check vertical 2-in-a-row
                    blockCol = checkVerticalTwoThreat(board, row, col);
                    if (blockCol != -1) {
                        return blockCol;
                    }
                    // Check diagonal 2-in-a-row
                    blockCol = checkDiagonalTwoThreat(board, row, col);
                    if (blockCol != -1) {
                        return blockCol;
                    }
                }
            }
        }
        
        return -1;
    }
    
    private int checkHorizontalTwoThreat(GameBoard board, int row, int col) {
        int[][] gameBoard = board.getBoard();
        int cols = board.getCols();
        
        // Check if player has 2 consecutive pieces horizontally
        if (col + 1 < cols && gameBoard[row][col] == 1 && gameBoard[row][col + 1] == 1) {
            // Block the right side (more dangerous)
            if (col + 2 < cols && gameBoard[row][col + 2] == 0 && board.isValidMove(col + 2)) {
                return col + 2;
            }
            // Block the left side
            if (col - 1 >= 0 && gameBoard[row][col - 1] == 0 && board.isValidMove(col - 1)) {
                return col - 1;
            }
        }
        
        // Check if player has 2 pieces with gap (X _ X)
        if (col + 2 < cols && gameBoard[row][col] == 1 && gameBoard[row][col + 2] == 1 && 
            gameBoard[row][col + 1] == 0 && board.isValidMove(col + 1)) {
            return col + 1;
        }
        
        return -1;
    }
    
    private int checkVerticalTwoThreat(GameBoard board, int row, int col) {
        int[][] gameBoard = board.getBoard();
        int rows = board.getRows();
        
        // Check if player has 2 consecutive pieces vertically
        if (row + 1 < rows && gameBoard[row][col] == 1 && gameBoard[row + 1][col] == 1) {
            // Block the column
            if (board.isValidMove(col)) {
                return col;
            }
        }
        
        return -1;
    }
    
    private int checkDiagonalTwoThreat(GameBoard board, int row, int col) {
        int[][] gameBoard = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        // Check diagonal top-left to bottom-right
        if (row + 1 < rows && col + 1 < cols && 
            gameBoard[row][col] == 1 && gameBoard[row + 1][col + 1] == 1) {
            // Block the next position
            if (row + 2 < rows && col + 2 < cols && gameBoard[row + 2][col + 2] == 0 && 
                board.isValidMove(col + 2)) {
                return col + 2;
            }
            if (row - 1 >= 0 && col - 1 >= 0 && gameBoard[row - 1][col - 1] == 0 && 
                board.isValidMove(col - 1)) {
                return col - 1;
            }
        }
        
        // Check diagonal top-right to bottom-left
        if (row + 1 < rows && col - 1 >= 0 && 
            gameBoard[row][col] == 1 && gameBoard[row + 1][col - 1] == 1) {
            // Block the next position
            if (row + 2 < rows && col - 2 >= 0 && gameBoard[row + 2][col - 2] == 0 && 
                board.isValidMove(col - 2)) {
                return col - 2;
            }
            if (row - 1 >= 0 && col + 1 < cols && gameBoard[row - 1][col + 1] == 0 && 
                board.isValidMove(col + 1)) {
                return col + 1;
            }
        }
        
        return -1;
    }
    
    private int findPlayerThreat(GameBoard board) {
        // Find if player has 3 in a row that could become 4
        int rows = board.getRows();
        int cols = board.getCols();
        int[][] gameBoard = board.getBoard();
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (gameBoard[row][col] == 1) {
                    // Check horizontal threat
                    int blockCol = checkHorizontalThreat(board, row, col);
                    if (blockCol != -1) {
                        return blockCol;
                    }
                    // Check vertical threat
                    blockCol = checkVerticalThreat(board, row, col);
                    if (blockCol != -1) {
                        return blockCol;
                    }
                    // Check diagonal threats
                    blockCol = checkDiagonalThreat(board, row, col);
                    if (blockCol != -1) {
                        return blockCol;
                    }
                }
            }
        }
        
        return -1;
    }
    
    private int checkHorizontalThreat(GameBoard board, int row, int col) {
        int[][] gameBoard = board.getBoard();
        int cols = board.getCols();
        int playerCount = 1;
        
        // Count player pieces to the right
        for (int c = col + 1; c < cols && c < col + 4 && gameBoard[row][c] == 1; c++) {
            playerCount++;
        }
        
        // Count player pieces to the left
        for (int c = col - 1; c >= 0 && c > col - 4 && gameBoard[row][c] == 1; c--) {
            playerCount++;
        }
        
        // If player has 3 in a row horizontally
        if (playerCount >= 3) {
            // Check right side first (columns 3,4,5 -> block 6)
            if (col + 3 < cols && gameBoard[row][col + 1] == 1 && gameBoard[row][col + 2] == 1) {
                int blockCol = col + 3;
                if (board.isValidMove(blockCol) && gameBoard[row][blockCol] == 0) {
                    return blockCol;
                }
            }
            // Check left side (columns 3,4,5 -> block 2)
            if (col - 3 >= 0 && gameBoard[row][col - 1] == 1 && gameBoard[row][col - 2] == 1) {
                int blockCol = col - 3;
                if (board.isValidMove(blockCol) && gameBoard[row][blockCol] == 0) {
                    return blockCol;
                }
            }
            // Check middle positions (like columns 2,3,4 -> block 1 or 5)
            if (col + 2 < cols && col - 1 >= 0 && 
                gameBoard[row][col + 1] == 1 && gameBoard[row][col - 1] == 1) {
                // Prioritize blocking right side (more dangerous)
                if (col + 2 < cols && gameBoard[row][col + 2] == 0 && board.isValidMove(col + 2)) {
                    return col + 2;
                }
                // Block left side if right is not available
                if (col - 2 >= 0 && gameBoard[row][col - 2] == 0 && board.isValidMove(col - 2)) {
                    return col - 2;
                }
            }
            // Check pattern like columns 3,4,5 (consecutive)
            if (col + 2 < cols && gameBoard[row][col] == 1 && 
                gameBoard[row][col + 1] == 1 && gameBoard[row][col + 2] == 1) {
                // Block right side first (column 6)
                if (col + 3 < cols && gameBoard[row][col + 3] == 0 && board.isValidMove(col + 3)) {
                    return col + 3;
                }
                // Block left side (column 2)
                if (col - 1 >= 0 && gameBoard[row][col - 1] == 0 && board.isValidMove(col - 1)) {
                    return col - 1;
                }
            }
        }
        
        // Check for 2 pieces with gap (like: X X _ X)
        if (col + 3 < cols) {
            if (gameBoard[row][col] == 1 && gameBoard[row][col + 1] == 1 && 
                gameBoard[row][col + 2] == 0 && gameBoard[row][col + 3] == 1) {
                if (board.isValidMove(col + 2)) {
                    return col + 2;
                }
            }
        }
        if (col - 3 >= 0) {
            if (gameBoard[row][col] == 1 && gameBoard[row][col - 1] == 1 && 
                gameBoard[row][col - 2] == 0 && gameBoard[row][col - 3] == 1) {
                if (board.isValidMove(col - 2)) {
                    return col - 2;
                }
            }
        }
        
        return -1;
    }
    
    private int checkVerticalThreat(GameBoard board, int row, int col) {
        int[][] gameBoard = board.getBoard();
        int rows = board.getRows();
        int playerCount = 1;
        
        // Count player pieces below
        for (int r = row + 1; r < rows && r < row + 4 && gameBoard[r][col] == 1; r++) {
            playerCount++;
        }
        
        // Count player pieces above
        for (int r = row - 1; r >= 0 && r > row - 4 && gameBoard[r][col] == 1; r--) {
            playerCount++;
        }
        
        // If player has 3 vertically, block the column
        if (playerCount >= 3) {
            if (board.isValidMove(col)) {
                return col;
            }
        }
        
        return -1;
    }
    
    private int checkDiagonalThreat(GameBoard board, int row, int col) {
        int[][] gameBoard = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        // Check diagonal top-left to bottom-right
        int count1 = 1;
        for (int i = 1; i < 4; i++) {
            int r = row + i;
            int c = col + i;
            if (r < rows && c < cols && gameBoard[r][c] == 1) {
                count1++;
            } else {
                break;
            }
        }
        for (int i = 1; i < 4; i++) {
            int r = row - i;
            int c = col - i;
            if (r >= 0 && c >= 0 && gameBoard[r][c] == 1) {
                count1++;
            } else {
                break;
            }
        }
        
        if (count1 >= 3) {
            // Find blocking position - prioritize right side
            for (int i = 1; i <= 3; i++) {
                int r = row + i;
                int c = col + i;
                if (r >= 0 && r < rows && c >= 0 && c < cols && gameBoard[r][c] == 0) {
                    if (board.isValidMove(c)) {
                        return c;
                    }
                }
            }
            for (int i = 1; i <= 3; i++) {
                int r = row - i;
                int c = col - i;
                if (r >= 0 && r < rows && c >= 0 && c < cols && gameBoard[r][c] == 0) {
                    if (board.isValidMove(c)) {
                        return c;
                    }
                }
            }
        }
        
        // Check diagonal top-right to bottom-left
        int count2 = 1;
        for (int i = 1; i < 4; i++) {
            int r = row + i;
            int c = col - i;
            if (r < rows && c >= 0 && gameBoard[r][c] == 1) {
                count2++;
            } else {
                break;
            }
        }
        for (int i = 1; i < 4; i++) {
            int r = row - i;
            int c = col + i;
            if (r >= 0 && c < cols && gameBoard[r][c] == 1) {
                count2++;
            } else {
                break;
            }
        }
        
        if (count2 >= 3) {
            // Find blocking position - prioritize right side
            for (int i = 1; i <= 3; i++) {
                int r = row + i;
                int c = col - i;
                if (r >= 0 && r < rows && c >= 0 && c < cols && gameBoard[r][c] == 0) {
                    if (board.isValidMove(c)) {
                        return c;
                    }
                }
            }
            for (int i = 1; i <= 3; i++) {
                int r = row - i;
                int c = col + i;
                if (r >= 0 && r < rows && c >= 0 && c < cols && gameBoard[r][c] == 0) {
                    if (board.isValidMove(c)) {
                        return c;
                    }
                }
            }
        }
        
        return -1;
    }
    
    private boolean hasThreeInRow(GameBoard board, int row, int col, int player) {
        int[][] gameBoard = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        
        // Check all directions
        if (countConsecutive(gameBoard, row, col, 0, 1, player, rows, cols) >= 3 ||
            countConsecutive(gameBoard, row, col, 1, 0, player, rows, cols) >= 3 ||
            countConsecutive(gameBoard, row, col, 1, 1, player, rows, cols) >= 3 ||
            countConsecutive(gameBoard, row, col, 1, -1, player, rows, cols) >= 3) {
            return true;
        }
        
        return false;
    }
    
    private int countConsecutive(int[][] board, int row, int col, int deltaRow, int deltaCol, int player, int rows, int cols) {
        int count = 1;
        
        // Count in positive direction
        int r = row + deltaRow;
        int c = col + deltaCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r = r + deltaRow;
            c = c + deltaCol;
        }
        
        // Count in negative direction
        r = row - deltaRow;
        c = col - deltaCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r = r - deltaRow;
            c = c - deltaCol;
        }
        
        return count;
    }
    
    private int evaluateMove(GameBoard board, int row, int col) {
        int score = 0;
        int[][] gameBoard = board.getBoard();
        
        // Evaluate all directions from the placed piece
        score = score + evaluateDirection(gameBoard, row, col, 0, 1, 2, board.getRows(), board.getCols());
        score = score + evaluateDirection(gameBoard, row, col, 1, 0, 2, board.getRows(), board.getCols());
        score = score + evaluateDirection(gameBoard, row, col, 1, 1, 2, board.getRows(), board.getCols());
        score = score + evaluateDirection(gameBoard, row, col, 1, -1, 2, board.getRows(), board.getCols());
        
        return score;
    }
    
    private int evaluateDirection(int[][] board, int row, int col, int deltaRow, int deltaCol, int player, int rows, int cols) {
        int score = 0;
        int count = 1;
        
        // Count consecutive pieces in positive direction
        int r = row + deltaRow;
        int c = col + deltaCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r = r + deltaRow;
            c = c + deltaCol;
        }
        
        // Count consecutive pieces in negative direction
        r = row - deltaRow;
        c = col - deltaCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r = r - deltaRow;
            c = c - deltaCol;
        }
        
        // Score based on consecutive pieces
        if (count >= 4) {
            score = score + 1000;
        } else if (count == 3) {
            score = score + 100;
        } else if (count == 2) {
            score = score + 20;
        }
        
        return score;
    }
    
    private boolean checkBlockPlayer(GameBoard board, int col) {
        // Check if player would win immediately in this column
        GameBoard testBoard = new GameBoard(board);
        int row = testBoard.makeMove(col, 1);
        
        if (row != -1 && testBoard.checkWin(row, col, 1)) {
            return true;
        }
        
        return false;
    }
}
