package src;

import java.awt.Color;

/**
 * <p>
 * StartingBlock represents the start location of the Maze.
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
public class StartingBlock extends PositionBlock {

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
	public StartingBlock(int x, int y, int width, int height, Color c) {
		super(x, y, width, height, c);
	}
}
