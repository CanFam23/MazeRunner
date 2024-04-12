package panels;

import java.awt.BorderLayout;
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
 * GameOverLOSE creates the game over screen when the user fails to find the end
 * of the maze.
 * 
 * This class provides a static method to display the game over screen with
 * options to exit the game or play again.
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since February 28, 2024
 */
public class GameOverLOSE extends JPanel {
	/**
	 * Image to use for background.
	 */
	private BufferedImage backgroundImage;

	private boolean isRunning = true;

	/**
	 * Creates JFrame and adds components. Displays the game over screen with
	 * options to exit the game or play again.
	 */
	public GameOverLOSE() {
		// Load the image
		try {
			backgroundImage = ImageIO.read(new File("images/GameOver2.png"));
		} catch (IOException e) {
			System.err.println("Failed to load game over screen background image!");
		}

		setLayout(new BorderLayout());

		JPanel panel = new JPanel() {
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
			isRunning = false;

		});

		// Set up panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(playAgain);
		buttonPanel.add(endGame);

		// Add components to this panel
		add(panel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

	}

	/**
	 * Returns true if the game over panel is running, false otherwise.
	 */
	public boolean isGameOverRunning() {
		return isRunning;
	}

	/**
	 * Main method
	 * 
	 * @param args arguements passed
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Game Over Window Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GameOverLOSE gameOverPanel = new GameOverLOSE();
		gameOverPanel.setPreferredSize(new Dimension(800, 600));

		frame.add(gameOverPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

//        // Set up the test frame
//        testFrame = new JFrame();
//        
//        // Call the method under test
//        GameOverLOSE.GameOver();
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
}