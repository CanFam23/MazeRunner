/*
 * ChunkManager.java
 * 
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * Date: March 2, 2024
 * 
 * Description: 
 * ChunkManager.java loads and holds the chunks that will be used in the game.
 * The chunks are basically building blocks of the level.
 * 		
 */

package src;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChunkManager implements GameVariables {
	// Declare attributes that are always the same
	static final String FILE_LOCATION = "data/";

	private final List<Chunk> activeChunks = new ArrayList<>();

	private int chunkWidth;
	private int chunkHeight;

	// Declare attributes that change

	// Starting and ending info
	private Chunk startChunk;
	private Chunk endChunk;
	
	private StartingBlock startBlock;
	private EndBlock endBlock;
	
	private int[] startCoords;
	private boolean endFound = false;
	
	// Level variables
	private String levelName = ""; 
	private int levelXDimension;
	private int levelYDimension;
	private int chunkXDimension;
	private int chunkYDimension;
	private Chunk[][] chunks;

	/**
	 * Loads level from levelNum and creates a 2D array of chunks, which represent each chunk of the maze
	 * 
	 * @param levelNum the level to load
	 * @return if level was loaded correctly
	 */
	public boolean loadLevel(int levelNum) {
		levelName = "level_" + levelNum;
		try (final Scanner input = new Scanner(new File(FILE_LOCATION + levelName + ".txt"))) {
			input.nextLine(); // Discard data description
			final String[] levelStrings = input.nextLine().split(":")[1].split("x"); // Save the dimension of the chunks
			                                                                         // - example: (x chunks, y
			                                                                         // chunks)
			levelXDimension = Integer.parseInt(levelStrings[0]); // TODO: Possibly change the level description loading
			                                                     // (redundant)
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
						        (yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.white);
					} else if (inputData[xPosition].equals("1")) { // Create a wall 
						pb = new Wall((xPosition % chunkXDimension) * WALL_WIDTH,
						        (yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.black);
					} else if (inputData[xPosition].equals("2")) { // Create a starting block
						pb = new StartingBlock((xPosition % chunkXDimension) * WALL_WIDTH,
						        (yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.blue);
						isStartingChunk = true;
					} else if (inputData[xPosition].equals("3")) { // Create an end block
						pb = new EndBlock((xPosition % chunkXDimension) * WALL_WIDTH,
						        (yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.green);
						isEndChunk = true;
					}
					chunks[yChunk][xChunk].add(xPosition % chunkXDimension, yPosition % chunkYDimension, pb);
					// Keep track of what chunks are the start and end
					if (isStartingChunk) {
						startChunk = chunks[yChunk][xChunk];
						startCoords = new int[] { xPosition % chunkXDimension * WALL_WIDTH,
						        yPosition % chunkYDimension * WALL_HEIGHT };
						startBlock = (StartingBlock) pb;
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
	 * Update coords of all chunks, if the chunk is visible, add to active chunks
	 * 
	 * @param dx int to change x by
	 * @param dy int to change y by
	 */
	public void updateCoords(int dx, int dy) {
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

		// Now, check if we are in the starting or ending chunk
		if (activeChunks.contains(startChunk)) {
			containsPlayer(startChunk, startBlock);
		} else if (activeChunks.contains(endChunk)) {
			if (containsPlayer(endChunk, endBlock)) {
				endFound = true;
			}
		}
	}

	/**
	 * @return if end has been found
	 */
	public boolean endFound() {
		return endFound;
	}

	/**
	 * Returns true if the player is in the given block, same idea used forcollisions
	 * 
	 * @param c Chunk to check for collision
	 * @param pb PositionBlock to check for collision
	 * @return if there is a full collision
	 */
	public boolean containsPlayer(Chunk c, PositionBlock pb) {

		if (c.collision(pb) == Collision.FULL_COLLISION) {
			return true;
		}
		return false;
	}

	/**
	 * Tell each chunk to draw itself
	 * @param g2d 2D graphics to draw on
	 */
	public void draw(Graphics2D g2d) {
		for (int i = 0; i < chunks.length; i++) {
			for (int j = 0; j < chunks[i].length; j++) {
				chunks[i][j].draw(g2d);
			}
		}
	}

	/**
	 * Checks all active chunks for collision between walls and the player, returns
	 * a list of collisions which represent what side of the player is colliding, if
	 * any
	 * 
	 * @return list of collisions that denote the direction of the collision or NO_COLLISION
	 */
	public List<Collision> checkCollision() {
		List<Collision> collisions = new ArrayList<>();

		// Adds any Integers returned from each chunk to the list
		for (Chunk c : activeChunks) {
			collisions.addAll(c.checkCollision());
		}

		return collisions;
	}

	/**
	 * @return All chunks in the level
	 */
	public Chunk[][] getChunks() {
		return chunks;
	}

	/**
	 * Return a list of the chunks that appear on the screen
	 * 
	 * @return the list of chunks that are visible on the screen
	 */
	public List<Chunk> getActiveChunks() {
		return activeChunks;
	}

	/**
	 * @param chunk chunk to check
	 * @return if chunk is currently visible on screen
	 */
	private boolean isVisible(Chunk chunk) {
		return (chunk.xPosition >= -chunkWidth && chunk.xPosition <= chunkWidth && chunk.yPosition >= -chunkHeight
		        && chunk.yPosition <= chunkHeight);
	}

	/**
	 * Sets the starting location to the start chunk
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