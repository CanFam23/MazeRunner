package blocks;

import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

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
	 * Image for all starting blocks.
	 */
	private static VolatileImage image;

	/**
	 * Statically set the VolatileImage for all starting blocks.
	 *
	 * @param positionBlockImage VolatileImage that will be used for all starting
	 *                           blocks.
	 */
	public static void setImage(VolatileImage positionBlockImage) {
		image = positionBlockImage;
	}

	/**
	 * Constructs a new StartingBlock based on the block's starting position.
	 *
	 * @param x int starting x position.
	 * @param y int starting y position.
	 */
	public StartingBlock(int x, int y) {
		super(x, y);
	}

	@Override
	public void draw(Graphics2D g, int chunkXPosition, int chunkYPosition) {
		g.drawImage(image, x + chunkXPosition, y + chunkYPosition, width, height, null);
	}
}
