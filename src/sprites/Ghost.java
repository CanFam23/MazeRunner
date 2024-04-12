package sprites;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * Ghost is a child of Enemy with attributes and unique images.
 * Testing is found in Enemy.java.
 * 
 * @author Andrew Denegar
 */
public class Ghost extends Enemy {

	/**
	 * Create a ghost, which should be done through a GhostFactory.
	 * 
	 * @param x starting x position
	 * @param y starting y position
	 * @param images static images for ghost
	 * @param PADDING image padding to remove for non-moving images
	 * @param attackImages the number of attacking images
	 */
	public Ghost(int x, int y, Map<State, List<BufferedImage>> images, int[] PADDING, int attackImages) {
		this.images = images;
		this.PADDING = PADDING;
		this.NUMATTACKINGIMAGES = attackImages;
		WIDTH = 60;
		HEIGHT = 60;
		position_x = x;
		position_y = y;
		speed = 3;
		defaultSpeed = speed;
		roamingSpeed = speed / 2;
		DELTAS = new int[][] { { -speed, 0 }, { 0, -speed }, { 0, speed }, { speed, 0 }, { speed, speed },
				{ -speed, speed }, { speed, -speed }, { -speed, -speed } };
	}
}