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
            		// Resize image
            		BufferedImage resizedImage = new BufferedImage(PLAYER_WIDTH, PLAYER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics2D = resizedImage.createGraphics();
                    
                    graphics2D.drawImage(subImage.getScaledInstance(PLAYER_WIDTH, PLAYER_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
                    graphics2D.dispose();
                    
            		images.get(playerState).get(direction).add(resizedImage);
            		
            		if (count % framesPerAnim == 0) { 
            			System.out.println(count);
            			
            			direction = Facing.values()[count / framesPerAnim];
            			images.get(playerState).put(direction, new LinkedList<BufferedImage>());
            		}
            		 count++;
            	}
            }
        }
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(image, 0, 0, PLAYER_WIDTH, PLAYER_HEIGHT, null);
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
		String playerState = "Move"; // Test the player state images (Move, Idle, etc.)
		int speed = (int) (0.1 * 1000); // Set seconds (first number) between each image.
		
//		while (true) {
//			BufferedImage img = p1.images.get(playerState).remove(0);
//			displayImage(img);
//			try {
//				Thread.sleep(speed);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			p1.images.get(playerState).add(img);
//		}
		}
}
