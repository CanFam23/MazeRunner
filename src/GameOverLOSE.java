/*
 * GameOverLOSE.java
 * 
 * Description: This class handles displaying the game over screen when the player loses the game.
 * It loads an image as the background for the game over screen and provides options
 * for the player to either exit the game or play again.
 * 
 * Date: 3.21.24
 * 
 * Author: Molly O'Connor, Nick Clouse, Andrew Denegar
 */

package src;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GameOverLOSE {
	// Background image for the game over screen
	private static BufferedImage backgroundImage;

	// Method to display the game over screen
	public static void GameOver() {
		// Load the image
		try {
			backgroundImage = ImageIO.read(new File("images/GameOver2.png"));
		} catch (IOException e) {
			System.err.println("Failed to load game over screen background image!");
		}

		// Create game over frame
		JFrame frame = new JFrame("Game Over");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);

		// Panel to display background image
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = -2844420929659285615L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};

		// Buttons for exit and play again options
		JButton endGame = new JButton("EXIT");
		endGame.setFont(new Font("Arial", Font.PLAIN, 24));
		endGame.addActionListener(e -> {
			System.exit(0);
		});

		JButton playAgain = new JButton("PLAY AGAIN");
		playAgain.setFont(new Font("Arial", Font.PLAIN, 24));
		playAgain.addActionListener(e -> {
			Main.runMainCode(); // Restart the game
			frame.dispose(); // Close the game over screen
		});

		// Panel to hold buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(playAgain);
		buttonPanel.add(endGame);

		// Set up the frame
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(panel);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	// JUnit test
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GameOverLOSE.GameOver();
			}
		});
		boolean testPassed = false;
		// Test whether the background image is loaded successfully
		BufferedImage backgroundImage = null;
		try {
			backgroundImage = ImageIO.read(new File("images/GameOver2.png"));
			assertNotNull("Background image should not be null", backgroundImage);
			testPassed = true;
		} catch (IOException e) {
			fail("Failed to load game over screen background image!");
			testPassed = false;
		}

		if (!testPassed) {
			System.out.println("The class has issues.");
		} else {
			System.out.println("Test cases passed!");
		}
	}
}
