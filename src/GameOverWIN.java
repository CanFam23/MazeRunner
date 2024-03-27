package src;

import java.awt.BorderLayout;
import java.awt.Container;
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
 * GameOverWIN creates the screen shown when user finds the end of the maze.
 * </p>
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since February 28, 2024
 */
public class GameOverWIN  {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 8986657739517647875L;

	/**
	 * Background image for screen
	 */
	private static BufferedImage backgroundImage;
	
	/**
	 * The main JFrame used to display the game over window.
	 */
    private static JFrame frame = new JFrame(); // Initialize the frame
	
    /**
     * The frame used for testing.
     */
    private static JFrame testFrame;
    
    /**
     * Indicates whether the game over window is running or not.
     */
	private static boolean isRunning = true;
	
	/**
	 * Constructs new GameOverWIN window
	 */
	public static void GameOverWIN() {
		// Load the image
		try {
			backgroundImage = ImageIO.read(new File("images/YouWin.png"));
		} catch (IOException e) {
			System.err.println("Failed to load win screen background image!");
		}

		frame.setTitle("YOU WON!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);

		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 2200499920275111737L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};

		JButton endGame = new JButton("EXIT");
		endGame.setFont(new Font("Arail", Font.PLAIN, 24));
		endGame.addActionListener(e -> {
			isRunning = false;
			System.exit(0);
		});

		JButton playAgain = new JButton("NEXT LEVEL");
		playAgain.setFont(new Font("Arail", Font.PLAIN, 24));
		playAgain.addActionListener(e -> {
			isRunning = false;
			frame.dispose();
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(playAgain);
		buttonPanel.add(endGame);

		// Set up the frame
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(panel);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		testFrame = frame;

	}
	
    /**
     * Returns true if the game over window is running, false otherwise.
     */
    public static boolean isGameOverRunning() {
        return isRunning;
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
        GameOverWIN.GameOverWIN();

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
