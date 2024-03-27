package src;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Container;

/**
 * GameOverLOSE creates the game over screen when the user fails to find the end
 * of the maze.
 * 
 * This class provides a static method to display the game over screen with options
 * to exit the game or play again.
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since February 28, 2024
 */
public class GameOverLOSE {
    /**
     * Image to use for background.
     */
    private static BufferedImage backgroundImage;
    
    /**
     * The frame used for testing.
     */
    private static JFrame testFrame;


    /**
     * Creates JFrame and adds components.
     * Displays the game over screen with options to exit the game or play again.
     */
    public static void GameOver() {
        // Load the image
        try {
            backgroundImage = ImageIO.read(new File("images/GameOver2.png"));
        } catch (IOException e) {
            System.err.println("Failed to load game over screen background image!");
        }

        JFrame frame = new JFrame("Game Over");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = -2844420929659285615L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        // User is done
        JButton endGame = new JButton("EXIT");
        endGame.setFont(new Font("Arial", Font.PLAIN, 24));
        endGame.addActionListener(e -> {
            System.exit(0);
        });

        // User plays again
        JButton playAgain = new JButton("PLAY AGAIN");
        playAgain.setFont(new Font("Arial", Font.PLAIN, 24));
        playAgain.addActionListener(e -> {
            frame.dispose();

        });

        // Set up panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playAgain);
        buttonPanel.add(endGame);

        // Set up the frame
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
     // Assign the frame to the testFrame field
        testFrame = frame;
    }
    
	/**
	 * Main method
	 * 
	 * @param args arguements passed
	 */
    public static void main(String[] args) {
        boolean allCasesPassed = true;

        // Set up the test frame
        testFrame = new JFrame();
        
        // Call the method under test
        GameOverLOSE.GameOver();

        // Check if the frame has any child components (i.e., if it's displaying the game over screen)
        Container contentPane = testFrame.getContentPane();
        if (contentPane.getComponentCount() <= 0) {
            allCasesPassed = false;
            System.err.println("Game over screen not displayed!");
        }

        // Check if the frame is visible
        if (!testFrame.isVisible()) {
            allCasesPassed = false;
            System.err.println("Frame is not visible!");
        }

        if (allCasesPassed) {
            System.out.println("All cases passed!");
        } else {
            System.err.println("At least one case failed!");
        }
    }
}