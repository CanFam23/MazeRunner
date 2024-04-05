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

/*
 * TODO:
 * Update main menu, see if theres way to use same frame for all panels
 * Add leaderboard and tracking for it
 */

/**
 * <p>
 * Serves as the main file for our game. This is the file you should run to play
 * our game.
 * </p>
 * 
 * <p>
 * This class initializes the game window, timer, and other necessary
 * components.
 * </p>
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since February 20, 2024
 * 
 * @see JFrame
 * @see HomeScreen
 * @see GamePanel
 */
public class Main {

	// Timer object used for timing how long the player has
	private static Timer timer;

	// Window used to display the game
	private static JFrame window;

	// Total elapsed seconds
	private static int seconds = 0;

	// Remaining seconds left for the player
	private static int seconds_left = 0;

	// The main game panel where the game is rendered
	private static GamePanel gamePanel;

	/**
	 * Main method to start the game.
	 * 
	 * @param args The arguments passed to the main method.
	 */
	public static void main(String[] args) {
		// Main Game Logic, calls on HomeScreen.java
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
//				HomeScreen homeScreen = new HomeScreen();
//				homeScreen.addWindowListener(new WindowAdapter() {
//					@Override
//					public void windowClosed(WindowEvent e) {
//						runMainCode();
//					}
//				});
//				homeScreen.setVisible(true);
				runMainCode();
			}
		});
	}

	/**
	 * Creates window and starts game. Initializes the game window and starts the
	 * game loop.
	 */
	public static void runMainCode() {
		// Creates window
		window = new JFrame();
		gamePanel = new GamePanel();

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Maze Runner - Use Arrows to start time");

		// window.setResizable(false);
		// Creates new game panel object
		window.add(gamePanel);

		// Timer to track seconds
		timer = new Timer(1000, new ActionListener() {

			/**
			 * Made to keep track of how much time is left in the game.
			 * 
			 * @param e ActionEvent to process.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				seconds++;
				seconds_left = 120 - seconds;

				if (seconds_left <= 0) {
					timer.stop();
					GameOverLOSE.GameOver();
					closeMainWindow();
				}

				window.setTitle("Maze Runner - Time Left: " + seconds_left + " seconds");

			}
		});

		gamePanel.addKeyListener(new KeyAdapter() {
			private boolean timerStarted = false;

			/**
			 * Made to check for key press. When a key is pressed, the timer starts.
			 * 
			 * @param e KeyEvent to process
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				// Start the timer only if it hasn't been started yet and arrow keys are pressed
				if (!timerStarted && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) {
					timerStarted = true;
					timer.start();
				}
			}
		});

		// Make sure the panel can receive focus
		gamePanel.setFocusable(true);
		gamePanel.requestFocusInWindow();

		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// Add window listener to handle window closing event
		window.addWindowListener(new WindowAdapter() {
			/**
			 * Used to handle window closing event.
			 * 
			 * @param e WindowEvent to process.
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Average FPS: " + gamePanel.getFPS());
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.dispose(); // Close the window

			}
		});

		// starts game
		gamePanel.startGameThread();
	}

	/**
	 * Closes the main window. Stops the game and disposes of the main window.
	 */
	public static void closeMainWindow() {
		window.dispose();
	}

	/**
	 * Resets the game timer and associated variables. Stops the timer, resets the
	 * elapsed time, and starts listening for arrow key presses to restart the
	 * timer.
	 */
	public static void resetTime() {
		timer.stop();
		seconds = 0;
		seconds_left = 0;
		window.setTitle("Maze Runner - Use Arrows to start time");
		gamePanel.addKeyListener(new KeyAdapter() {
			private boolean timerStarted = false;

			/**
			 * Made to check for key press. When a key is pressed, the timer starts.
			 * 
			 * @param e KeyEvent to process
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				// Start the timer only if it hasn't been started yet and arrow keys are pressed
				if (!timerStarted && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) {
					timerStarted = true;
					timer.start();
				}
			}
		});
	}

}