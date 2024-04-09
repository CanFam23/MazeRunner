package sprites;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GhostFactory will load the static images for Ghosts and create enemies using
 * the createEnemy function.
 *
 */
public class GhostFactory extends EnemyFactory {

	/** Hold images (which should be the same for all Ghosts) */
	static private Map<sprites.Enemy.State, List<BufferedImage>> images = new HashMap<>();

	static private int NUMATTACKINGIMAGES = 10;

	public GhostFactory() {
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
		return new Ghost(x, y, images, PADDING, NUMATTACKINGIMAGES);
	}

	@Override
	protected void load_images() {
		final String character_name = "Ghost";
		final String FILE_LOCATION = "Textures/Ghost/";
		int imageNumber = 3; // This is the number of images in the spriteSheet
		int imageNumber2 = 6; // Idle and Move differ from Attack and Dead

		// Load a spritesheet for each player state
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Idle, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Move, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Attack, NUMATTACKINGIMAGES, images);
		load_spritesheet(FILE_LOCATION, character_name, sprites.Enemy.State.Dead, 4, images);
	}

}