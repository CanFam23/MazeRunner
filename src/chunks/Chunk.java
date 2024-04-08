package chunks;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				 if (blocks[i][j] != null) { 
					 blocks[i][j].draw(g, xPosition, yPosition);
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
	 * @return 2D array of integers, which represent each 'corner' of a chunk.
	 */
	public int[][] getCoords() {
		return new int[][] { { xPosition, xPosition + chunkWidth, xPosition + chunkWidth, xPosition },
				{ yPosition, yPosition, yPosition + chunkHeight, yPosition + chunkHeight } };
	}
	
	//TODO add testing
	public int getXPosition() {
		return xPosition;
	}
	
	//TODO add testing
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

		final int[] chunkXCoords = new int[] { xPosition, xPosition + chunkWidth,
				xPosition + chunkWidth, xPosition };
		final int[] chunkYCoords = new int[] { yPosition, yPosition,
				yPosition + chunkHeight, yPosition + chunkHeight };

		// if player top left x < wall bottom right x
		if (xCoords[0] <= chunkXCoords[2]
				// if player bottom right x > wall top left x
				&& xCoords[2] >= chunkXCoords[0]
				// if player top left y < wall bottom right y
				&& yCoords[0] <= chunkYCoords[2]
				// if player bottom right y > wall top left y
				&& yCoords[2] >= chunkYCoords[0]) {
			return true;
		}
		
		return false;
	}
	
	
	
	/**
	 * Checks to see if any walls in the chunk are colliding with the given coordinates.
	 * 
	 * @param xCoords The y coordinates to use.
	 * @param yCoords The x coordinates to use.
	 * @return a List of Collision enumerators that represent the collisions found.
	 */
	public List<Collision> checkCollision(int[] xCoords, int[] yCoords) {

		final List<Collision> Collisions = new ArrayList<>();

		// Check each wall in the chunk, if a wall chunk and colliding, add it's
		// returned int value to list
		for (int r = 0; r < blocks.length; r++) {
			for (int c = 0; c < blocks[0].length; c++) {
				final PositionBlock temp = blocks[r][c];
				if (temp instanceof Wall) {
					
					final int[][] tempCoords = temp.getBounds(xPosition,yPosition);
					
					final Collision collided = CollisionDetection.getCollision(tempCoords[0], tempCoords[1], xCoords, yCoords);

					if (collided != Collision.NO_COLLISION) {
						Collisions.add(collided);
					}

				}
			}
		}

		return Collisions;
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

	/**
	 * Main Method.
	 * 
	 * @param args Arguements passed.
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
			System.out.println("Failed to load emptyBlock.png!");
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
		List<Collision> collisions = chunk.checkCollision(playerXCoords,playerYCoords);

		if (collisions.size() != numCollisions) {
			System.err.format("checkCollisions should've found %d collision, but it found %d\n", numCollisions,
					collisions.size());
			allPassed = false;
		}

		// Update the coords so two blocks should be colliding with the player
		chunk.updateCoords(WALL_WIDTH, WALL_HEIGHT / 2);
		numCollisions = 2;
		collisions = chunk.checkCollision(playerXCoords,playerYCoords);

		if (collisions.size() != numCollisions) {
			System.err.format("checkCollisions should've found %d collisions, but it found %d\n", numCollisions,
					collisions.size());
			allPassed = false;

		}

		// Update the coords so all blocks should be far away from the player, so 0
		// collisions should be found
		chunk.updateCoords((int) Math.pow(WALL_WIDTH, 2), WALL_HEIGHT / 2);
		numCollisions = 0;
		collisions = chunk.checkCollision(playerXCoords,playerYCoords);

		if (collisions.size() != numCollisions) {
			System.err.format("checkCollisions should've found %d collisions, but it found %d\n", numCollisions,
					collisions.size());
			allPassed = false;
		}

		// Checking collision function
		Chunk newChunk = new Chunk(0, 0, 0, 0);
		PositionBlock pb = new PositionBlock(PLAYER_X, PLAYER_Y, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		// Should find a collision because the PositiobBlock's coords are intersecting
		// with the players
		int[][] tempBounds = pb.getBounds(0, 0);
		if (CollisionDetection.getCollision(tempBounds[0],tempBounds[1],playerXCoords,playerYCoords) == Collision.NO_COLLISION) {
			System.err.println("Collision() found no collision, when it should have found one!");
			allPassed = false;
		}

		// Should find a collision because the PositiobBlock's coords are intersecting
		// with the players, this time they are slightly different
		pb = new PositionBlock(PLAYER_X + WALL_WIDTH / 4, PLAYER_Y, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);
		
		tempBounds = pb.getBounds(0, 0);

		if (CollisionDetection.getCollision(tempBounds[0],tempBounds[1],playerXCoords,playerYCoords) == Collision.NO_COLLISION) {
			System.err.println("Collision() found no collision, when it should have found one!");
			allPassed = false;
		}

		// Should not find a collision because the block is at 0,0
		pb = new PositionBlock(0, 0, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);
		tempBounds = pb.getBounds(0, 0);

		if (CollisionDetection.getCollision(tempBounds[0],tempBounds[1],playerXCoords,playerYCoords) != Collision.NO_COLLISION) {
			System.err.println("Collision() found a collision, when it shouldn't have found one!");
			allPassed = false;
		}
		
		boolean contains = newChunk.containsPoints(playerXCoords, playerYCoords);
		if(contains) {
			System.err.println("containsPoints() said the chunk contained the point, when it didn't!");
		}
		
		
		
		newChunk = new Chunk(chunkLength, chunkLength, chunkX, chunkY);
		contains = newChunk.containsPoints(playerXCoords, playerYCoords);
		if(!contains) {
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
