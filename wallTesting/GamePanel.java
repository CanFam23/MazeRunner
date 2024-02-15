package se;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    private static final long serialVersionUID = 123455L;

    // Screen Settings

    private final int screenWidth = 1000;
    private final int screenHeight = 700;

    private KeyHandler keyH = new KeyHandler(); // Uses to detect key presses/releases
    private Thread gameThread; // Thread is a thread of execution in a program
    
    private boolean isRunning = true;
 
    //Array used to store walls
    private Wall[] walls = new Wall[40];
    
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        
        Color tempColor = Color.blue;
        int x = -screenWidth/2;
        int y = screenHeight/2+100;
        
        //This makes the horizontal walls
        for(int i = 0; i < walls.length/2; i++) {
        	if(i % 2 == 0) {
        		tempColor = Color.blue;
        	}else {
        		tempColor = Color.yellow;
        	}
        	
			walls[i] = new Wall(x + 200*i, y,200,25, tempColor);
        }
        
        x = 200;
        y = 0;
        int count = 0;
        
        //this makes the vertical squares
        for(int i = walls.length/2; i < walls.length; i++) {
        	if(i % 2 == 0) {
        		tempColor = Color.blue;
        	}else {
        		tempColor = Color.yellow;
        	}
        	
			walls[i] = new Wall(x, y + 200*count, 25,200,tempColor);
        	count++;
        }
        

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
		double AmountOfTicks = 60;
		double ns = 1000000000 / AmountOfTicks;
		double delta = 0;
		int frames = 0;
		double time = System.currentTimeMillis();
		
		while(isRunning == true) {
			long now = System.nanoTime();
			delta += (now - lastime) / ns;
			lastime = now;
			
			if(delta >= 1) {
				
				
				update();
				repaint();
				
				
				frames++;
				delta--;
				if(System.currentTimeMillis() - time >= 1000) {
					System.out.println("fps:" + frames);
					time += 1000;
					frames = 0;
				}
			}
		}
    }

    // Updates contents
    public void update() {
    	
    	int x = 0;
    	int y = 0;
    	
    	// Uses key presses to determine where to move walls
        if (keyH.upPressed) {
            y += 5;
        }
        if (keyH.downPressed) {
            y -= 5;
        }
        if (keyH.rightPressed) {
            x -= 5;
        }
        if (keyH.leftPressed) {
            x += 5;
        }
        
        //Update coords of each wall
        for(Wall w: walls) {
        	w.updateCoords(x, y);
        }


    }

    // Paints contents on screen
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        //Draw 'player' sqaure
        g2.setColor(Color.red);
        g2.fillRect(screenWidth/2, screenHeight/2, 50, 50);
        
        //Paint all walls
        for(Wall w: walls) {
        	if(w.isVisible()) {
        		w.draw(g2);
        	}
        }

        // Saves some memory
        g2.dispose();
    }


}
