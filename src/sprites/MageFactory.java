package sprites;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MageFactory will load the Mage images and create Mage instances using the createEnemy function.
 *
 */
public class MageFactory extends EnemyFactory {
	
	/** Hold images (which should be the same for all Mages) */
	static private Map<sprites.Enemy.State, List<BufferedImage>> images = new HashMap<>();
	
	static private int NUMATTACKINGIMAGES = 6;
	
	static private int[] PADDING;
	
	public MageFactory() {
		// Store the padding for each enemy within it's factory class.
		TOP_PADDING = 12;
		RIGHT_PADDING = 12;
		BOTTOM_PADDING = 12;
		LEFT_PADDING = 12;
		PADDING = new int[] {
				TOP_PADDING,
				RIGHT_PADDING,
				BOTTOM_PADDING,
				LEFT_PADDING
		};
		load_images();
	}
	
	@Override
	public Enemy createEnemy(int x, int y) {
		return new Mage(x, y, images, PADDING, NUMATTACKINGIMAGES);
	}

	@Override
	protected void load_images() {
		final String character_name = "Mage";
		final String FILE_LOCATION = "Textures/Mage/";
		int imageNumber = 3; // This is the number of images in the spriteSheet
		int imageNumber2 = 6; // Idle and Move differ from Attack and Dead
		
		// Load a sprite sheet for each player state
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Idle, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Move, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Attack, NUMATTACKINGIMAGES, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Dead, imageNumber2, images);
	}
	
}
