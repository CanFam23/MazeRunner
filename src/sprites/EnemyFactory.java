package sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * EnemyFactory will be used to create up to five enemy factory classes: Beaver,
 * Ghost, Mage, Necromancer, and Salamander. Extensions of EnemyFactories will
 * load the static images for each of the classes. Testing is found in
 * Enemy.java.
 *
 */
public abstract class EnemyFactory {

	/**
	 * A user can create an enemy by calling this method and specifying the starting coordinates of the enemy.
	 * 
	 * @param x upper-left x coordinate of the enemy.
	 * @param y upper-left y coordinate of the enemy.
	 * @return Enemy the created enemy
	 */
	public abstract Enemy createEnemy(int x, int y);

	// Store the padding for each enemy within it's factory class.
	protected int TOP_PADDING;
	protected int RIGHT_PADDING;
	protected int BOTTOM_PADDING;
	protected int LEFT_PADDING;

	/**
	 * Padding will hold all the padding values which will be passed to the enemy
	 */
	protected int[] PADDING;

	/** load_images should be implemented separately for each enemy */
	protected abstract void load_images();

	/** load_spritesheet should be the same for each enemy. */
	protected void load_spritesheet(String FILE_LOCATION, String character_name, Enemy.State playerState,
			int imageNumber, Map<Enemy.State, List<BufferedImage>> images) {
		BufferedImage spriteSheet = null;
		// Load the spritesheet file
		if (playerState.toString() != null) {
			final String resource = FILE_LOCATION + character_name + "_" + playerState.toString() + ".png";
			try {
				spriteSheet = ImageIO.read(new File(resource));
			} catch (IOException e) {
				System.out.println("Image not found at '" + resource + "'");
			}
		}
		images.put(playerState, new LinkedList<>());
		// Save constants used for sprite sheet loading
		final int height = spriteSheet.getHeight();
		final int width = spriteSheet.getWidth();
		if (playerState == sprites.Enemy.State.Attack) {
			for (int i = 0; i < imageNumber; i++) {
				BufferedImage img = spriteSheet.getSubimage(0, (height / imageNumber) * i, width, height / imageNumber);
				images.get(playerState).add(img);
			}
		} else if (playerState == sprites.Enemy.State.Dead) {
			for (int i = imageNumber - 1; i >= 0; i--) {
				BufferedImage img = spriteSheet.getSubimage(LEFT_PADDING, (height / imageNumber) * i + TOP_PADDING,
						(width - LEFT_PADDING) - RIGHT_PADDING,
						((height / imageNumber) - TOP_PADDING) - BOTTOM_PADDING);
				images.get(playerState).add(img);
			}
		} else {
			for (int i = 0; i < imageNumber; i++) {
				BufferedImage img = spriteSheet.getSubimage(LEFT_PADDING, (height / imageNumber) * i + TOP_PADDING,
						(width - LEFT_PADDING) - RIGHT_PADDING,
						((height / imageNumber) - TOP_PADDING) - BOTTOM_PADDING);
				images.get(playerState).add(img);
			}
		}
	}
}