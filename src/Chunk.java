package src;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

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

	/** Used to make the block hitboxes slightly bigger then their dimensions. */
	private static final int HITBOX_BUFFER_AMOUNT = 6; // TODO How close is close enough to register a collision?

	/**
	 * Used to check if the collision overlap is close enough for collision, mainly
	 * multiple sides colliding at once.
	 */
	private static final int COLLISION_INT = 5;

	/**
	 * Used to check if a full collision is occurring, when all four sides of the
	 * object are overlapping by this amount or less.
	 */
	private static final int FULL_COLLISION_INT = 75;

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
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j].draw(g, xPosition, yPosition);
			}
		}

	}

	/**
	 * @return true if chunk is start chunk.
	 */
	public boolean isStartChunk() {
		return isStartChunk;
	}

	/**
	 * @return true if chunk is end chunk.
	 */
	public boolean isEndChunk() {
		return isEndChunk;
	}

	/**
	 * @return 2D array of integers, which represent each 'corner' of a chunk.
	 */
	public int[][] getCoords() {
		return new int[][] { { xPosition, xPosition + chunkWidth, xPosition + chunkWidth, xPosition },
				{ yPosition, yPosition, yPosition + chunkHeight, yPosition + chunkHeight } };
	}
	
	public int getXPosition() {
		return xPosition;
	}
	
	public int getYPosition() {
		return yPosition;
	}

	/**
	 * Returns a List of enumerators, which represent what side of the player is
	 * colliding with a wall.
	 * 
	 * @return ArrayList of Collision enumerators.
	 */
	public List<Collision> checkCollision() {

		final List<Collision> Collisions = new ArrayList<>();

		// Check each wall in the chunk, if a wall chunk and colliding, add it's
		// returned int value to list
		for (int r = 0; r < blocks.length; r++) {
			for (int c = 0; c < blocks[0].length; c++) {
				final PositionBlock temp = blocks[r][c];
				if (temp instanceof Wall) {
					final Collision collided = collision(temp);

					if (collided != Collision.NO_COLLISION) {
						Collisions.add(collided);
					}

				}
			}
		}

		return Collisions;
	}

	/**
	 * Checks for collision between player and wall.
	 * 
	 * @param PositionBlock w the block to check for collision.
	 * @return The corresponding collision.
	 */
	public Collision collision(PositionBlock w) {
		// Convert 2D array of coords into arrays of x coords and y coords
		final int[] otherCoords = w.getCoords();
		final int x = otherCoords[0] + xPosition;
		final int y = otherCoords[1] + yPosition;

		final int[] otherXCoords = new int[] { x - HITBOX_BUFFER_AMOUNT, x + WALL_WIDTH + HITBOX_BUFFER_AMOUNT,
				x + WALL_WIDTH + HITBOX_BUFFER_AMOUNT, x - HITBOX_BUFFER_AMOUNT };
		final int[] otherYCoords = new int[] { y - HITBOX_BUFFER_AMOUNT, y - HITBOX_BUFFER_AMOUNT,
				y + WALL_HEIGHT + HITBOX_BUFFER_AMOUNT, y + WALL_HEIGHT + HITBOX_BUFFER_AMOUNT };

		// if player top left x < wall bottom right x
		if (playerXCoords[0] <= otherXCoords[2]
				// if player bottom right x > wall top left x
				&& playerXCoords[2] >= otherXCoords[0]
				// if player top left y < wall bottom right y
				&& playerYCoords[0] <= otherYCoords[2]
				// if player bottom right y > wall top left y
				&& playerYCoords[2] >= otherYCoords[0]) {

			// find which side of the player is touching a wall
			final int overlapLeft = otherXCoords[1] - playerXCoords[0];
			final int overlapRight = playerXCoords[1] - otherXCoords[0];
			final int overlapTop = otherYCoords[2] - playerYCoords[0];
			final int overlapBottom = playerYCoords[2] - otherYCoords[1];

			// Find the smallest overlap
			final int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

			// this checks if the user is in the general center of the block, a 'full
			// collision'
			if (overlapLeft > FULL_COLLISION_INT && overlapRight > FULL_COLLISION_INT && overlapTop > FULL_COLLISION_INT
					&& overlapBottom > FULL_COLLISION_INT) {
				return Collision.FULL_COLLISION;
			}

			// If two sides are colliding at the same time
			// Bottom right corner
			if (overlapRight < COLLISION_INT && overlapBottom < COLLISION_INT) {
				return Collision.BOTTOM_RIGHT_CORNER;
				// Bottom left corner
			} else if (overlapLeft < COLLISION_INT && overlapBottom < COLLISION_INT) {
				return Collision.BOTTOM_LEFT_CORNER;
				// Top Right corner
			} else if (overlapRight < COLLISION_INT && overlapTop < COLLISION_INT) {
				return Collision.TOP_RIGHT_CORNER;
				// Top left corner
			} else if (overlapLeft < COLLISION_INT && overlapTop < COLLISION_INT) {
				return Collision.TOP_LEFT_CORNER;
			}

			// Return the side of the collision
			if (minOverlap == overlapLeft) {
				return Collision.LEFT_SIDE; // Collided from the left
			} else if (minOverlap == overlapRight) {
				return Collision.RIGHT_SIDE; // Collided from the right
			} else if (minOverlap == overlapTop) {
				return Collision.TOP_SIDE; // Collided from the top
			} else {
				return Collision.BOTTOM_SIDE; // Collided from the bottom
			}
		} else {
			// no collision
			return Collision.NO_COLLISION;
		}

	}

	/**
	 * @return The 2D array of blocks.
	 */
	public PositionBlock[][] getBlocks() {
		return blocks;
	}

	/**
	 * @return chunkWidth.
	 */
	public int getChunkWidth() {
		return chunkWidth;
	}

	/**
	 * @return chunkHeight.
	 */
	public int getChunkHeight() {
		return chunkHeight;
	}

	/**
	 * @return String format of chunk.
	 */
	public String toString() {
		String ret = "";
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[y].length; x++) {
				ret += blocks[y][x].toString();
			}
			ret += "\n";
		}
		return ret;
	}

	public static void main(String[] args) {
		boolean allPassed = true;

		final int chunkLength = 10;
		final int chunkX = 0;
		final int chunkY = 0;

		final int[][] initialCoords = {
				{ chunkX, chunkX + chunkLength * WALL_WIDTH, chunkX + chunkLength * WALL_WIDTH, chunkX },
				{ chunkY, chunkY, chunkY + chunkLength * WALL_HEIGHT, chunkY + chunkLength * WALL_HEIGHT } };

		final Chunk chunk = new Chunk(chunkLength, chunkLength, chunkX, chunkY);

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

		Image positionBlockImage = null;
		try {
			positionBlockImage = ImageIO.read(new File("images/emptyBlock.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Adding blocks to chunk
		for (int i = 0; i < Math.pow(chunkLength, 2); i++) {
			final PositionBlock pb = new Wall((r % chunkLength) * WALL_WIDTH, (c % chunkLength) * WALL_HEIGHT,
					WALL_WIDTH, WALL_HEIGHT, positionBlockImage);
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
		int numCollisions = 1;
		List<Collision> collisions = chunk.checkCollision();

		if (collisions.size() != numCollisions) {
			System.err.format("checkCollisions should've found %d collision, but it found %d\n", numCollisions,
					collisions.size());
			allPassed = false;
		}

		// Update the coords so two blocks should be colliding with the player
		chunk.updateCoords(WALL_WIDTH, WALL_HEIGHT / 2);
		numCollisions = 2;
		collisions = chunk.checkCollision();

		if (collisions.size() != numCollisions) {
			System.err.format("checkCollisions should've found %d collisions, but it found %d\n", numCollisions,
					collisions.size());
			allPassed = false;

		}

		// Update the coords so all blocks should be far away from the player, so 0
		// collisions should be found
		chunk.updateCoords((int) Math.pow(WALL_WIDTH, 2), WALL_HEIGHT / 2);
		numCollisions = 0;
		collisions = chunk.checkCollision();

		if (collisions.size() != numCollisions) {
			System.err.format("checkCollisions should've found %d collisions, but it found %d\n", numCollisions,
					collisions.size());
			allPassed = false;
		}

		// Checking collision function
		final Chunk newChunk = new Chunk(0, 0, 0, 0);
		PositionBlock pb = new PositionBlock(PLAYER_X, PLAYER_Y, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		// Should find a collision because the PositiobBlock's coords are intersecting
		// with the players
		if (newChunk.collision(pb) == Collision.NO_COLLISION) {
			System.err.println("Collision() found no collision, when it should have found one!");
			allPassed = false;
		}

		// Should find a collision because the PositiobBlock's coords are intersecting
		// with the players, this time they are slightly different
		pb = new PositionBlock(PLAYER_X + WALL_WIDTH / 4, PLAYER_Y, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		if (newChunk.collision(pb) == Collision.NO_COLLISION) {
			System.err.println("Collision() found no collision, when it should have found one!");
			allPassed = false;
		}

		// Should not find a collision because the block is at 0,0
		pb = new PositionBlock(0, 0, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		if (newChunk.collision(pb) != Collision.NO_COLLISION) {
			System.err.println("Collision() found a collision, when it shouldn't have found one!");
			allPassed = false;
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
