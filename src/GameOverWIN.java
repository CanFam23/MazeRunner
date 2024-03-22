/*
 * GameOverWIN.java
 * 
 * Description: This class handles displaying the win screen when the player successfully completes the game.
 * It loads an image as the background for the win screen and provides options for the player to
 * either proceed to the next level or exit the game.
 * 
 * Date: 3.21.24
 * 
 * Author: Molly O'Connor, Nick Clouse, Andrew Denegar
 */

package src;

//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.fail;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameOverWIN {
	// Background image for the win screen
	private BufferedImage backgroundImage;

	// Constructor to initialize the win screen
	public GameOverWIN() {
		// Load the image
		try {
			backgroundImage = ImageIO.read(new File("images/YouWin.png"));
		} catch (IOException e) {
			System.err.println("Failed to load win screen background image!");
		}

		// Create win screen frame
		JFrame frame = new JFrame("YOU WON!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);

		// Panel to display background image
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 2200499920275111737L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};

		// Buttons for next level and exit options
		JButton endGame = new JButton("EXIT");
		endGame.setFont(new Font("Arial", Font.PLAIN, 24));
		endGame.addActionListener(e -> {
			System.exit(0);
		});

		JButton nextLevel = new JButton("NEXT LEVEL");
		nextLevel.setFont(new Font("Arial", Font.PLAIN, 24));
		nextLevel.addActionListener(e -> {
			Main.runMainCode(); // Proceed to next level
			frame.dispose(); // Close the win screen
		});

		// Panel to hold buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(nextLevel);
		buttonPanel.add(endGame);

		// Set up the frame
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(panel);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

//	// JUnit test
//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				GameOverWIN gameOver = new GameOverWIN();
//			}
//		});
//		boolean testPassed = false;
//		// Test whether the background image is loaded successfully
//		BufferedImage backgroundImage = null;
//		try {
//			backgroundImage = ImageIO.read(new File("images/YouWin.png"));
//			assertNotNull("Background image should not be null", backgroundImage);
//			testPassed = true;
//		} catch (IOException e) {
//			fail("Failed to load game over screen background image!");
//			testPassed = false;
//		}
//
//		if (!testPassed) {
//			System.out.println("The class has issues.");
//		} else {
//			System.out.println("Test cases passed!");
//		}
//	}
}
