package blocks;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.VolatileImage;

/**
 * <p>
 * EmptyBlock represents the end location of the Maze.
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
public class EndBlock extends PositionBlock {

	/**
	 * Constructs a new EndBlock with the given parameters.
	 * 
	 * @param x      The x-coordinate relative to the top-left coordinate of the
	 *               chunk.
	 * @param y      The y-coordinate relative to the top-left coordinate of the
	 *               chunk.
	 * @param width  The width of the block.
	 * @param height The height of the block.
	 * @param positionBlockImage      The color of the block.
	 */
	public EndBlock(int x, int y, int width, int height, VolatileImage positionBlockImage) {
		super(x, y, width, height, positionBlockImage);
	}

}
