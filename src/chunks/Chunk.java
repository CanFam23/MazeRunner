package chunks;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import blocks.EndBlock;
import blocks.PositionBlock;
import blocks.StartingBlock;
import blocks.Wall;
import gameTools.CollisionDetection;
import gameTools.GameVariables;

/**
 * Chunk holds an nxn array of PositionBlocks and stores the x and y position of
 * the entire array. The draw() method draws all position blocks in the array.
 * It handles collision detection of each block within itself, keeps track of
 * its x and y position, and determines if it's a starting or ending chunk.
 * Implements the GameVariables interface.
 *
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 *
 * @since March 2, 2024
 *
 * @see GameVariables
 * @see PositionBlock
 */
public class Chunk implements GameVariables {

	/** The group of blocks the chunk keeps track of. */
	private PositionBlock[][] blocks;

	/** Width of the chunk. */
	private int chunkWidth;

	/** Height of the chunk. */
	private int chunkHeight;

	/** If the chunk contains the start block. */
	private boolean isStartChunk = false;

	/** If the chunk contains the end block. */
	private boolean isEndChunk = false;

	/** X position of the top left corner of the chunk. */
	public int xPosition;

	/** Y position of the top left corner of the chunk. */
	public int yPosition;

	/**
	 * Constructs a new Chunk with given dimensions and coordinates.
	 *
	 * @param xDimension number of rows in chunk.
	 * @param yDimension number of columns in chunk.
	 * @param xPosition  the top left x of the chunk relative to all other chunks.
	 * @param yPosition  the top left y of the chunk relative to all other chunks.
	 */
	public Chunk(int xDimension, int yDimension, int xPosition, int yPosition) {
		blocks = new PositionBlock[yDimension][xDimension];
		this.xPosition = WALL_WIDTH * xPosition * xDimension;
		this.yPosition = WALL_HEIGHT * yPosition * yDimension;

		this.chunkWidth = xDimension * WALL_WIDTH;
		this.chunkHeight = yDimension * WALL_HEIGHT;
	}

	/**
	 * Adds a position block to the chunk with position xPosition and yPosition.
	 *
	 * @param xPosition is the x position that the block appears in the chunk.
	 * @param yPosition is the y position that the block appears in the chunk.
	 * @param Block     can be any of the PositionBlock types (EmptyBlock, EndBlock,
	 *                  Wall, etc.).
	 */
	public void add(int xPosition, int yPosition, PositionBlock block) {
		blocks[yPosition][xPosition] = block;

		if (block instanceof StartingBlock) {
			isStartChunk = true;
		}
		if (block instanceof EndBlock) {
			isEndChunk = true;
		}
	}

	/**
	 * Update the coordinates of the chunk.
	 *
	 * @param dx is the change in the x direction to the position of the chunk.
	 * @param dy is the change in the y direction to the position of the chunk.
	 */
	public void updateCoords(int dx, int dy) {
		xPosition += dx;
		yPosition += dy;
	}

	/**
	 * Draw every PositionBlock in the chunk.
	 *
	 * @param g is the Graphics2D object that will be drawn with.
	 */
	public void draw(Graphics2D g) {
		for (PositionBlock[] block : blocks) {
			for (int j = 0; j < block.length; j++) {
				if (block[j] != null) {
					block[j].draw(g, xPosition, yPosition);
				}
			}
		}
	}

	/**
	 * Returns a boolean which represents if the chunk contains the start block.
	 *
	 * @return true if chunk is start chunk.
	 */
	public boolean isStartChunk() {
		return isStartChunk;
	}

	/**
	 * Returns a boolean which represents if the chunk contains the end block.
	 *
	 * @return true if chunk is end chunk.
	 */
	public boolean isEndChunk() {
		return isEndChunk;
	}

	/**
	 * Gets the coordinates of each corner of the chunk.
	 *
	 * @return 2D array of integers, which represent each 'corner' of a chunk.
	 */
	public int[][] getCoords() {
		return new int[][] { { xPosition, xPosition + chunkWidth, xPosition + chunkWidth, xPosition },
				{ yPosition, yPosition, yPosition + chunkHeight, yPosition + chunkHeight } };
	}

	/**
	 * Gets the current y position of the chunk.
	 *
	 * @return y position of chunk.
	 */
	public int getXPosition() {
		return xPosition;
	}

	/**
	 * Gets the current x position of the chunk.
	 *
	 * @return x position of chunk.
	 */
	public int getYPosition() {
		return yPosition;
	}

	/**
	 * Checks if the chunk contains the given coordinates.
	 *
	 * @param xCoords The y coordinates to use.
	 * @param yCoords The x coordinates to use.
	 * @return true if the chunk contains those points.
	 */
	public boolean containsPoints(int[] xCoords, int[] yCoords) {

		final int[] chunkXCoords = new int[] { xPosition, xPosition + chunkWidth, xPosition + chunkWidth, xPosition };
		final int[] chunkYCoords = new int[] { yPosition, yPosition, yPosition + chunkHeight, yPosition + chunkHeight };

		return CollisionDetection.getCollision(xCoords, yCoords, chunkXCoords, chunkYCoords);
	}

	/**
	 * Checks for any collision between the given coordinates, and all walls in the
	 * chunk. Deltas is used to displace the blocks if needed, to see if displacing
	 * the blocks by those amounts would result in a collision.
	 *
	 * @param xCoords X coordinates to use.
	 * @param yCoords Y coordinates to use.
	 * @param deltas  Displacement of x and y.
	 * @return true if a collision is found.
	 */
	public boolean checkCollision(int[] xCoords, int[] yCoords, Integer[] deltas) {

		boolean collided = false;
		// Check each wall in the chunk, if a wall chunk and colliding, return true;
		for (PositionBlock[] block : blocks) {
			for (int c = 0; c < blocks[0].length; c++) {
				final PositionBlock temp = block[c];
				if (temp instanceof Wall) {

					//We get the hitbox coords of each wall using the position if we were to update it with deltas
					final int[][] tempCoords = temp.getHitbox(xPosition - deltas[0], yPosition - deltas[1]);

					collided = CollisionDetection.getCollision(tempCoords[0], tempCoords[1], xCoords, yCoords);

					if (collided) {
						return true;
					}

				}
			}
		}

		return false;
	}

	/**
	 * Gets all blocks in the chunk.
	 *
	 * @return The 2D array of blocks.
	 */
	public PositionBlock[][] getBlocks() {
		return blocks;
	}

	/**
	 * Gets the width of the chunk.
	 *
	 * @return chunkWidth.
	 */
	public int getChunkWidth() {
		return chunkWidth;
	}

	/**
	 * Gets the height of the chunk.
	 *
	 * @return chunkHeight.
	 */
	public int getChunkHeight() {
		return chunkHeight;
	}

	/**
	 * Converts chunk to string.
	 *
	 * @return String format of chunk.
	 */
	@Override
	public String toString() {
		String ret = "";
		for (PositionBlock[] block : blocks) {
			for (int x = 0; x < block.length; x++) {
				ret += block[x].toString();
			}
			ret += "\n";
		}
		return ret;
	}

	/**
	 * Main Method, used for testing.
	 *
	 * @param args Arguments passed.
	 */
	public static void main(String[] args) {
		boolean allPassed = true;

		final int chunkLength = 10;
		final int chunkX = 0;
		final int chunkY = 0;

		final int[][] initialCoords = {
				{ chunkX, chunkX + chunkLength * WALL_WIDTH, chunkX + chunkLength * WALL_WIDTH, chunkX },
				{ chunkY, chunkY, chunkY + chunkLength * WALL_HEIGHT, chunkY + chunkLength * WALL_HEIGHT } };

		final Chunk chunk = new Chunk(chunkLength, chunkLength, chunkX, chunkY);

		if (chunk.getXPosition() != chunkX) {
			System.err.println("Error when initializing chunk x coordinate!");
			allPassed = false;
		}

		if (chunk.getYPosition() != chunkY) {
			System.err.println("Error when initializing chunk y coordinate!");
			allPassed = false;
		}

		// Testing chunk width and height initializing
		if (chunk.getChunkHeight() != chunkLength * WALL_HEIGHT && chunk.getChunkHeight() != chunkLength * WALL_WIDTH) {
			System.err.println("Error when initializing chunk width and height!");
			allPassed = false;
		}

		// Checking initial chunk coords
		final int[][] tempCoords = chunk.getCoords();
		for (int r = 0; r < tempCoords[0].length; r++) {
			if (tempCoords[0][r] != initialCoords[0][r]) {
				System.err.println("Chunk x dimensions are incorrect!");
				System.err.format("%d should be %d\n", tempCoords[0][r], initialCoords[0][r]);
			} else if (tempCoords[1][r] != initialCoords[1][r]) {
				System.err.println("Chunk y dimensions are incorrect!");
				System.err.format("%d should be %d\n", tempCoords[1][r], initialCoords[1][r]);
			}
		}

		// Move chunk slightly
		chunk.updateCoords(chunkLength, chunkLength);

		// Checking new chunk coords/ update function
		final int[][] newCoords = chunk.getCoords();
		for (int r = 0; r < tempCoords[0].length; r++) {
			if (tempCoords[0][r] != initialCoords[0][r]) {
				System.err.println("Chunk x dimensions are incorrect!");
				System.err.format("%d should be %d\n", newCoords[0][r], initialCoords[0][r]);
			} else if (tempCoords[1][r] != initialCoords[1][r]) {
				System.err.println("Chunk y dimensions are incorrect!");
				System.err.format("%d should be %d\n", newCoords[1][r], initialCoords[1][r]);
			}
		}

		int r = 0;
		int c = 0;

		VolatileImage vImage = null;

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfig = device.getDefaultConfiguration();

		try {
			vImage = graphicsConfig.createCompatibleVolatileImage(WALL_WIDTH, WALL_HEIGHT);
			Graphics2D g = vImage.createGraphics();
			final BufferedImage positionBlockImage = ImageIO.read(new File("images/emptyBlock.png"));
			g.drawImage(positionBlockImage, 0, 0, null);
			g.dispose();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Adding blocks to chunk
		for (int i = 0; i < Math.pow(chunkLength, 2); i++) {
			final PositionBlock pb = new Wall((r % chunkLength) * WALL_WIDTH, (c % chunkLength) * WALL_HEIGHT,
					WALL_WIDTH, WALL_HEIGHT);
			chunk.add(r, c, pb);

			if (r == chunkLength - 1) {
				r = 0;
				c++;
			} else {
				r++;
			}
		}

		// Testing isEndChunk returns correct boolean
		if (chunk.isEndChunk()) {
			System.err.println("isEndChunk() said the chunk is a end chunk, when it's not!");
			allPassed = false;
		}

		// Testing isStartChunk returns correct boolean
		if (chunk.isStartChunk()) {
			System.err.println("isStartChunk() said the chunk is a start chunk, when it's not!");
			allPassed = false;
		}

		// The starting location of the chunk is (0,0), and with how many blocks there
		// are (10x10 == 100), there should only
		// be one block thats colliding with the player
		boolean collisions = chunk.checkCollision(playerXCoords, playerYCoords, new Integer[] { 0, 0 });

		if (!collisions) {
			System.err.println("checkCollisions should've found a collision!");
			allPassed = false;
		}

		// Update the coords so two blocks should be colliding with the player
		chunk.updateCoords(WALL_WIDTH, WALL_HEIGHT / 2);
		collisions = chunk.checkCollision(playerXCoords, playerYCoords, new Integer[] { 0, 0 });

		if (!collisions) {
			System.err.println("checkCollisions should've found a collision!");
			allPassed = false;
		}

		// Update the coords so all blocks should be far away from the player, so 0
		// collisions should be found
		chunk.updateCoords((int) Math.pow(WALL_WIDTH, 2), WALL_HEIGHT / 2);
		collisions = chunk.checkCollision(playerXCoords, playerYCoords, new Integer[] { 0, 0 });

		if (collisions) {
			System.err.println("checkCollisions shouldn't have found a collision!");
			allPassed = false;
		}

		// Checking collision function
		Chunk newChunk = new Chunk(0, 0, 0, 0);
		PositionBlock pb = new PositionBlock(PLAYER_X, PLAYER_Y, WALL_WIDTH, WALL_HEIGHT);

		// Should find a collision because the PositiobBlock's coords are intersecting
		// with the players
		int[][] tempBounds = pb.getHitbox(0, 0);
		if (!CollisionDetection.getCollision(tempBounds[0], tempBounds[1], playerXCoords, playerYCoords)) {
			System.err.println("Collision() found no collision, when it should have found one!");
			allPassed = false;
		}

		// Should find a collision because the PositiobBlock's coords are intersecting
		// with the players, this time they are slightly different
		pb = new PositionBlock(PLAYER_X + WALL_WIDTH / 4, PLAYER_Y, WALL_WIDTH, WALL_HEIGHT);

		tempBounds = pb.getHitbox(0, 0);

		if (!CollisionDetection.getCollision(tempBounds[0], tempBounds[1], playerXCoords, playerYCoords)) {
			System.err.println("Collision() found no collision, when it should have found one!");
			allPassed = false;
		}

		// Should not find a collision because the block is at 0,0
		pb = new PositionBlock(0, 0, WALL_WIDTH, WALL_HEIGHT);
		tempBounds = pb.getHitbox(0, 0);

		if (CollisionDetection.getCollision(tempBounds[0], tempBounds[1], playerXCoords, playerYCoords)) {
			System.err.println("Collision() found a collision, when it shouldn't have found one!");
			allPassed = false;
		}

		boolean contains = newChunk.containsPoints(playerXCoords, playerYCoords);
		if (contains) {
			System.err.println("containsPoints() said the chunk contained the point, when it didn't!");
		}

		newChunk = new Chunk(chunkLength, chunkLength, chunkX, chunkY);
		contains = newChunk.containsPoints(playerXCoords, playerYCoords);
		if (!contains) {
			System.err.println("containsPoints() said the chunk didn't contain the points, when it did!");
		}

		// Testing toString
		String chunkStr = chunk.toString();

		String[] chunkStrSplit = chunkStr.split("\n");

		String correctStr = "";

		// Make a string of 'wall' with wall repeated chunkLength times
		for (int i = 0; i < chunkLength; i++) {
			correctStr += "wall";
		}

		// Each line should equal the correctStr
		for (String s : chunkStrSplit) {
			if (!s.equals(correctStr)) {
				System.err.println("toString failed!");
				allPassed = false;
			}
		}

		if (allPassed) {
			System.out.println("All cases passed!");
		} else {
			System.err.println("At least 1 case failed!");
		}
	}

}
