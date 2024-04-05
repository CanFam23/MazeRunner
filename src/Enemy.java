package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * 	This file holds Enemy.java, Mage, Ghost, EnemyFactory, MageFactory, and GhostFactory. These are the classes used to
 * 	create enemies including loading their images, storing their locations, and drawing to the screen. Mage and Ghost
 *  are child classes of Enemy and MageFactory and GhostFactory are child classes of EnemyFactory.
 * 
 * @author Andrew Denegar
 * @since March 26, 2024
 *
 */


/*
 * For better moving:
 * 
 * Get active chunks
 * Find chunk enemy is in
 * In that chunk, check walls to see if moving would make it closer to enemy without it colliding to the wall
 * Can move 8 directions(Straight and diagonal)
 * If it would be closer, move it, else, do nothing
 * 
 * If not tracking player, move from left to right, if move would result in collision, turn the opposite way
 * 
 * 
 * Loading enemies in:
 * Put numbers in maze that represent each enemy (5,6,etc)
 * load_level could return the enemies list, unless we wanted chunk manager to manage enemies
 * get active enemies, those currently on the screen
 * update/move accordingly
 */

abstract class Enemy implements GameVariables {
	
	protected static final int X_DETECTION_RANGE = SCREEN_WIDTH/3;
	protected static final int Y_DETECTION_RANGE = (int) (SCREEN_HEIGHT/2.5);
	
	/** How many times the enemy is drawn before the next image is selected */
	protected static final int DRAW_FRAMES = 10;

	
	// Attributes that will be set in the constructor of child classes. If capitalized, it will not change.
	protected int WIDTH;
	protected int HEIGHT;
	protected int position_x;
	protected int position_y;
	protected int speed;
	
	/** draw count keeps track of how many times draw has been called before switching to the next image in the list. */
	protected int drawCount = 0;
	
	// Set initial direction faced and sprite state
	protected State currentState = State.Idle;
	protected Facing currentFacing = Facing.E;
	/** Holds all Buffered images for each state. Images do not change based on, 'Facing' except for being flipped left and right. */
	protected Map<State, List<BufferedImage>> images;
	
	/**
	 * What the sprite is currently doing.
	 */
	public enum State {
		Idle, Move, Attack, Dead
	}

	/**
	 * The direction the sprite is currently facing.
	 */
	public enum Facing {
		S, SE, E, NE, N, NW, W, SW
	}
	
	/**
	 * Move decides how the enemy should move based on the player position (GameVariables), current position, and offset.
	 * TODO: Implement more intelligent movement (Player detection area, maze traversal strategies).
	 * 
	 * @param offset the x and y coordinates respectively indicating the difference in position from the original maze to the current maze.
	 */
	public void move(int[] offset) {
		
		// Store the position based on map movement
		final int final_x = position_x + offset[0];
		final int final_y = position_y + offset[1];
		
		//Checks if player is in range of enemy
		if(inRangeOfPlayer(final_x,final_y)) {
			System.out.println("Attack");
		}else {
		    //If player is within the detection range, enemy should move towards the player	
			if(Math.abs(PLAYER_X - final_x) <= X_DETECTION_RANGE && Math.abs(PLAYER_Y - final_y) <= Y_DETECTION_RANGE) {
				int dx = 0;
				int dy = 0;
				if (PLAYER_X > final_x + PLAYER_WIDTH / 2)
					dx = speed;
				else if (PLAYER_X < final_x - PLAYER_WIDTH / 2)
					dx = -speed;
				if (PLAYER_Y > final_y + PLAYER_HEIGHT / 2)
					dy = speed;
				else if (PLAYER_Y < final_y - PLAYER_HEIGHT / 2)
					dy = -speed;
				// Now that we have the movement that will happen, adjust the enemy's state/direction
				if (dx != 0 || dy != 0) // NOTE: This will have to be modified when attacking and/or other modifications are made.
					currentState = State.Move;
				else
					currentState = State.Idle;
				if (dx > 0) 
					currentFacing = Facing.E;
				else if (dx < 0)
					currentFacing = Facing.W;
				// Update position (movement)
				update_coords(dx, dy);
			}
		}
		
		
		
	}
	
	/**
	 * Checks if the enemy is in range of the player. 
	 * 
	 * @param x x coordinate to use
	 * @param y y coordinate to use
	 * @return true if enemy is in range of the player
	 */
	public boolean inRangeOfPlayer(int x, int y) {
		
		// Convert 2D array of coords into arrays of x coords and y coords
		final int[] otherXCoords = new int[] { x, x + WIDTH,
				x + WIDTH, x};
		final int[] otherYCoords = new int[] { y, position_y,
				y + HEIGHT, y + HEIGHT };

		// if player top left x < enemy bottom right x
		if (playerXCoords[0] <= otherXCoords[2]
				// if player bottom right x > enemy top left x
				&& playerXCoords[2] >= otherXCoords[0]
				// if player top left y < enemy bottom right y
				&& playerYCoords[0] <= otherYCoords[2]
				// if player bottom right y > enemy top left y
				&& playerYCoords[2] >= otherYCoords[0]) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Update the position of the enemy. 
	 * 
	 * @param dx horizontal shift in the enemy's position
	 * @param dy vertical shift in the enemy's position
	 */
	private void update_coords(int dx, int dy) {
		position_x += dx;
		position_y += dy;
	}
	
	//TODO: Implement more than two directions to be drawn
	/** Draw the enemy */
	public void draw(Graphics2D g, int[] offset) {
		// Store position based on movement of the map
		final int final_x = offset[0] + position_x;
		final int final_y = offset[1] + position_y;
		if (drawCount < DRAW_FRAMES) { // Draw the current image and increment drawCount
			if (currentFacing == Facing.E) { // Facing right
				g.drawImage(images.get(currentState).get(0), final_x + WIDTH, final_y, -WIDTH,
						HEIGHT, null);
			} else { // Facing left
				g.drawImage(images.get(currentState).get(0), final_x, final_y, WIDTH,
						HEIGHT, null);
			}
			drawCount++;
		} else { // Draw the same thing as last time, then switch to the next image for next draw
			BufferedImage img = images.get(currentState).remove(0);
			if (currentFacing == Facing.E) { // Facing right
				g.drawImage(img, final_x + WIDTH, final_y, -WIDTH,
						HEIGHT, null);
			} else { // Facing left
				g.drawImage(img, final_x, final_y, WIDTH,
						HEIGHT, null);
			}
			images.get(currentState).add(img); // Add the current image to the back of the list.
			drawCount = 0;
		}
		
		g.setColor(Color.RED);
		g.drawRect(final_x, final_y, WIDTH, HEIGHT);
		g.drawRect(PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
	
	// Test enemy classes
	public static void main(String[] args) {
		// Create factories that will create our images.
		EnemyFactory mageCreator = new MageFactory();
		EnemyFactory ghostCreator = new GhostFactory();
		
		// Create enemy instances using 'magic' numbers for x and y positions.
		Enemy merlin = mageCreator.createEnemy(10, 10);
		Enemy casper = ghostCreator.createEnemy(110, 10);
		Enemy gandalf = mageCreator.createEnemy(110, 110);

		// Create JFrame and DrawingPanel to test our enemy drawing and image loading.
        JFrame frame = new JFrame("Drawing Application");
        DrawingPanel panel = new DrawingPanel();
        
        // Add our enemies to the drawing panel to be drawn
        panel.addDrawable(merlin);
        panel.addDrawable(casper);
        panel.addDrawable(gandalf);

        // More GUI setup
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack(); // Adjusts window to fit the preferred size and layouts of its subcomponents
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }

	// Drawing panel used solely for testing purposes
    static class DrawingPanel extends JPanel {
        private List<Enemy> drawables = new ArrayList<>();

        public DrawingPanel() {
            setPreferredSize(new Dimension(800, 600)); // Set the size of the panel
        }

        public void addDrawable(Enemy drawable) {
            drawables.add(drawable);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            for (Enemy drawable : drawables) {
                drawable.draw(g2d, new int[] {0, 0});
            }
        }
    }
}

/**
 * Mage is a child of Enemy with attributes and unique images.
 * @author Andrew Denegar
 */
class Mage extends Enemy {
	
	public Mage(int x, int y, Map<State, List<BufferedImage>> images) {
		this.images = images;
		WIDTH = 100;
		HEIGHT = 100;
		position_x = x;
		position_y = y;
		speed = 2;
	}
}

/**
 * Ghost is a child of Enemy with attributes and unique images.
 * @author Andrew Denegar
 */
class Ghost extends Enemy {
	
	public Ghost(int x, int y, Map<State, List<BufferedImage>> images) {
		this.images = images;
		WIDTH = 100;
		HEIGHT = 100;
		position_x = x;
		position_y = y;
		speed = 3;
	}
}


/**
 * EnemyFactory will be used to create up to five enemy factory classes: Beaver, Ghost, Mage, Necromancer, and Salamander.
 * 	Extensions of EnemyFactories will load the static images for each of the classes
 * 
 */
abstract class EnemyFactory {
	
	// Function that can be used by a user.
	public abstract Enemy createEnemy(int x, int y);
	
	/** load_images should be implemented separately for each enemy */
	protected abstract void load_images();
	
	/** load_spritesheet should be the same for each enemy. */
	protected void load_spritesheet(String FILE_LOCATION, String character_name, src.Enemy.State playerState, int imageNumber, Map<src.Enemy.State, List<BufferedImage>> images) {
		BufferedImage spriteSheet = null;
		// Load the spritesheet file
		if (playerState.toString() != null) {
			final String resource = FILE_LOCATION + character_name + "_" + playerState.toString() + ".png";
			try {
				spriteSheet = ImageIO.read(new File(resource));
			} catch (IOException e) {
				System.out.println("Image not found at '" + resource + "'");
			}
		}
		images.put(playerState, new LinkedList<>());
		// Save constants used for sprite sheet loading
		final int height = spriteSheet.getHeight();
		final int width = spriteSheet.getWidth();
		for (int i = 0; i < imageNumber; i++) {
			BufferedImage img = spriteSheet.getSubimage(0, (height / imageNumber) * i, width, (height / imageNumber));
			images.get(playerState).add(img);
		}
	}
}

/**
 * MageFactory will load the Mage images and create Mage instances using the createEnemy function.
 *
 */
class MageFactory extends EnemyFactory {
	
	/** Hold images (which should be the same for all Mages) */
	static private Map<src.Enemy.State, List<BufferedImage>> images = new HashMap<>();
	
	public MageFactory() {
		load_images();
	}
	
	@Override
	public Enemy createEnemy(int x, int y) {
		return new Mage(x, y, images);
	}

	@Override
	protected void load_images() {
		final String character_name = "Mage";
		final String FILE_LOCATION = "Textures/Mage/";
		int imageNumber = 3; // This is the number of images in the spriteSheet
		int imageNumber2 = 6; // Idle and Move differ from Attack and Dead
		
		// Load a sprite sheet for each player state
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Idle, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Move, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Attack, imageNumber2, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Dead, imageNumber2, images);
	}
	
}

/**
 * GhostFactory will load the static images for Ghosts and create enemies using the createEnemy function.
 *
 */
class GhostFactory extends EnemyFactory {

	/** Hold images (which should be the same for all Ghosts) */
	static private Map<src.Enemy.State, List<BufferedImage>> images = new HashMap<>();
	
	public GhostFactory() {
		load_images();
	}
	
	@Override
	public Enemy createEnemy(int x, int y) {
		return new Ghost(x, y, images);
	}

	@Override
	protected void load_images() {
		final String character_name = "Ghost";
		final String FILE_LOCATION = "Textures/Ghost/";
		int imageNumber = 3; // This is the number of images in the spriteSheet
		int imageNumber2 = 6; // Idle and Move differ from Attack and Dead
		
		// Load a spritesheet for each player state
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Idle, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Move, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Attack, imageNumber2, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Dead, imageNumber2, images);
	}
	
}