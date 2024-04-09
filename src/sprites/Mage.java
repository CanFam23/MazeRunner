package sprites;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * Mage is a child of Enemy with attributes and unique images.
 * @author Andrew Denegar
 */
public class Mage extends Enemy {
	
	public Mage(int x, int y, Map<State, List<BufferedImage>> images, int[] PADDING, int attackImages) {
		this.images = images;
		this.PADDING = PADDING;
		this.NUMATTACKINGIMAGES = attackImages;
		WIDTH = 70;
		HEIGHT = 70;
		position_x = x;
		position_y = y;
		speed = 2;
		roamingSpeed = speed/2;
		DELTAS = new int[][]{{-speed, 0}, {0, -speed}, {0, speed}, {speed, 0}, {speed, speed},{-speed, speed},{speed, -speed},{-speed, -speed}};
	}
}