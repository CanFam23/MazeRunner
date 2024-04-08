package sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * EnemyFactory will be used to create up to five enemy factory classes: Beaver, Ghost, Mage, Necromancer, and Salamander.
 * 	Extensions of EnemyFactories will load the static images for each of the classes
 * 
 */
public abstract class EnemyFactory {
	
	// Function that can be used by a user.
	public abstract Enemy createEnemy(int x, int y);
	
	// Store the padding for each enemy within it's factory class.
	public int TOP_PADDING;
	public int RIGHT_PADDING;
	public int BOTTOM_PADDING;
	public int LEFT_PADDING;
	
	/** load_images should be implemented separately for each enemy */
	protected abstract void load_images();
	
	/** load_spritesheet should be the same for each enemy. */
	protected void load_spritesheet(String FILE_LOCATION, String character_name, sprites.Enemy.State playerState, int imageNumber, Map<sprites.Enemy.State, List<BufferedImage>> images) {
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
		for (int i = 0; i < imageNumber; i++) {
			BufferedImage img = spriteSheet.getSubimage(LEFT_PADDING, (height / imageNumber) * i + TOP_PADDING, (width - LEFT_PADDING) - RIGHT_PADDING, ((height / imageNumber) - TOP_PADDING) - BOTTOM_PADDING);
			images.get(playerState).add(img);
		}
	}
}