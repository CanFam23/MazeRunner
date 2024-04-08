package panels;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * <p>
 * GameOverWIN creates the panel shown when user finds the end of the maze.
 * </p>
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since February 28, 2024
 */
public class GameOverWIN extends JPanel {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 8986657739517647875L;

    /**
     * Background image for panel
     */
    private BufferedImage backgroundImage;

    /**
     * Indicates whether the game over panel is running or not.
     */
    private boolean isRunning = true;

    /**
     * Constructs new GameOverWIN panel
     */
    public GameOverWIN() {
        // Load the image
        try {
            backgroundImage = ImageIO.read(new File("images/YouWin.png"));
        } catch (IOException e) {
            System.err.println("Failed to load win screen background image!");
        }

        setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JButton endGame = new JButton("EXIT");
        endGame.setFont(new Font("Arial", Font.PLAIN, 24));
        endGame.addActionListener(e -> {
            isRunning = false;
            System.exit(0);
        });

        JButton playAgain = new JButton("NEXT LEVEL");
        playAgain.setFont(new Font("Arial", Font.PLAIN, 24));
        playAgain.addActionListener(e -> {
            isRunning = false;
            // You can handle what happens when "NEXT LEVEL" button is clicked here
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playAgain);
        buttonPanel.add(endGame);

        // Add components to this panel
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        isRunning = true;
    }

    /**
     * Returns true if the game over panel is running, false otherwise.
     */
    public boolean isGameOverRunning() {
        return isRunning;
    }
	
//	/**
//	 * Main method
//	 * 
//	 * @param args arguements passed
//	 */
	public static void main(String[] args) {
	    JFrame frame = new JFrame("Game Over Window Test");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	    GameOverWIN gameOverPanel = new GameOverWIN();
	    gameOverPanel.setPreferredSize(new Dimension(800, 600));
	
	    frame.add(gameOverPanel);
	
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
}
//    public static void main(String[] args) {
//        boolean allCasesPassed = true;
//
//        // Set up the test frame
//        testFrame = new JFrame();
//        
//        // Call the method under test
//        GameOverWIN.GameOverWIN();
//
//        // Check if the frame has any child components (i.e., if it's displaying the game over screen)
//        Container contentPane = testFrame.getContentPane();
//        if (contentPane.getComponentCount() <= 0) {
//            allCasesPassed = false;
//            System.err.println("Game over screen not displayed!");
//        }
//
//        // Check if the frame is visible
//        if (!testFrame.isVisible()) {
//            allCasesPassed = false;
//            System.err.println("Frame is not visible!");
//        }
//
//        if (allCasesPassed) {
//            System.out.println("All cases passed!");
//        } else {
//            System.err.println("At least one case failed!");
//        }
//    }
//}
