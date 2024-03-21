/*
 * GamePanel.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: February 24, 2024
 * 
 * Desc:
 * This Java class handles creating the screen, and has the main game loop in it. 
 * It also handles the top level updating and rendering of all objects on the screen
 */
package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, GameVariables {
	private static final long serialVersionUID = 123455L;

	// Screen Settings

	private final ChunkManager cmanager;

	private final KeyHandler keyH = new KeyHandler(); // Uses to detect key presses/releases

	private final List<Integer> fpsTracker = new ArrayList<Integer>();

	private final int displacement = 1;
	private final int speed = 6;

	// Create our player. Initialize later to offer player selection of different
	// characters.
	private Player ourPlayer;

	private Thread gameThread; // Thread is a thread of execution in a program

	private boolean isRunning = true;

	public GamePanel() {
		// this.setPreferredSize(new Dimension(JFrame.MAXIMIZED_HORIZ,
		// JFrame.MAXIMIZED_VERT));
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);

		this.addKeyListener(keyH);
		this.setFocusable(true);

		cmanager = new ChunkManager();
		cmanager.loadLevel(1);

		// Create our player and load the images
		ourPlayer = new Player();
		ourPlayer.load_images("Civilian1(black)");

	}

	/**
	 * Initializes new thread and starts it
	 */
	public void startGameThread() {

		gameThread = new Thread(this);
		gameThread.start();
	}

	/**
	 * When we start the gameThread, this function is ran Updates and repaints
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
	 * Updates location of walls based on key presses and collisions
	 */
	public void update() {
		// If the end is found, stop game loop and display win screen
		if (cmanager.endFound()) {
			new GameOverWIN();
			Main.closeMainWindow();
			isRunning = false;
			return;
		}

		int dx = 0;
		int dy = 0;

		boolean topCollided = false;
		boolean botCollided = false;
		boolean rightCollided = false;
		boolean leftCollided = false;

		List<Collision> collisions = cmanager.checkCollision();

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
		ourPlayer.updateState(keyH.upPressed, keyH.downPressed, keyH.rightPressed, keyH.leftPressed);
	}

	/**
	 * Paints all elements on screen
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		final Graphics2D g2 = (Graphics2D) g;

		// Draw map
		cmanager.draw(g2);

		ourPlayer.draw(g2);

		// Saves some memory
		g2.dispose();
	}

	/**
	 * @return double the average FPS count during the programs duration
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
	 * @return boolean if the game loop is running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Checks if thread has started
	 * 
	 * @return boolean if thread has started or not
	 */
	public boolean threadStarted() {
		if (gameThread == null) {
			return false;
		}
		return gameThread.isAlive();
	}

	public static void main(String[] args) {

		boolean allPassed = true;

		GamePanel gp = new GamePanel();

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

		int avgFPS = (int) gp.getFPS();
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