package sprites;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import chunks.Chunk;
import chunks.ChunkManager;
import gameTools.CollisionDetection;
import gameTools.GameVariables;
import main.Main;
import panels.GamePanel;

/**
 *
 * This file holds Enemy.java, Mage, Ghost, EnemyFactory, MageFactory, and
 * GhostFactory. These are the classes used to create enemies including loading
 * their images, storing their locations, and drawing to the screen. Mage and
 * Ghost are child classes of Enemy and MageFactory and GhostFactory are child
 * classes of EnemyFactory.
 *
 * @author Andrew Denegar
 * @since March 26, 2024
 *
 */
public abstract class Enemy implements GameVariables {
	/**
	 * Calculates the distance between two points using the
	 * <a href="https://www.purplemath.com/modules/distform.htm">distance
	 * formula<a/>.
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

	/**
	 * Checks for a collision between eOne and all enemies in activeEnemies.
	 *
	 * @param eOne The enemy to use.
	 * @param newX The x coordinate to use.
	 * @param newY The y coordinate to use.
	 * @return true if there's a collision.
	 */
	public static List<Enemy> enemyCollision(Enemy eOne, int newX, int newY) {
		final int[] eOneXCoords = new int[] { newX, newX + eOne.getWidth(), newX + eOne.getWidth(), newX };
		final int[] eOneYCoords = new int[] { newY, newY, newY + eOne.getHeight(), newY + eOne.getHeight() };

		List<Enemy> hits = new ArrayList<>();

		for (Enemy eTwo : activeEnemies) {
			if (eTwo.equals(eOne)) {
				continue;
			}
			final int[] eTwoCoords = eTwo.getPosition();

			final int[] eTwoXCoords = new int[] { eTwoCoords[0], eTwoCoords[0] + eTwo.getWidth(),
					eTwoCoords[0] + eTwo.getWidth(), eTwoCoords[0] };
			final int[] eTwoYCoords = new int[] { eTwoCoords[1], eTwoCoords[1], eTwoCoords[1] + eTwo.getHeight(),
					eTwoCoords[1] + eTwo.getHeight() };

			final boolean c = CollisionDetection.getCollision(eOneXCoords, eOneYCoords, eTwoXCoords, eTwoYCoords);

			if (c) {
				hits.add(eTwo);
			}
		}

		return hits;
	}

	/**
	 * List of all enemies. This is static for easier access for collision
	 * detection.
	 */
	public static final Set<Enemy> enemies = new HashSet<>();
	/**
	 * List of all enemies currently visible on the screen. This is static for
	 * easier access for collision detection.
	 */
	public static final Set<Enemy> activeEnemies = new HashSet<>();

	/**
	 * How close the player can get to the enemy horizontally before it goes towards
	 * the player.
	 */

	protected static final int X_DETECTION_RANGE = SCREEN_WIDTH / 3;
	/**
	 * How close the player can get to the enemy vertically before it goes towards
	 * the player.
	 */
	protected static final int Y_DETECTION_RANGE = (int) (SCREEN_HEIGHT / 2.5);

	/** How many times the enemy is drawn before the next image is selected . */
	protected static final int DRAW_FRAMES = 5;

	/** Conversion is a constant used to resize our enemy to our liking */
	private static final int IMAGESIZECONVERSION = 2;

	/** Width of enemy. */
	protected int WIDTH;

	/** Height of enemy. */
	protected int HEIGHT;

	/** X position of enemy. */
	protected int position_x;

	/** Y position of enemy. */

	protected int position_y;

	/** Speed of enemy. */
	protected int speed;

	/**
	 * Default speed of enemy.
	 */
	protected int defaultSpeed;

	protected int NUMATTACKINGIMAGES;

	/**
	 * Speed used when not tracking player.
	 */
	protected int roamingSpeed;

	// TODO Figure out real number
	/**
	 * Tracks the hit count of our enemy
	 */
	private int hitCount = 3; // 3 hits to make an enemy die? change number?

	/**
	 * All directions the enemy can move.
	 */
	protected int[][] DELTAS;

	/**
	 * draw count keeps track of how many times draw has been called before
	 * switching to the next image in the list.
	 */
	protected int drawCount = 0;
	/**
	 * attackCount keeps track of the number of frames that have passed since
	 * attacking has started.
	 */
	protected int attackCount = 0;

	/**
	 * stateLocked and facingLocked decides if the enemy should continue to change
	 * the direction or the state
	 */
	protected boolean stateLocked = false;
	protected boolean facingLocked = false;

	/** Set initial state. */
	protected State currentState = State.Attack;

	/** Set initial state. */
	protected Facing currentFacing = Facing.E;
	/**
	 * Holds all Buffered images for each state. Images do not change based on,
	 * 'Facing' except for being flipped left and right.
	 */
	protected Map<State, List<BufferedImage>> images;

	/**
	 * TODO Comment this, idk what it does
	 */
	protected int[] PADDING;

	/**
	 * Keeps track if enemy is chasing player, only used for drawing a line between
	 * enemy and player TODO remove
	 */
	protected boolean chasing = false;

	/** How many times the player is knocked back. */
	private final int maxKnockbackCount = 10;

	/** Used to check if player should be knocked back. */
	private boolean knockback = false;

	/** Keeps track of how many times player is knocked back. */
	private int knockbackCounter = 0;

	/** Displace player x coordinate by this amount. */
	private int knockbackDx = 0;

	/** Displace player y coordinate by this amount. */
	private int knockbackDy = 0;

	/** Damage dealt to player when enemy hits the player. */
	protected int damage = 0;

	/** Direction to knock player back. */
	private Facing knockbackDir = Facing.N;

	/**
	 * Move decides how the enemy should move based on the player position and
	 * current position, if the enemy can see the player, and if the enemy has
	 * gotten hit or not.
	 */
	public void move() {

		// Get the current position
		final int[] currentCoords = getPosition();
		final int currentX = currentCoords[0];
		final int currentY = currentCoords[1];

		if (knockback) {
			// If knockback would result in a collision with a wall, then end the knockback
			if (wallCollision(position_x + ChunkManager.xOffset + knockbackDx,
					position_y + ChunkManager.yOffset + knockbackDy)) {
				resetKnockback();
			} else {

				// If knocking the enemy back would result in hitting another enemy, knock that
				// enemy back too
				List<Enemy> hitEnemies = enemyCollision(this, position_x + ChunkManager.xOffset + knockbackDx,
						position_y + ChunkManager.yOffset + knockbackDy);
				if (hitEnemies.size() != 0) {
					for (Enemy e : hitEnemies) {
						e.knockback(knockbackDir);
					}
					// Else just update the coords
				} else {
					update_coords(knockbackDx, knockbackDy);
				}

			}

			knockbackCounter++;
			// Knockback limit, reset everything
			if (knockbackCounter == maxKnockbackCount) {
				resetKnockback();
			}
			// Checks if player is in range of enemy
		} else if (canAttack(currentX, currentY)) {
			facePlayer();
			if (currentState != State.Attack) {
				stateLocked = true;
				facingLocked = true;
				currentState = State.Attack;
				attackCount = 0;
				drawCount = 0;

				Facing dirToPlayer = Facing.N;

				int[] newDeltas = newPosition();
				//Determine which direction the enemy is to the player.
				if(newDeltas[0] < 0 && newDeltas[1] < 0) {
					dirToPlayer = Facing.NW;
				}else if(newDeltas[0] > 0 && newDeltas[1] < 0) {
					dirToPlayer = Facing.NE;
				}else if(newDeltas[0] > 0 && newDeltas[1] > 0) {
					dirToPlayer = Facing.SE;
				}else if(newDeltas[0] < 0 && newDeltas[1] > 0) {
					dirToPlayer = Facing.SW;
				}else if(newDeltas[0] > 0) {
					dirToPlayer = Facing.W;
				}else if(newDeltas[0] < 0) {
					dirToPlayer = Facing.E;
				}else if(newDeltas[1] > 0) {
					dirToPlayer = Facing.S;
				}else if(newDeltas[1] < 0) {
					dirToPlayer = Facing.N;
				}

				/*
				 * Pass handling hitting the player to ChunkManager, with the direction to move the player.
				 * Use oppositeDirection map to find the opposite direction the enemy is to the player, so
				 * we know which way to move the player.
				 */
				GamePanel.ourPlayer.setGettingAttacked(true);
				ChunkManager.playerHit(oppositeDirection.get(dirToPlayer), this.damage);
			}
		} else {
			// If player is within the detection range, enemy should move towards the player
			if (inRangeOfPlayer()) {
				chasing = true;
				final int[] newDeltas = newPosition();

				final int dx = newDeltas[0];
				final int dy = newDeltas[1];

				// Now that we have the movement that will happen, adjust the enemy's
				// state/direction
				changeState(dx, dy);
				// Update position (movement)
				update_coords(dx, dy);

			} else {
				GamePanel.ourPlayer.setGettingAttacked(false);
				chasing = false;
				changeState(roamingSpeed, 0);
				roam();
			}
		}
	}

	/**
	 * Stops and resets the knockback effect.
	 */
	public void resetKnockback() {
		knockback = false;
		knockbackCounter = 0;
		speed = defaultSpeed;
		stateLocked = false;
		facingLocked = false;
		knockbackDx = 0;
		knockbackDy = 0;
	}

	/**
	 * Changes the direction the enemy is facing so it's always facing the player.
	 */
	public void facePlayer() {
		final int currentX = position_x + ChunkManager.xOffset;

		if (SCREEN_WIDTH / 2 > currentX + WIDTH / 2) {
			currentFacing = Facing.E;
		} else {
			currentFacing = Facing.W;
		}
	}

	/**
	 * Changes state and direction of Enemy depending on values of x and y. If x and
	 * y both don't equal 0, then the enemy is moving, else, it's idle. If s is
	 * positive, the enemy is moving east, else its moving west.
	 *
	 * @param x x coordinate to check.
	 * @param y y coordinate to check.
	 */
	public void changeState(int x, int y) {
		if (!stateLocked) {
			if (x != 0 || y != 0) {
				currentState = State.Move;
			} else {
				currentState = State.Idle;
			}
		}
		if (!facingLocked) {
			if (x > 0) {
				currentFacing = Facing.E;
			} else if (x < 0) {
				currentFacing = Facing.W;
			}
		}
	}

	/**
	 * Moves the enemy back and forth when it's not tracking the player.
	 */
	public void roam() {
		// If Enemy isn't colliding with a wall or other enemy, move it.
		if (!wallCollision(position_x + ChunkManager.xOffset, position_y + ChunkManager.yOffset)
				&& enemyCollision(this, roamingSpeed, 0).size() == 0) {
			update_coords(roamingSpeed, 0);
			// Else, change its direction
		} else {
			roamingSpeed *= -1;
			update_coords(roamingSpeed * 2, 0);
			changeState(roamingSpeed, 0);

		}

	}

	/**
	 * Checks if the enemy is currently visible on the screen.
	 *
	 * @return true if the Enemy is currently visible on the screen.
	 */
	public boolean isVisible() {
		final int tempX = position_x + ChunkManager.xOffset;
		final int tempY = position_y + ChunkManager.yOffset;

		return (tempX + WIDTH > 0 && tempX < SCREEN_WIDTH) && (tempY + HEIGHT > 0 && tempY < SCREEN_HEIGHT);
	}

	/**
	 * Finds a new position for the enemy. It first finds the distance between the
	 * player and the enemy. It then checks all possible directions it can move. If
	 * the move would result in a smaller distance to the player, and wouldn't
	 * result in a collision between a wall or a enemy, then it's considered a valid
	 * move.
	 *
	 * @return A 2D array of integers, which are the enemies new coordinates.
	 */
	public int[] newPosition() {
		final int[] currentCoords = getPosition();
		final int currentX = currentCoords[0];
		final int currentY = currentCoords[1];
		final double currentDistance = calculateDistance(PLAYER_X, PLAYER_Y, currentX, currentY);

		int[] newDeltas = new int[] { 0, 0 };

		double minDistance = currentDistance;

		// Check all possible ways the enemy could move.
		for (int[] delta : DELTAS) {

			int newX = currentX + delta[0];
			int newY = currentY + delta[1];
			final double distance = calculateDistance(PLAYER_X, PLAYER_Y, newX, newY);

			/*
			 * if distance is less than current distance and theres no wall collision, thats
			 * the new best delta vlues and distance
			 */
			if (distance < minDistance && !wallCollision(newX, newY) && enemyCollision(this, newX, newY).size() == 0) {
				minDistance = distance;
				newDeltas = delta;
			}
		}

		return newDeltas;
	}

	/**
	 * Checks for a collision between the enemy and the walls in
	 * ChunkManager.activeChunks.
	 *
	 * @param x The x coordinate to use.
	 * @param y The y coordinate to use.
	 * @return true if there is a collision between any wall in any chunk.
	 */
	public boolean wallCollision(int x, int y) {

		final int[] xCoords = new int[] { x, x + WIDTH, x + WIDTH, x };

		final int[] yCoords = new int[] { y, y, y + HEIGHT, y + HEIGHT };

		// If a chunk contains any part of the square formed by the coordinates, check
		// for a collision between it's walls and the coords.
		for (Chunk c : ChunkManager.activeChunks) {
			if (c.containsPoints(xCoords, yCoords)) {
				final boolean collided = c.checkCollision(xCoords, yCoords, new Integer[] { 0, 0 });
				if (collided) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the enemy is in range of the player so it can start moving towards
	 * the player.
	 *
	 * @return true if the enemy is in range of the player.
	 */
	public boolean inRangeOfPlayer() {
		final int[] currentCoords = getPosition();
		final int currentX = currentCoords[0];
		final int currentY = currentCoords[1];
		return Math.abs(PLAYER_X - currentX) <= X_DETECTION_RANGE && Math.abs(PLAYER_Y - currentY) <= Y_DETECTION_RANGE;
	}

	/**
	 * Checks if the enemy is close enough to the player to attack.
	 *
	 * @param x The x coordinate to use.
	 * @param y The y coordinate to use.
	 * @return true If the enemy is closer enough to attack.
	 */
	public boolean canAttack(int x, int y) {

		// Convert 2D array of coords into arrays of x coords and y coords
		final int[] otherXCoords = new int[] { x, x + WIDTH, x + WIDTH, x };
		final int[] otherYCoords = new int[] { y, position_y, y + HEIGHT, y + HEIGHT };

		return CollisionDetection.getCollision(playerXCoords, playerYCoords, otherXCoords, otherYCoords);
	}

	/**
	 * Determines which direction to knockback the enemy, and sets knockback-related
	 * variables to true;
	 *
	 * @param d The direction to knockback the enemy.
	 */
	public void knockback(Facing d) {
		if (!knockback) {
			stateLocked = true;
			facingLocked = true;
			speed += 10;
			knockback = true;
			knockbackDir = d;

			switch (knockbackDir) {
			case N:
				knockbackDy = -speed;
				break;
			case S:
				knockbackDy = speed;
				break;
			case E:
				knockbackDx = speed;
				break;
			case W:
				knockbackDx = -speed;
				break;
			case NW:
				knockbackDx = -speed;
				knockbackDy = -speed;
				break;
			case NE:
				knockbackDx = speed;
				knockbackDy = -speed;
				break;
			case SW:
				knockbackDx = -speed;
				knockbackDy = speed;
				break;
			case SE:
				knockbackDx = speed;
				knockbackDy = speed;
				break;
			}
		}
	}

	/**
	 * Gets the actual position of the enemy.
	 *
	 * @return The actual position of the enemy.
	 */
	public int[] getPosition() {
		return new int[] { position_x + ChunkManager.xOffset, position_y + ChunkManager.yOffset };
	}

	/**
	 * Gets the speed of the enemy.
	 *
	 * @return The speed of the enemy.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Gets the width of the enemy.
	 *
	 * @return The width of the enemy.
	 */
	public int getWidth() {
		return WIDTH;
	}

	/**
	 * Gets the height of the enemy.
	 *
	 * @return The height of the enemy.
	 */
	public int getHeight() {
		return HEIGHT;
	}

	// TODO Test
	/**
	 * Subtract health from the player.
	 *
	 * @param amount The amount of health to subtract.
	 */
	public void subtractHitCount(int amount) {
		hitCount -= amount;
		if (hitCount <= 0) {
			GamePanel.ourPlayer.setGettingAttacked(false);
			enemies.remove(this);
			activeEnemies.remove(this);
			Main.addTime();
			Main.enemyKilled();
		}
	}

	/**
	 * Set player health back to 100
	 */
	public void resetHitCount() {
		hitCount = 5;
	}

	/**
	 * Return the current health of player.
	 */
	public int getHitCount() {
		return hitCount;
	}

	/**
	 * Update the position of the enemy.
	 *
	 * @param dx horizontal shift in the enemy's position.
	 * @param dy vertical shift in the enemy's position.
	 */
	private void update_coords(int dx, int dy) {
		position_x += dx;
		position_y += dy;
	}

	/**
	 * Draw the enemy to the screen.
	 *
	 * @param g Graphics2D object used for drawing.
	 */
	public void draw(Graphics2D g) {
		// Store position based on movement of the map
		final int final_x = ChunkManager.xOffset + position_x;
		final int final_y = ChunkManager.yOffset + position_y;
		final int attackXAdjustment;
		final int attackYAdjustment;
		if (currentState == State.Attack) {
			attackXAdjustment = PADDING[1] * IMAGESIZECONVERSION;
			attackYAdjustment = PADDING[0] * IMAGESIZECONVERSION;
			attackCount++;
		} else {
			attackXAdjustment = 0;
			attackYAdjustment = 0;
		}
		if (drawCount < DRAW_FRAMES) { // Draw the current image and increment drawCount
			if (currentFacing == Facing.E) { // Facing right
				g.drawImage(images.get(currentState).get(0), final_x + WIDTH + attackXAdjustment,
						final_y - attackYAdjustment, -(WIDTH + attackXAdjustment * 2), HEIGHT + attackYAdjustment * 2,
						null);
			} else { // Facing left
				g.drawImage(images.get(currentState).get(0), final_x - attackXAdjustment, final_y - attackYAdjustment,
						WIDTH + attackXAdjustment * 2, HEIGHT + attackYAdjustment * 2, null);
			}
			drawCount++;
		} else { // Draw the same thing as last time, then switch to the next image for next draw
			BufferedImage img = images.get(currentState).remove(0);
			if (currentFacing == Facing.E) { // Facing right
				g.drawImage(img, final_x + WIDTH + attackXAdjustment, final_y - attackYAdjustment,
						-(WIDTH + attackXAdjustment * 2), HEIGHT + attackYAdjustment * 2, null);
			} else { // Facing left
				g.drawImage(img, final_x - attackXAdjustment, final_y - attackYAdjustment,
						WIDTH + attackXAdjustment * 2, HEIGHT + attackYAdjustment * 2, null);
			}
			images.get(currentState).add(img); // Add the current image to the back of the list.
			drawCount = 0;
		}
		if (attackCount >= (DRAW_FRAMES + 1) * NUMATTACKINGIMAGES) {
			attackCount = 0;
			stateLocked = false;
			facingLocked = false;
			currentState = State.Idle;
		}

		// TODO remove
//		g.setColor(Color.RED);
//		g.drawRect(final_x, final_y, WIDTH, HEIGHT);
//		if(chasing)
//			g.drawLine(final_x + WIDTH/2, final_y + HEIGHT/2, PLAYER_X + PLAYER_WIDTH/2, PLAYER_Y + PLAYER_HEIGHT/2);
	}

	/**
	 * Main method, used for testing.
	 * 
	 * @param args Arguments passed.
	 */
	public static void main(String[] args) {
		// Create factories that will create our images.
		EnemyFactory mageCreator = MageFactory.getInstance();
		EnemyFactory ghostCreator = GhostFactory.getInstance();

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
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack(); // Adjusts window to fit the preferred size and layouts of its subcomponents
		frame.setLocationRelativeTo(null); // Center the window
		frame.setVisible(true);

		boolean allPassed = true;

		// Load the 0 level, it'll create two enemies
		ChunkManager cmanager = ChunkManager.getInstance();
		cmanager.loadLevel(0,0);

		final int x1 = 20;
		final int x2 = 40;
		final int y1 = 20;
		final int y2 = 40;

		final double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

		final double calcDistance = calculateDistance(x1, y1, x2, y2);
		// Testing calculateDistance
		if (distance != calcDistance) {
			System.err.println("CalculateDistance returned an incorrect distance!");
			allPassed = false;
		}

		Enemy tester = (Enemy) enemies.toArray()[0];
		final int[] initialCoords = tester.getPosition();
		tester.move();
		final int[] newCoords = tester.getPosition();

		// Testing move function
		if (initialCoords[0] == newCoords[0] && initialCoords[1] == newCoords[1]) {
			System.err.println("Enemy didn't move when it should have!");
			allPassed = false;
		}

		tester.roam();
		int[] updatedCoords = tester.getPosition();
		// Testing roam function
		if (updatedCoords[0] == newCoords[0] && updatedCoords[1] == newCoords[1]) {
			System.err.println("Enemy didn't roam when it should have!");
			allPassed = false;
		}

		// Testing inRangeOfPlayer
		if (tester.inRangeOfPlayer()) {
			System.err.println("Enemy said it's in range of player, when it isn't!");
			allPassed = false;
		}

		// Testing isVisible
		if (tester.isVisible()) {
			System.err.println("Enemy said it's visible on the screen, when it isn't!");
			allPassed = false;
		}

		/*
		 * Testing facePlayer The enemy isn't currently facing the player, so we'll make
		 * it
		 */
		Facing d = tester.currentFacing;
		tester.facePlayer();
		Facing newD = tester.currentFacing;
		if (d == newD) {
			System.err.println("Enemy failed to change direction!");
			allPassed = false;
		}

		// Testing canAttack
		if (tester.canAttack(updatedCoords[0], updatedCoords[1])) {
			System.err.println("Enemy said it can attack player, when it can't!");
			allPassed = false;
		}

		// Testing enemyCollision
		if (enemyCollision(tester, updatedCoords[0], updatedCoords[1]).size() != 0) {
			System.err.println("Enemy is colliding with another enemy, when it shouldn't be!");
			allPassed = false;
		}

		// Testing changeState
		tester.changeState(0, 0);
		if (tester.currentState != State.Idle) {
			System.err.println("Enemy should be in the Idle state, when it shouldn't be!");
			allPassed = false;
		}

		tester.changeState(2, 0);
		if (tester.currentState == State.Idle) {
			System.err.println("Enemy should be in the moving state, when it shouldn't be!");
			allPassed = false;
		}

		// Testing wallCollision
		if (tester.wallCollision(updatedCoords[0], updatedCoords[1])) {
			System.err.println("wallCollision returned true, when it should've returned false!");
			allPassed = false;
		}

		// Testing getSpeed, getWidth, getHeight
		for (Enemy e : enemies) {
			if (e instanceof Mage) {
				if (e.getSpeed() != 2) {
					System.err.println("Mage Enemy has the wrong speed!");
					allPassed = false;
				}
				if (e.getHeight() != 70) {
					System.err.println("Mage Enemy has the wrong height!");
					allPassed = false;
				}
				if (e.getWidth() != 70) {
					System.err.println("Mage Enemy has the wrong width!");
					allPassed = false;
				}
			} else if (e instanceof Ghost) {
				if (e.getSpeed() != 3) {
					System.err.println("Ghost Enemy has the wrong speed!");
					allPassed = false;
				}
				if (e.getHeight() != 60) {
					System.err.println("Ghost Enemy has the wrong height!");
					allPassed = false;
				}
				if (e.getWidth() != 60) {
					System.err.println("Ghost Enemy has the wrong width!");
					allPassed = false;
				}
			}
		}

		cmanager.updateCoords(-WALL_WIDTH * 3, 0);
		cmanager.updateEnemies();

		// Move enemies
		for (Enemy e : enemies) {
			e.move();

		}

		// Testing some methods again now that the enemies are closer to the player

		// Testing inRangeOfPlayer
		if (!tester.inRangeOfPlayer()) {
			System.err.println("Enemy said it's not in range of player, when it is!");
			allPassed = false;
		}

		// Testing isVisible
		if (!tester.isVisible()) {
			System.err.println("Enemy said it's not visible on the screen, when it is!");
			allPassed = false;
		}

		/*
		 * Testing facePlayer The enemy isn't currently facing the player, so we'll make
		 * it
		 */
		d = tester.currentFacing;
		tester.facePlayer();
		newD = tester.currentFacing;
		if (d != newD) {
			System.err.println("Enemy changed direction when it was already facing the player!");
			allPassed = false;
		}

		/*
		 * Testing if newPosition works Since the player is now in range of the enemy,
		 * new position should find a closer spot to move
		 */
		final int[] foundCoords = tester.newPosition();
		if (foundCoords[0] == 0 && foundCoords[1] == 0) {
			System.err.println("newPosition said the enemy couldn't move closer to the player, when it could!");
			allPassed = false;
		}

		// Testing knockback effect
		final int[] preKnockbackCoords = tester.getPosition();
		tester.knockback(Facing.E);
		tester.move();
		final int[] postKnockbackCoords = tester.getPosition();

		if (preKnockbackCoords[0] == postKnockbackCoords[0] && preKnockbackCoords[1] == postKnockbackCoords[1]) {
			System.err.println("Enemy didn't get knocked back when it should have!");
			allPassed = false;
		}

		tester.resetKnockback();
		tester.move();
		final int[] newMoveCoords = tester.getPosition();
		// Since we reset the knockback, and the enemy is already right next to the
		// player, it shouldn't move.
		if (newMoveCoords[0] != postKnockbackCoords[0] && newMoveCoords[1] != postKnockbackCoords[1]) {
			System.err.println("Enemy move when it shouldn't have!");
			allPassed = false;
		}

		if (allPassed) {
			System.out.println("All cases passed!");
		} else {
			System.err.println("At least one case failed!");
		}
	}

	// Drawing panel used solely for testing purposes
	static class DrawingPanel extends JPanel {
		private static final long serialVersionUID = 1L;
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
