/*
 * Main.java
 * 
 * Description: This program creates a simple maze runner game where the player
 * navigates through a maze using arrow keys. The objective is to
 * reach the end of the maze within a certain time limit.
 * 
 * The game window displays the maze and tracks the time elapsed
 * since the start of the game. If the player fails to reach the
 * end of the maze within the time limit, a game over screen is
 * displayed.
 * 
 * Date: March 2, 2024
 * 
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 */

package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {

	// Timer for tracking time elapsed
	private static Timer timer;
	// Main game window
	private static JFrame window;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create and display the home screen
				HomeScreen homeScreen = new HomeScreen();
				homeScreen.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						// Once home screen is closed, start the main game
						runMainCode();
					}
				});
				homeScreen.setVisible(true);
			}
		});
	}

	// Method to run the main game code
	public static void runMainCode() {
		// Create game window
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Maze Runner - Use Arrows to start time");

		// Create game panel
		GamePanel gamePanel = new GamePanel();
		window.add(gamePanel);

		// Timer to track seconds elapsed
		timer = new Timer(1000, new ActionListener() {
			int seconds = 0;
			int seconds_left = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				seconds++;
				seconds_left = 120 - seconds;

				// Check if time limit reached
				if (seconds_left <= 0) {
					timer.stop();
					GameOverLOSE.GameOver();
					closeMainWindow();
				}

				// Update window title with time left
				window.setTitle("Maze Runner - Time Left: " + seconds_left + " seconds");
			}
		});

		// Key listener to start the timer when arrow keys are pressed
		gamePanel.addKeyListener(new KeyAdapter() {
			private boolean timerStarted = false;

			@Override
			public void keyPressed(KeyEvent e) {
				if (!timerStarted && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) {
					timerStarted = true;
					timer.start();
				}
			}
		});

		// Make game panel focusable
		gamePanel.setFocusable(true);
		gamePanel.requestFocusInWindow();

		// Pack window and display
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// Add window listener to handle window closing event
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Average FPS: " + gamePanel.getFPS());
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.dispose();
			}
		});

		// Start game
		gamePanel.startGameThread();
	}

	// Method to close the main game window
	public static void closeMainWindow() {
		window.dispose();
	}
}