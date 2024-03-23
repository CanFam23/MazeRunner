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

public class GameOverWIN extends JFrame{
	private static final long serialVersionUID = 8986657739517647875L;
	private BufferedImage backgroundImage;
	
	private boolean nextLevel = true; 

	public GameOverWIN() {
		// Load the image
		try {
			backgroundImage = ImageIO.read(new File("images/YouWin.png"));
		} catch (IOException e) {
			System.err.println("Failed to load win screen background image!");
		}

		setTitle("YOU WON!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 800);

		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 2200499920275111737L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};

		JButton endGame = new JButton("EXIT");
		endGame.setFont(new Font("Arail", Font.PLAIN, 24));
		endGame.addActionListener(e -> {
			System.exit(0);
		});

		JButton playAgain = new JButton("NEXT LEVEL");
		playAgain.setFont(new Font("Arail", Font.PLAIN, 24));
		playAgain.addActionListener(e -> {
			nextLevel = true;
			dispose();
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(playAgain);
		buttonPanel.add(endGame);

		// Set up the frame
		add(buttonPanel, BorderLayout.SOUTH);
		add(panel);
		setLocationRelativeTo(null);
		setVisible(true);

	}
	
	public boolean loadNextLevel() {
		return nextLevel;
	}
	
	public static void main(String[] args) {
		GameOverWIN w = new GameOverWIN();
	}
}
