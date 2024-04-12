package blocks;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

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

		VolatileImage vImage = null;
		
		// Create utilities for Volatile Image loading
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfig = device.getDefaultConfiguration();
		
        // TODO: For image testing, all block types would need to be loaded and set.
		try {
			vImage = graphicsConfig.createCompatibleVolatileImage(WALL_WIDTH, WALL_HEIGHT);
			Graphics2D g = vImage.createGraphics();
			final BufferedImage positionBlockImage = ImageIO.read(new File("images/emptyBlock.png"));
			g.drawImage(positionBlockImage, 0, 0, null);
			g.dispose();
		} catch (IOException e) { // Something went wrong loading our image
			e.printStackTrace();
			System.err.println("Problem loading Volatile image");
		}
		
		// Set the static images of each type of position block
		EmptyBlock.setImage(vImage);
		EndBlock.setImage(vImage);
		Wall.setImage(vImage);
		StartingBlock.setImage(vImage);

		// Testing a position block
		PositionBlock pb = new PositionBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT);
		
		String blockType = "????";
		
		if (!pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, blockType)) {
			allPassed = false;
		}
		
		// Testing an empty block
		pb = new EmptyBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT);
		
		blockType = "empt";
		
		if (!pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, blockType)) {
			allPassed = false;
		}
		
		// Testing a wall
		pb = new Wall(initialX, initialY, WALL_WIDTH, WALL_HEIGHT);

		blockType = "wall";
		
		if (!pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, blockType)) {
			allPassed = false;
		}
				
		// Testing a starting block
		pb = new StartingBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT);

		blockType = "strt";
		
		if (!pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, blockType)) {
			allPassed = false;
		}
		
		// Testing a end block
		pb = new EndBlock(initialX, initialY, WALL_WIDTH, WALL_HEIGHT);

		blockType = "EndB";
		
		if (!pb.testMethods(initialX, initialY, WALL_WIDTH, WALL_HEIGHT, blockType)) {
			allPassed = false;
		}
		
		if (allPassed) {
			System.out.println("All cases passed!");
		} else {
			System.err.println("At least 1 case failed!");
		}
	}

	/** Height of the block. */
	protected int height;

	/** Block Image. PositionBlock should never directly be drawn */
	private VolatileImage image = null;

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
	public PositionBlock(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
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
	 * Creates the hitbox for the positionBlock and returns it.
	 * 
	 * @param xPosition X Position to base coordinates off of.
	 * @param yPosition Y Position to base coordinates off of.
	 * @return A 2D int[] array that represent the hitbox of the block.
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
	 * Gets coordinates of block.
	 * 
	 * @return the coordinates of the block.
	 */
	public int[] getCoords() {
		return new int[] { x, y };
	}

	/**
	 * Gets height of block.
	 * 
	 * @return height of block.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets width of block.
	 * 
	 * @return width of block.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Converts block to string.
	 * 
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
		// TODO: Now that we are using a static VolatileImage, this image loading test would have to be different
//		if (!this.image.equals(image)) {
//			System.err.println("Image assignment failed!");
//			allPassed = false;
//		}

		// Checking for correct toString conversion
		if (!toString().equals(blockStr)) {
			System.err.format("toString should be %s, not %s!\n", blockStr, toString());
			allPassed = false;
		}

		final int[] wallXCoords = new int[] { initX - HITBOX_BUFFER_AMOUNT, initX + WALL_WIDTH + HITBOX_BUFFER_AMOUNT,
				initX + WALL_WIDTH + HITBOX_BUFFER_AMOUNT, initX - HITBOX_BUFFER_AMOUNT };
		final int[] wallYCoords = new int[] { initY - HITBOX_BUFFER_AMOUNT, initY - HITBOX_BUFFER_AMOUNT,
				initY + WALL_HEIGHT + HITBOX_BUFFER_AMOUNT, initY + WALL_HEIGHT + HITBOX_BUFFER_AMOUNT };
		final int[][] checkCoords = new int[][] {wallXCoords,wallYCoords};
		
		final int[][] coords = getBounds(0,0);
		for(int r = 0; r < coords.length; r++) {
			for(int c = 0; c < coords[0].length; c++) {
				if(checkCoords[r][c] != coords[r][c]) {
					System.err.println("Hitbox coords are incorrect!");
					allPassed = false;
				}
			}
		}

		return allPassed;
	}
}
