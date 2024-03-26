package src;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Description:
 * This file holds Enemy.java, Mage, Ghost, EnemyFactory, MageFactory, and GhostFactory. These are the classes used to
 * 	create enemies including loading their images, storing their locations, and drawing to the screen.
 * 
 * @author Andrew Denegar
 * @date 2024-03-26
 *
 */

abstract class Enemy {
	/**
	 * What the sprite is currently doing.
	 */
	public enum State {
		Idle, Move, Attack, Dead
	}

	/**
	 * The direction the sprite is currently facing.
	 */
	public enum Facing {
		S, SE, E, NE, N, NW, W, SW
	}
	
	/** Draw the enemy */
	public abstract void draw(Graphics2D g);
	
	// Test enemy classes
	public static void main(String[] args) {
		EnemyFactory mageCreator = new MageFactory();
		EnemyFactory ghostCreator = new GhostFactory();
		
		Enemy merlin = mageCreator.createEnemy();
		Enemy casper = ghostCreator.createEnemy();

        JFrame frame = new JFrame("Drawing Application");
        DrawingPanel panel = new DrawingPanel();
        
        panel.addDrawable(merlin);
        panel.addDrawable(casper);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack(); // Adjusts window to fit the preferred size and layouts of its subcomponents
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }

    static class DrawingPanel extends JPanel {
        private List<Enemy> drawables = new ArrayList<>();

        public DrawingPanel() {
            setPreferredSize(new Dimension(800, 600)); // Set the size of the panel
        }

        public void addDrawable(Enemy drawable) {
            drawables.add(drawable);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            for (Enemy drawable : drawables) {
                drawable.draw(g2d);
            }
        }
    }
}



class Mage extends Enemy {
	public static Map<State, List<BufferedImage>> images;
	
	private static final int MAGE_WIDTH = 100;
	private static final int MAGE_HEIGHT = 100;
	private State currentState = State.Idle;
	private Facing currentFacing = Facing.E;
	private int position_x = 10;
	private int position_y = 10;
	
	@Override
	public void draw(Graphics2D g) {
		if (currentFacing == Facing.E) {
			g.drawImage(images.get(currentState).get(0), position_x + MAGE_WIDTH, position_y, -MAGE_WIDTH,
					MAGE_HEIGHT, null);
		} else {
			g.drawImage(images.get(currentState).get(0), position_x, position_y, MAGE_WIDTH,
					MAGE_HEIGHT, null);
		}
		
		
	}
	
}

class Ghost extends Enemy {
	public static Map<State, List<BufferedImage>> images;
	
	private static final int GHOST_WIDTH = 100;
	private static final int GHOST_HEIGHT = 100;
	private State currentState = State.Idle;
	private Facing currentFacing = Facing.E;
	private int position_x = 110;
	private int position_y = 110;
	
	@Override
	public void draw(Graphics2D g) {
		if (currentFacing == Facing.E) {
			g.drawImage(images.get(currentState).get(0), position_x + GHOST_WIDTH, position_y, -GHOST_WIDTH,
					GHOST_HEIGHT, null);
		} else {
			g.drawImage(images.get(currentState).get(0), position_x, position_y, GHOST_WIDTH,
					GHOST_HEIGHT, null);
		}
		
		
	}

	
}


/**
 * EnemyFactory will be used to create up to five enemy factory classes: Beaver, Ghost, Mage, Necromancer, and Salamander.
 * 	Extensions of EnemyFactories will load the static images for each of the classes
 * 
 */
abstract class EnemyFactory {
	
	// Function that can be used by a user.
	public abstract Enemy createEnemy();
	
	/** load_images should be implemented separately for each enemy */
	protected abstract void load_images();
	
	/** load_spritesheet should be the same for each enemies*/
	protected void load_spritesheet(String FILE_LOCATION, String character_name, src.Enemy.State playerState, int imageNumber, Map<src.Enemy.State, List<BufferedImage>> images) {
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
		// Save constants used for spritesheet loading
		final int height = spriteSheet.getHeight();
		final int width = spriteSheet.getWidth();
		for (int i = 0; i < imageNumber; i++) {
			BufferedImage img = spriteSheet.getSubimage(0, (height / imageNumber) * i, width, (height / imageNumber));
			images.get(playerState).add(img);
		}
	}
}

/**
 * MageFactory will load the Mage images and create Mage instances using the createEnemy function.
 *
 */
class MageFactory extends EnemyFactory {
	
	static private Map<src.Enemy.State, List<BufferedImage>> images = new HashMap<>();
	
	public MageFactory() {
		load_images();
	}
	
	@Override
	public Enemy createEnemy() {
		return new Mage();
	}

	@Override
	protected void load_images() {
		final String character_name = "Mage";
		final String FILE_LOCATION = "Textures/Mage/";
		int imageNumber = 3; // This is the number of images in the spriteSheet
		int imageNumber2 = 6; // Idle and Move differ from Attack and Dead
		
		// Load a sprite sheet for each player state
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Idle, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Move, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Attack, imageNumber2, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Dead, imageNumber2, images);
		Mage.images = images;
	}
	
}

/**
 * GhostFactory will load the static images for Ghosts and create enemies using the createEnemy function.
 *
 */
class GhostFactory extends EnemyFactory {

	static private Map<src.Enemy.State, List<BufferedImage>> images = new HashMap<>();
	
	public GhostFactory() {
		load_images();
	}
	
	@Override
	public Enemy createEnemy() {
		return new Ghost();
	}

	@Override
	protected void load_images() {
		final String character_name = "Ghost";
		final String FILE_LOCATION = "Textures/Ghost/";
		int imageNumber = 3; // This is the number of images in the spriteSheet
		int imageNumber2 = 6; // Idle and Move differ from Attack and Dead
		
		// Load a spritesheet for each player state
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Idle, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Move, imageNumber, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Attack, imageNumber2, images);
		load_spritesheet(FILE_LOCATION, character_name, src.Enemy.State.Dead, imageNumber2, images);
		Ghost.images = images;
	}
	
}