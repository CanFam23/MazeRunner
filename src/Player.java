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
	
	private int drawCount = 0;
	
	public State currentState = State.Idle;
	public Facing currentFacing = Facing.N;
	
	public Map<State, Map<Facing, List<BufferedImage>>> images = new HashMap<>();
	
	public enum State {
		Idle,
		Move
	}
	
	public enum Facing {
		S, SE, E, NE,
		N, NW, W, SW
	}
	
	public void load_images(String character_name) {
		load_spritesheet(character_name, State.Idle, 4, 2);
		load_spritesheet(character_name, State.Move, 4, 8);
	}
	
	public void load_spritesheet(String character_name, State playerState, int xDim, int yDim) {
		BufferedImage spriteSheet = null;
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
        // 1. Reuse variables from one resize to the next OR
        // 2. Resize images originally, then you won't have to resize them here
        if (spriteSheet != null) {
        	images.put(playerState, new HashMap<>());
            int height = spriteSheet.getHeight();
            int width = spriteSheet.getWidth();
            int framesPerAnim = xDim * yDim / Facing.values().length;
            int count = 0;
            Facing direction = Facing.values()[count];
            images.get(playerState).put(Facing.S, new LinkedList<BufferedImage>());
            for (int y = 0; y < yDim; y++) {
            	for (int x = 0; x < xDim; x++) {
            		// Read and resize image
            		// Read image
            		BufferedImage subImage = spriteSheet.getSubimage(x * width / xDim, y * height / yDim, width / xDim, height / yDim);
            		// Now that we have the image split from the other part, let's remove the whitespace
            		BufferedImage finalImage = subImage.getSubimage(width / xDim / 3, height / yDim / 3, width / xDim / 3, height / yDim / 3);
                    
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
	
	public void updateState(int dx, int dy) {
		if (dx == 0 && dy == 0) {
			currentState = State.Idle;
			return;
		} else {
			currentState = State.Move;
		}
		if (dy > 0) { // Moving up
			if (dx < 0) {
				currentFacing = Facing.NE;
			} else if (dx == 0) {
				currentFacing = Facing.N;
			} else {
				currentFacing = Facing.NW;
			}
		} else if (dy < 0) { // Moving down
			if (dx < 0) {
				currentFacing = Facing.SE;
			} else if (dx == 0) {
				currentFacing = Facing.S;
			} else {
				currentFacing = Facing.SW;
			}
		} else if (dy == 0) { // Check that should always be true (not moving up or down)
			if (dx > 0) {
				currentFacing = Facing.W;
			} else {
				currentFacing = Facing.E;
			}
		}
	}
	
	// Change the direction the player is facing
	public void setFacing(Facing direction) {
		currentFacing = direction;
	}
	
	// Change the state of the player
	public void setState(State playerState) {
		currentState = playerState;
	}
	
	// Draw our player
	public void draw(Graphics2D g) { 
		if (drawCount < 5) { // For x ticks of the game loop, draw the same image.
			g.drawImage(images.get(currentState).get(currentFacing).get(0), PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, null);
			g.setColor(Color.RED);
			g.drawRect(PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
			drawCount++;
		} else { // Then, switch the image to the next one in the sequence.
			BufferedImage img = images.get(currentState).get(currentFacing).remove(0);
			g.drawImage(img, PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, null);
			images.get(currentState).get(currentFacing).add(img);
			g.setColor(Color.RED);
			g.drawRect(PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
			drawCount = 0;
		}
		
	}
	
	
	/**
	 * Player code below is used for testing image loading. 
	 * This code is all at the bottom because it shouldn't be used besides testing
	 */
	private static JFrame frame;
    private static JPanel panel;
    private static BufferedImage image;

    public static void initializeGUI() {
        frame = new JFrame("Image Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(10, 10, 400, 400);

        panel = new JPanel() {
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
		
		initializeGUI();
				
		
		// Change these two variables to modify the animations tested
		State playerState = State.Idle; // Test the player state images (Move, Idle, etc.)
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
