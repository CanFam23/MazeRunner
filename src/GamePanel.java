package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 123455L;

	// Screen Settings

	private final int screenWidth = 1000;
	private final int screenHeight = 800;
	private final ChunkManager cmanager;

	private final KeyHandler keyH = new KeyHandler(); // Uses to detect key presses/releases
	private Thread gameThread; // Thread is a thread of execution in a program

	private boolean isRunning = true;

	private final int[][] grid = { { 1, 0, 0, 0, 0, 1 }, 
							{ 1, 0, 1, 1, 0, 1 }, 
							{ 1, 0, 1, 1, 0, 1 }, 
							{ 1, 0, 0, 0, 0, 1 },
							{ 1, 0, 1, 1, 1, 1 } };

	private final List<Wall> wallsList = new ArrayList<Wall>();
	private final List<Integer> fpsTracker = new ArrayList<Integer>();

	private final int playerX = screenWidth / 3;
	private final int playerY = screenHeight / 3;
	private final int PLAYER_WIDTH = 50;
	private final int PLAYER_HEIGHT = 50;
	
	private final int WALL_WIDTH = screenWidth/5;
	private final int WALL_HEIGHT = screenHeight/5;

	public GamePanel() {
		// this.setPreferredSize(new Dimension(JFrame.MAXIMIZED_HORIZ,
		// JFrame.MAXIMIZED_VERT));
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);

		this.addKeyListener(keyH);
		this.setFocusable(true);

		cmanager = new ChunkManager();
		cmanager.loadLevel(1);
	}

	// Makes new thread and starts its
	public void startGameThread() {

		gameThread = new Thread(this);
		gameThread.start();
	}

	// When we start the gameThread, this function is ran
	// This is the same game loop Minecraft uses
	@Override
	public void run() {

		long lastime = System.nanoTime();
		final double AmountOfTicks = 60;
		final double ns = 1000000000 / AmountOfTicks;
		double delta = 0;
		int frames = 0;
		double time = System.currentTimeMillis();

		while (isRunning == true) {
			long now = System.nanoTime();
			delta += (now - lastime) / ns;
			lastime = now;

			if (delta >= 1) {

				update();
				repaint();

				frames++;
				delta--;
				if (System.currentTimeMillis() - time >= 1000) {
					System.out.println("fps:" + frames);
					fpsTracker.add(frames);
					time += 1000;
					frames = 0;
				}
			}
		}
	}

	// Updates contents
	public void update() {

		int dx = 0;
		int dy = 0;
		
		boolean topCollided = false;
		boolean botCollided = false;
		boolean rightCollided = false;
		boolean leftCollided = false;

//		for (Wall w : wallsList) {
//			if(w.isVisible()) {
//				// Check for collision
//				int collisionSide = collision(w);
//				if (collisionSide != 0) {
//					System.out.print("Collision detected from the ");
//					// Handle collision
//	                switch (collisionSide) {
//	                	//Depending on what side is colliding, this changes dx or dy by one to make the player 'bounce off' the wall.
//	                    case 1: // Collided from the left
//	                    	System.out.println("left side");
//	                    	if(!leftCollided) {
//	                    		dx -= 1;
//	                        }
//	                        	
//	                        leftCollided = true;
//	                        break;
//	                    case 2: // Collided from the right
//	                    	System.out.println("right side");
//	                    	if(!rightCollided) {
//	                        	dx += 1;
//	                        }
//	                        	
//	                        rightCollided = true;
//	                        break;
//	                    case 3: // Collided from the top
//	                    	System.out.println("top side");
//	                        if(!topCollided) {
//	                        	dy -= 1;
//	                        }
//	                        	
//	                        topCollided = true;
//	                        break;
//	                    case 4: // Collided from the bottom
//	                    	System.out.println("bottom side");
//	                    	if(!botCollided) {
//	                        	dy += 1;
//	                        }
//	                    	botCollided = true;
//	                        break;
//	                }
//	                //break;
//				}
//			}
//		}
		
		// Uses key presses to determine where to move walls
		if (keyH.upPressed && !topCollided) {
			dy += 6;
		}
		if (keyH.downPressed && !botCollided) {
			dy -= 6;
		}
		if (keyH.rightPressed && !rightCollided) {
			dx -= 6;
		}
		if (keyH.leftPressed && !leftCollided) {
			dx += 6;
		}
		
		cmanager.updateCoords(dx, dy);	
	}

	// Checks for collision between player and wall
	public int  collision(Wall w) {
		// Convert 2D array of coords into arrays of x coords and y coords
        final int[][] otherCoords = w.getHitboxCoords();

        final int[] playerXCoords = new int[]{playerX,playerX+PLAYER_WIDTH,playerX+PLAYER_WIDTH,playerX};
        final int[] playerYCoords = new int[]{playerY,playerY,playerY+PLAYER_HEIGHT,playerY+PLAYER_HEIGHT};
        
        final int[] otherXCoords = otherCoords[0];
        final int[] otherYCoords = otherCoords[1];

        // if player top left x < wall bottom right x
        if (playerXCoords[0] <= otherXCoords[2]
                // if player bottom right x > wall top left x
                && playerXCoords[2] >= otherXCoords[0]
                // if player top left y < wall bottom right y
                && playerYCoords[0] <= otherYCoords[2]
                // if player bottom right y > wall top left y
                && playerYCoords[2] >= otherYCoords[0]) {

            // find which side of the player is touching a wall
        	int overlapLeft = otherXCoords[1] - playerXCoords[0];
			int overlapRight = playerXCoords[1] - otherXCoords[0];
			int overlapTop = otherYCoords[2] - playerYCoords[0];
			int overlapBottom = playerYCoords[2] - otherYCoords[1];

			// Find the smallest overlap
			int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
			
			// Return the side of the collision
			if (minOverlap == overlapLeft) {
				return 1; // Collided from the left
			} else if (minOverlap == overlapRight) {
				return 2; // Collided from the right
			} else if (minOverlap == overlapTop) {
				return 3; // Collided from the top
			} else {
				return 4; // Collided from the bottom
			}
        } else {
        	//no collision
            return 0;
        }

	}

	// Paints contents on screen
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		final Graphics2D g2 = (Graphics2D) g;

		// Draw map
		cmanager.draw(g2);
		
		// Draw 'player' sqaure
		g2.setColor(Color.red);
		g2.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

		// Saves some memory
		g2.dispose();
	}

	public double getFPS() {
		int sum = 0;
		for (Integer n : fpsTracker) {
			sum += n;
		}

		return sum / (double) fpsTracker.size();
	}

}