package blocks;

import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

/**
 * <p>
 * EmptyBlock represents a empty space in the maze.
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
public class EmptyBlock extends PositionBlock {

	/**
	 * Image for all empty blocks.
	 */
	private static VolatileImage image;

	/**
	 * Statically set the image for all empty blocks.
	 *
	 * @param positionBlockImage the image that will be used for empty blocks.
	 */
	public static void setImage(VolatileImage positionBlockImage) {
		image = positionBlockImage;
	}

	/**
	 * Constructs a new EmptyBlock based on the starting position.
	 *
	 * @param x The x-coordinate relative to the top-left coordinate of the chunk.
	 * @param y The y-coordinate relative to the top-left coordinate of the chunk.
	 */
	public EmptyBlock(int x, int y) {
		super(x, y);
	}

	@Override
	public void draw(Graphics2D g, int chunkXPosition, int chunkYPosition) {
		g.drawImage(image, x + chunkXPosition, y + chunkYPosition, width, height, null);
	}
}
