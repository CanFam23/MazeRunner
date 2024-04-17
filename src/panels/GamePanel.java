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
	private static final long serialVersionUID = 123455L;

	/** ChunkManager object. */
	private ChunkManager cmanager;

	/** KeyHandler object used to detect key presses/releases. */
	private final KeyHandler keyH = new KeyHandler();

	/** ArrayList used to track average fps. */
	private final List<Integer> fpsTracker = new ArrayList<>();

	/** Speed of player. */
	private final int speed = 6;

	@SuppressWarnings("serial")
	Map<Facing, Integer[]> deltas = new HashMap<>() {
		{
			put(Facing.N, new Integer[] { 0, -speed });
			put(Facing.S, new Integer[] { 0, speed });
			put(Facing.E, new Integer[] { speed, 0 });
			put(Facing.W, new Integer[] { -speed, 0 });
		}
	};

	/** Number of levels in game. */
	private final int NUM_LEVELS = 3;

	/** Current level the player is on. */
	private int current_level = 1;

	/**
	 * Create our player. Initialize later to offer player selection of different
	 * characters.
	 */
	public static Player ourPlayer = new Player();

	/** Visibility object, used to change visibility as time goes on. */
	private Visibility v = Visibility.getInstance();

	/** Thread used for our game */
	private Thread gameThread;

	/** Keeps track of game state (Running vs not running). */
	private boolean isRunning = true;

	/**
	 * Background image.
	 */
	private Image backgroundImage;

	/**
	 * Version of the current level, each level has 5 versions.
	 */
	private int levelVersionNumber;

	/**
	 * Random object, used for generating which level version to use.
	 */
	private final Random random = new Random();

	// Health bar properties
	/** Make the health bar width slightly smaller than the panel width. */
	private int healthBarWidth = 200;
	/** Reduce the height of the health bar.*/
	private int healthBarHeight = 20;
	/** Padding from the top and right edges of the panel.*/
	private int padding = 30;
	
	/** Time left text x coord. */
	private final int timeX = 25;
	
	/** Time left text y coord. */
	private final int timeY = 25;
	
	/** Enemy Kill count text x coord.*/
	private final int enemyKillCountX = timeX;
	
	/** Enemy Kill count text y coord.*/
	private final int enemyKillCountY = timeY + 25;
	
	/** Keeps track of if time is being added or not. 
	 *  If this is set to true, the addText string is 
	 *  displayed on the screen.
	 */
	private boolean addingTime = false;
	
	/** How long to display addText.*/
	private final int maxAddTime = 3 * 1000;
	
	
	/** How long addText has been displayed for.*/
	private int addTimeElapsed = 0;
	
	/** Text displayed when adding time. */
	private final String addText = "Adding 15 seconds!";
	/** X coord for adding time text. */
	private final int addTextX = PLAYER_X - 75;
	
	/** Y coord for adding time text. */
	private final int addTextY = PLAYER_Y - 25;
    /** Create a custom font with size 20, used for writing the time left and enemies killed.*/
    private final Font customFont = new Font("Courier", Font.BOLD, 20);
    
	

	/**
	 * Constructs a GamePanel object.
	 */
	public GamePanel(Image backgroundImage) {

		this.backgroundImage = backgroundImage;
		setPreferredSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setDoubleBuffered(true);

		this.addKeyListener(keyH);
		this.setFocusable(true);

		cmanager = ChunkManager.getInstance();
        // Generate a random number between 1 and 5 (inclusive)
        levelVersionNumber = random.nextInt(1,5);
		cmanager.loadLevel(current_level, levelVersionNumber);

		// Create our player and load the images
		ourPlayer.load_images("Civilian1(black)"); // Civilian1(black)

	}

	/**
	 * Initializes new thread and starts it
	 */
	public void startGameThread() {

		gameThread = new Thread(this);
		gameThread.start();
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
			long now = System.nanoTime();
			delta += (now - lastime) / ns;
			lastime = now;

			if (delta >= 1) {

				update();
				repaint();

				frames++;
				delta--;
				if (System.currentTimeMillis() - time >= 1000) {
					//If more time needs to be added, wait three seconds before doing so
					if(Main.addTime && addTimeElapsed < maxAddTime) {
						addingTime = true;
						addTimeElapsed += 1000;
						
						/*
						 * If addTime has reached three seconds or theres less than three seconds in the game
						 * Add more time.
						 */
						if(addTimeElapsed >= maxAddTime || Main.seconds_left <= 3) {
							addingTime = false;
							addTimeElapsed = 0;
							Main.addTime(15);
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
	 * Updates location of walls based on key presses and collisions.
	 */
	public void update() {
		// If the end is found, go to the next level
		if (cmanager.endFound()) {
			stopLoop();
			Main.stopTime();
			ourPlayer.reset();
			// Disable player movements when end block is reached
			keyH.upPressed = false;
			keyH.downPressed = false;
			keyH.rightPressed = false;
			keyH.leftPressed = false;
			if (current_level == NUM_LEVELS) {
				System.out.print("");
				Main.gameOverPanel(true);				
				/*
				 *
				 *
				 * Make this show gameOverWin screen
				 *
				 *
				 */
			} else {
				Main.showNextLevelPanel(true);
				while (Main.otherPanelRunning()) {
					System.out.print("");
				}
			}
			reset();
			current_level++;
			levelVersionNumber = random.nextInt(1,5);
			cmanager.loadLevel(current_level, levelVersionNumber);
			Main.showGamePanel();
			Main.resetTime();
			continueLoop();
		}

		if (ourPlayer.getHealth() <= 0) {
			cmanager.stopKnockback();
			ourPlayer.reset();
			cmanager.reset();
			cmanager.loadLevel(current_level, levelVersionNumber);
		}

		// Move Player
		int dx = 0;
		int dy = 0;

		if (cmanager.getKnockback()) {
			cmanager.knockback();
		} else {

			// Check if moving in a direction would result in a collision. If so, the user
			// can't move that way
			boolean topCollided = cmanager.checkCollision(deltas.get(Facing.N));
			boolean botCollided = cmanager.checkCollision(deltas.get(Facing.S));
			boolean rightCollided = cmanager.checkCollision(deltas.get(Facing.E));
			boolean leftCollided = cmanager.checkCollision(deltas.get(Facing.W));

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

			if (!ourPlayer.isStateLocked()) {
				ourPlayer.updateState(keyH.upPressed, keyH.downPressed, keyH.rightPressed, keyH.leftPressed);
			}
			if (keyH.spacePressed) {
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
			// If the player is attacking, check if they've hit anyone
			if (ourPlayer.getState().equals("Attack")) {
				ourPlayer.attacking();
			}

			// If the user isn't attacking but there are enemies that have been hit, deal
			// with them
			if (ourPlayer.hitEnemies() && !ourPlayer.getState().equals("Attack")) {
				ourPlayer.handleAttack();
			}
		}

		cmanager.updateCoords(dx, dy);
		cmanager.updateEnemies();

	}

	/**
	 * Resets several objects in this class.
	 */
	public void reset() { // TODO add testing?
		ourPlayer.reset();
		cmanager.reset();
		v.reset();
		current_level = 1;
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
	 * Checks if the user has won the game at least one time.
	 * 
	 * @return true if the user has won at least once.
	 */
	public boolean hasWon() {
		return current_level == NUM_LEVELS && cmanager.hasWon();
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

		// Draw the active chunks on the map by extracting all the relevant position blocks
		cmanager.draw(g2);
		cmanager.drawEnemies(g2);

		v.drawVision(g2);
		if (ourPlayer.getHealth() < 10000 && !ourPlayer.isGettingAttacked()) {
			ourPlayer.addHealth(1);
		}

		drawHealthBar(g);

		ourPlayer.draw(g2);
		
		drawStats(g2);
		
		// Saves some memory
		g2.dispose();
	}
	
	/**
	 * Draws the seconds left and enemies left on the screen.
	 * 
	 * @param g2 Graphics to draw on.
	 */
	private void drawStats(Graphics2D g2) {
		g2.setColor(Color.red);

        // Set the custom font
        g2.setFont(customFont);
		final String timeLeft = "Time left: " + String.valueOf(Main.seconds_left) + " seconds";
		g2.drawString(timeLeft, timeX, timeY);
		
		final String enemiesKilled = "Enemies killed: " + String.valueOf(Main.totalEnemiesKilled);
		g2.drawString(enemiesKilled, enemyKillCountX, enemyKillCountY);

		//If adding time, tell the user its happening
		if(addingTime) {
			g2.drawString(addText, addTextX,addTextY);
		}
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
		final int healthPercentage = (int) (((float) ourPlayer.getHealth() / 10000) * 100); // Assuming maximum health is
																						// 10000

		// Draw title
		g.setColor(Color.WHITE);
		String title = "Player Health: " + healthPercentage + "%";
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
	 * Gets the average fps count of the game.
	 *
	 * @return The average FPS count during the programs duration.
	 */
	public double getFPS() {
		int sum = 0;
		for (Integer n : fpsTracker) {
			sum += n;
		}

		return sum / (double) fpsTracker.size();
	}

	/**
	 * Stops the game loop
	 */
	public void stopLoop() {
		isRunning = false;
	}

	/**
	 * Continues the game loop
	 */
	public void continueLoop() {
		isRunning = true;
	}

	/**
	 * @return true if the game loop is running
	 */
	public boolean isRunning() {
		return isRunning;
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

	public static void main(String[] args) {

		Image backgroundImage = null;
		// Load Background Image
		try {
			backgroundImage = ImageIO.read(new File("images/wall.png"));
		} catch (IOException e) {
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

		gp.stopLoop();
		// Testing if gameLoop is paused when it should be
		if (gp.isRunning()) {
			allPassed = false;
			System.err.println("Game loop is running, when it should be stopped!");
		}

		gp.continueLoop();

		// Testing if gameLoop starts again correctly
		if (!gp.isRunning()) {
			allPassed = false;
			System.err.println("Game loop isn't running, when it should be!");
		}

		//Testing wonGame
		if(gp.wonGame()) {
			allPassed = false;
			System.err.println("wonGame said the game was won, when it wasn't!");
		}

		// Sleep main thread for 10 seconds, so the game thread still runs to test FPS
		// tracking
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
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
}