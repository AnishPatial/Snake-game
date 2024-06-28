import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
    private static final int TILE_SIZE = 25;
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private LinkedList<Point> snake;
    private Point food;
    private char direction;
    private Timer timer;
    private boolean running = false;
    private int score = 0;

    private Rectangle startButton;
    private final Rectangle restartButton;
    private Rectangle quitButton;
    private String hoveredButton = "";

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        startButton = new Rectangle(WIDTH / 2 - 50, HEIGHT / 2 - 30, 100, 30);
        restartButton = new Rectangle(WIDTH / 2 - 50, HEIGHT / 2, 100, 30);
        quitButton = new Rectangle(WIDTH / 2 - 50, HEIGHT / 2 + 30, 100, 30);

        showStartScreen();
    }

    private void showStartScreen() {
        running = false;
        repaint();
    }

    private void startGame() {
        snake = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            snake.add(new Point(WIDTH / 2, HEIGHT / 2 + i * TILE_SIZE));
        }
        direction = 'U';
        placeFood();
        score = 0;
        running = true;

        timer = new Timer(100, this);
        timer.start();

        requestFocusInWindow();
        repaint();
    }

    private void placeFood() {
        Random random = new Random();
        food = new Point(random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE,
                         random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            drawGrid(g);
            drawFood(g);
            drawSnake(g);
            drawScore(g);
        } else {
            if (score == 0) {
                drawStartScreen(g);
            } else {
                showGameOver(g);
            }
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(50, 50, 50));
        for (int i = 0; i <= WIDTH / TILE_SIZE; i++) {
            for (int j = 0; j <= HEIGHT / TILE_SIZE; j++) {
                g.drawRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawFood(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(food.x, food.y, TILE_SIZE, TILE_SIZE);
    }

    private void drawSnake(Graphics g) {
        for (Point point : snake) {
            g.setColor(Color.GREEN);
            g.fillRect(point.x, point.y, TILE_SIZE, TILE_SIZE);
        }
    }

    private void drawScore(Graphics g) {
        String scoreText = "Score: " + score;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(scoreText, 10, 20);
    }

    private void drawStartScreen(Graphics g) {
        String title = "Snake Game";
        String instructions = "Use arrow keys to move the snake.";
        String start = "Start";
        String quit = "Quit";

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(title, (WIDTH - metrics.stringWidth(title)) / 2, HEIGHT / 2 - 100);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        metrics = getFontMetrics(g.getFont());
        g.drawString(instructions, (WIDTH - metrics.stringWidth(instructions)) / 2, HEIGHT / 2 - 50);

        drawButton(g, startButton, start);
        drawButton(g, quitButton, quit);
    }

    private void showGameOver(Graphics g) {
        String gameOver = "Game Over";
        String scoreText = "Final Score: " + score;
        String restart = "Restart";
        String quit = "Quit";

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(gameOver, (WIDTH - metrics.stringWidth(gameOver)) / 2, HEIGHT / 2 - 100);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        metrics = getFontMetrics(g.getFont());
        g.drawString(scoreText, (WIDTH - metrics.stringWidth(scoreText)) / 2, HEIGHT / 2 - 60);

        drawButton(g, restartButton, restart);
        drawButton(g, quitButton, quit);
    }

    private void drawButton(Graphics g, Rectangle rect, String text) {
        g.setColor(hoveredButton.equals(text) ? Color.YELLOW : Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, x, y);
    }

    // @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollision();
            checkFood();
        }
        repaint();
    }

    private void move() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case 'U':
                newHead.y -= TILE_SIZE;
                break;
            case 'D':
                newHead.y += TILE_SIZE;
                break;
            case 'L':
                newHead.x -= TILE_SIZE;
                break;
            case 'R':
                newHead.x += TILE_SIZE;
                break;
        }

        snake.addFirst(newHead);
        if (!newHead.equals(food)) {
            snake.removeLast();
        }
    }

    private void checkCollision() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
            }
        }

        if (!running) {
            timer.stop();
        }
    }

    private void checkFood() {
        Point head = snake.getFirst();
        if (head.equals(food)) {
            snake.addLast(new Point(-1, -1));
            placeFood();
            score++;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        Point point = e.getPoint();
        if (startButton.contains(point)) {
            startGame();
        } else if (restartButton.contains(point)) {
            startGame();
        } else if (quitButton.contains(point)) {
            System.exit(0);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    
    public void mouseMoved(MouseEvent e) {
        Point point = e.getPoint();
        if (startButton.contains(point)) {
            hoveredButton = "Start";
        } else if (restartButton.contains(point)) {
            hoveredButton = "Restart";
        } else if (quitButton.contains(point)) {
            hoveredButton = "Quit";
        } else {
            hoveredButton = "";
        }
        repaint();
    }

    public void mouseDragged(MouseEvent e) {}
}
