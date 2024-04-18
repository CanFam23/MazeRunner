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
	 * Constructs a new Wall based on the wall's starting position.
	 * 
	 * @param x int starting x position.
	 * @param y int starting y position.
	 */
	public Wall(int x, int y) {
		super(x, y);
	}

	/**
	 * Statically set the VolatileImage for all walls.
	 * 
	 * @param positionBlockImage VolatileImage that will be used for all walls.
	 */
	public static void setImage(VolatileImage positionBlockImage) {
		image = positionBlockImage;
	}

	@Override
	public void draw(Graphics2D g, int chunkXPosition, int chunkYPosition) {
		g.drawImage(image, x + chunkXPosition, y + chunkYPosition, width, height, null);
	}
}
