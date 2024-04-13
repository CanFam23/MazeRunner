package blocks;

import java.awt.Graphics2D;
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

	private static VolatileImage image;

	/**
	 * Constructs a new EndBlock with the given parameters.
	 *
	 * @param x                  The x-coordinate relative to the top-left
	 *                           coordinate of the chunk.
	 * @param y                  The y-coordinate relative to the top-left
	 *                           coordinate of the chunk.
	 * @param width              The width of the block.
	 * @param height             The height of the block.
	 * @param positionBlockImage The color of the block.
	 */
	public EndBlock(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public static void setImage(VolatileImage positionBlockImage) {
		image = positionBlockImage;
	}

	@Override
	public void draw(Graphics2D g, int chunkXPosition, int chunkYPosition) {
		g.drawImage(image, x + chunkXPosition, y + chunkYPosition, width, height, null);
	}
}
