/**
 * Player.java
 * Authors: Andrew Denegar, Nick Clouse, Molly O'Connor
 * Date: 2024-03-20
 * Description: Player.java holds character attributes including images, state (Idle, Move...), facing (N, NE, E...) 
 * 	and methods including loading images, updating state, and drawing. 
 */

package src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import java.awt.*;

import javax.imageio.ImageIO;

public class Player implements GameVariables {
	
	private static final String FILE_LOCATION = "images/";
	
	public enum State {
		Idle, Move
	}

	// The direction the player is currently facing.
	public enum Facing {
		S, SE, E, NE, N, NW, W, SW
	}

	// draw count is used
	private int drawCount = 0;

	// Set initial player attributes
	private State currentState = State.Idle;
	private Facing currentFacing = Facing.N;

	// A map of images that can be accessed by first specifying the player state and direction faced
	private Map<State, Map<Facing, List<BufferedImage>>> images = new HashMap<>();
	
	/**
	 * Load images for each player state
	 * @param character_name: the name of the player file to be selected (Civilian1, Civilian2, Civilian1(black), etc)
	 */
	public void load_images(String character_name) {
		// Declare spritesheet dimensions
		final int SPRITESHEET_WIDTH = 4;
		final int SPRITESHEET_IDLE_HEIGHT = 2;
		final int SPRITESHEET_MOVE_HEIGHT = 8;
		
		// Load a spritesheet for each player state
		load_spritesheet(character_name, State.Idle, SPRITESHEET_WIDTH, SPRITESHEET_IDLE_HEIGHT);
		load_spritesheet(character_name, State.Move, SPRITESHEET_WIDTH, SPRITESHEET_MOVE_HEIGHT);
	}

	/**
	 * Load an individual spritesheet
	 * @param character_name: the name of the player file to be selected (Civilian1, Civilian2, Civilian1(black), etc)
	 * @param playerState: The player state that is to be loaded
	 * @param xDim: The x dimension of the spritesheet
	 * @param yDim: The y dimension of the spritesheet
	 */
	private void load_spritesheet(String character_name, State playerState, int xDim, int yDim) {
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
		// Split the spritesheet into individual images.
		// TODO: The image reading process could be improved in the future:
		// - Resize images originally, then you won't have to resize them here
		if (spriteSheet != null) {
			// Create a new HashMap for the specified player state.
			images.put(playerState, new HashMap<>());
			// Save constants used for spritesheet loading
			final int height = spriteSheet.getHeight();
			final int width = spriteSheet.getWidth();
			final int framesPerAnim = xDim * yDim / Facing.values().length;
			// Save image resizing constants
			final double WIDTH_POSITION_ADJUSTMENT = 2.5;
			final double HEIGHT_POSITION_ADJUSTMENT = 3.2;
			final double WIDTH_SIZE_ADJUSTMENT = 4;
			final double HEIGHT_SIZE_ADJUSTMENT = 2.7;
			// Count and direction will be changed based on the number of the image being loaded.
			int count = 0;
			Facing direction = Facing.values()[count];
			// Create the first list for the original state-facing pair.
			images.get(playerState).put(Facing.S, new LinkedList<BufferedImage>());
			for (int y = 0; y < yDim; y++) {
				for (int x = 0; x < xDim; x++) {
					// Read and resize image
					// Read image
					BufferedImage subImage = spriteSheet.getSubimage(x * width / xDim, y * height / yDim, width / xDim,
							height / yDim);
					// Now that we have the image split from the other part, let's remove the
					// whitespace
					BufferedImage finalImage = subImage.getSubimage((int) (width / xDim / WIDTH_POSITION_ADJUSTMENT), (int) (height / yDim / HEIGHT_POSITION_ADJUSTMENT),
							(int) (width / xDim / WIDTH_SIZE_ADJUSTMENT), (int) (height / yDim / HEIGHT_SIZE_ADJUSTMENT));

					images.get(playerState).get(direction).add(finalImage);

					count++;
					if (count % framesPerAnim == 0 && count != xDim * yDim) {
						direction = Facing.values()[count / framesPerAnim];
						images.get(playerState).put(direction, new LinkedList<BufferedImage>());
					}
				}
			}
		}
	}
	
	/**
	 * Update the direction that our player is facing
	 * @param up: The up arrow key is pressed
	 * @param down: The down arrow key is pressed 
	 * @param right: The right arrow key is pressed
	 * @param left: The left arrow key is pressed
	 */
	public void updateState(boolean up, boolean down, boolean right, boolean left) {
		// Set the player state (idle or move)
		if (up == false && down == false && right == false && left == false) {
			currentState = State.Idle;
			return;
		} else {
			currentState = State.Move;
		}
		// Set the direction headed
		String dir = "";
		if (up && !down)
			dir += "N";
		else if (down && !up)
			dir += "S";

		if (left && !right)
			dir += "W";
		else if (right && !left)
			dir += "E";

		if (!dir.equals(""))
			currentFacing = Facing.valueOf(dir);
	}
	
	public String getState() {
		return currentState.toString();
	}
	
	public String getFacing() {
		return currentFacing.toString();

	}

	// Change the direction the player is facing
	public void setFacing(Facing direction) {
		currentFacing = direction;
	}

	// Change the state of the player
	public void setState(State playerState) {
		currentState = playerState;
	}

	/**
	 * Draw our player. Draw handles the switching from one image in a sequence to the next. 
	 * @param g
	 */
	public void draw(Graphics2D g) {
		if (drawCount < 5) { // For x ticks of the game loop, draw the same image.
			g.drawImage(images.get(currentState).get(currentFacing).get(0), PLAYER_X, PLAYER_Y, PLAYER_WIDTH,
					PLAYER_HEIGHT, null);
			drawCount++;
		} else { // Then, switch the image to the next one in the sequence.
			BufferedImage img = images.get(currentState).get(currentFacing).remove(0);
			g.drawImage(img, PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, null);
			images.get(currentState).get(currentFacing).add(img);
			drawCount = 0;
		}

	}
	
	///////////////// BELOW CODE IS USED JUST FOR TESTING PURPOSES //////////////////
	/**
	 * Player code below is used for testing image loading. This code is all at the
	 * bottom because it shouldn't be used besides testing
	 */
	private static JFrame frame;
	private static JPanel panel;
	private static BufferedImage image;

	public static void initializeGUI() {
		frame = new JFrame("Image Display");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(10, 10, 400, 400);

		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// Draw the current image, if not null
				if (image != null) {
					g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
				}
			}
		};

		frame.add(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

	public static void displayImage(BufferedImage newImage) {
		image = newImage;
		panel.repaint(); // This will trigger paintComponent to redraw the image
	}

	public static void main(String[] args) {

		Player p1 = new Player();

		// Choose the player model analyzed
		p1.load_images("Civilian1(black)");

		// Start: Unit Testing
		boolean all_passed = true;
		// Update the state as if all keys were pressed at the same time
		// By default, this should set the current direction faced to North
		p1.updateState(true, true, true, true);
		if (p1.currentFacing != Facing.N) {
			System.out.println("When updateState() registers all keys being pressed, Facing should be set to north.");
			all_passed = false;
		}
		// Now set key press
		p1.updateState(false, false, true, false);
		if (p1.currentFacing != Facing.E) {
			System.out.println("Key press of right should change the player to be facing east.");
			all_passed = false;
		}
		p1.updateState(false, false, false, false);
		if (p1.currentState != State.Idle) {
			System.out.println("When no key is pressed, the current state should be idle.");
			all_passed = false;
		}
		p1.setFacing(Facing.SE);
		if (p1.currentFacing != Facing.SE) {
			System.out.println("setFacing() failed to set the direction of the player to southeast.");
			all_passed = false;
		}
		if (all_passed) {
			System.out.println("All cases passed, good job.");
		} else {
			System.out.println("At least one case failed");
		}
				
		// Start: Image testing
		initializeGUI();
		
		// Change these two variables to modify the animations tested
		State playerState = State.Move; // Test the player state images (Move, Idle, etc.)
		Facing direction = Facing.S; // Test the direction the player is facing
		int speed = (int) (0.1 * 1000); // Set seconds (first number) between each image.

		while (true) {
			BufferedImage img = p1.images.get(playerState).get(direction).remove(0);
			displayImage(img);
			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p1.images.get(playerState).get(direction).add(img);
		}
	}
}
