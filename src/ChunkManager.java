package src;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

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
	
	/** List of chunks currently visible on the screen. */
	public static final List<Chunk> activeChunks = new ArrayList<>();
	
	/** File location, should always be in data folder. */
	private static final String FILE_LOCATION = "data/";
	
	/**
	 * The x offset of the maze from it's starting position.
	 */
	protected static int xOffset = 0;
	/**
	 * The y offset of the maze from it's starting position.
	 */
	protected static int yOffset = 0;
	
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
	
	private final EnemyFactory mageCreator = new MageFactory();
	private final EnemyFactory ghostCreator = new GhostFactory();

	/**
	 * Loads level from levelNum and creates a 2D array of chunks, which represent
	 * each chunk of the maze.
	 * 
	 * @param levelNum the level to load.
	 * @return true If level was loaded correctly.
	 */
	public boolean loadLevel(int levelNum) {
		levelName = "level_" + levelNum;
		try (final Scanner input = new Scanner(new File(FILE_LOCATION + levelName + ".txt"))) {
			input.nextLine(); // Discard data description
			final String[] levelStrings = input.nextLine().split(":")[1].split("x"); // Save the dimension of the chunks
																						// - example: (x chunks, y
																						// chunks)
			levelXDimension = Integer.parseInt(levelStrings[0]); // TODO: Possibly change the level description loading

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
			
			// Load Images
			Image positionBlockImage = null;
			try {
				positionBlockImage = ImageIO.read(new File("images/emptyBlock.png"));
			} catch (IOException e) {
				System.err.println("Failed to load emptyBlock.png!");
			}

			Image wallImage = null;
			try {
				wallImage = ImageIO.read(new File("images/wall.png"));
			} catch (IOException e) {
				System.err.println("Failed to load wall.png!");
			}

			Image startImage = null;
			Image endImage = null;
			try {
				startImage = ImageIO.read(new File("images/startBlock.png"));
				endImage = ImageIO.read(new File("images/startBlock.png"));
			} catch (IOException e) {
				System.err.println("Failed to load startBlock.png!");
			}
			
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
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT,
								positionBlockImage);
					} else if (inputData[xPosition].equals("1")) { // Create a wall
						pb = new Wall((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, wallImage);
					} else if (inputData[xPosition].equals("2")) { // Create a starting block
						pb = new StartingBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, startImage);
						isStartingChunk = true;
					} else if (inputData[xPosition].equals("3")) { // Create an end block
						pb = new EndBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, endImage);
						isEndChunk = true;
					} else if (inputData[xPosition].equals("4")) { //Puts a ghost in this space, empty block behind it
						pb = new EmptyBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT,
								positionBlockImage);
						Enemy.enemies.add(ghostCreator.createEnemy(xPosition * WALL_WIDTH + WALL_WIDTH/4, yPosition * WALL_HEIGHT + WALL_HEIGHT/4));
					} else if (inputData[xPosition].equals("5")) { //Puts a mage in this space, empty block behind it
						pb = new EmptyBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT,
								positionBlockImage);
						Enemy.enemies.add(mageCreator.createEnemy(xPosition * WALL_WIDTH + WALL_WIDTH/4, yPosition * WALL_HEIGHT + WALL_HEIGHT/4));
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
	 * Resets ChunkManager.
	 */
	public void reset() { // TODO Add testing?
		endFound = false;
		activeChunks.clear();
		Enemy.activeEnemies.clear();
		Enemy.enemies.clear();
		resetOffset();
	}
	
	/**
	 * TODO add testing and javadoc
	 */
	public void updateEnemies() {
		//Get enemies that can be see on the screen right now
		for (Enemy e : Enemy.enemies) {
			//e.move(get_offset(),getActiveChunks());
			if(e.isVisible()) {
				if(!Enemy.activeEnemies.contains(e)) {
					Enemy.activeEnemies.add(e);
				}
			}else {
				Enemy.activeEnemies.remove(e);
			}
		}
		
		//Move enemies that are active
		for(Enemy e: Enemy.activeEnemies) {
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
	public void updateCoords(int dx, int dy) {
		
		updateOffset(dx,dy);
		
		for (int x = 0; x < chunks.length; x++) {
			for (int y = 0; y < chunks[0].length; y++) {
				Chunk temp = chunks[x][y]; // Select a chunk
				temp.updateCoords(dx, dy); // Update the chunk's coordinates
				// Now, decide if the chunk should be added to or removed from the activeChunks list
				if (isVisible(temp)) {
					if (!activeChunks.contains(temp)) {
						activeChunks.add(temp);
					}

				} else {
					activeChunks.remove(temp);
				}
			}
		}

		// Now, check if we are in the ending chunk
		if (activeChunks.contains(endChunk)) {
			if (containsPlayer(endChunk, endBlock)) {
				endFound = true;
			}
		}
	}

	/**
	 * @return true if end has been found.
	 */
	public boolean endFound() { // TODO add testing?
		return endFound;
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

		final int[][] pbBounds = pb.getBounds(c.xPosition, c.yPosition);
		
		if (CollisionDetection.getCollision(pbBounds[0],pbBounds[1],playerXCoords,playerYCoords) == Collision.FULL_COLLISION) {
			return true;
		}
		return false;
	}

	/**
	 * Calls each chunk to draw itself.
	 * 
	 * @param g2d 2D graphics to draw on.
	 */
	public void draw(Graphics2D g2d) {
		// Loop through active chunks, those are the only ones we need to draw
		for(Chunk c: activeChunks) {
			c.draw(g2d);
		}
	}
	
	/**TODO Add testing and javadoc
	 * @param g2d
	 */
	public void drawEnemies(Graphics2D g2d) {
		for (Enemy e : Enemy.activeEnemies) {
			e.draw(g2d);
		}
	}

	/**
	 * Checks all active chunks for collision between walls and the player, returns
	 * a list of collisions which represent what side of the player is colliding, if
	 * any.
	 * 
	 * @return list of collisions that denote the direction of the collision or
	 *         NO_COLLISION.
	 */
	public List<Collision> checkCollision() {
		List<Collision> collisions = new ArrayList<>();

		// Adds any Integers returned from each chunk to the list
		for (Chunk c : activeChunks) {
			collisions.addAll(c.checkCollision(playerXCoords,playerYCoords));
		}

		return collisions;
	}

	/**
	 * @return All chunks in the level.
	 */
	public Chunk[][] getChunks() {
		return chunks;
	}

	/**
	 * Return a list of the chunks that appear on the screen.
	 * 
	 * @return the list of chunks that are visible on the screen.
	 */
	public List<Chunk> getActiveChunks() {
		return activeChunks;
	}
	
	/**
	 * @param chunk the chunk to check.
	 * @return If chunk is currently visible on screen.
	 */
	private boolean isVisible(Chunk chunk) {
		return (chunk.xPosition >= -chunkWidth && chunk.xPosition <= chunkWidth && chunk.yPosition >= -chunkHeight
				&& chunk.yPosition <= chunkHeight);
	}

	/**
	 * Sets the starting location to the start chunk.
	 */
	private void setStartLocation() {
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

	public static void main(String[] args) {
		// Create a chunk manager and load the level data.
		ChunkManager chunky = new ChunkManager();
		chunky.loadLevel(1);

		boolean all_passed = true;
		// Test that dimensions have been loaded correctly
		if (chunky.levelXDimension != 4) {
			System.out.println("The X-level dimension was not 4");
			all_passed = false;
		}
		if (chunky.chunkXDimension != 10) {
			System.out.println("The X Chunk size was not 10");
			all_passed = false;
		}
		if (chunky.getActiveChunks().size() != 4) {
			System.out.println("Originally, there should be four visible chunks");
			all_passed = false;
		}
		// Move our player to the left border of the screen, now only two chunks should
		// be visible
		chunky.updateCoords(-4000, 0);
		if (chunky.getActiveChunks().size() != 2) {
			System.out.println(
					"The player has been moved to the left border of the maze, only two chunks should be visible");
			all_passed = false;
		}
		// Move our player to the top left corner of the screen, now only one chunk
		// should be visible
		chunky.updateCoords(0, -3200);
		if (chunky.getActiveChunks().size() != 1) {
			System.out.println(
					"The player has been moved to the top left border of the maze, only one chunk should be visible");
			all_passed = false;
		}
		// Move our player way off the map, no chunks should be visible
		chunky.updateCoords(-20000, 0);
		if (chunky.getActiveChunks().size() != 0) {
			System.out.println("The player has been moved way off the screen, no chunks should be visible.");
			all_passed = false;
		}

		if (all_passed == true) {
			System.out.println("All cases passed! :)");
		} else {
			System.out.println("At least one case failed! :(");
		}
	}
}