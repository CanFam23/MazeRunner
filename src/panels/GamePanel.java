package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import audio.AudioPlayer;
import chunks.ChunkManager;
import gameTools.GameVariables;
import gameTools.KeyHandler;
import main.Main;
import sprites.Enemy;
import sprites.Player;

/**
 * <p>
 * GamePanel handles creating the screen and contains the main game loop. It
 * also manages the top-level updating and rendering of all objects on the
 * screen.
 * </p>
 *
 * <p>
 * This class extends {@link JPanel} and implements {@link Runnable} and
 * {@link GameVariables}.
 * </p>
 *
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 *
 * @since February 24, 2024
 *
 * @see JPanel
 * @see Runnable
 * @see GameVariables
 */
public class GamePanel extends JPanel implements Runnable, GameVariables {
	/**
	 * Create our player. Initialize later to offer player selection of different
	 * characters.
	 */
	public static Player ourPlayer = new Player();

	/** Current level the player is on. */
	private static int current_level = 1;

	/** Keeps track of game state (Running vs not running). */
	private static boolean isRunning = true;

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 123455L;

	/**
	 * Continues the game loop
	 */
	public static void continueLoop() {
		isRunning = true;
	}

	/**
	 * Returns the integer indicating the current level of the game.
	 *
	 * @return the integer current level of the game.
	 */
	public static int getCurrentLevel() {
		return current_level;
	}

	/**
	 * Testing driver for GamePanel.java
	 *
	 * @param args not used as of 2024-04-18
	 */
	public static void main(String[] args) {

		Image backgroundImage = null;
		// Load Background Image
		try {
			backgroundImage = ImageIO.read(new File("images/wall.png"));
		} catch (final IOException e) {
			System.err.println("Failed to find wall image!");
		}
		boolean allPassed = true;

		final GamePanel gp = new GamePanel(backgroundImage);

		// Making sure game thread hasn't started yet
		if (gp.threadStarted()) {
			allPassed = false;
			System.err.println("gameThread started when it shouldn't have!");
		}

		gp.startGameThread();

		// Testing if game thread correctly started
		if (!gp.threadStarted()) {
			allPassed = false;
			System.err.println("Failed to start gameThread!");
		}

		// Testing if gameLoop is running
		if (!gp.isRunning()) {
			allPassed = false;
			System.err.println("Game loop isn't running, when it should be!");
		}

		GamePanel.stopLoop();
		// Testing if gameLoop is paused when it should be
		if (gp.isRunning()) {
			allPassed = false;
			System.err.println("Game loop is running, when it should be stopped!");
		}

		GamePanel.continueLoop();

		// Testing if gameLoop starts again correctly
		if (!gp.isRunning()) {
			allPassed = false;
			System.err.println("Game loop isn't running, when it should be!");
		}

		// Testing wonGame
		if (gp.wonGame()) {
			allPassed = false;
			System.err.println("wonGame said the game was won, when it wasn't!");
		}

		// Sleep main thread for 10 seconds, so the game thread still runs to test FPS
		// tracking
		try {
			Thread.sleep(10000);
		} catch (final InterruptedException e) {
			System.err.println("Error when sleeping main thread!");
		}

		final int avgFPS = (int) gp.getFPS();
		// Testing if game is running at 60 FPS

		if (avgFPS != 60) {
			allPassed = false;
			System.err.format("Game should be running at 60 FPS, but it averaged %s FPS!\n", avgFPS);
		}

		if (allPassed) {
			System.out.println("All cases passed!");
		} else {
			System.out.println("At least 1 case failed!");
		}

		System.exit(0);
	}

	/**
	 * Stops the game loop
	 */
	public static void stopLoop() {
		isRunning = false;
	}

	/**
	 * Keeps track of if time is being added or not. If this is set to true, the
	 * addText string is displayed on the screen.
	 */
	private boolean addingTime = false;

	/** Text displayed when adding time. */
	private final String addText = "Adding 15 seconds!";

	/** X coord for adding time text. */
	private final int addTextX = PLAYER_X - 75;

	/** Y coord for adding time text. */
	private final int addTextY = PLAYER_Y - 25;

	/** How long addText has been displayed for. */
	private int addTimeElapsed = 0;

	/**
	 * Background image.
	 */
	private final Image backgroundImage;

	/** ChunkManager object. */
	private final ChunkManager cmanager;

	/**
	 * Create a custom font with size 20, used for writing the time left and enemies
	 * killed.
	 */
	private final Font customFont = new Font("Courier", Font.BOLD, 20);

	/** When deathAnimation is true, we are animating the player's death. */
	private boolean deathAnimation = false;

	/**
	 * How many frames will pass from the start of the death animation to the end.
	 */
	private final int DEATHANIMATIONTIME = 300;

	/**
	 * deathCount keeps track of the how many frames we have drawn since the start
	 * of the death animation.
	 */
	private int deathCount = 0;
	
	/** Time left text x coord. */
	private final int timeX = 25;

	/** Time left text y coord. */
	private final int timeY = 25;

	/** Enemy Kill count text x coord. */
	private final int enemyKillCountX = timeX;

	/** Enemy Kill count text y coord. */
	private final int enemyKillCountY = timeY + 25;

	/** ArrayList used to track average fps. */
	private final List<Integer> fpsTracker = new ArrayList<>();

	/** Thread used for our game */
	private Thread gameThread;

	/** Reduce the height of the health bar. */
	private final int healthBarHeight = 20;

	// Health bar properties
	/** Make the health bar width slightly smaller than the panel width. */
	private final int healthBarWidth = 200;

	/** KeyHandler object used to detect key presses/releases. */
	private final KeyHandler keyH = new KeyHandler();

	/**
	 * Version of the current level, each level has 5 versions.
	 */
	private int levelVersionNumber;

	/** How long to display addText. */
	private final int maxAddTime = 3 * 1000;

	/** Number of levels in game. */
	private final int NUM_LEVELS = 1;

	/** Padding from the top and right edges of the panel. */
	private final int padding = 30;

	/**
	 * Random object, used for generating which level version to use.
	 */
	private final Random random = new Random();
	/** Speed of player. */
	private final int speed = 6;

	/** Visibility object, used to change visibility as time goes on. */
	private final Visibility v = Visibility.getInstance();

	/** Visibility object, used to change visibility as time goes on. */
	private AudioPlayer audio;
	
	private AudioPlayer moving;
	
	private AudioPlayer hit;
	
	private AudioPlayer moreTime;
		
	private AudioPlayer death;
	
	private AudioPlayer levelUp;


	private boolean deathPlayedOnce = false;


	private boolean playedOnce = false;

	
	/**
	 * deltas holds the distance that would be moved in each direction based on the
	 * player's speed.
	 */
	@SuppressWarnings("serial")
	Map<Facing, Integer[]> deltas = new HashMap<>() {
		{
			put(Facing.N, new Integer[] { 0, -speed });
			put(Facing.S, new Integer[] { 0, speed });
			put(Facing.E, new Integer[] { speed, 0 });
			put(Facing.W, new Integer[] { -speed, 0 });
		}
	};

	/**
	 * Constructs a GamePanel object.
	 *
	 * @param backgroundImage is seen wherever there are not any position blocks
	 *                        (background)
	 */
	public GamePanel(Image backgroundImage) {

		this.backgroundImage = backgroundImage;
		setPreferredSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setDoubleBuffered(true);

		this.addKeyListener(keyH);
		this.setFocusable(true);

		audio = new AudioPlayer();
		moving = new AudioPlayer();
		moreTime = new AudioPlayer();
		hit = new AudioPlayer();
		death = new AudioPlayer();
		levelUp = new AudioPlayer();


		cmanager = ChunkManager.getInstance();
		// Generate a random number between 1 and 5 (inclusive)
		levelVersionNumber = random.nextInt(1, 5);
		cmanager.loadLevel(1, levelVersionNumber);

		// Create our player and load the images
//		ourPlayer.load_images(character_name); // Civilian1(black)

	}

	/**
	 * Gets the average fps count of the game.
	 *
	 * @return The average FPS count during the programs duration.
	 */
	public double getFPS() {
		int sum = 0;
		for (final Integer n : fpsTracker) {
			sum += n;
		}

		return sum / (double) fpsTracker.size();
	}

	/**
	 * Checks if the user has won the game at least one time.
	 *
	 * @return true if the user has won at least once.
	 */
	public boolean hasWon() {
		return current_level == NUM_LEVELS && cmanager.hasWon();
	}

	/**
	 * @return true if the game loop is running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Paints all elements on screen
	 *
	 * @param g Graphics to draw on
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Creates green grass background
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		} else {
			// If no image is available, fallback to a solid color background
			g.setColor(Color.RED); // Change to desired background color
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		final Graphics2D g2 = (Graphics2D) g;

		// Draw the active chunks on the map by extracting all the relevant position
		// blocks
		cmanager.draw(g2);
		cmanager.drawEnemies(g2);

		if (deathAnimation) {
			v.updateRadius();
			v.createVis();
			v.drawVision(g2);
		}

		drawHealthBar(g);

		ourPlayer.draw(g2);

		drawStats(g2);

		// Saves some memory
		g2.dispose();
	}

	/**
	 * Resets several objects in this class.
	 */
	public void reset() {
		ourPlayer.reset();
		cmanager.reset();
		v.reset();
	}

	/**
	 * Set the level back to the very start
	 */
	public void resetLevel() {
		current_level = 1;
	}

	/**
	 * When we start the gameThread, this function is ran. Updates and repaints
	 * contents every frame
	 */
	@Override
	public void run() {

		long lastime = System.nanoTime();
		final double AmountOfTicks = 60;
		final double ns = 1000000000 / AmountOfTicks;
		double delta = 0;
		int frames = 0;
		double time = System.currentTimeMillis();

		// Game loop
		while (isRunning) {
			final long now = System.nanoTime();
			delta += (now - lastime) / ns;
			lastime = now;

			if (delta >= 1) {

				update();
				repaint();

				frames++;
				delta--;
				if (System.currentTimeMillis() - time >= 1000) {
					// If more time needs to be added, wait three seconds before doing so
					if (Main.addTime && addTimeElapsed < maxAddTime) {
						addingTime = true;
						if (moreTime.isActive() == false && playedOnce == false) {
							moreTime.playSongOnce("moreTime.wav");
							playedOnce = true;
						}
						addTimeElapsed += 1000;

						/*
						 * If addTime has reached three seconds or theres less than three seconds in the
						 * game Add more time.
						 */
						if (addTimeElapsed >= maxAddTime || Main.seconds_left <= 3) {
							addingTime = false;
							Main.addTime = false;
							addTimeElapsed = 0;
							playedOnce = false;
						}
					}
					fpsTracker.add(frames);
					time += 1000;
					frames = 0;
				}
			}
		}
	}

	/**
	 * Initializes new thread and starts it
	 */
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	/**
	 * Load the images for our player based on a character name.
	 * 
	 * @param character_name the name of the player to be loaded.
	 */
	public void loadPlayer(String character_name) {
		ourPlayer.load_images(character_name);
	}

	/**
	 * Checks if thread has started
	 *
	 * @return true if thread has started
	 */
	public boolean threadStarted() {
		if (gameThread == null) {
			return false;
		}
		return gameThread.isAlive();
	}

	/**
	 * Updates location of walls based on key presses and collisions.
	 */
	public void update() {
		// If the end is found, go to the next level
		if (cmanager.endFound()) {
			stopLoop();
			// Disable player movements when end block is reached
			keyH.upPressed = false;
			keyH.downPressed = false;
			keyH.rightPressed = false;
			keyH.leftPressed = false;
			Main.stopTime();
			Main.addTime = false;
			addingTime = false;
			addTimeElapsed = 0;

			ourPlayer.reset();
			moving.stop();

			if (levelUp.isActive() == false) {
				levelUp.playSongOnce("levelUp.wav");
			}
			Main.updateTotalTimeAndEnemies();
			Main.addScoreToLeader();
			if (current_level == NUM_LEVELS) {
				Main.showFinalWinScreen(true);
				moving.stop();
				// User won game
				reset();
				resetLevel();
				Main.resetTime();
			} else {
				// Go to next level.
				Main.showNextLevelPanel(true);
				while (Main.otherPanelRunning()) {
					System.out.print("");
				}
				reset();
				cmanager.reset();
				current_level++;
				levelVersionNumber = random.nextInt(1, 5);
				cmanager.loadLevel(current_level, levelVersionNumber);
				Main.showGamePanel();
				Main.resetTime();
				continueLoop();
			}
		}
		
		// If the player is attacking, check if they've hit anyone
		if (ourPlayer.getState().equals("Attack")) {
			ourPlayer.attacking();
		}
		
		// If the user isn't attacking but there are enemies that have been hit, deal
		// with them
		if (ourPlayer.hitEnemies() && !ourPlayer.getState().equals("Attack") && ourPlayer.getHealth() >= 1) {
			hit.playSongOnce("hitEnemy.wav");
			ourPlayer.handleAttack();
		}
		
		
		// Our player is out of health (passed out, fainted, dead)
		if (ourPlayer.getHealth() < 1) {
			hit.stop();
			moving.stop();
			if (death.isActive() == false && deathPlayedOnce == false) {
				death.playSongOnce("death.wav");
			}
			if (!deathAnimation) {
				deathCount = 0;
				deathAnimation = true;
				ourPlayer.setState(State.Dead);
				ourPlayer.setFacing(Facing.N);
				ourPlayer.lockState();
				ourPlayer.lockFacing();
				cmanager.stopKnockback();
			} else if (deathCount == DEATHANIMATIONTIME) {
				deathAnimation = false;
				deathPlayedOnce = false;
				v.reset();
				ourPlayer.unlockState();
				ourPlayer.unlockFacing();
				cmanager.restart();
				cmanager.stopKnockback();
				ourPlayer.reset();
			} else {
				deathPlayedOnce = true;
				deathCount++;
			}

		}
		

		// Move Player
		int dx = 0;
		int dy = 0;

		if (deathAnimation) {
			return;
		}
		
		if (cmanager.getKnockback()) {
			cmanager.knockback();
		} else {

			// Check if moving in a direction would result in a collision. If so, the user
			// can't move that way
			final boolean topCollided = cmanager.checkCollision(deltas.get(Facing.N));
			final boolean botCollided = cmanager.checkCollision(deltas.get(Facing.S));
			final boolean rightCollided = cmanager.checkCollision(deltas.get(Facing.E));
			final boolean leftCollided = cmanager.checkCollision(deltas.get(Facing.W));

			// Uses key presses to determine where to move walls
			if (keyH.upPressed && !topCollided) {
				dy += speed;
			}
			if (keyH.downPressed && !botCollided) {
				dy -= speed;
			}
			if (keyH.rightPressed && !rightCollided) {
				dx -= speed;
			}
			if (keyH.leftPressed && !leftCollided) {
				dx += speed;
			}
			if ((keyH.upPressed || keyH.leftPressed || keyH.rightPressed || keyH.downPressed) && moving.isActive() == false) {
				moving.playSongOnce("playerMove.wav");
			} 
			if (dy == 0 && dx ==0) {
				moving.stop();
			}

			if (!ourPlayer.isStateLocked()) {
				ourPlayer.updateState(keyH.upPressed, keyH.downPressed, keyH.rightPressed, keyH.leftPressed);
			}
			if (keyH.spacePressed) {
				if (audio.isActive() == false) {
					audio.playSongOnce("attack2.wav");
				}
				if (ourPlayer.getState() != "Attack") {
					// Set our player to be attacking
					ourPlayer.setState(sprites.Player.State.Attack);
					if (Enemy.activeEnemies.size() != 0 && Enemy.enemies.size() != 0) {
						ourPlayer.resetDrawCount();
					}
				}
				ourPlayer.lockState();
				ourPlayer.lockFacing();
			}
		}
		
		if (ourPlayer.getHealth() < 10000 && !ourPlayer.isGettingAttacked() && !deathAnimation) {
			ourPlayer.addHealth(1);
		}

		cmanager.updateCoords(dx, dy);
		cmanager.updateEnemies();
		}

	/**
	 * Checks if the user completed the last level, and won the game.
	 *
	 * @return true if user completed all levels.
	 */
	public boolean wonGame() {
		return current_level == NUM_LEVELS && cmanager.endFound();
	}

	/**
	 * Draws the player health bar on the gicen Graphics
	 *
	 * @param g graphics to draw on.
	 */
	private void drawHealthBar(Graphics g) {
		final int healthBarX = getWidth() - healthBarWidth - padding; // Adjust X coordinate to be near the right edge
		final int healthBarY = padding; // Adjust Y coordinate to be near the top edge
		final int titleX = getWidth() - healthBarWidth - padding; // X coordinate of the title (aligned with health bar)
		final int titleY = padding - 5; // Y coordinate of the title (just above the health bar)
		final int healthPercentage = (int) (((float) ourPlayer.getHealth() / 10000) * 100); // Assuming maximum health

		// Draw title
		g.setColor(Color.WHITE);
		final String title = "Player Health: " + healthPercentage + "%";
		g.drawString(title, titleX, titleY);

		// Draw health bar outline
		g.setColor(Color.BLACK); // Outline color
		g.drawRect(healthBarX - 1, healthBarY - 1, healthBarWidth + 2, healthBarHeight + 2); // Outline

		// Draw health bar background
		g.setColor(Color.RED); // Background color
		g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight); // Background

		// Calculate the width of the health bar based on player's health percentage
		final int currentHealth = ourPlayer.getHealth();
		final int barWidth = (int) (((double) currentHealth / 10000) * healthBarWidth);

		// Draw the health portion of the health bar
		g.setColor(Color.GREEN); // Health color
		g.fillRect(healthBarX, healthBarY, barWidth, healthBarHeight); // Health
	}

	/**
	 * Draws the seconds left and enemies left on the screen.
	 *
	 * @param g2 Graphics to draw on.
	 */
	private void drawStats(Graphics2D g2) {
		g2.setColor(Color.WHITE);

		// Set the custom font
		g2.setFont(customFont);
		final String timeLeft = "Time left: " + String.valueOf(Main.seconds_left) + " seconds";
		g2.drawString(timeLeft, timeX, timeY);

		final String enemiesKilled = "Enemies killed: " + String.valueOf(Main.enemiesKilled);
		g2.drawString(enemiesKilled, enemyKillCountX, enemyKillCountY);

		// If adding time, tell the user its happening
		if (addingTime) {
			g2.drawString(addText, addTextX, addTextY);
		}
	}
}