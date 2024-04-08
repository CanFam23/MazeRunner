package sprites;

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
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import chunks.Chunk;
import chunks.ChunkManager;
import gameTools.GameVariables;
import gameTools.CollisionDetection;



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
public abstract class Enemy implements GameVariables {
	/**
	 * Calculates the distance between two points using 
	 * the <a href="https://www.purplemath.com/modules/distform.htm">distance formula<a/>. 
	 * 
	 * @param x1 First x coordinate.
	 * @param y1 First y coordinate.
	 * @param x2 Second x coordinate.
	 * @param y2 Second y coordinate.
	 * @return The distance between the two points.
	 */
	public static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
	
	//TODO Add testing
	/**
	 * Checks for a collision between eOne and all enemies in activeEnemies. 
	 * 
	 * @param eOne The enemy to use.
	 * @param newX The x coordinate to use.
	 * @param newY The y coordinate to use.
	 * @return true if there's a collision.
	 */
	public static boolean enemyCollision(Enemy eOne, int newX, int newY) {
		final int[] eOneXCoords = new int[] {newX,newX+eOne.getWidth(),newX+eOne.getWidth(),newX};
		final int[] eOneYCoords = new int[] {newY,newY,newY+eOne.getHeight(),newY+eOne.getHeight()};
		
		for(Enemy eTwo: activeEnemies) {
			if(eTwo.equals(eOne)) {
				continue;
			}
			final int[] eTwoCoords = eTwo.getPosition();
			
			final int[] eTwoXCoords = new int[] {eTwoCoords[0],eTwoCoords[0]+eTwo.getWidth(),eTwoCoords[0]+eTwo.getWidth(),eTwoCoords[0]};
			final int[] eTwoYCoords = new int[] {eTwoCoords[1],eTwoCoords[1],eTwoCoords[1]+eTwo.getHeight(),eTwoCoords[1]+eTwo.getHeight()};
			
			final Collision c = CollisionDetection.getCollision(eOneXCoords, eOneYCoords, eTwoXCoords, eTwoYCoords);

			if(c != Collision.NO_COLLISION) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public static void enemiesHit(Set<Enemy> hitEnemies) {
		/*
		 * For each enemy:
		 * start with farthest away enemy
		 * If they can move back without hitting a wall or enemy, move back.
		 * If not, dont move back
		 * Update health, or die
		 */
		
		   System.out.println("You hit " + hitEnemies.size() + " Enemies!");
		   for(Enemy e: hitEnemies) {
			   for(int i = 0; i < 50;i++) {
				   e.update_coords(e.getSpeed(), 0);
			   }
		   }

		
	}
	
	/**
	 * List of all enemies. This is static for easier access for collision detection.
	 */
	public static final List<Enemy> enemies = new ArrayList<>();
	/**
	 * List of all enemies currently visible on the screen. This is static for easier access for collision detection.
	 */
	public static final List<Enemy> activeEnemies = new ArrayList<>();

	
	/**
	 * How close the player can get to the enemy horizontally before it goes towards the player.
	 */
	protected static final int X_DETECTION_RANGE = SCREEN_WIDTH/3;
	/**
	 * How close the player can get to the enemy vertically before it goes towards the player.
	 */
	protected static final int Y_DETECTION_RANGE = (int) (SCREEN_HEIGHT/2.5);
	
	/** How many times the enemy is drawn before the next image is selected .*/
	protected static final int DRAW_FRAMES = 10;
	

	
	/** Width of enemy. */
	protected int WIDTH;
	
	/** Height of enemy. */
	protected int HEIGHT;
	
	/** X position of enemy.*/
	protected int position_x;
	
	/** Y position of enemy.*/

	protected int position_y;
	
	/** Speed of enemy. */
	protected int speed;
	
	/**
	 * Speed used when not tracking player.
	 */
	protected int roamingSpeed; 
	
	//TODO Figure out real number
	/**
	 *  Tracks the hit count of our enemy
	 */
    private int hitCount = 5; // 5 hits to make an enemy die? change number?
	
	/**
	 * All directions the enemy can move.
	 */
	protected int[][] DELTAS;
	protected int displacement = 4;
	
	/** draw count keeps track of how many times draw has been called before switching to the next image in the list. */
	protected int drawCount = 0;
	
	// Set initial direction faced and sprite state
	protected State currentState = State.Idle;
	protected Facing currentFacing = Facing.E;
	/** Holds all Buffered images for each state. Images do not change based on, 'Facing' except for being flipped left and right. */
	protected Map<State, List<BufferedImage>> images;
	
	protected boolean chasing = false;

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
	

	//TODO update testing
	/**
	 * Move decides how the enemy should move based on the player position and current position.
	 * 
	 * @param ChunkManager.activeChunks The chunks to use when checking collisions with walls.
	 */
	public void move() {
		
		//Get the current position 
		final int[] currentCoords = getPosition();
		final int currentX = currentCoords[0];
		final int currentY = currentCoords[1];
		
		//Checks if player is in range of enemy
		if(canAttack(currentX,currentY)) {
			facePlayer();
			//System.out.println("Attack");
		}else {
		    //If player is within the detection range, enemy should move towards the player	
			if(inRangeOfPlayer()) {
				chasing = true;
				final int[] newDeltas = newPosition();
				
				final int dx = newDeltas[0];
				final int dy = newDeltas[1];

				// Now that we have the movement that will happen, adjust the enemy's state/direction
				changeState(dx,dy);
				// Update position (movement)
				update_coords(dx, dy);
				
			}else {
				chasing = false;
				changeState(roamingSpeed,0);
				roam();
			}
		}
	}
	
	//TODO Add testing
	/**
	 * Changes the direction the enemy is facing so it's always facing the player.
	 */
	public void facePlayer() {
		final int currentX = position_x + ChunkManager.xOffset;
		
		if(SCREEN_WIDTH/2 > currentX + WIDTH/2) {
			currentFacing = Facing.E;
		}else {
			currentFacing = Facing.W;
		}
	}
	
	//TODO Add testing
	/**
	 * Changes state and direction of Enemy depending on values of x and y.
	 * If x and y both don't equal 0, then the enemy is moving, else, it's idle.
	 * If s is positive, the enemy is moving east, else its moving west.
	 * 
	 * @param x x coordinate to check.
	 * @param y y coordinate to check.
	 */
	public void changeState(int x, int y) {
		if (x != 0 || y != 0)
			currentState = State.Move;
		else
			currentState = State.Idle;
		if (x > 0) 
			currentFacing = Facing.E;
		else if (x < 0)
			currentFacing = Facing.W;
	}
	
	//TODO Add testing 
	/**
	 * Moves the enemy back and forth when it's not tracking the player.
	 * 
	 * @param ChunkManager.activeChunks Chunks checked for collision so Enemy can't move through walls.
	 */
	public void roam() {
		//If Enemy isn't colliding with a wall or other enemy, move it.
		if(!wallCollision(position_x + ChunkManager.xOffset,position_y + ChunkManager.yOffset) && !enemyCollision(this,roamingSpeed,0)) {
			update_coords(roamingSpeed,0);
		//Else, change its direction
		}else {
			roamingSpeed *= -1;
			update_coords(roamingSpeed*2,0);
			changeState(roamingSpeed,0);
			
		}

	}
	
	//TODO Add testing
	/**
	 * Checks if the enemy is currently visible on the screen.
	 * 
	 * @return true if the Enemy is currently visible on the screen.
	 */
	public boolean isVisible() {
		final int tempX = position_x + ChunkManager.xOffset;
		final int tempY = position_y + ChunkManager.yOffset;
		
		if((tempX + WIDTH > 0 && tempX < SCREEN_WIDTH) && (tempY + HEIGHT > 0 && tempY < SCREEN_HEIGHT)) {
			return true;
		}
		return false;
	}
	
	//TODO Add testing
	/**
	 * Finds a new position for the enemy. It first finds the distance between the player and the enemy.
	 * It then checks all possible directions it can move. If the move would result in a smaller distance to the player,
	 * and wouldn't result in a collision between a wall or a enemy, then it's considered a valid move.
	 * 
	 * @param currentX The current x position of the enemy.
	 * @param currentY The current y position of the enemy.
	 * @param ChunkManager.activeChunks The chunks to check for collision.
	 * @return A 2D array of integers, which are the enemies new coordinates.
	 */
	public int[] newPosition() {
		final int[] currentCoords = getPosition();
		final int currentX = currentCoords[0];
		final int currentY = currentCoords[1];
		final double currentDistance = calculateDistance(PLAYER_X,PLAYER_Y,currentX,currentY);
		
		int[] newDeltas = new int[]{0,0};
		
		double minDistance = currentDistance;
		
		//Check all possible ways the enemy could move.
		for(int[] delta: DELTAS) {
			
			int newX = currentX + delta[0];
			int newY = currentY + delta[1];
			final double distance = calculateDistance(PLAYER_X,PLAYER_Y,newX,newY);
		
			/*
			 * if distance is less than current distance and theres no wall collision, thats the new best delta vlues and distance
			 */
			if(distance < minDistance && !wallCollision(newX,newY) && !enemyCollision(this,newX,newY)) { 
				minDistance = distance;
				newDeltas = delta;
			}
		}
		
		return newDeltas;
	}
	
	//TODO Add testing
	/**
	 * Checks for a collision between the enemy and the walls in ChunkManager.activeChunks.
	 * 
	 * @param x The x coordinate to use.
	 * @param y The y coordinate to use.
	 * @param ChunkManager.activeChunks The chunks to check for collision of walls.
	 * @return true if there is a collision between any wall in any chunk.
	 */
	public boolean wallCollision(int x,int y) {
		
		final int[] xCoords = new int[] {x, x + WIDTH, x + WIDTH, x};
		
		final int[] yCoords = new int[] {y, y, y + HEIGHT, y + HEIGHT};

		final List<Collision> collisions = new ArrayList<Collision>();
		
		//If a chunk contains any part of the square formed by the coordinates, check for a collision between it's walls and the coords.
		for(Chunk c: ChunkManager.activeChunks) {
			if(c.containsPoints(xCoords, yCoords)) {
				collisions.addAll(c.checkCollision(xCoords, yCoords));
			}
		}
		
		return collisions.size() != 0;
	}
	
	//TODO Add testing 
	/**
	 * Checks if the enemy is in range of the player so it can start moving towards the player.
	 * 
	 * @return true if the enemy is in range of the player.
	 */
	public boolean inRangeOfPlayer() {
		final int[] currentCoords = getPosition();
		final int currentX = currentCoords[0];
		final int currentY = currentCoords[1];
		return Math.abs(PLAYER_X - currentX) <= X_DETECTION_RANGE && Math.abs(PLAYER_Y - currentY) <= Y_DETECTION_RANGE;
	}
	
	//TODO Add testing
	/**
	 * Checks if the player is close enough to the player to attack.
	 * 
	 * @param x The x coordinate to use.
	 * @param y The y coordinate to use.
	 * @return true If the enemy is closer enough to attack.
	 */
	public boolean canAttack(int x, int y) {
		
		// Convert 2D array of coords into arrays of x coords and y coords
		final int[] otherXCoords = new int[] { x, x + WIDTH,
				x + WIDTH, x};
		final int[] otherYCoords = new int[] { y, position_y,
				y + HEIGHT, y + HEIGHT };

		return CollisionDetection.getCollision(playerXCoords, playerYCoords, otherXCoords, otherYCoords) != Collision.NO_COLLISION;
	}
	
	//TODO Add testing
	/**
	 * Gets the actual position of the enemy. 
	 * 
	 * @return The actual position of the enemy. 
	 */
	public int[] getPosition() {
		return new int[] {position_x + ChunkManager.xOffset,position_y + ChunkManager.yOffset };
	}
	
	//TODO Add testing
	/**
	 * Gets the speed of the enemy.
	 * 
	 * @return The speed of the enemy.
	 */
	public int getSpeed() {
		return speed;
	}
	
	//TODO Add testing
	/**
	 * Gets the width of the enemy.
	 * 
	 * @return The width of the enemy.
	 */
	public int getWidth() {
		return WIDTH;
	}
	
	//TODO Add testing
	/**
	 * Gets the height of the enemy.
	 * 
	 * @return The height of the enemy.
	 */
	public int getHeight() {
		return HEIGHT;
	}
	
	/**
     * Subtract health from the player.
     * 
     * @param amount The amount of health to subtract.
     */
    public void subtractHitCount(int amount) {
        hitCount -= amount;
    }
    
	/**
     * Set player health back to 100 
     */
    public void resetHitCount() {
    	hitCount = 5; 
    }
    
	/**
     * Return the current health of player 
     */
    public int getHitCount() {
    	return hitCount;
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
	public void draw(Graphics2D g) {
		// Store position based on movement of the map
		final int final_x = ChunkManager.xOffset + position_x;
		final int final_y = ChunkManager.yOffset + position_y;
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
		
		//TODO remove
		g.setColor(Color.RED);
		g.drawRect(final_x, final_y, WIDTH, HEIGHT);
		if(chasing)
			g.drawLine(final_x + WIDTH/2, final_y + HEIGHT/2, PLAYER_X + PLAYER_WIDTH/2, PLAYER_Y + PLAYER_HEIGHT/2);
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
        
        boolean allPassed = true;
        
        /*
         * Testing:
         * calculateDistance 
         * enemyCollision 
         * move 
         * facePlayer 
         * changeState 
         * roam 
         * isVisible 
         * newPosition 
         * wallCollision 
         * inRangeOfPlayer 
         * canAttack 
         * getPosition 
         * getSpeed 
         * getWidth 
         * getHeight
         */
        
        ChunkManager cmanager = new ChunkManager();
        cmanager.loadLevel(0);
        
        final int x1 = 20;
        final int x2 = 40;
        final int y1 = 20;
        final int y2 = 40;
        
        final double distance =  Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        
        final double calcDistance = calculateDistance(x1,y1,x2,y2);
        //Testing calculateDistance
        if(distance != calcDistance) {
        	System.err.println("CalculateDistance returned an incorrect distance!");
        	allPassed = false;
        }
        
        final int startX = SCREEN_WIDTH/2 + 100;
        final int startY = SCREEN_HEIGHT/2 + 100;
        Enemy tempEnemy =  mageCreator.createEnemy(startX,startY);
        
        int[] coords = tempEnemy.getPosition();
        
        if(coords[0] != startX || coords[1] != startY) {
        	System.err.println("Starting coordinates are incorrect!");
        	allPassed = false;
        }
        
        tempEnemy.move();
        coords = tempEnemy.getPosition();
        System.out.println(startX + " " + startY);
        System.out.println(coords[0] + " " + coords[1]);
        
        
        
        
        
        if(allPassed) {
        	System.out.println("All cases passed!");
        }else {
        	System.err.println("At least one case failed!");
        }
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
                drawable.draw(g2d);
            }
        }
    }
}

