package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;
import java.io.File;
import java.awt.Font;


public class Main {
	
	private static BufferedImage backgroundImage;
	private static Timer timer;

    public static void main(String[] args) {
    	runMainCode();
    }

    public static void runMainCode() {
    	

        // Creates window
        JFrame window = new JFrame();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Maze Runner - Use Arrows to start time");

        //window.setResizable(false);
        // Creates new game panel object
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        

        // Timer to track seconds
        timer = new Timer(1000, new ActionListener() {
            int seconds = 0;
            int seconds_left = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                seconds_left = 120-seconds;

                if (seconds_left <= 0) {
                	timer.stop();
                	gameOver();
                	window.dispose();
                }
                	
                window.setTitle("Maze Runner - Time Left: " + seconds_left + " seconds");
            
            }
        });
        
        gamePanel.addKeyListener(new KeyAdapter() {
            private boolean timerStarted = false;

            @Override
            public void keyPressed(KeyEvent e) {
                // Start the timer only if it hasn't been started yet and arrow keys are pressed
                if (!timerStarted && (e.getKeyCode() == KeyEvent.VK_UP ||
                                       e.getKeyCode() == KeyEvent.VK_DOWN ||
                                       e.getKeyCode() == KeyEvent.VK_LEFT ||
                                       e.getKeyCode() == KeyEvent.VK_RIGHT)) {
                    timerStarted = true;
                    timer.start();
                }
            }
        });

        // Make sure the panel can receive focus
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        

        //window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        // Add window listener to handle window closing event
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	System.out.println("Average FPS: " + gamePanel.getFPS());
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.dispose(); // Close the window
                
            }            
        });
        
        // starts game
        gamePanel.startGameThread();
    }

	private static void gameOver() {
		// Load the image
        try {
            backgroundImage = ImageIO.read(new File("src/src/GameOver2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }  
        
        JFrame frame = new JFrame("Game Over");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800); 
        
        
		JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        
        JButton endGame = new JButton("EXIT");
        endGame.setFont(new Font("Arail", Font.PLAIN,24));
        endGame.addActionListener ( e -> {
        	System.exit(0);
        });
        
        JButton playAgain = new JButton("PLAY AGAIN");
        playAgain.setFont(new Font("Arail", Font.PLAIN,24));
        playAgain.addActionListener(e -> {
        	Main.runMainCode();
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playAgain);
        buttonPanel.add(endGame);
           
        
        // Set up the frame
        frame.add(buttonPanel,BorderLayout.SOUTH);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);     
		
	}

}
