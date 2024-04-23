package sprites;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MageFactory will load the Mage images and create Mage instances using the
 * createEnemy function. Testing is found in Enemy.java.
 *
 */
public class MageFactory extends EnemyFactory {

	/** Singleton: Only one MageFactory should be created. */
	private static MageFactory single_instance = null;

	/** Hold images (which should be the same for all Mages) */
	static private Map<Enemy.State, List<BufferedImage>> images = new HashMap<>();

	static private final int NUMATTACKINGIMAGES = 6;

	static private final int NUMDEATHIMAGES = 6;
	
	static private int[] PADDING;
	
	static public BufferedImage finalDeathImage;

	private MageFactory() {
		// Store the padding for each enemy within it's factory class.
		TOP_PADDING = 12;
		RIGHT_PADDING = 12;
		BOTTOM_PADDING = 12;
		LEFT_PADDING = 12;
		PADDING = new int[] { TOP_PADDING, RIGHT_PADDING, BOTTOM_PADDING, LEFT_PADDING };
		load_images();
	}

	/**
	 * Singleton pattern returning the one instance of MageFactory to be used. 
	 * 
	 * @return MageFactory that can create Mage instances.
	 */
	public static synchronized MageFactory getInstance() {
		if (single_instance == null) {
			single_instance = new MageFactory();
		}

		return single_instance;
	}

	@Override
	public Enemy createEnemy(int x, int y) {
		return new Mage(x, y, images, PADDING, NUMATTACKINGIMAGES, finalDeathImage);
	}

	@Override
	protected void load_images() {
		final String character_name = "Mage";
		final String FILE_LOCATION = "Textures/Mage/";
		int imageNumber = 3; // This is the number of images in the spriteSheet

		// Load a sprite sheet for each player state
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Idle, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Move, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Attack, NUMATTACKINGIMAGES, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Dead, NUMDEATHIMAGES, images);
		set_final_death_image();
	}

	private void set_final_death_image() {
		finalDeathImage = images.get(Enemy.State.Dead).get(NUMDEATHIMAGES - 1);
	}
	
}
