package blocks;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import gameTools.GameVariables;

/**
 * PositionBlock creates a individual block of the maze, which could be a wall,
 * empty space, start block, or end block.
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since March 2, 2024
 * 
 * @see GameVariables
 * @see Wall
 * @see EmptyBlock
 * @see EndBlock
 * @see StartingBlocks
 */
public class PositionBlock implements GameVariables {

	/**
	 * Main method
	 * 
	 * @param args arguements passed
	 */
	public static void main(String[] args) {
		boolean allPassed = true;

		final int initialX = 0;
		final int initialY = 0;

		Image positionBlockImage = null;
		try {
			positionBlockImage = ImageIO.read(new File("images/emptyBlock.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block, change this
			e.printStackTrace();
		}

		// Testing a position block
		PositionBlock pb = new PositionBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		allPassed = pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, "????");

		// Testing an empty block
		pb = new EmptyBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		allPassed = pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, "empt");

		// Testing a wall
		pb = new Wall(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		allPassed = pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, "wall");

		// Testing a starting block
		pb = new StartingBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		allPassed = pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, "strt");

		// Testing a end block
		pb = new EndBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, positionBlockImage);

		allPassed = pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, "EndB");

		if (allPassed) {
			System.out.println("All cases passed!");
		} else {
			System.err.println("At least 1 case failed!");
		}
	}
	/** Block Image. */
	private Image image;

	/** Height of the block. */
	protected int height;

	/** Width of the block. */
	protected int width;

	/** X coordinate of the block. */
	protected int x;

	/** Y coordinate of the block. */
	protected int y;

	/**
	 * Constructs a new StartingBlock with the given parameters.
	 * 
	 * @param x      The x-coordinate relative to the top-left coordinate of the
	 *               chunk.
	 * @param y      The y-coordinate relative to the top-left coordinate of the
	 *               chunk.
	 * @param width  The width of the block.
	 * @param height The height of the block.
	 * @param c      The color of the block.
	 */
	public PositionBlock(int x, int y, int width, int height, Image image) {
		this.x = x;
		this.y = y;
		this.image = image;
		this.width = width;
		this.height = height;
	}

	/**
	 * Draws block on g.
	 *
	 * @param g              2DGraphics to draw on.
	 * @param chunkXPosition x coordinate of chunk.
	 * @param chunkYPosition y coordinate of chunk.
	 */
	public void draw(Graphics2D g, int chunkXPosition, int chunkYPosition) {
		g.drawImage(image, x + chunkXPosition, y + chunkYPosition, width, height, null);
	}

	/**
	 * 
	 * TODO ADD testing
	 * 
	 * @return
	 */
	public int[][] getBounds(int xPosition, int yPosition) {
		final int newX = x + xPosition;
		final int newY = y + yPosition;

		final int[] wallXCoords = new int[] { newX - HITBOX_BUFFER_AMOUNT, newX + WALL_WIDTH + HITBOX_BUFFER_AMOUNT,
				newX + WALL_WIDTH + HITBOX_BUFFER_AMOUNT, newX - HITBOX_BUFFER_AMOUNT };
		final int[] wallYCoords = new int[] { newY - HITBOX_BUFFER_AMOUNT, newY - HITBOX_BUFFER_AMOUNT,
				newY + WALL_HEIGHT + HITBOX_BUFFER_AMOUNT, newY + WALL_HEIGHT + HITBOX_BUFFER_AMOUNT };

		return new int[][] { wallXCoords, wallYCoords };
	}

	/**
	 * @return the coordinates of the block.
	 */
	public int[] getCoords() {
		return new int[] { x, y };
	}

	/**
	 * @return height of block.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return width of block.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return string version of block.
	 */
	public String toString() {
		if (this instanceof EmptyBlock)
			return "empt";
		else if (this instanceof Wall)
			return "wall";
		else if (this instanceof StartingBlock)
			return "strt";
		else if (this instanceof EndBlock) {
			return "EndB";
		} else
			return "????";
	}

	/**
	 * Updates block coordinates.
	 *
	 * @param dx the integer to update x by.
	 * @param dy the integer to update y by.
	 */
	public void updateCoords(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	/**
	 * Tests methods of PositionBlock. We made this a method so we could test all
	 * child classes within this file too.
	 *
	 * @param initX    starting x coordinate
	 * @param initY    starting y coordinate
	 * @param width    of the block
	 * @param height   of the block
	 * @param blockStr expected string representation of the block
	 * @return true if all tests were passed
	 */
	private boolean testMethods(int initX, int initY, int width, int height, String blockStr) {
		boolean allPassed = true;

		int[] initialCoords = getCoords();

		System.out.format("Testing PositionBlock of type %s.\n", toString());

		// Checking initial X coord is correct
		if (initialCoords[0] != initX) {
			System.err.format("Initial x should be %d, not %d!\n", initX, initialCoords[0]);
			allPassed = false;
		}

		// Checking initial Y coord is correct
		if (initialCoords[1] != initY) {
			System.err.format("Initial y should be %d, not %d!\n", initY, initialCoords[1]);
			allPassed = false;
		}

		// Checking for correct width
		if (getWidth() != width) {
			System.err.format("Width should be %d, not %d!\n", width, getWidth());
			allPassed = false;
		}

		// Checking for correct height
		if (getHeight() != height) {
			System.err.format("Height should be %d, not %d!\n", height, getHeight());
			allPassed = false;
		}
		// Checking for correct image assignment
		if (!this.image.equals(image)) {
			System.err.println("Image assignment failed!");
			allPassed = false;
		}

		// Checking for correct toString conversion
		if (!toString().equals(blockStr)) {
			System.err.format("toString should be %s, not %s!\n", blockStr, toString());
			allPassed = false;
		}

		return allPassed;
	}
}
