package sprites;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GhostFactory will load the static images for Ghosts and create enemies using
 * the createEnemy function. Testing is found in Enemy.java.
 *
 */
public class GhostFactory extends EnemyFactory {

	/**
	 * Final death image.
	 */
	static private BufferedImage finalDeathImage;

	/** Hold images (which should be the same for all Ghosts) */
	static private Map<Enemy.State, List<BufferedImage>> images = new HashMap<>();

	/**
	 * Number of attacking images.
	 */
	static private final int NUMATTACKINGIMAGES = 10;

	/**
	 * Number of death images.
	 */
	static private final int NUMDEATHIMAGES = 4;

	/** Singleton: Only one GhostFactory should be created. */
	private static GhostFactory single_instance = null;

	/**
	 * Singleton pattern returning the one instance of GhostFactory to be used.
	 *
	 * @return GhostFactory that can create Ghost instances.
	 */
	public static synchronized GhostFactory getInstance() {
		if (single_instance == null) {
			single_instance = new GhostFactory();
		}

		return single_instance;
	}

	/**
	 * Creates a new ghost factory
	 */
	private GhostFactory() {
		// Store the padding for each enemy within it's factory class.
		TOP_PADDING = 10;
		RIGHT_PADDING = 14;
		BOTTOM_PADDING = 14;
		LEFT_PADDING = 14;
		PADDING = new int[] { TOP_PADDING, RIGHT_PADDING, BOTTOM_PADDING, LEFT_PADDING };
		load_images();
	}

	@Override
	public Enemy createEnemy(int x, int y) {
		return new Ghost(x, y, images, PADDING, NUMATTACKINGIMAGES, finalDeathImage);
	}

	/**
	 * Sets the final death image.
	 */
	private void set_final_death_image() {
		finalDeathImage = images.get(Enemy.State.Dead).get(NUMDEATHIMAGES - 1);
	}

	@Override
	protected void load_images() {
		final String character_name = "Ghost";
		final String FILE_LOCATION = "Textures/Ghost/";
		final int imageNumber = 3; // This is the number of images in the spriteSheet

		// Load a spritesheet for each player state
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Idle, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Move, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Attack, NUMATTACKINGIMAGES, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Dead, NUMDEATHIMAGES, images);
		set_final_death_image();
	}
}