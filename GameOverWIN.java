package src;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameOverWIN {
	private static BufferedImage backgroundImage;

	public static void GameOverWIN() {
		// Load the image
        try {
            backgroundImage = ImageIO.read(new File("images/YouWin.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }  
        
        JFrame frame = new JFrame("YOU WON!");
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
        
        JButton playAgain = new JButton("NEXT LEVEL");
        playAgain.setFont(new Font("Arail", Font.PLAIN,24));
        playAgain.addActionListener(e -> {
        	Main.runMainCode();
        	frame.dispose();
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
