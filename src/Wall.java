package src;

import java.awt.Color;

/**
 * <p>
 * Wall class represents a wall in the maze.
 * </p>
 * <p>
 * It extends the {@link PositionBlock} class.
 * </p>
 * 
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * 
 * @since March 2, 2024
 */
public class Wall extends PositionBlock {

	/**
	 * Constructs a new Wall with given parameters.
	 * 
	 * @param x      coordinate relative to the top left coordinate of the chunk.
	 * @param y      coordinate relative to the top left coordinate of the chunk.
	 * @param width  of the block.
	 * @param height of the block.
	 * @param c      color of the block.
	 */
	public Wall(int x, int y, int width, int height, Color c) {
		super(x, y, width, height, c);
	}
}
