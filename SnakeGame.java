import java.awt.EventQueue;
import javax.swing.JFrame;

public class SnakeGame {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
