package src;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GameOverLOSE {
	private static BufferedImage backgroundImage;

	public static void GameOver() {
		// Load the image
		try {
			backgroundImage = ImageIO.read(new File("images/GameOver2.png"));
		} catch (IOException e) {
			System.err.println("Failed to load game over screen background image!");
		}

		JFrame frame = new JFrame("Game Over");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = -2844420929659285615L;

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

		JButton playAgain = new JButton("PLAY AGAIN");
		playAgain.setFont(new Font("Arail", Font.PLAIN, 24));
		playAgain.addActionListener(e -> {
			Main.runMainCode();
			frame.dispose();

		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(playAgain);
		buttonPanel.add(endGame);

		// Set up the frame
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(panel);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}