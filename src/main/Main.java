package main;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
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

import audio.AudioPlayer;
import chunks.ChunkManager;
import gameTools.Leaderboard;
import panels.GameOverLOSE;
import panels.GameOverWIN;
import panels.GamePanel;
import panels.HomeScreen;
import panels.finalWinScreen;
import sprites.Enemy;

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

	/**
	 * Score is time_left Scoreboard for level 1,2, and total score
	 */

	/**
	 * Keeps track if user has been added to leaderboard.
	 */
	public static boolean addedToLeaderboard = false;

	/** Keeps track of if time should be added. */
	public static boolean addTime = false;

	/**
	 * Number of enemies killed for current level.
	 */
	public static int enemiesKilled = 0;

	/** Time elapsed for level. */
	public static int timeAmount = 120;

	/** Remaining seconds left for the player on current level. */
	public static int seconds_left = timeAmount;

	/** Total enemies killed by the player. */
	public static int totalEnemiesKilled = 0;

	/**
	 * Total time the player takes to beat all levels.
	 */
	public static int totalTimePlayed = 0;

	/** Background image that is the same as the maze walls. */
	private static BufferedImage backgroundImage = null;

	/**
	 * Default Cursor, used when player is in between levels or at home/end screen.
	 */
	private static final Cursor defaultCursor = Cursor.getDefaultCursor();

	/** The main game panel where the game is rendered. */
	private static GamePanel gamePanel;

	/** Home screen. */
	private static HomeScreen homePanel;

	/** Win screen when user reaches end of a level. */
	private static GameOverWIN nextLevel;

	/** Stores player's name. */
	private static String playerName = "";

	/** Lose screen when user runs out of time. */
	private static GameOverLOSE timeOut;

	/** Timer object used for timing how long the player has. */
	private static Timer timer;

	/**
	 * Create a transparent cursor. Used when player is player the game, so the
	 * cursor disappears from the screen.
	 */
	private static final Cursor transparentCursor = Toolkit.getDefaultToolkit()
			.createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "InvisibleCursor");

	/** Window used to display the game. */
	private static JFrame window;
	
	
	
	private static AudioPlayer gameOver;
	
	private static AudioPlayer levelPlay;
	
	
	private static AudioPlayer homeScreen;
	
	private static AudioPlayer wonGame;
	



	

	/**
	 * Win screen.
	 */
	private static finalWinScreen winner;

	/**
	 * Adds player score to respective leaderboard.
	 */
	public static void addScoreToLeader() {
		final Leaderboard leader = Leaderboard.levels.get(GamePanel.getCurrentLevel());
		// If player reached third level,
		if (GamePanel.getCurrentLevel() == 3) {
			final int added = leader.addEntry(playerName, totalTimePlayed);
			if (added != -1) {
				addedToLeaderboard = true;
			}
			leader.updateleaderboardFile();
			return;
		}

		final int playerScore = seconds_left;
		leader.addEntry(playerName, playerScore);
		leader.updateleaderboardFile();
	}

	/**
	 * Adds more time to the time player has left.
	 *
	 * @param t Time to add.
	 */
	public static void addTime(int t) {
		seconds_left += 15;
		addTime = true;
	}

	/**
	 * Closes the main window. Stops the game and disposes of the main window.
	 */
	public static void closeMainWindow() {
		window.dispose();
	}

	/**
	 * Removes the game over screen, used when the player chooses play again after
	 * running out of time in a level.
	 */
	public static void disablePanels() {
		window.remove(timeOut);
		GamePanel.continueLoop();
	}

	/**
	 * Adds 1 to the players enemy kill count
	 */
	public static void enemyKilled() {
		enemiesKilled += 1;
	}

	/**
	 * Disables the other panels and displays the "Game Over" game panel, when
	 * player runs out of time
	 *
	 * @param show If game over screen should be displayed.
	 */
	public static void gameOverPanel(boolean show) {
		window.setCursor(defaultCursor);
		gameOver.playSongOnce("gameover.wav");
		final String formattedString = String.format("Failed to complete the level in 120 seconds");
		window.setTitle(formattedString);
		window.setVisible(true);
		window.getContentPane().add(timeOut);
		updateTotalTimeAndEnemies();
		timeOut.updatePanel();
		timeOut.requestFocusInWindow();
		timeOut.setVisible(show);
		timeOut.setFocusable(show);
		gamePanel.setVisible(false);
		homePanel.setVisible(false);
		homePanel.setRunning(false);
	}

	/**
	 * Gets the current level the player is on. If the homePanel hasn't been
	 * initialized yet or it's currently being displayed, then it returns 3 so the
	 * overall leaderboard can be displayed.
	 *
	 * @return The current level.
	 */
	public static int getLevel() {
		if (homePanel == null || homePanel.isRunning()) {
			return 3;
		}

		return GamePanel.getCurrentLevel();
	}

	/**
	 * Gets the score of the player.
	 *
	 * @return The current score for the player.
	 */
	public static int getScore() {
		// If they beat final level, get their total score
		if (GamePanel.getCurrentLevel() == 3) {
			return totalTimePlayed;
		}

		// So home screen score is 0
		if (homePanel == null || homePanel.isRunning()) {
			return 0;
		}

		return seconds_left;
	}

	/**
	 * Main method to start the game.
	 *
	 * @param args The arguments passed to the main method.
	 */
	public static void main(String[] args) {
		gameOver = new AudioPlayer();
		levelPlay = new AudioPlayer();
		homeScreen = new AudioPlayer();
		wonGame = new AudioPlayer();

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

		homePanel.setRunning(true);
		homeScreen.playSong("menu.wav");
		// Add action listener to the button in HomeScreen
		homePanel.getStartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String name = homePanel.getName();
				if (name.isBlank()) {
					JOptionPane.showMessageDialog(window, "Please enter a name!");
				} else if (name.length() > 10) {
					JOptionPane.showMessageDialog(window, "Name can't be longer than 10 characters!");
				} else {
					playerName = name;
					homePanel.setVisible(false);
					homePanel.setRunning(false);
					homeScreen.stop();
					runMainCode();
				}
			}
		});
	}

	/**
	 * Returns true if the next level panel is still running and being seen
	 *
	 * @return true if gameOver is running
	 */
	public static boolean otherPanelRunning() {
		if (nextLevel.isRunning()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Resets the game timer and associated variables. Stops the timer, resets the
	 * elapsed time, and starts listening for arrow key presses to restart the
	 * timer.
	 */
	public static void resetTime() {
		seconds_left = timeAmount;
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
	 * Resets the game and removes the old window and starts a new one
	 */
	public static void restartGame() {
		if (window == null) {
			return;
		}
		wonGame.stop();
		seconds_left = timeAmount;
		totalEnemiesKilled = 0;
		totalTimePlayed = 0;
		enemiesKilled = 0;
		ChunkManager.activeChunks.clear();
		Enemy.activeEnemies.clear();
		Enemy.enemies.clear();
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
		gameOver = new AudioPlayer();
		levelPlay = new AudioPlayer();
		homeScreen = new AudioPlayer();
		wonGame = new AudioPlayer();

		gamePanel.resetLevel();
		gamePanel.reset();
		homeScreen.playSong("menu.wav");

		// Add action listener to the button in HomeScreen
		homePanel.getStartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String name = homePanel.getName().trim();
				if (name.isBlank()) {
					JOptionPane.showMessageDialog(window, "Please enter a name!");
				} else if (name.length() > 10) {
					JOptionPane.showMessageDialog(window, "Name can't be longer than 10 characters!");
				} else if (name.contains(";")) {
					JOptionPane.showMessageDialog(window, "Name can't contain ';' character");
				} else {
					playerName = name;
					homePanel.setVisible(false);
					homePanel.setRunning(false);
					gamePanel.setVisible(true);
					GamePanel.continueLoop();
					homeScreen.stop();
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
		} catch (final IOException e) {
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
				seconds_left--; // time they have left to complete
				final int currentLevel = GamePanel.getCurrentLevel();
				window.setTitle("Maze Runner - Level: " + currentLevel);
				if (seconds_left <= 0) {
					gameOver.playSongOnce("timeUp.wav");
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
				window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				window.dispose(); // Close the window
				System.exit(0);
			}
		});

		window.setCursor(transparentCursor);

		// starts game
		gamePanel.startGameThread();
	}

	/**
	 * Disables the other panels and displays the "You win" game panel
	 *
	 * @param show If final win screen should be displayed.
	 */
	public static void showFinalWinScreen(boolean show) {
		window.setCursor(defaultCursor);
		wonGame.playSong("winner.wav");
		final String formattedString = String.format("YOU WIN");

		// leaderboard.updateleaderboardFile();
		winner.updatePanel();
		window.setTitle(formattedString);
		window.setVisible(true);
		window.getContentPane().add(winner);
		winner.requestFocusInWindow();
		winner.setVisible(show);
		winner.setFocusable(show);
		gamePanel.setVisible(false);
		homePanel.setVisible(false);
		homePanel.setRunning(false);
	}

	/**
	 * Disables the other panels and displays the main game panel
	 */
	public static void showGamePanel() {
		window.setCursor(transparentCursor);
		window.setVisible(true);
		timeOut.setVisible(false);
		window.getContentPane().add(gamePanel);
		gamePanel.setVisible(true);
		gamePanel.setFocusable(true);
		gamePanel.startGameThread();
		nextLevel.setVisible(false);
		homePanel.setVisible(false);
		homePanel.setRunning(false);

	}

	/**
	 * Disables the other panels and displays the "Next Level" game panel
	 *
	 * @param show If next level screen should be displayed.
	 */
	public static void showNextLevelPanel(boolean show) {
		window.setCursor(defaultCursor);
		levelPlay.playSongOnce("lvlcompleted.wav");

		window.setVisible(true);
		window.getContentPane().add(nextLevel);

		nextLevel.updatePanel();

		nextLevel.requestFocusInWindow();
		nextLevel.setVisible(show);
		nextLevel.setFocusable(show);
		nextLevel.setVisible(true);
		nextLevel.setRunning(true);
		gamePanel.setVisible(false);
		homePanel.setVisible(false);
		homePanel.setRunning(false);
	}

	/**
	 * Stops the timer
	 */
	public static void stopTime() {
		timer.stop();
	}

	/**
	 * Updates the total time and the total enemies a player has killed in a level
	 */
	public static void updateTotalTimeAndEnemies() {
		totalTimePlayed += seconds_left;
		totalEnemiesKilled += enemiesKilled;
	}
}