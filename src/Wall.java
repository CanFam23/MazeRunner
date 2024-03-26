package src;

import java.awt.Color;

/**
 * <h1>StartingBlock.java</h1>
 * 
 * <p>Wall class represents a wall in the maze
 * It extends the PositionBlock class.</p>
 * 
 * @author Nick Clouse, Andrew Denegar, Molly O'Connor
 * 
 * @since February 20, 2024
 * 
 * @see {@link PositionBlock}
 */
public class Wall extends PositionBlock {

	/**
	 * Constructs a new EmptyBlock with given parameters.
	 * 
	 * @param x coordinate relative to the top left coordinate of the chunk.
	 * @param y coordinate relative to the top left coordinate of the chunk.
	 * @param width of the block.
	 * @param height of the block.
	 * @param c color of the block.
	 */
	public Wall(int x, int y, int width, int height, Color c) {
		super(x, y, width, height, c);
	}
}
