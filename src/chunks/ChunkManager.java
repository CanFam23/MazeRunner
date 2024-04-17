package chunks;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;

import blocks.EmptyBlock;
import blocks.EndBlock;
import blocks.PositionBlock;
import blocks.StartingBlock;
import blocks.Wall;
import gameTools.CollisionDetection;
import gameTools.GameVariables;
import panels.GamePanel;
import sprites.Enemy;
import sprites.EnemyFactory;
import sprites.GhostFactory;
import sprites.MageFactory;

/**
 * ChunkManager loads and holds the chunks that will be used in the game. The
 * chunks are basically building blocks of the level.
 *
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 *
 * @since March 2, 2024
 *
 * @see Chunk
 */
public class ChunkManager implements GameVariables {
	/** List of chunks currently visible on the screen. */
	public static final Set<Chunk> activeChunks = new HashSet<>();

	/** File location, should always be in data folder. */
	private static final String FILE_LOCATION = "data/";

	/**
	 * The current instance of ChunkManager.
	 */
	private static ChunkManager single_instance = null;

	/**
	 * The x offset of the maze from it's starting position. This is public so other
	 * classes can access it, like Player and Enemy
	 */
	public static int xOffset = 0;
	/**
	 * The y offset of the maze from it's starting position. This is public so other
	 * classes can access it, like Player and Enemy
	 */
	public static int yOffset = 0;

	/** Width of each chunk. */
	private int chunkWidth;

	/** Height of each chunk. */
	private int chunkHeight;

	/** Keeps track of chunk with start block in it. */
	private Chunk startChunk;

	/** Keeps track of chunk with end block in it. */
	private Chunk endChunk;

	/** The end block. */
	private EndBlock endBlock;

	/** Coordinates of start block. */
	private int[] startCoords;

	/** Keeps track if end has been found. */
	private boolean endFound = false;

	/** Name of level. */
	private String levelName = "";

	/** Number of rows of chunks. */
	private int levelXDimension;

	/** Number of columns of chunks. */
	private int levelYDimension;

	/** Number of chunks per row. */
	private int chunkXDimension;

	/** Number of chunks per column. */
	private int chunkYDimension;

	/** 2D array of all chunks. */
	private Chunk[][] chunks;

	/** Used to make mage enemies. */
	private final EnemyFactory mageCreator = MageFactory.getInstance();

	/** Used to make ghost enemies. */
	private final EnemyFactory ghostCreator = GhostFactory.getInstance();

	/** How many times the player is knocked back. */
	private final int maxKnockbackCount = 14;

	/** Speed player is knocked back each time. */
	private final int knockbackSpeed = 10;

	/** Used to check if player should be knocked back. */
	private boolean knockback = false;

	/** Keeps track of how many times player is knocked back. */
	private int knockbackCounter = 0;

	/** Displace player x coordinate by this amount. */
	private int knockbackDx = 0;

	/** Displace player y coordinate by this amount. */
	private int knockbackDy = 0;

	/** Direction to knock player back. */
	private Facing knockbackDir = Facing.N;

	/**
	 * Keeps track of if the player has completed the game at least once.
	 */
	private boolean hasWon = false;

	/**
	 * Constructor for ChunkManager. This is private because ChunkManager is a
	 * singleton, and only one instance of ChunkManager can exist at a time.
	 */
	private ChunkManager() {

	}

	/**
	 * Makes a new instance of ChunkManager. Chunk manager is a singleton, which
	 * means only one instance of ChunkManager can exist at a time. ChunkManager is
	 * a singleton because we only need one instance of it for our game, and don't
	 * want multiple instances to be made.
	 *
	 * @return The current instance of ChunkManager.
	 */
	public static synchronized ChunkManager getInstance() {
		if (single_instance == null) {
			single_instance = new ChunkManager();
		}

		return single_instance;
	}

	/**
	 * Update the offsets.
	 *
	 * @param dx The number to update x offset by.
	 * @param dy The number to update y offset by.
	 */
	public static void updateOffset(int dx, int dy) {
		xOffset += dx;
		yOffset += dy;
	}

	/**
	 * Resets the offset.
	 */
	public static void resetOffset() {
		xOffset = 0;
		yOffset = 0;
	}

	/**
	 * When the player is hit, this function tells the current instance of
	 * ChunkManager to handle it. It's static so other classes can call it without
	 * having to use a single instance of ChunkManager.
	 *
	 * @param d The direction the player should be knocked back.
	 */
	public static void playerHit(Facing d, int damage) {
		if (single_instance != null) {
			single_instance.handlePlayerHit(d, damage);
		}
	}

	/**
	 * Loads level from levelNum and creates a 2D array of chunks, which represent
	 * each chunk of the maze.
	 *
	 * @param levelNum the level to load.
	 * @return true If level was loaded correctly.
	 */
	public boolean loadLevel(int levelNum, int levelVersionNumber) {

		levelName = "level_" + levelNum + "_v" + levelVersionNumber;
		if (levelNum == 0) {
			levelName = "level_0";
		}
		try (final Scanner input = new Scanner(new File(FILE_LOCATION + levelName + ".txt"))) {
			input.nextLine(); // Discard data description
			final String[] levelStrings = input.nextLine().split(":")[1].split("x"); // Save the dimension of the chunks
																						// - example: (x chunks, y
																						// chunks)
			levelXDimension = Integer.parseInt(levelStrings[0]);

			levelYDimension = Integer.parseInt(levelStrings[1]);
			final String[] chunkStrings = input.nextLine().split(":")[1].split("x"); // Save chunk dimensions - example:
																						// (x walls, y walls)
			chunkXDimension = Integer.parseInt(chunkStrings[0]);
			chunkYDimension = Integer.parseInt(chunkStrings[1]);

			this.chunkWidth = chunkXDimension * WALL_WIDTH;
			this.chunkHeight = chunkYDimension * WALL_HEIGHT;

			// Initialize Chunks with correct chunk sizes.
			chunks = new Chunk[levelYDimension][levelXDimension];
			for (int y = 0; y < chunks.length; y++) {
				for (int x = 0; x < chunks[y].length; x++) {
					chunks[y][x] = new Chunk(chunkXDimension, chunkYDimension, x, y);
				}
			}

			// Set PositionBlock size
			PositionBlock.setBlockSize(WALL_WIDTH, WALL_HEIGHT);
			// Load Images for each type of PositionBlock.
			VolatileImage EmptyImage = null;
			try {
				EmptyImage = convertToVolatile(ImageIO.read(new File("images/emptyBlock.png")));
			} catch (IOException e) {
				System.err.println("Failed to load emptyBlock.png!");
			}
			EmptyBlock.setImage(EmptyImage);

			VolatileImage wallImage = null;
			try {
				wallImage = convertToVolatile(ImageIO.read(new File("images/wall.png")));
			} catch (IOException e) {
				System.err.println("Failed to load wall.png!");
			}
			Wall.setImage(wallImage);

			VolatileImage startImage = null;
			try {
				startImage = convertToVolatile(ImageIO.read(new File("images/startBlock.png")));
			} catch (IOException e) {
				System.err.println("Failed to load startBlock.png!");
			}
			StartingBlock.setImage(startImage);

			VolatileImage endImage = null;
			try {
				endImage = convertToVolatile(ImageIO.read(new File("images/endBlock.png")));
			} catch (IOException e) {
				System.err.println("Failed to load endBlock.png!");
			}
			EndBlock.setImage(endImage);

			// Load the level data
			int yPosition = 0;
			boolean isStartingChunk = false;
			boolean isEndChunk = false;
			while (input.hasNextLine()) { // For every line in the file
				int yChunk = yPosition / chunkYDimension;
				String[] inputData = input.nextLine().split("");

				for (int xPosition = 0; xPosition < inputData.length; xPosition++) { // For every wall in the line
					int xChunk = xPosition / chunkXDimension;
					PositionBlock pb = null;

					if (inputData[xPosition].equals("0")) { // Create an empty block
						pb = new EmptyBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT);
					} else if (inputData[xPosition].equals("1")) { // Create a wall
						pb = new Wall((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT);
					} else if (inputData[xPosition].equals("2")) { // Create a starting block
						pb = new StartingBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT);
						isStartingChunk = true;
					} else if (inputData[xPosition].equals("3")) { // Create an end block
						pb = new EndBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT);
						isEndChunk = true;
					} else if (inputData[xPosition].equals("4")) { // Puts a ghost in this space, empty block behind it
						pb = new EmptyBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT);
						Enemy.enemies.add(ghostCreator.createEnemy(xPosition * WALL_WIDTH + WALL_WIDTH / 4,
								yPosition * WALL_HEIGHT + WALL_HEIGHT / 4));
					} else if (inputData[xPosition].equals("5")) { // Puts a mage in this space, empty block behind it
						pb = new EmptyBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT);
						Enemy.enemies.add(mageCreator.createEnemy(xPosition * WALL_WIDTH + WALL_WIDTH / 4,
								yPosition * WALL_HEIGHT + WALL_HEIGHT / 4));
					}
					chunks[yChunk][xChunk].add(xPosition % chunkXDimension, yPosition % chunkYDimension, pb);
					// Keep track of what chunks are the start and end
					if (isStartingChunk) {
						startChunk = chunks[yChunk][xChunk];
						startCoords = new int[] { xPosition % chunkXDimension * WALL_WIDTH,
								yPosition % chunkYDimension * WALL_HEIGHT };
						isStartingChunk = false;
					} else if (isEndChunk) {
						endChunk = chunks[yChunk][xChunk];
						endBlock = (EndBlock) pb;
						isEndChunk = false;

					}
				}
				yPosition++;
			}

			setStartLocation();

			return true;
		} catch (FileNotFoundException e) {
			System.err.println("File: '" + FILE_LOCATION + levelName + ".txt" + "' not found");
		}
		return false;
	}

	/**
	 * Convert original images to volatile images because they are going to be
	 * loaded over and over again.
	 *
	 * @param originalImage the image to be converted to volatile.
	 * @return VolatileImage same image, but now volatile which increases
	 *         performance when drawn over and over again.
	 */
	private VolatileImage convertToVolatile(BufferedImage originalImage) {
		// Volatile image loading utilities
		final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice device = env.getDefaultScreenDevice();
		final GraphicsConfiguration graphicsConfig = device.getDefaultConfiguration();
		VolatileImage vImage = null;
		// Conversion
		vImage = graphicsConfig.createCompatibleVolatileImage(originalImage.getWidth(), originalImage.getHeight());
		Graphics2D g = vImage.createGraphics();
		g.drawImage(originalImage, 0, 0, null);
		g.dispose();
		return vImage;
	}

	/**
	 * Resets ChunkManager. Sets variables like endFound and x/y offsets to their
	 * starting values, and clears Enemy lists.
	 */
	public void reset() {
		endFound = false;
		activeChunks.clear();
		Enemy.activeEnemies.clear();
		Enemy.enemies.clear();
		resetOffset();
	}

	/**
	 * Handles when the player is hit by a enemy. Sets knockback to true and decides
	 * which way to knockback the player.
	 *
	 * @param d The direction to move the player.
	 */
	public void handlePlayerHit(Facing d, int damage) {
		GamePanel.ourPlayer.subtractHealth(damage);
		if (!knockback) {
			knockback = true;
			knockbackDir = d;

			switch (knockbackDir) {
			case N:
			case NW:
			case NE:
				knockbackDy = -knockbackSpeed;
				break;
			case S:
			case SW:
			case SE:
				knockbackDy = knockbackSpeed;
				break;
			case E:
				knockbackDx = -knockbackSpeed;
				break;
			case W:
				knockbackDx = knockbackSpeed;
				break;
			}
		}
	}

	/**
	 * Gets knockback variable, which represents if the player is getting knocked
	 * back or not.
	 *
	 * @return true if the player is getting knocked back.
	 */
	public boolean getKnockback() {
		return knockback;
	}

	/**
	 * Knockback the player when knockback is set to true. If knocking back the
	 * player would result in hitting a wall, the knockback effect stops.
	 */
	public void knockback() {
		// If knockback would result in a collision with a wall, then end the knockback
		if (checkCollision(new Integer[] { -knockbackDx, -knockbackDy })) {
			stopKnockback();
		} else {
			updateCoords(knockbackDx, knockbackDy);

		}

		knockbackCounter++;
		// Knockback limit, reset everything
		if (knockbackCounter == maxKnockbackCount) {
			stopKnockback();
		}
	}

	/**
	 * Stops knockback effect and resets variables.
	 */
	public void stopKnockback() {
		knockback = false;
		knockbackCounter = 0;
		knockbackDx = 0;
		knockbackDy = 0;

	}

	/**
	 * Adds all enemies currently visible on the screen to activeEnemies Set, so
	 * they can be drawn. It then moves all enemies that are active.
	 */
	public synchronized void updateEnemies() {
		// Get enemies that can be see on the screen right now
		for (Enemy e : Enemy.enemies) {
			if (e.isVisible()) {
				Enemy.activeEnemies.add(e);
			} else {
				Enemy.activeEnemies.remove(e);
			}
		}

		// Move enemies that are active
		for (Enemy e : Enemy.activeEnemies) {
			e.move();
		}
	}

	/**
	 * Update coordinates of all chunks, if the chunk is visible, add to active
	 * chunks.
	 *
	 * @param dx integer to change x by.
	 * @param dy integer to change y by.
	 */
	public synchronized void updateCoords(int dx, int dy) {

		updateOffset(dx, dy);

		for (Chunk[] chunk : chunks) {
			for (int y = 0; y < chunks[0].length; y++) {
				Chunk temp = chunk[y]; // Select a chunk
				temp.updateCoords(dx, dy); // Update the chunk's coordinates
				// Now, decide if the chunk should be added to or removed from the activeChunks
				// list
				if (isVisible(temp)) {
					activeChunks.add(temp);
				} else {
					activeChunks.remove(temp);
				}
			}
		}

		// Now, check if we are in the ending chunk
		if (activeChunks.contains(endChunk)) {
			if (containsPlayer(endChunk, endBlock)) {
				endFound = true;
				hasWon = true;
			}
		}
	}

	/**
	 * Checks if the end has been found.
	 *
	 * @return true if end has been found.
	 */
	public boolean endFound() {
		return endFound;
	}

	/**
	 * Checks if the user has won the game at least one time.
	 * 
	 * @return true if the user has won at least once.
	 */
	public boolean hasWon() {
		return hasWon;
	}

	/**
	 * Returns true if the player is in the given block, same idea used for
	 * collisions.
	 *
	 * @param c  Chunk to check for collision.
	 * @param pb PositionBlock to check for collision.
	 * @return true if there is a full collision.
	 *
	 * @see {@link Chunk#collision(PositionBlock)}
	 */
	public boolean containsPlayer(Chunk c, PositionBlock pb) {

		final int[][] pbBounds = pb.getHitbox(c.xPosition, c.yPosition);

		return CollisionDetection.fullCollision(pbBounds[0], pbBounds[1], playerXCoords, playerYCoords);
	}

	/**
	 * Calls each chunk to draw itself.
	 *
	 * @param g2d 2D graphics to draw on.
	 */
	public synchronized void draw(Graphics2D g2d) {
		for (Chunk c : activeChunks) {
			PositionBlock[][] pbs = c.getBlocks();
			for (PositionBlock[] pb : pbs) {
				for (int x = 0; x < pbs[0].length; x++) {
					pb[x].draw(g2d, c.getXPosition(), c.getYPosition());
				}
			}
		}
	}

	/**
	 * Draws all active enemies to the screen. A enemy is considered active if it's
	 * current position is visible on the screen.
	 *
	 * @param g2d The 2D graphics to draw on.
	 */
	public synchronized void drawEnemies(Graphics2D g2d) {
		for (Enemy e : Enemy.activeEnemies) {
			e.draw(g2d);
		}
	}

	/**
	 * Checks for collision between the player and all blocks in the current active
	 * chunks.
	 *
	 * @param deltas The change in x and y.
	 * @return true if there is any collision.
	 */
	public boolean checkCollision(Integer[] deltas) {

		// Check each chunk in active chunks for a collision.
		for (Chunk c : activeChunks) {
			final boolean collided = c.checkCollision(playerXCoords, playerYCoords, deltas);
			if (collided) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets all the chunks in the level.
	 *
	 * @return All chunks in the level.
	 */
	public Chunk[][] getChunks() {
		return chunks;
	}

	/**
	 * Return a list of the chunks that appear on the screen.
	 *
	 * @return The list of chunks that are visible on the screen.
	 */
	public Set<Chunk> getActiveChunks() {
		return activeChunks;
	}

	/**
	 * Checks if the given chunk is currently visible on the screen.
	 *
	 * @param chunk The chunk to check.
	 * @return If chunk is currently visible on screen.
	 */
	private boolean isVisible(Chunk chunk) {
		return (chunk.xPosition >= -chunkWidth && chunk.xPosition <= chunkWidth && chunk.yPosition >= -chunkHeight
				&& chunk.yPosition <= chunkHeight);
	}

	/**
	 * Sets the starting location to the start chunk.
	 */
	public void setStartLocation() {
		// Move all chunks so the start chunk is the first one on the screen
		int dx = -startChunk.xPosition;
		int dy = -startChunk.yPosition;

		// Put the top left corner of the chunk in the top left corner of the screen
		dx += WALL_WIDTH * 2;
		dy += WALL_HEIGHT * 2;

		// Move the chunks so the player starts on the start block
		dx -= startCoords[0];
		dy -= startCoords[1];

		updateCoords(dx, dy);

	}

	/**
	 * Main method. Used for testing.
	 *
	 * @param args Arguments passed.
	 */
	public static void main(String[] args) {
		// Create a chunk manager and load the level data.
		ChunkManager chunky = ChunkManager.getInstance();
		chunky.loadLevel(3, 1);

		boolean allPassed = true;
		// Test that dimensions have been loaded correctly
		if (chunky.levelXDimension != 4) {
			System.out.println("The X-level dimension was not 4");
			allPassed = false;
		}
		if (chunky.chunkXDimension != 10) {
			System.out.println("The X Chunk size was not 10");
			allPassed = false;
		}
		if (chunky.getActiveChunks().size() != 4) {
			System.out.println("Originally, there should be four visible chunks");
			allPassed = false;
		}
		// Move our player to the left border of the screen, now only two chunks should
		// be visible
		chunky.updateCoords(-4000, 0);
		if (chunky.getActiveChunks().size() != 2) {
			System.out.println(
					"The player has been moved to the left border of the maze, only two chunks should be visible");
			allPassed = false;
		}
		// Move our player to the top left corner of the screen, now only one chunk
		// should be visible
		chunky.updateCoords(0, -3200);
		if (chunky.getActiveChunks().size() != 1) {
			System.out.println(
					"The player has been moved to the top left border of the maze, only one chunk should be visible");
			allPassed = false;
		}
		// Move our player way off the map, no chunks should be visible
		chunky.updateCoords(-20000, 0);
		if (chunky.getActiveChunks().size() != 0) {
			System.out.println("The player has been moved way off the screen, no chunks should be visible.");
			allPassed = false;
		}

		chunky.loadLevel(0, 0);

		// Testing resetOffset
		final int[] preResetOffset = new int[] { xOffset, yOffset };
		resetOffset();
		final int[] postResetOffset = new int[] { xOffset, yOffset };
		if (preResetOffset[0] == postResetOffset[0] && preResetOffset[1] == postResetOffset[1]) {
			System.out.println("Failed to reset offset.");
			allPassed = false;
		}

		// Testing updateOffset
		final int[] preUpdateOffset = new int[] { xOffset, yOffset };
		updateOffset(100, 100);
		final int[] postUpdateOffset = new int[] { xOffset, yOffset };
		if (preUpdateOffset[0] == postUpdateOffset[0] && preUpdateOffset[1] == postUpdateOffset[1]) {
			System.out.println("Failed to update offset.");
			allPassed = false;
		}

		// Testing reset
		chunky.reset();
		if (chunky.endFound || Enemy.activeEnemies.size() != 0 || Enemy.enemies.size() != 0
				|| preUpdateOffset[0] == postUpdateOffset[0] && preUpdateOffset[1] == postUpdateOffset[1]) {
			System.out.println("Failed to reset ChunkManager.");
			allPassed = false;
		}

		chunky.loadLevel(0, 0);

		// Testing update enemies
		Set<Enemy> preUpdateEnemies = Set.copyOf(Enemy.activeEnemies);
		chunky.updateCoords(-WALL_WIDTH * 3, 0);
		chunky.updateEnemies();
		Set<Enemy> postUpdateEnemies = Enemy.activeEnemies;
		// Check each enemy to see if their coords changed
		for (Enemy eOne : preUpdateEnemies) {
			for (Enemy eTwo : postUpdateEnemies) {
				if (eOne.equals(eTwo)) {
					final int[] coordsOne = eOne.getPosition();
					final int[] coordsTwo = eTwo.getPosition();
					if (coordsOne[0] == coordsTwo[0] || coordsOne[1] == coordsTwo[1]) {
						System.err.println(
								"The coordinates of the enemy should've changed after updateEnemies is called!");
						allPassed = false;
					}
					break;
				}
			}
		}

		// Checking if endFound works
		if (chunky.endFound()) {
			System.err.println("The end was found when it should've been!");
			allPassed = false;
		}
		// Move the chunks enough so the end should be found at some point
		for (int i = 0; i < 10; i++) {
			chunky.updateCoords(-WALL_WIDTH, 0);
		}
		// End should be found now
		if (!chunky.endFound()) {
			System.err.println("The end wasn't found when it should've been!");
			allPassed = false;
		}

		if (chunky.getKnockback()) {
			System.err.println("Knockback should be set to false, but it's set to true!");
			allPassed = false;
		}

		// Move chunks back
		for (int i = 0; i < 10; i++) {
			chunky.updateCoords(WALL_WIDTH, 0);
		}

		// Testing playerHit
		ChunkManager.playerHit(Facing.E, 50);
		if (!chunky.getKnockback()) {
			System.err.println("Knockback should be set to true, but it's set to false!");
			allPassed = false;
		}

		// Testing if the knockback effect stops
		chunky.stopKnockback();
		if (chunky.getKnockback()) {
			System.err.println("Knockback should be set to false, but it's set to true!");
			allPassed = false;
		}

		// Testing handlePlayerHit
		chunky.handlePlayerHit(Facing.S, 50);
		if (!chunky.getKnockback()) {
			System.err.println("Knockback should be set to true, but it's set to false!");
			allPassed = false;
		}

		// Getting the y coords of each chunk before the knockback effect
		final int[] preKnockbackYs = new int[chunky.chunks.length * chunky.chunks[0].length];
		int counter = 0;
		for (Chunk[] chunk : chunky.chunks) {
			for (int c = 0; c < chunky.chunks[0].length; c++) {
				preKnockbackYs[counter] = chunk[c].yPosition;
				counter++;
			}
		}

		// knockback the chunks
		chunky.knockback();

		// Getting the y coords of each chunk after the knockback effect
		final int[] postKnockbackYs = new int[chunky.chunks.length * chunky.chunks[0].length];
		counter = 0;
		for (Chunk[] chunk : chunky.chunks) {
			for (int c = 0; c < chunky.chunks[0].length; c++) {
				postKnockbackYs[counter] = chunk[c].yPosition;
				counter++;
			}
		}

		// The y coord should be changed now
		for (int i = 0; i < preKnockbackYs.length; i++) {
			if (preKnockbackYs[1] == postKnockbackYs[1]) {
				System.err.println("Knockback didn't move the chunks!");
				allPassed = false;
			}
		}

		if (allPassed) {
			System.out.println("All cases passed! :)");
		} else {
			System.out.println("At least one case failed! :(");
		}
	}
}