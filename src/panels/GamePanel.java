package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import chunks.ChunkManager;
import gameTools.GameVariables;
import gameTools.KeyHandler;
import main.Main;
import sprites.Player;
import sprites.Player.State;

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
	private final List<Integer> fpsTracker = new ArrayList<Integer>();

	/** Number to displace character by when needed. */
	private final int displacement = 1;

	/** Speed of player. */
	private final int speed = 6;

	/** Number of levels in game. */
	private final int NUM_LEVELS = 3;

	/** Current level the player is on. */
	private int current_level = 0;

	/**
	 * Create our player. Initialize later to offer player selection of different
	 * characters.
	 */
	private Player ourPlayer;

	/** Visibility object, used to change visibility as time goes on. */
	private Visibility v = new Visibility();

	/** Thread used for our game */
	private Thread gameThread;

	/** Keeps track of game state (Running vs not running). */
	private boolean isRunning = true;

	private Image backgroundImage;

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

		cmanager = new ChunkManager();
		cmanager.loadLevel(current_level);

		// Create our player and load the images
		ourPlayer = new Player();
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
		while (isRunning == true) {
			long now = System.nanoTime();
			delta += (now - lastime) / ns;
			lastime = now;

			if (delta >= 1) {

				update();
				repaint();

				frames++;
				delta--;
				if (System.currentTimeMillis() - time >= 1000) {
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
			// Disable player movements when end block is reached
			keyH.upPressed = false;
			keyH.downPressed = false;
			keyH.rightPressed = false;
			keyH.leftPressed = false;
			if (current_level == NUM_LEVELS) {
				System.out.print("");
				Main.gameOverPanel(true);
			} else {
				Main.showNextLevelPanel(true);
				while (Main.otherPanelRunning()) {
					System.out.print("");
				}
			}
			reset();
			current_level++;
			cmanager.loadLevel(current_level);
			Main.showGamePanel();
			Main.resetTime();
			continueLoop();
		}
		if (ourPlayer.getHealth() <= 0) {
//			ChunkManager.setStartLocation();
		}
		

		// Move Player
		int dx = 0;
		int dy = 0;

		boolean topCollided = false;
		boolean botCollided = false;
		boolean rightCollided = false;
		boolean leftCollided = false;

		List<Collision> collisions = cmanager.checkCollision();
		
		cmanager.updateEnemies();

		// If theres at least one collision
		if (collisions.size() > 0) {
			for (Collision collisionNum : collisions) {
				// Handle collision
				switch (collisionNum) {
				// Depending on what side is colliding, this changes dx or dy by one to make the
				// player 'bounce off' the wall.
				case LEFT_SIDE: // Collided from the left
					if (!leftCollided) {
						dx -= displacement;
					}

					leftCollided = true;
					break;
				case RIGHT_SIDE: // Collided from the right
					if (!rightCollided) {
						dx += displacement;
					}

					rightCollided = true;
					break;
				case TOP_SIDE: // Collided from the top
					if (!topCollided) {
						dy -= displacement;
					}

					topCollided = true;
					break;
				case BOTTOM_SIDE: // Collided from the bottom
					if (!botCollided) {
						dy += displacement;
					}
					botCollided = true;
					break;
				case BOTTOM_RIGHT_CORNER: // Collided from right and bottom
					if (!botCollided && !rightCollided) {
						dy += displacement * 2;
						dx += displacement;
					}
					botCollided = true;
					rightCollided = true;
					break;
				case BOTTOM_LEFT_CORNER: // Collided from left and bottom
					if (!botCollided && !leftCollided) {
						dy += displacement * 2;
						dx -= displacement;
					}
					botCollided = true;
					leftCollided = true;
					break;
				case TOP_RIGHT_CORNER: // Collided from right and top
					if (!topCollided && !rightCollided) {
						dy -= displacement * 2;
						dx += displacement;
					}
					rightCollided = true;
					topCollided = true;
					break;
				case TOP_LEFT_CORNER: // Collided from left and top
					if (!topCollided && !leftCollided) {
						dy -= displacement * 2;
						dx -= displacement;
					}
					leftCollided = true;
					topCollided = true;
					break;
				default:
					break;
				}
			}

		}

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
		cmanager.updateCoords(dx, dy);
		if (!ourPlayer.isStateLocked()) {
			ourPlayer.updateState(keyH.upPressed, keyH.downPressed, keyH.rightPressed, keyH.leftPressed);
		}
		
		if (keyH.spacePressed) {
			if (ourPlayer.getState() != "Attack") {
				// Set our player to be attacking
				ourPlayer.setState(sprites.Player.State.Attack);
				ourPlayer.attack();
				ourPlayer.resetDrawCount();
			}
			ourPlayer.lockState();
			ourPlayer.lockFacing();
		}
		v.updateRadius();
	}

	/**
	 * Resets several objects in this class.
	 */
	public void reset() { // TODO add testing?
		ourPlayer.reset();
		cmanager.reset();
		v.reset();
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

		// Draw map
		cmanager.draw(g2);
		cmanager.drawEnemies(g2);


		ourPlayer.draw(g2);

		v.drawVision(g2);

		// Saves some memory
		g2.dispose();
	}

	/**
	 * @return the average FPS count during the programs duration
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean allPassed = true;

		final GamePanel gp = new GamePanel(backgroundImage);

		// Making sure game thread hasn't started yet
		if (gp.threadStarted() == true) {
			allPassed = false;
			System.err.println("gameThread started when it shouldn't have!");
		}

		gp.startGameThread();

		// Testing if game thread correctly started
		if (gp.threadStarted() != true) {
			allPassed = false;
			System.err.println("Failed to start gameThread!");
		}

		// Testing if gameLoop is running
		if (gp.isRunning() != true) {
			allPassed = false;
			System.err.println("Game loop isn't running, when it should be!");
		}

		gp.stopLoop();
		// Testing if gameLoop is paused when it should be
		if (gp.isRunning() == true) {
			allPassed = false;
			System.err.println("Game loop is running, when it should be stopped!");
		}

		gp.continueLoop();

		// Testing if gameLoop starts again correctly
		if (gp.isRunning() != true) {
			allPassed = false;
			System.err.println("Game loop isn't running, when it should be!");
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