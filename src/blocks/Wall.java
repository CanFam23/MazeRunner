package blocks;

import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

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

	private static VolatileImage image;

	/**
	 * Constructs a new Wall with given parameters.
	 *
	 * @param x                  coordinate relative to the top left coordinate of
	 *                           the chunk.
	 * @param y                  coordinate relative to the top left coordinate of
	 *                           the chunk.
	 * @param width              of the block.
	 * @param height             of the block.
	 * @param positionBlockImage color of the block.
	 */
	public Wall(int x, int y, int width, int height) {
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
