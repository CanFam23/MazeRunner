package main;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import gameTools.Leaderboard;
import panels.GameOverLOSE;
import panels.GameOverWIN;
import panels.GamePanel;
import panels.HomeScreen;

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

	private static int secondsTotal = 0;
	
	// Total elapsed seconds for each level
	private static int seconds = 0;

	// Remaining seconds left for the player
	private static int seconds_left = 0;

	// Time elapsed for level
	private static int timeAmount = 120;

	// The main game panel where the game is rendered
	private static GamePanel gamePanel;

	// Background image that is the same as the maze walls
	private static BufferedImage backgroundImage = null;

	private static GameOverWIN nextLevel;

	private static GameOverLOSE timeOut;

	private static HomeScreen homePanel;
	
	private static String playerName = "";
	

	public final static Leaderboard leaderboard = new Leaderboard("leaderboards/overall_time_leaderboard.txt");
	
	/**
	 * Main method to start the game.
	 * 
	 * @param args The arguments passed to the main method.
	 */
	public static void main(String[] args) {
		window = new JFrame();
		homePanel = new HomeScreen();
		// Set up the window and display the HomeScreen panel
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setPreferredSize(new Dimension(1000, 800));
		window.getContentPane().add(homePanel);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// Add action listener to the button in HomeScreen
		homePanel.getStartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = homePanel.getName();
				if(name.isBlank()) {
					JOptionPane.showMessageDialog(window, "Please enter a name!");
				}else if(name.length() > 10) {
					JOptionPane.showMessageDialog(window, "Name can't be longer than 10 characters!");
				}else {
					playerName = name;
					homePanel.setVisible(false);
					runMainCode();
				}
			}
		});
	}

	/**
	 * Creates window and starts game. Initializes the game window and starts the
	 * game loop.
	 */
	public static void runMainCode() {
		// Load Background Image
		try {
			backgroundImage = ImageIO.read(new File("images/backgroundBlock.png"));
		} catch (IOException e) {
			System.err.println("Failed to load backgroundBlock.png!");
		}

		// Creates window
		gamePanel = new GamePanel(backgroundImage);
		nextLevel = new GameOverWIN();
		timeOut = new GameOverLOSE();
		nextLevel.setPreferredSize(new Dimension(800, 600));
		timeOut.setPreferredSize(new Dimension(800, 600));

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Maze Runner - Use Arrows to start time");

		// window.setResizable(false);
		// Creates new game panel object
		timeOut.setVisible(false);
		nextLevel.setVisible(false);
		window.getContentPane().add(gamePanel);

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
				secondsTotal++;
				seconds_left = timeAmount - seconds;

				if (seconds_left <= 0) {
					timer.stop();
					gameOverPanel(true);
				} else {
					window.setTitle("Maze Runner - Time Left: " + seconds_left + " seconds");
				}
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

		nextLevel.setFocusable(true);
		nextLevel.requestFocusInWindow();

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
				final int added = leaderboard.addEntry(playerName, secondsTotal);
				//Check if user found the end of the maze
				if(gamePanel.wonGame()) {
					//If they did, add their score to the leaderboard if it was low enough
					if(added == -1) {
						JOptionPane.showMessageDialog(window, "You didn't make the leaderboard :( Womp Womp");
					}else {
						JOptionPane.showMessageDialog(window, String.format("Nice job! You're #%d on the leaderboard!", added+1));
						leaderboard.updateleaderboardFile();
					}
				}else {
					JOptionPane.showMessageDialog(window, "Why didn't you finish the game???");
				}
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

	public static void stopTime() {
		timer.stop();
	}

	public static void showNextLevelPanel(boolean show) {
		String formattedString = String.format("Completed Level in %d seconds", 120 - seconds_left);
		window.setTitle(formattedString);
		window.setVisible(true);
		window.getContentPane().add(nextLevel);
		nextLevel.setVisible(show);
		nextLevel.setFocusable(show);
		nextLevel.setVisible(true);
		nextLevel.setIsGameOverRunning(true);
		gamePanel.stopLoop();
		gamePanel.setVisible(false);
		homePanel.setVisible(false);

	}

	public static void showGamePanel() {
		window.setVisible(true);
		window.getContentPane().add(gamePanel);
		gamePanel.setVisible(true);
		gamePanel.setFocusable(true);
		gamePanel.startGameThread();
		nextLevel.setVisible(false);
		homePanel.setVisible(false);

	}

	public static void gameOverPanel(boolean show) {
		final String formattedString = String.format("Failed to complete the level in 120 seconds");
		window.setTitle(formattedString);
		window.setVisible(true);
		window.getContentPane().add(timeOut);
		timeOut.setVisible(show);
		timeOut.setFocusable(show);
		gamePanel.stopLoop();
		gamePanel.setVisible(false);
		homePanel.setVisible(false);
	}

	public static boolean otherPanelRunning() {
		if (nextLevel.isGameOverRunning()) {
			return true;
		} else {
			return false;
		}
	}
}