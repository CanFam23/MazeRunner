package sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

import gameTools.CollisionDetection;
import gameTools.GameVariables;

import java.awt.*;

import javax.imageio.ImageIO;

/**
 * <p>
 * Player holds character attributes including images, state (Idle, Move...),
 * facing (N, NE, E...) and methods including loading images, updating state,
 * and drawing.
 * </p>
 * 
 * @author Andrew Denegar, Nick Clouse, Molly O'Connor
 * 
 * @since March 20, 2024
 * 
 * @see GameVariables
 */
public class Player implements GameVariables {
	/** location of player sprites. */
	private static final String FILE_LOCATION = "images/";

	/**
	 * Player States.
	 */
	public enum State {
		/** Constant for when player is idle. */
		Idle,
		/** Constant for when player is moving. */
		Move,
		/** When the player is attacking. */
		Attack;
	}

	/**
	 * The direction the player is currently facing.
	 */
	public enum Facing {
		/** Constant for the player facing south. */
		S,
		/** Constant for the player facing southeast. */
		SE,
		/** Constant for the player facing east. */
		E,
		/** Constant for the player facing northeast. */
		NE,
		/** Constant for the player facing north. */
		N,
		/** Constant for the player facing northwest. */
		NW,
		/** Constant for the player facing west. */
		W,
		/** Constant for the player facing southwest. */
		SW
	}

	/**
	 *  Tracks the health of our player
	 */
    private int health = 8000;

	/**
	 * Draw count is used to track the number of draws that have occurred since the last animation update.
	 */
	private int drawCount = 0;
	
	/**
	 * Count of frames that attack has been drawn.
	 */
	private int attackCount = 0;
	
	/**
	 * Size of the player (this controls the drawn size while PLAYER_WIDTH/HEIGHT controls the hit box).
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
	
	private final int framesPerSwitch = 6;

	/**
	 * A map of images that can be accessed by first specifying the player state and
	 * direction faced.
	 */
	private Map<State, Map<Facing, List<BufferedImage>>> images = new HashMap<>();
	
	/**
	 * A map of Facing enumerators and Rectangles
	 */
	private Map<Facing,Rectangle> hitboxes = new HashMap<>();
	
	private Set<Enemy> hitEnemies = new HashSet<>();
	
	public Player() {
		//Add values to hitboxes
		hitboxes.put(Facing.E, new Rectangle(PLAYER_X + PLAYER_WIDTH/2, PLAYER_Y, (int)(PLAYER_WIDTH*1.5), PLAYER_HEIGHT));
		hitboxes.put(Facing.W, new Rectangle(PLAYER_X - PLAYER_WIDTH, PLAYER_Y, (int)(PLAYER_WIDTH*1.5), PLAYER_HEIGHT));
		hitboxes.put(Facing.N, new Rectangle(PLAYER_X, PLAYER_Y-PLAYER_HEIGHT/2, PLAYER_WIDTH, PLAYER_HEIGHT));
		hitboxes.put(Facing.S, new Rectangle(PLAYER_X, PLAYER_Y+PLAYER_HEIGHT/2, PLAYER_WIDTH, PLAYER_HEIGHT));
		hitboxes.put(Facing.NE, new Rectangle(PLAYER_X+ PLAYER_WIDTH/2, PLAYER_Y-PLAYER_HEIGHT/3, (int)(PLAYER_WIDTH*1.25), PLAYER_HEIGHT));
		hitboxes.put(Facing.SE, new Rectangle(PLAYER_X+ PLAYER_WIDTH/2, PLAYER_Y+PLAYER_HEIGHT/3, (int)(PLAYER_WIDTH*1.25), PLAYER_HEIGHT));
		hitboxes.put(Facing.NW, new Rectangle(PLAYER_X- PLAYER_WIDTH, PLAYER_Y-PLAYER_HEIGHT/3, (int)(PLAYER_WIDTH*1.25), PLAYER_HEIGHT));
		hitboxes.put(Facing.SW, new Rectangle(PLAYER_X- PLAYER_WIDTH, PLAYER_Y+PLAYER_HEIGHT/3, (int)(PLAYER_WIDTH*1.25), PLAYER_HEIGHT));

	}

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
			if (holdingWeapon)
				resource = FILE_LOCATION + character_name + "_" + playerState.toString() + "(Weapon1)" + ".png";
			else
				resource = FILE_LOCATION + character_name + "_" + playerState.toString() + ".png";
			try {
				spriteSheet = ImageIO.read(new File(resource));
			} catch (IOException e) {
				System.out.println("Image not found at '" + resource + "'");
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
					// Now that we have the image split from the other part, let's remove the
					// whitespace
					images.get(playerState).get(direction).add(subImage);

					count++;
					if (count % framesPerAnim == 0 && count != xDim * yDim) {
						direction = Facing.values()[count / framesPerAnim];
						images.get(playerState).put(direction, new LinkedList<BufferedImage>());
					}
				}
			}
		}
	}
	
	public void attacking() {
		Rectangle hitbox = hitboxes.get(currentFacing);
		int x = (int) hitbox.getX();
		int y = (int) hitbox.getY();
		int width = (int) hitbox.getHeight();
		int height = (int) hitbox.getWidth();
		
		final int[] xCoords = new int[] { x, x + width,
				x + width, x };
		final int[] yCoords = new int[] { y, y,
				y + height, y + height };
		
		if (Enemy.activeEnemies.size() != 0 && Enemy.enemies.size() != 0) {
			for(Enemy e: Enemy.activeEnemies) {
				final int[] eCoords = e.getPosition();
				final int[] eXCoords = new int[] {eCoords[0],eCoords[0]+e.getWidth(),eCoords[0]+e.getWidth(),eCoords[0]};
				final int[] eYCoords = new int[] {eCoords[1],eCoords[1],eCoords[1]+e.getHeight(),eCoords[1]+e.getHeight()};
				if(CollisionDetection.getCollision(xCoords, yCoords, eXCoords, eYCoords) != Collision.NO_COLLISION) {
//					System.out.println("Hit Enemy " + e);
					e.subtractHitCount(1);
					break;
				}
			}
		}
	}
	
	public void handleAttack() {
		if(hitEnemies.size() != 0 && currentState == State.Attack && attackCount == framesPerSwitch * 2) {
			Enemy.enemiesHit(hitEnemies);
			System.out.println("Hit Enemy ");

			hitEnemies.clear();
		}
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
		if (up == false && down == false && right == false && left == false) {
			currentState = State.Idle;
			return;
		} else {
			currentState = State.Move;
		}
		// Set the direction headed
		String dir = "";
		if (up && !down)
			dir += "N";
		else if (down && !up)
			dir += "S";

		if (left && !right)
			dir += "W";
		else if (right && !left)
			dir += "E";

		if (!dir.equals(""))
			currentFacing = Facing.valueOf(dir);
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
	 * @param g 2Dgraphics to draw on
	 */
	public void draw(Graphics2D g) {		
		final BufferedImage myImage = images.get(currentState).get(currentFacing).get(0);
		final int imageXAdjustment = (int) ((myImage.getWidth() * SIZE - PLAYER_WIDTH) / 2);
		final int imageYAdjustment = (int) ((myImage.getHeight() * SIZE - PLAYER_HEIGHT) / 2);
		if (currentState == State.Attack)
			attackCount += 1;
		
		if (drawCount < framesPerSwitch - 1) { // For x ticks of the game loop, draw the same image.
			g.drawImage(myImage, PLAYER_X - imageXAdjustment, PLAYER_Y - imageYAdjustment, PLAYER_WIDTH + imageXAdjustment * 2,PLAYER_HEIGHT + imageYAdjustment * 2, null);
			g.setColor(Color.red);
			g.drawRect(PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
			drawCount++;
		} else { // Then, switch the image to the next one in the sequence.
			BufferedImage img = images.get(currentState).get(currentFacing).remove(0);
			g.drawImage(img, PLAYER_X - imageXAdjustment, PLAYER_Y - imageYAdjustment, PLAYER_WIDTH + imageXAdjustment * 2, PLAYER_HEIGHT + imageYAdjustment * 2, null);
			g.setColor(Color.red);
			g.drawRect(PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
			images.get(currentState).get(currentFacing).add(img);
			drawCount = 0;
		}
		
		//TODO remove
		g.setColor(Color.RED);
		g.drawRect(PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
		
		g.setColor(Color.blue);
		if(getState().equals("Attack")) {
			g.draw(hitboxes.get(currentFacing));
			

		}
		
		
		if (attackCount == framesPerSwitch * 4) {
			unlockState();
			unlockFacing();
			currentState = State.Idle;
			attackCount = 0;
		}

	}

	// TODO add testing
	/**
	 * Reset player state and direction
	 */
	public void reset() {
		setState(State.Idle);
		setFacing(Facing.N);
		health = 8000;
	}
	
	public void resetDrawCount() {
		drawCount = 0;
	}
	
	public boolean isStateLocked() {
		return stateLocked;
	}
	
	public boolean isFacingLocked() {
		return facingLocked;
	}
	
	/**
	 * Set the state to be fixed
	 */
	public void lockState() {
		stateLocked = true;
	}
	
	/**
	 * Set facing to be fixed
	 */
	public void lockFacing() {
		facingLocked = true;
	}
	
	/**
	 * Allow state to be changed
	 */
	public void unlockState() {
		stateLocked = false;
	}
	
	/**
	 * Allow facing to be changed
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
     * Set player health back to 100 
     */
    public void resetHealth() {
    	health = 100; 
    }
    
	/**
     * Return the current health of player 
     */
    public int getHealth() {
    	return health;
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	 * Main method
	 * 
	 * @param args arguments passed
	 */
	public static void main(String[] args) {

		Player p1 = new Player();

		// Choose the player model analyzed
		p1.load_images("Civilian1(black)");

		// Start: Unit Testing
		boolean all_passed = true;
		// Update the state as if all keys were pressed at the same time
		// By default, this should set the current direction faced to North
		p1.updateState(true, true, true, true);
		if (p1.currentFacing != Facing.N) {
			System.out.println("When updateState() registers all keys being pressed, Facing should be set to north.");
			all_passed = false;
		}
		// Now set key press
		p1.updateState(false, false, true, false);
		if (p1.currentFacing != Facing.E) {
			System.out.println("Key press of right should change the player to be facing east.");
			all_passed = false;
		}
		p1.updateState(false, false, false, false);
		if (p1.currentState != State.Idle) {
			System.out.println("When no key is pressed, the current state should be idle.");
			all_passed = false;
		}
		p1.setFacing(Facing.SE);
		if (p1.currentFacing != Facing.SE) {
			System.out.println("setFacing() failed to set the direction of the player to southeast.");
			all_passed = false;
		}
		if (all_passed) {
			System.out.println("All cases passed, good job.");
		} else {
			System.out.println("At least one case failed");
		}

		// Start: Image testing
		initializeGUI();

		// Change these two variables to modify the animations tested
		State playerState = State.Attack; // Test the player state images (Move, Idle, etc.)
		Facing direction = Facing.E; // Test the direction the player is facing
		int speed = (int) (0.1 * 1000); // Set seconds (first number) between each image.

		while (true) {
			BufferedImage img = p1.images.get(playerState).get(direction).remove(0);
			displayImage(img);
			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p1.images.get(playerState).get(direction).add(img);
		}
	}
}
