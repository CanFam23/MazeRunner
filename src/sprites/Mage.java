package sprites;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * Mage is a child of Enemy with attributes and unique images. Testing is found
 * in Enemy.java.
 *
 * @author Andrew Denegar
 */
public class Mage extends Enemy {

	/**
	 * Create a Mage, which should be done through a MageFactory.
	 *
	 * @param x            starting x position
	 * @param y            starting y position
	 * @param images       static images for mage
	 * @param PADDING      image padding to remove for non-moving images
	 * @param attackImages the number of attacking images
	 */
	public Mage(int x, int y, Map<State, List<BufferedImage>> images, int[] PADDING, int attackImages, BufferedImage finalDeathImage) {
		this.images = images;
		this.PADDING = PADDING;
		this.NUMATTACKINGIMAGES = attackImages;
		this.damage = 2500;
		this.finalDeathImage = finalDeathImage;
		WIDTH = 70;
		HEIGHT = 70;
		position_x = x;
		position_y = y;
		speed = 2;
		defaultSpeed = speed;
		roamingSpeed = speed / 2;
		DELTAS = new int[][] { { -speed, 0 }, { 0, -speed }, { 0, speed }, { speed, 0 }, { speed, speed },
				{ -speed, speed }, { speed, -speed }, { -speed, -speed } };
	}
}