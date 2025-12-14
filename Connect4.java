// Connect 4 Game - Main GUI Class
// This class handles the user interface and game flow

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Connect4 extends JFrame {
    private int rows = 6;
    private int cols = 7;
    private GameBoard board;
    private JButton[] columnButtons;
    private JPanel gamePanel;
    private JLabel statusLabel;
    private JPanel statusPanel;
    private boolean isPlayerTurn;
    private AI cpu;
    private JFrame settingsFrame;
    private String difficulty = "Easy"; // "Easy" or "Hardcore"
    
    // Score tracking
    private int playerWins = 0;
    private int cpuWins = 0;
    private int draws = 0;
    private JLabel playerWinsLabel;
    private JLabel cpuWinsLabel;
    private JLabel drawsLabel;
    
    public Connect4() {
        showSettingsDialog();
    }
    
    private void showSettingsDialog() {
        settingsFrame = new JFrame("Game Settings");
        settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settingsFrame.setLayout(new BorderLayout());
        settingsFrame.setSize(400, 250);
        settingsFrame.setLocationRelativeTo(null);
        
        // Title
        JLabel titleLabel = new JLabel("Connect 4 - Game Settings", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 100, 200));
        settingsFrame.add(titleLabel, BorderLayout.NORTH);
        
        // Settings panel
        JPanel settingsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        settingsPanel.setBackground(new Color(240, 240, 240));

        JLabel rowsLabel = new JLabel("Number of Rows:");
        rowsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(6, 4, 10, 1));
        rowsSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel colsLabel = new JLabel("Number of Columns:");
        colsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(7, 4, 10, 1));
        colsSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JComboBox<String> difficultyCombo = new JComboBox<>(new String[]{"Easy", "Hardcore"});
        difficultyCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        difficultyCombo.setSelectedItem(difficulty);

        settingsPanel.add(rowsLabel);
        settingsPanel.add(rowsSpinner);
        settingsPanel.add(colsLabel);
        settingsPanel.add(colsSpinner);
        settingsPanel.add(difficultyLabel);
        settingsPanel.add(difficultyCombo);
        
        // Start button
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(new Color(50, 150, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setPreferredSize(new Dimension(200, 40));
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rows = (Integer) rowsSpinner.getValue();
                cols = (Integer) colsSpinner.getValue();
                difficulty = (String) difficultyCombo.getSelectedItem();
                settingsFrame.dispose();
                initializeGame();
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(startButton);
        
        settingsFrame.add(settingsPanel, BorderLayout.CENTER);
        settingsFrame.add(buttonPanel, BorderLayout.SOUTH);
        settingsFrame.setVisible(true);
    }
    
    private void initializeGame() {
        board = new GameBoard(rows, cols);
        if ("Easy".equals(difficulty)) {
            cpu = new GreedyAI();
        } else if ("Hardcore".equals(difficulty)) {
            cpu = new HardcoreAI();
        }
        isPlayerTurn = true;
        
        setTitle("Connect 4 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 248, 248));
        
        // Scoreboard panel - rounded boxes
        JPanel scoreboardPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        scoreboardPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        scoreboardPanel.setBackground(new Color(248, 248, 248));
        
        // Your Wins box - rounded with red gradient
        RoundedPanel playerWinsPanel = new RoundedPanel(new BorderLayout(), 15);
        playerWinsPanel.setBackground(new Color(255, 245, 245));
        playerWinsPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        JLabel playerWinsTitle = new JLabel("Your Wins", JLabel.CENTER);
        playerWinsTitle.setFont(new Font("Arial", Font.BOLD, 13));
        playerWinsTitle.setForeground(new Color(180, 0, 0));
        playerWinsLabel = new JLabel("0", JLabel.CENTER);
        playerWinsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        playerWinsLabel.setForeground(Color.RED);
        playerWinsPanel.add(playerWinsTitle, BorderLayout.NORTH);
        playerWinsPanel.add(playerWinsLabel, BorderLayout.CENTER);
        
        // CPU Wins box - rounded with yellow/orange gradient
        RoundedPanel cpuWinsPanel = new RoundedPanel(new BorderLayout(), 15);
        cpuWinsPanel.setBackground(new Color(255, 250, 240));
        cpuWinsPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        JLabel cpuWinsTitle = new JLabel("CPU Wins", JLabel.CENTER);
        cpuWinsTitle.setFont(new Font("Arial", Font.BOLD, 13));
        cpuWinsTitle.setForeground(new Color(180, 120, 0));
        cpuWinsLabel = new JLabel("0", JLabel.CENTER);
        cpuWinsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        cpuWinsLabel.setForeground(new Color(255, 165, 0));
        cpuWinsPanel.add(cpuWinsTitle, BorderLayout.NORTH);
        cpuWinsPanel.add(cpuWinsLabel, BorderLayout.CENTER);
        
        // Draws box - rounded grey
        RoundedPanel drawsPanel = new RoundedPanel(new BorderLayout(), 15);
        drawsPanel.setBackground(new Color(245, 245, 245));
        drawsPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        JLabel drawsTitle = new JLabel("Draws", JLabel.CENTER);
        drawsTitle.setFont(new Font("Arial", Font.BOLD, 13));
        drawsTitle.setForeground(new Color(100, 100, 100));
        drawsLabel = new JLabel("0", JLabel.CENTER);
        drawsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        drawsLabel.setForeground(new Color(60, 60, 100));
        drawsPanel.add(drawsTitle, BorderLayout.NORTH);
        drawsPanel.add(drawsLabel, BorderLayout.CENTER);
        
        scoreboardPanel.add(playerWinsPanel);
        scoreboardPanel.add(cpuWinsPanel);
        scoreboardPanel.add(drawsPanel);
        add(scoreboardPanel, BorderLayout.NORTH);
        
        // Status panel at bottom with red circle indicator - create early
        statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        statusPanel.setBackground(new Color(248, 248, 248));
        statusLabel = new JLabel("Your Turn");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setForeground(Color.BLACK);
        statusPanel.add(createStatusIndicator());
        statusPanel.add(statusLabel);
        
        // Main content panel containing buttons and game board
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(248, 248, 248));
        
        // Column buttons - rounded, first one shows red circle when player's turn
        JPanel buttonPanel = new JPanel(new GridLayout(1, cols, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        buttonPanel.setBackground(new Color(248, 248, 248));
        columnButtons = new JButton[cols];
        for (int i = 0; i < cols; i++) {
            columnButtons[i] = new RoundedButton("Drop");
            columnButtons[i].setFont(new Font("Arial", Font.PLAIN, 13));
            columnButtons[i].setBackground(new Color(240, 240, 240));
            columnButtons[i].setForeground(new Color(80, 80, 80));
            columnButtons[i].setFocusPainted(false);
            columnButtons[i].setHorizontalTextPosition(JButton.CENTER);
            columnButtons[i].setPreferredSize(new Dimension(80, 40));
            ButtonHandler handler = new ButtonHandler(i);
            columnButtons[i].addActionListener(handler);
            buttonPanel.add(columnButtons[i]);
        }
        updateColumnButtons();
        mainContentPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Game panel - rounded cells
        gamePanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        gamePanel.setBackground(new Color(248, 248, 248));
        int cellSize = Math.min(750 / cols, 550 / rows);
        gamePanel.setPreferredSize(new Dimension(cols * (cellSize + 5), rows * (cellSize + 5)));
        updateDisplay();
        
        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setBackground(new Color(248, 248, 248));
        gameContainer.add(gamePanel, BorderLayout.CENTER);
        mainContentPanel.add(gameContainer, BorderLayout.CENTER);
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Add status panel at bottom
        add(statusPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createStatusIndicator() {
        JPanel indicator = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isPlayerTurn) {
                    g.setColor(Color.RED);
                    g.fillOval(2, 2, 12, 12);
                }
            }
        };
        indicator.setPreferredSize(new Dimension(16, 16));
        indicator.setBackground(new Color(248, 248, 248));
        return indicator;
    }
    
    private void updateColumnButtons() {
        for (int i = 0; i < cols; i++) {
            if (isPlayerTurn && i == 0) {
                columnButtons[i].setBackground(new Color(255, 240, 240));
            } else {
                columnButtons[i].setBackground(new Color(240, 240, 240));
            }
            columnButtons[i].repaint();
        }
        if (statusPanel != null) {
            statusPanel.removeAll();
            statusPanel.add(createStatusIndicator());
            statusPanel.add(statusLabel);
            statusPanel.revalidate();
            statusPanel.repaint();
        }
    }
    
    private void makeMove(int col, int player) {
        int row = board.makeMove(col, player);
        if (row == -1) {
            return;
        }
        
        updateDisplay();
        
        // Check for win
        if (board.checkWin(row, col, player)) {
            if (player == 1) {
                playerWins++;
                playerWinsLabel.setText(String.valueOf(playerWins));
                statusLabel.setText("You win the game");
            } else {
                cpuWins++;
                cpuWinsLabel.setText(String.valueOf(cpuWins));
                statusLabel.setText("CPU wins!");
            }
            disableButtons();
            return;
        }
        
        // Check for draw
        if (board.isBoardFull()) {
            draws++;
            drawsLabel.setText(String.valueOf(draws));
            statusLabel.setText("It's a draw!");
            disableButtons();
            return;
        }
        
        // Switch turns
        if (player == 1) {
            isPlayerTurn = false;
            statusLabel.setText("CPU is thinking...");
            statusLabel.setForeground(Color.BLACK);
            updateColumnButtons();
            cpuMove();
        } else {
            isPlayerTurn = true;
            statusLabel.setText("Your Turn");
            statusLabel.setForeground(Color.BLACK);
            updateColumnButtons();
        }
    }
    
    private void cpuMove() {
        int cpuCol = cpu.getBestMove(board);
        makeMove(cpuCol, 2);
    }
    
    private void updateDisplay() {
        gamePanel.removeAll();
        int[][] gameBoard = board.getBoard();
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                RoundedCell cell = new RoundedCell(gameBoard[row][col]);
                cell.setBackground(new Color(248, 248, 248));
                gamePanel.add(cell);
            }
        }
        
        gamePanel.revalidate();
        gamePanel.repaint();
    }
    
    private void disableButtons() {
        for (int i = 0; i < columnButtons.length; i++) {
            columnButtons[i].setEnabled(false);
            columnButtons[i].setBackground(new Color(220, 220, 220));
        }
    }
    
    // Inner class for rounded panels
    class RoundedPanel extends JPanel {
        private int radius;
        
        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.radius = radius;
            setOpaque(false);
        }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }
    
    // Inner class for rounded buttons
    class RoundedButton extends JButton {
        private int radius = 10;
        
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
        }
        
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            
            // Draw text
            super.paintComponent(g);
            if (getText() != null && !getText().isEmpty()) {
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - 2;
                g.setColor(getForeground());
                g.setFont(getFont());
                g.drawString(getText(), x, y);
            }
        }
    }
    
    // Inner class for rounded cells
    class RoundedCell extends JPanel {
        private int player;
        private int radius = 8;
        
        public RoundedCell(int playerType) {
            player = playerType;
            setBackground(new Color(248, 248, 248));
            setOpaque(false);
        }
        
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            if (player == 1) {
                int size = Math.min(getWidth(), getHeight()) - 10;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.setColor(Color.RED);
                g2.fillOval(x, y, size, size);
            } else if (player == 2) {
                int size = Math.min(getWidth(), getHeight()) - 10;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.setColor(Color.YELLOW);
                g2.fillOval(x, y, size, size);
            }
            g2.dispose();
        }
    }
    
    // Inner class for button handling
    class ButtonHandler implements ActionListener {
        private int column;
        
        public ButtonHandler(int col) {
            column = col;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (isPlayerTurn && board.isValidMove(column)) {
                makeMove(column, 1);
            }
        }
    }
    
    public static void main(String[] args) {
        new Connect4();
    }
}
