package main;

import java.awt.Dimension;
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
import javax.swing.WindowConstants;

import gameTools.Leaderboard;
import panels.GameOverLOSE;
import panels.GameOverWIN;
import panels.GamePanel;
import panels.HomeScreen;
import panels.finalWinScreen;

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

	/** Timer object used for timing how long the player has. */
	private static Timer timer;

	/** Window used to display the game. */
	private static JFrame window;

	/** Total seconds the player has been playing the level. */
	private static int secondsLevel = 0;

	/** Time elapsed for level. */
	private static int timeAmount = 120;

	/**
	 * Total time allowed for all three levels.
	 */
	private static final int TOTAL_TIME_ALLOWED = timeAmount * 3;

	/**
	 * Max score you can gain by killing all enemies in every level. We got this
	 * number by finding the highest number of enemies for each level, and then
	 * adding them up and multiplying that number by 15 because that how many
	 * seconds each enemy is worth.
	 */
	private static final int MAX_SCORE_FROM_ENEMIES = 1095;

	/**
	 * Max score the player can get.
	 */
	private static final int MAX_SCORE = MAX_SCORE_FROM_ENEMIES - TOTAL_TIME_ALLOWED;

	/**
	 * Total time the player takes to beat all levels.
	 */
	private static int totalTimePlayed = 0;

	/** Remaining seconds left for the player on current level. */
	public static int seconds_left = timeAmount;

	/** The main game panel where the game is rendered. */
	private static GamePanel gamePanel;

	/** Background image that is the same as the maze walls. */
	private static BufferedImage backgroundImage = null;

	/** Win screen when user reaches end of a level. */
	private static GameOverWIN nextLevel;

	/** Lose screen when user runs out of time. */
	private static GameOverLOSE timeOut;

	/** Home screen. */
	private static HomeScreen homePanel;

	/**
	 * Win screen.
	 */
	private static finalWinScreen winner;

	/** Stores player's name. */
	private static String playerName = "";

	/** Keeps track of leaderboard, and updates it when called. */
	public final static Leaderboard leaderboard = new Leaderboard("leaderboards/overall_time_leaderboard.txt");

	/** Keeps track of if time should be added. */
	public static boolean addTime = false;

	/**
	 * Keeps track if user has been added to leaderboard.
	 */
	public static boolean addedToLeaderboard = true;

	/** Total enemies killed by the player. */
	public static int totalEnemiesKilled = 0;

	/**
	 * Number of enemies killed for current level.
	 */
	public static int enemiesKilled = 0;

	/**
	 * Main method to start the game.
	 *
	 * @param args The arguments passed to the main method.
	 */
	public static void main(String[] args) {
		window = new JFrame();
		window.setResizable(false);
		homePanel = new HomeScreen();
		// Set up the window and display the HomeScreen panel
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setPreferredSize(new Dimension(1000, 725));
		window.getContentPane().add(homePanel);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// Add action listener to the button in HomeScreen
		homePanel.getStartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = homePanel.getName();
				if (name.isBlank()) {
					JOptionPane.showMessageDialog(window, "Please enter a name!");
				} else if (name.length() > 10) {
					JOptionPane.showMessageDialog(window, "Name can't be longer than 10 characters!");
				} else {
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

		/*
		 * Reset gamepanel, this is added so if the user plays again after they lose,
		 * the game will reset correctly
		 */
		if (gamePanel != null) {
			gamePanel.reset();

		}
		// Creates window
		gamePanel = new GamePanel(backgroundImage);
		nextLevel = new GameOverWIN();
		timeOut = new GameOverLOSE();
		winner = new finalWinScreen();
		nextLevel.setPreferredSize(new Dimension(800, 600));
		timeOut.setPreferredSize(new Dimension(800, 600));
		winner.setPreferredSize(new Dimension(800, 600));

		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setTitle("Maze Runner - Use Arrows to start time");

		// window.setResizable(false);
		// Creates new game panel object
		timeOut.setVisible(false);
		nextLevel.setVisible(false);
		winner.setVisible(false);
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
				secondsLevel++; // running total of time taken to complete level
				seconds_left--; // time they have left to complete
				int currentLevel = GamePanel.getCurrentLevel();
				window.setTitle("Maze Runner - Level: " + currentLevel);
				if (seconds_left <= 0) {
					// player lost logic
					GamePanel.stopLoop();
					timer.stop();
					gameOverPanel(true);
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
				GamePanel.stopLoop();
				// Check if user has won at least once.
				if (!gamePanel.hasWon()) {
					JOptionPane.showMessageDialog(window, "Why didn't you finish the game???");
					System.exit(0);
				}
				window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
		seconds_left = timeAmount;
		totalEnemiesKilled += enemiesKilled;
		enemiesKilled = 0;
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

	/**
	 * Stops the timer
	 */
	public static void stopTime() {
		timer.stop();
	}

	/**
	 * Adds more time to the time player has left.
	 * 
	 * @param t Time to add.
	 */
	public static void addTime(int t) {
		seconds_left += 15;
		addTime = false;
	}

	/**
	 * Adds 1 to the players enemy kill count
	 */
	public static void enemyKilled() {
		enemiesKilled += 1;
	}

	/**
	 * Disables the other panels and displays the "Next Level" game panel
	 */
	public static void showNextLevelPanel(boolean show) {
		System.out.println("Total enemies killed: " + totalEnemiesKilled);
		System.out.println("Total time: " + totalTimePlayed);

		String formattedString = String.format("Completed Level in %d seconds", 120 - seconds_left);
		window.setTitle(formattedString);
		window.setVisible(true);
		window.getContentPane().add(nextLevel);
		nextLevel.requestFocusInWindow();
		nextLevel.setVisible(show);
		nextLevel.setFocusable(show);
		nextLevel.setVisible(true);
		nextLevel.setIsGameOverRunning(true);
		gamePanel.setVisible(false);
		homePanel.setVisible(false);
	}

	/**
	 * Disables the other panels and displays the main game panel
	 */
	public static void showGamePanel() {
		window.setVisible(true);
		timeOut.setVisible(false);
		window.getContentPane().add(gamePanel);
		gamePanel.setVisible(true);
		gamePanel.setFocusable(true);
		gamePanel.startGameThread();
		nextLevel.setVisible(false);
		homePanel.setVisible(false);

	}

	/**
	 * Disables the other panels and displays the "Game Over" game panel, when
	 * player runs out of time
	 */
	public static void gameOverPanel(boolean show) {
		final String formattedString = String.format("Failed to complete the level in 120 seconds");
		window.setTitle(formattedString);
		window.setVisible(true);
		window.getContentPane().add(timeOut);
		timeOut.requestFocusInWindow();
		timeOut.setVisible(show);
		timeOut.setFocusable(show);
		gamePanel.setVisible(false);
		homePanel.setVisible(false);
	}

	/**
	 * Disables the other panels and displays the "You win" game panel
	 */
	public static void showFinalWinScreen(boolean show) {
		final String formattedString = String.format("YOU WIN");
		leaderboard.updateleaderboardFile();
		window.setTitle(formattedString);
		window.setVisible(true);
		window.getContentPane().add(winner);
		winner.requestFocusInWindow();
		winner.setVisible(show);
		winner.setFocusable(show);
		gamePanel.setVisible(false);
		homePanel.setVisible(false);
	}

	/**
	 * Returns true if the next level panel is still running and being seen
	 */
	public static boolean otherPanelRunning() {
		if (nextLevel.isGameOverRunning()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * removes the game over screen, used when the player chooses play again after
	 * running out of time in a level.
	 */
	public static void disablePanels() {
		window.remove(timeOut);
		GamePanel.continueLoop();
	}

	/**
	 * Calculates the players score and adds them to the leader board
	 */
	public static void addScoreToLeader() {
		final int playerScore = MAX_SCORE + totalTimePlayed - (totalEnemiesKilled * 15);
		final int added = leaderboard.addEntry(playerName, playerScore);
		if (added != -1) {
			addedToLeaderboard = true;
		}
	}

	/**
	 * Updates the total time and the total enemies a player has killed in a level
	 */
	public static void updateTotalTimeAndEnemies() {
		totalTimePlayed += secondsLevel;
		totalEnemiesKilled += enemiesKilled;
	}

	/**
	 * Resets the game and removes the old window and starts a new one
	 */
	public static void restartGame() {
		// Dispose of the current window
		window.dispose();
		window.remove(gamePanel);
		window.remove(timeOut);
		window.remove(homePanel);
		window.remove(nextLevel);

		window = new JFrame();
		homePanel = new HomeScreen();
		// Set up the window and display the HomeScreen panel
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
				if (name.isBlank()) {
					JOptionPane.showMessageDialog(window, "Please enter a name!");
				} else if (name.length() > 10) {
					JOptionPane.showMessageDialog(window, "Name can't be longer than 10 characters!");
				} else if (name.contains(";")) {
					JOptionPane.showMessageDialog(window, "Name can't contain ';' character");
				} else {
					playerName = name;
					homePanel.setVisible(false);
					gamePanel.setVisible(true);
					GamePanel.continueLoop();
					runMainCode();
				}
			}
		});
	}
}