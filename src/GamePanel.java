/*
 * GamePanel.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: February 24, 2024
 * 
 * Desc:
 * 'TBD'
 */
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

	private final List<Integer> fpsTracker = new ArrayList<Integer>();

	private final int playerX = screenWidth / 2;
	private final int playerY = screenHeight / 2;
	private final int PLAYER_WIDTH = 50;
	private final int PLAYER_HEIGHT = 50;
	
	private final int displacement = 1;
	private final int speed = 6;


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
		
		cmanager.checkCollision();

		int dx = 0;
		int dy = 0;
		
		boolean topCollided = false;
		boolean botCollided = false;
		boolean rightCollided = false;
		boolean leftCollided = false;
		
		
		List<Integer> collisions = cmanager.checkCollision();

		
		if(collisions.size() > 0) {
			for(Integer collisionNum: collisions) {
				// Handle collision
                switch (collisionNum) {
                	//Depending on what side is colliding, this changes dx or dy by one to make the player 'bounce off' the wall.
                    case 1: // Collided from the left
                    	//System.out.println("left side");
                    	if(!leftCollided) {
                    		dx -= displacement;
                        }
                        	
                        leftCollided = true;
                        break;
                    case 2: // Collided from the right
                    	//System.out.println("right side");
                    	if(!rightCollided) {
                        	dx += displacement;
                        }
                        	
                        rightCollided = true;
                        break;
                    case 3: // Collided from the top
                    	//System.out.println("top side");
                        if(!topCollided) {
                        	dy -= displacement;
                        }
                        	
                        topCollided = true;
                        break;
                    case 4: // Collided from the bottom
                    	//System.out.println("bottom side");
                    	if(!botCollided) {
                        	dy += displacement;
                        }
                    	botCollided = true;
                        break;
                    case 5: //Collided from right and bottom
                    	//System.out.println("right and bottom");
                    	if(!botCollided && !rightCollided) {
                    		dy += displacement*2;
                    		dx += displacement;
                    	}
                    	botCollided = true;
                    	rightCollided = true;
                    	break;
                    case 6: //Collided from left and bottom
                    	//System.out.println("left and bottom");
                    	if(!botCollided && !leftCollided) {
                    		dy += displacement*2;
                    		dx -= displacement;
                    	}
                    	botCollided = true;
                    	leftCollided = true;
                    	break;
                    case 7: //Collided from right and top
                    	//System.out.println("right and top");
                    	if(!topCollided && !rightCollided) {
                    		dy -= displacement*2;
                    		dx += displacement;
                    	}
                    	rightCollided = true;
                    	topCollided = true;
                    	break;
                    case 8: //Collided from left and top
                    	//System.out.println("left and top");
                    	if(!topCollided && !leftCollided) {
                    		dy -= displacement*2;
                    		dx -= displacement;
                    	}
                    	leftCollided = true;
                    	topCollided = true;
                    	break;
                }
			}

		}
		
		// Uses key presses to determine where to move walls
		if (keyH.upPressed && !topCollided) {
			dy += speed;
		}
		if (keyH.downPressed && !botCollided) {
			dy -= speed;
		}
		if (keyH.rightPressed && !rightCollided) {
			dx -= speed;
		}
		if (keyH.leftPressed && !leftCollided) {
			dx += speed;
		}
		cmanager.updateCoords(dx, dy);
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