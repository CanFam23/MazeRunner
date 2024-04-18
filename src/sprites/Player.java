package sprites;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import gameTools.GameVariables;

/**
 * <p>
 * Player holds character attributes including images, state (Idle, Move...),
 * facing (N, NE, E...) and methods including loading images, updating state,
 * and drawing.
 * </p>
 *
 * @author Andrew Denegar
 * @author Nick Clouse
 * @author Molly O'Connor
 *
 * @since March 20, 2024
 *
 * @see GameVariables
 */
public class Player implements GameVariables {
	/** location of player sprites. */
	private static final String FILE_LOCATION = "images/";

	/**
	 * Tracks the health of our player.
	 */
	private int health = 10000;

	/**
	 * Draw count is used to track the number of draws that have occurred since the
	 * last animation update.
	 */
	private int drawCount = 0;

	/**
	 * Count of frames that attack has been drawn.
	 */
	private int attackCount = 0;

	/**
	 * Size of the player (this controls the drawn size while PLAYER_WIDTH/HEIGHT
	 * controls the hit box).
	 */
	public static final int SIZE = 3;

	/**
	 * Default the player to be holding a weapon.
	 */
	private boolean holdingWeapon = true;

	/**
	 * True if an animation has started that must be completed.
	 */
	private boolean stateLocked = false;

	/**
	 * True if an animation has started that must be completed.
	 */
	private boolean facingLocked = false;

	/** Set initial player state. */
	private State currentState = State.Idle;

	/** Set initial player direction. */
	private Facing currentFacing = Facing.N;

	/** How many frames that pass before each image switch. */
	private final int FRAMESPERSWITCH = 6;
	
	/**
	 * Keeps track of if the player is getting attacked.
	 */
	private boolean gettingAttacked = false;

	/**
	 * Keeps track of the final attack images to determine when we should switch to the next animation.
	 */
	private Map<Facing, BufferedImage> finalAttackImages;
	
	/**
	 * A map of images that can be accessed by first specifying the player state and
	 * direction faced.
	 */
	private static final Map<State, Map<Facing, List<BufferedImage>>> images = new HashMap<>();

	/**
	 * Enemies the player hit.
	 */
	private final Set<Enemy> hitEnemies = new HashSet<>();

	/**
	 * A map of Facing enumerators and Rectangles.
	 */
	@SuppressWarnings("serial")
	private final Map<Facing, Rectangle> hitboxes = new HashMap<>() {
		{
			put(Facing.E,
					new Rectangle(PLAYER_X + PLAYER_WIDTH / 2, PLAYER_Y, (int) (PLAYER_WIDTH * 1.5), PLAYER_HEIGHT));
			put(Facing.W, new Rectangle(PLAYER_X - PLAYER_WIDTH, PLAYER_Y, (int) (PLAYER_WIDTH * 1.5), PLAYER_HEIGHT));
			put(Facing.N, new Rectangle(PLAYER_X, PLAYER_Y - PLAYER_HEIGHT, PLAYER_WIDTH, (int) (PLAYER_HEIGHT * 1.5)));
			put(Facing.S,
					new Rectangle(PLAYER_X, PLAYER_Y + PLAYER_HEIGHT / 2, PLAYER_WIDTH, (int) (PLAYER_HEIGHT * 1.5)));
			put(Facing.NE, new Rectangle(PLAYER_X + PLAYER_WIDTH / 2, PLAYER_Y - PLAYER_HEIGHT / 3,
					(int) (PLAYER_WIDTH * 1.5), PLAYER_HEIGHT));
			put(Facing.SE, new Rectangle(PLAYER_X + PLAYER_WIDTH / 2, PLAYER_Y + PLAYER_HEIGHT / 3,
					(int) (PLAYER_WIDTH * 1.5), PLAYER_HEIGHT));
			put(Facing.NW, new Rectangle(PLAYER_X - PLAYER_WIDTH, PLAYER_Y - PLAYER_HEIGHT / 3,
					(int) (PLAYER_WIDTH * 1.5), PLAYER_HEIGHT));
			put(Facing.SW, new Rectangle(PLAYER_X - PLAYER_WIDTH, PLAYER_Y + PLAYER_HEIGHT / 3,
					(int) (PLAYER_WIDTH * 1.5), PLAYER_HEIGHT));
		}
	};

	/**
	 * Which way the player is looking when attacking.
	 */
	private Facing attackFacing = Facing.N;

	/**
	 * Load images for each player state.
	 *
	 * @param character_name the name of the player file to be selected (Civilian1,
	 *                       Civilian2, Civilian1(black), etc).
	 */
	public void load_images(String character_name) {
		// Declare spritesheet dimensions
		final int SPRITESHEET_WIDTH = 4;
		final int SPRITESHEET_IDLE_HEIGHT = 2;
		final int SPRITESHEET_MOVE_HEIGHT = 8;

		// Load a spritesheet for each player state
		load_spritesheet(character_name, State.Idle, SPRITESHEET_WIDTH, SPRITESHEET_IDLE_HEIGHT);
		load_spritesheet(character_name, State.Move, SPRITESHEET_WIDTH, SPRITESHEET_MOVE_HEIGHT);
		load_spritesheet(character_name, State.Attack, SPRITESHEET_WIDTH, SPRITESHEET_MOVE_HEIGHT);
		set_final_attack_images();
		load_dead_image(character_name);
	}
	
	/**
	 * Set the final attack images to determine when the animation should stop.
	 */
	public void set_final_attack_images() {
		finalAttackImages = new HashMap<>();
		final Map<Facing, List<BufferedImage>> attackMap = images.get(State.Attack);
		for (Facing direction : Facing.values()) {
			finalAttackImages.put(direction, attackMap.get(direction).get(attackMap.get(direction).size() - 1));
		}
	}

	/**
	 * Load an individual spritesheet.
	 *
	 * @param character_name the name of the player file to be selected (Civilian1,
	 *                       Civilian2, Civilian1(black), etc).
	 * @param playerState    the player state that is to be loaded.
	 * @param xDim           the x dimension of the spritesheet.
	 * @param yDim           the y dimension of the spritesheet.
	 */
	private void load_spritesheet(String character_name, State playerState, int xDim, int yDim) {
		BufferedImage spriteSheet = null;
		// Load the spritesheet file
		if (playerState.toString() != null) {
			final String resource;
			if (holdingWeapon && character_name != "Knight1") {
				resource = FILE_LOCATION + character_name + "_" + playerState.toString() + "(Weapon1)" + ".png";
			} else {
				resource = FILE_LOCATION + character_name + "_" + playerState.toString() + ".png";
			}
			try {
				spriteSheet = ImageIO.read(new File(resource));
			} catch (IOException e) {
				System.err.println("Image not found at '" + resource + "'");
			}
		}
		// Split the spritesheet into individual images.
		// TODO: The image reading process could be improved in the future:
		// - Resize images originally, then you won't have to resize them here
		if (spriteSheet != null) {
			// Create a new HashMap for the specified player state.
			images.put(playerState, new HashMap<>());
			// Save constants used for spritesheet loading
			final int height = spriteSheet.getHeight();
			final int width = spriteSheet.getWidth();
			final int framesPerAnim = xDim * yDim / Facing.values().length;

			// Count and direction will be changed based on the number of the image being
			// loaded.
			int count = 0;
			Facing direction = Facing.values()[count];
			// Create the first list for the original state-facing pair.
			images.get(playerState).put(Facing.S, new LinkedList<BufferedImage>());
			for (int y = 0; y < yDim; y++) {
				for (int x = 0; x < xDim; x++) {
					// Read and resize image
					// Read image
					BufferedImage subImage = spriteSheet.getSubimage(x * width / xDim, y * height / yDim, width / xDim,
							height / yDim);

					// Add to the map
					images.get(playerState).get(direction).add(subImage);

					// Update the count of images added to this particular direction
					count++;
					if (count % framesPerAnim == 0 && count != xDim * yDim) {
						direction = Facing.values()[count / framesPerAnim];
						images.get(playerState).put(direction, new LinkedList<BufferedImage>());
					}
				}
			}
		}
	}
	
	private void load_dead_image(String character_name) {
		BufferedImage spriteSheet = null;
		final String resource;
		resource = FILE_LOCATION + character_name + "_" + "Dead" + ".png";
		try {
			spriteSheet = ImageIO.read(new File(resource));
		} catch (IOException e) {
			System.err.println("Image not found at '" + resource + "'");
		}
		images.put(State.Dead, new HashMap<>());
		images.get(State.Dead).put(Facing.N, new LinkedList<BufferedImage>());
		images.get(State.Dead).get(Facing.N).add(spriteSheet);
	}
	
	/**
	 * Checks if the player hits any enemies when they attack.
	 *
	 * Uses the attack hitboxes and enemy hitbox to make rectangles and check for
	 * collision between the two. If there is a collision, add the enemy to hit
	 * enemies set.
	 */
	public void attacking() {
		final Rectangle hitbox = hitboxes.get(currentFacing);
		if (Enemy.activeEnemies.size() != 0 && Enemy.enemies.size() != 0) {
			for (Enemy e : Enemy.activeEnemies) {
				final int[] eCoords = e.getPosition();
				final Rectangle eHitbox = new Rectangle(eCoords[0], eCoords[1], e.getWidth(), e.getHeight());
				if (hitbox.intersects(eHitbox)) {
					hitEnemies.add(e);
					attackFacing = currentFacing;
					break;
				}
			}
		}
	}

	/**
	 * Handles the player attack and knocks back all enemies the player hit.
	 */
	public void handleAttack() {
		for (Enemy e : hitEnemies) {
			e.subtractHitCount(1);
			e.knockback(attackFacing);
		}
		hitEnemies.clear();
	}

	/**
	 * Checks if any enemies are in hitEnemies Set.
	 *
	 * @return If any enemies are in hitEnemies Set.
	 */
	public boolean hitEnemies() {
		return hitEnemies.size() != 0;
	}

	/**
	 * Update the direction that our player is facing.
	 *
	 * @param up    The up arrow key is pressed.
	 * @param down  The down arrow key is pressed.
	 * @param right The right arrow key is pressed.
	 * @param left  The left arrow key is pressed.
	 */
	public void updateState(boolean up, boolean down, boolean right, boolean left) {
		// Set the player state (idle or move)
		if (!up && !down && !right && !left) {
			currentState = State.Idle;
			return;
		} else {
			currentState = State.Move;
		}
		// Set the direction headed
		String dir = "";
		if (up && !down) {
			dir += "N";
		} else if (down && !up) {
			dir += "S";
		}

		if (left && !right) {
			dir += "W";
		} else if (right && !left) {
			dir += "E";
		}

		if (!dir.equals("")) {
			currentFacing = Facing.valueOf(dir);
		}
	}

	/**
	 * @return currentState in string format.
	 */
	public String getState() {
		return currentState.toString();
	}

	/**
	 * @return currentFacing in string format.
	 */
	public String getFacing() {
		return currentFacing.toString();

	}

	/**
	 * Change the direction the player is facing.
	 *
	 * @param direction to set.
	 */
	public void setFacing(Facing direction) {
		currentFacing = direction;
	}

	/**
	 * Change the state of the player.
	 *
	 * @param playerState to set.
	 */
	public void setState(State playerState) {
		currentState = playerState;
	}

	/**
	 * Draw our player. Draw handles the switching from one image in a sequence to
	 * the next.
	 *
	 * @param g 2Dgraphics to draw on.
	 */
	public void draw(Graphics2D g) {
		final BufferedImage myImage = images.get(currentState).get(currentFacing).get(0);
		final int imageXAdjustment = (myImage.getWidth() * SIZE - PLAYER_WIDTH) / 2;
		final int imageYAdjustment = (myImage.getHeight() * SIZE - PLAYER_HEIGHT) / 2;
		if (currentState == State.Attack) {
			attackCount += 1;
		}
		if (drawCount < FRAMESPERSWITCH - 1) { // For x ticks of the game loop, draw the same image.
			g.drawImage(myImage, PLAYER_X - imageXAdjustment, PLAYER_Y - imageYAdjustment,
					PLAYER_WIDTH + imageXAdjustment * 2, PLAYER_HEIGHT + imageYAdjustment * 2, null);
			drawCount++;
		} else { // Then, switch the image to the next one in the sequence.
			BufferedImage img = images.get(currentState).get(currentFacing).remove(0);
			g.drawImage(img, PLAYER_X - imageXAdjustment, PLAYER_Y - imageYAdjustment,
					PLAYER_WIDTH + imageXAdjustment * 2, PLAYER_HEIGHT + imageYAdjustment * 2, null);
			images.get(currentState).get(currentFacing).add(img);
			drawCount = 0;
			// Check currentState before test to increase performance with a quicker test.
			if (currentState == State.Attack && img == finalAttackImages.get(currentFacing)) {
				unlockState();
				unlockFacing();
				currentState = State.Idle;
				attackCount = 0;
			}
		}

		// TODO remove
		// Show Player hitbox
//		g.setColor(Color.RED);
//		g.drawRect(PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
//		g.setColor(Color.BLUE);
//		g.drawRect(PLAYER_X - imageXAdjustment, PLAYER_Y - imageYAdjustment,
//					PLAYER_WIDTH + imageXAdjustment * 2, PLAYER_HEIGHT + imageYAdjustment * 2);
		
		// Show attacking area
//		g.setColor(Color.blue);
//		if (getState().equals("Attack")) {
//			g.draw(hitboxes.get(currentFacing));
//
//		}

	}

	/**
	 * Reset player state and direction.
	 */
	public synchronized void reset() {
		stateLocked = false;
		facingLocked = false;
		attackCount = 0;
		drawCount = 0;
		setState(State.Idle);
		setFacing(Facing.N);
		health = 10000;
	}

	/**
	 * Set the drawcount back to zero.
	 */
	public void resetDrawCount() {
		drawCount = 0;
	}

	/**
	 * Return whether or not the player's state is currently locked.
	 * 
	 * @return boolean true or false.
	 */
	public boolean isStateLocked() {
		return stateLocked;
	}
	
	/**
	 * Return whether or not the player's direction is currently locked.
	 * 
	 * @return boolean true or false.
	 */
	public boolean isFacingLocked() {
		return facingLocked;
	}
	/**
	 * Set the state to be fixed.
	 */
	public void lockState() {
		stateLocked = true;
	}

	/**
	 * Set facing to be fixed.
	 */
	public void lockFacing() {
		facingLocked = true;
	}

	/**
	 * Allow state to be changed.
	 */
	public void unlockState() {
		stateLocked = false;
	}

	/**
	 * Allow facing to be changed.
	 */
	public void unlockFacing() {
		facingLocked = false;
	}

	/**
	 * Subtract health from the player.
	 *
	 * @param amount The amount of health to subtract.
	 */
	public void subtractHealth(int amount) {
		health -= amount;
	}

	/**
	 * Subtract health from the player.
	 *
	 * @param amount The amount of health to subtract.
	 */
	public void addHealth(int amount) {
		health += amount;
	}

	/**
	 * Set player health back to 100.
	 */
	public void resetHealth() {
		health = 10000;
	}

	/**
	 * Return the current health of player.
	 * 
	 * @return int current health of the player.
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * While the player is being hit by an enemy, this will return true.
	 * 
	 * @return boolean if the player is getting attacked.
	 */
	public boolean isGettingAttacked() {
		return gettingAttacked;
	}

	/**
	 * Set whether or not the player is attacking.
	 * 
	 * @param t boolean true to set the player to be attacking and false to be not attacking.
	 */
	public void setGettingAttacked(boolean t) {
		gettingAttacked = t;
	}

	///////////////// BELOW CODE IS USED JUST FOR TESTING PURPOSES
	///////////////// //////////////////
	/**
	 * Player code below is used for testing image loading. This code is all at the
	 * bottom because it shouldn't be used besides testing
	 */
	private static JFrame frame;
	private static JPanel panel;
	private static BufferedImage image;

	/**
	 * Initialize a basic GUI for testing.
	 */
	public static void initializeGUI() {
		frame = new JFrame("Image Display");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setBounds(10, 10, 400, 400);

		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			/**
			 * Paint component.
			 *
			 * @param g graphics to draw on.
			 */
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// Draw the current image, if not null
				if (image != null) {
					final int sizeMult = 20;
					g.drawImage(image, 0, 0, image.getWidth() * sizeMult, image.getHeight() * sizeMult, this);
					g.setColor(Color.RED);
					g.drawRect(0, 0, image.getWidth() * sizeMult, image.getHeight() * sizeMult);
				}
			}
		};

		frame.add(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

	/**
	 * Displayed given image.
	 *
	 * @param newImage image to draw.
	 */
	public static void displayImage(BufferedImage newImage) {
		image = newImage;
		panel.repaint(); // This will trigger paintComponent to redraw the image
	}

	/**
	 * Main method. Used for testing.
	 *
	 * @param args arguments passed
	 */
	public static void main(String[] args) {

		Player p1 = new Player();

		// Choose the player model analyzed
		p1.load_images("Civilian1(black)");
//		p1.load_images("Knight");

		// Start: Unit Testing
		boolean allPassed = true;
		// Update the state as if all keys were pressed at the same time
		// By default, this should set the current direction faced to North
		p1.updateState(true, true, true, true);
		if (p1.currentFacing != Facing.N) {
			System.err.println("When updateState() registers all keys being pressed, Facing should be set to north.");
			allPassed = false;
		}
		// Now set key press
		p1.updateState(false, false, true, false);
		if (p1.currentFacing != Facing.E) {
			System.err.println("Key press of right should change the player to be facing east.");
			allPassed = false;
		}
		p1.updateState(false, false, false, false);
		if (p1.currentState != State.Idle) {
			System.err.println("When no key is pressed, the current state should be idle.");
			allPassed = false;
		}
		p1.setFacing(Facing.SE);
		if (p1.currentFacing != Facing.SE) {
			System.err.println("setFacing() failed to set the direction of the player to southeast.");
			allPassed = false;
		}
		/*
		 * stateLocked = false; facingLocked = false; attackCount = 0; drawCount = 0;
		 * setState(State.Idle); setFacing(Facing.N); health = 10000;
		 */
		// Testing reset
		p1.reset();
		if (p1.stateLocked || p1.facingLocked || p1.attackCount != 0 || p1.drawCount != 0
				|| p1.currentState != State.Idle || p1.currentFacing != Facing.N || p1.health != 10000) {
			System.err.println("reset() failed to reset 1 or more variables!");
			allPassed = false;
		}

		p1.attacking();
		if (p1.hitEnemies.size() != 0) {
			System.err.println("A enemy was hit when no enemies existed!");
			allPassed = false;
		}

		// Make a enemy and add it to enemy lists
		p1.setFacing(Facing.W);
		MageFactory mf = MageFactory.getInstance();
		Enemy m = mf.createEnemy(PLAYER_X, PLAYER_Y);
		Enemy.activeEnemies.add(m);
		Enemy.enemies.add(m);
		// We should get a hit enemy now.
		p1.attacking();
		if (p1.hitEnemies.size() == 0) {
			System.err.println("No enemies were hit, when one should've been!");
			allPassed = false;
		}

		// Hit enemies should return true here
		if (!p1.hitEnemies()) {
			System.err.println("Hit enemies should return true, but it returned false!");
			allPassed = false;
		}

		final int preHitCount = m.getHitCount();
		p1.handleAttack();
		// Since we called handleAttack, the hit count of the enemy should have went
		// down one
		for (Enemy e : p1.hitEnemies) {
			if (e.getHitCount() != preHitCount) {
				System.err.println("Enemies hit count should have decreased!");
				allPassed = false;
			}
		}

		if (allPassed) {
			System.out.println("All cases passed, good job.");
		} else {
			System.out.println("At least one case failed");
		}

		// Start: Image testing
		initializeGUI();

		// Change these two variables to modify the animations tested
		State playerState = State.Dead; // Test the player state images (Move, Idle, etc.)
		Facing direction = Facing.N; // Test the direction the player is facing
		int speed = (int) (0.1 * 1000); // Set seconds (first number) between each image.

		while (true) {
			BufferedImage img = Player.images.get(playerState).get(direction).remove(0);
			displayImage(img);
			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Player.images.get(playerState).get(direction).add(img);
		}
	}
}
