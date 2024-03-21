/*
 * ChunkManager.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: March 2, 2024
 * 
 * Desc:
 * 'TBD'
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

	private Chunk startChunk;
	private Chunk endChunk;

	private StartingBlock startBlock;
	private int[] startCoords;
	private EndBlock endBlock;

	private boolean endFound = false;

	// TODO: Refactor private
	// Declare attributes that change
	public String levelName = ""; // TODO: local refactor
	public int levelXDimension;
	public int levelYDimension;
	public int chunkXDimension;
	public int chunkYDimension;
	public Chunk[][] chunks;

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
			// TODO: Move variable declarations into loop
			int yPosition = 0;
			int yChunk = 0;
			int xChunk = 0;
			boolean isStartingChunk = false;
			boolean isEndChunk = false;
			while (input.hasNextLine()) {
				yChunk = yPosition / chunkYDimension;
				String[] inputData = input.nextLine().split("");
				for (int xPosition = 0; xPosition < inputData.length; xPosition++) {
					xChunk = xPosition / chunkXDimension;
					PositionBlock pb = null;
					if (inputData[xPosition].equals("0")) {
						pb = new EmptyBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.white);
					} else if (inputData[xPosition].equals("1")) {
						pb = new Wall((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.black);
					} else if (inputData[xPosition].equals("2")) {
						pb = new StartingBlock((xPosition % chunkXDimension) * WALL_WIDTH,
								(yPosition % chunkYDimension) * WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT, Color.blue);
						isStartingChunk = true;
					} else if (inputData[xPosition].equals("3")) {
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

	// Update coords of all chunks, if the chunk is visible, add to active chunks
	public void updateCoords(int dx, int dy) {
		for (int x = 0; x < chunks.length; x++) {
			for (int y = 0; y < chunks[0].length; y++) {
				Chunk temp = chunks[x][y];
				temp.updateCoords(dx, dy);
				if (isVisible(temp)) {
					if (!activeChunks.contains(temp)) {
						activeChunks.add(temp);
					}

				} else {
					activeChunks.remove(temp);
				}
			}
		}

		if (activeChunks.contains(startChunk)) {
			containsPlayer(startChunk, startBlock);
		} else if (activeChunks.contains(endChunk)) {
			if (containsPlayer(endChunk, endBlock)) {
				endFound = true;
			}
		}
	}

	public boolean endFound() {
		return endFound;
	}

	// Returns true if the player is in the given chunk, same idea used for
	// collisions
	public boolean containsPlayer(Chunk chunk) {

		final int[] playerXCoords = new int[] { SCREEN_WIDTH / 2, SCREEN_WIDTH / 2 + PLAYER_WIDTH,
				SCREEN_WIDTH / 2 + PLAYER_WIDTH, SCREEN_WIDTH / 2 };
		final int[] playerYCoords = new int[] { SCREEN_HEIGHT / 2, SCREEN_HEIGHT / 2, SCREEN_HEIGHT / 2 + PLAYER_WIDTH,
				SCREEN_HEIGHT / 2 + PLAYER_WIDTH };

		int[][] chunkCoords = chunk.getCoords();

		int[] chunkXCoords = chunkCoords[0];
		int[] chunkYCoords = chunkCoords[1];

		return (playerXCoords[0] <= chunkXCoords[2]
				// if player bottom right x > chunk top left x
				&& playerXCoords[2] >= chunkXCoords[0]
				// if player top left y < chunk bottom right y
				&& playerYCoords[0] <= chunkYCoords[2]
				// if player bottom right y > chunk top left y
				&& playerYCoords[2] >= chunkYCoords[0]);
	}

	// Returns true if the player is in the given block, same idea used for
	// collisions
	public boolean containsPlayer(Chunk c, PositionBlock pb) {

		if (c.collision(pb) == Collision.FULL_COLLISION) {
			return true;
		}
		return false;
	}

	// Returns true if the given chunk is currently visible on the screen, or close
	// to being visible
	public boolean isVisible(Chunk chunk) {

		return (chunk.xPosition >= -chunkWidth && chunk.xPosition <= chunkWidth && chunk.yPosition >= -chunkHeight
				&& chunk.yPosition <= chunkHeight);
	}

	// Sets the starting location to the start chunk
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

	public void draw(Graphics2D g2d) {
		for (int i = 0; i < chunks.length; i++) {
			for (int j = 0; j < chunks[i].length; j++) {
				chunks[i][j].draw(g2d);
			}
		}
	}

	/*
	 * Checks all active chunks for collision between walls and the player, returns
	 * a list of integers which represent what side of the player is colliding, if
	 * any
	 */
	public List<Collision> checkCollision() {
		List<Collision> collisions = new ArrayList<>();

		// Adds any Integers returned from each chunk to the list
		for (Chunk c : activeChunks) {
			collisions.addAll(c.checkCollision());
		}

		return collisions;
	}

	public Chunk[][] getChunks() {
		return chunks;
	}

	public List<Chunk> getActiveChunks() {
		return activeChunks;
	}

	public static void main(String[] args) {
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
		if (all_passed == true) {
			System.out.println("All cases passed! :)");
		} else {
			System.out.println("At least one case failed! :(");
		}

//		Chunk[][] chunks = chunky.getChunks();

//		for (int r = 0; r < chunks.length; r++) {
//			for (int c = 0; c < chunks.length; c++) {
//				System.out.println(chunks[r][c].xPosition + " " + chunks[r][c].yPosition);
//
//			}
//			System.out.println();
//		}

//        Chunk c = chunky.getCurrentChunk();
//        
//        System.out.println(c.xPosition + " " + c.yPosition + " " + c.getChunkNumber());
//        

	}
}