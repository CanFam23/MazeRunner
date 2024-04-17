package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import main.Main;

/**
 * GameOverLOSE creates the game over screen when the user fails to find the end
 * of the maze.
 *
 * This class provides a static method to display the game over screen with
 * options to exit the game or play again.
 *
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 *
 * @since February 28, 2024
 */
public class GameOverLOSE extends JPanel {
	/**
	 * Image to use for background.
	 */
	private BufferedImage backgroundImage;

	private boolean isRunning = true;

	private JPanel mainPanel;
	private JPanel instructionsPanel;
	private JButton againButton;
	private JButton exitButton;
	private JButton scoreButton;
	private JButton backButton;

	/**
	 * Creates JFrame and adds components. Displays the game over screen with
	 * options to exit the game or play again.
	 */
	public GameOverLOSE() {
		// Load the image
		try {
			backgroundImage = ImageIO.read(new File("images/GameOver2.png"));
		} catch (IOException e) {
			System.err.println("Failed to load game over screen background image!");
		}

		setLayout(new BorderLayout());
		mainPanel = createMainPanel();
		add(mainPanel, BorderLayout.CENTER);

//		scoreBoardPanel = createScoreBoardPanel();
	}

	private JPanel createMainPanel() {
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // top, left, bottom, right

		exitButton = createButton("EXIT");
		againButton = createButton("PLAY AGAIN");
		scoreButton = createButton("SCOREBOARD");

		buttonPanel.add(exitButton);
		buttonPanel.add(againButton);
		buttonPanel.add(scoreButton);

		// Add buttons with horizontal gap of 20 pixels
		buttonPanel.add(exitButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(30, 0))); // Add space between buttons
		buttonPanel.add(againButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(30, 0))); // Add space between buttons
		buttonPanel.add(scoreButton);

		panel.setLayout(new BorderLayout());
		panel.add(buttonPanel, BorderLayout.SOUTH);

		return panel;

	}

	private JButton createButton(String text) {
		Dimension buttonSize = new Dimension(225, 50);
		JButton button = new JButton(text);
		button.setPreferredSize(buttonSize);
		button.setFont(new Font("Monospaced", Font.PLAIN, 24));
		button.setForeground(Color.WHITE);
		button.setBackground(Color.BLACK);
		button.setFocusable(false);

		// Set the content area background color
		button.setContentAreaFilled(false);
		button.setOpaque(false);

		// Create a line border with white color and 2 pixels thickness
		Color brighterPurple = new Color(120, 0, 200); // Adjusted RGB values for brighter purple
		Border border = BorderFactory.createLineBorder(brighterPurple, 1);

		// Set the border for the button
		button.setBorder(border);

		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setForeground(Color.BLUE);
				button.setBackground(Color.WHITE);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setForeground(Color.WHITE);
			}
		});

		if (text.equals("SCOREBOARD")) {
//            button.addActionListener(e -> showInstructionsPanel());
		}

		if (text.equals("PLAY AGAIN")) {
			button.addActionListener((e -> Main.restartGame()));
		}

		if (text.equals("EXIT")) {
			button.addActionListener(e -> System.exit(0));
		}
		return button;
	}

	/**
	 * Returns true if the game over panel is running, false otherwise.
	 */
	public boolean isGameOverRunning() {
		return isRunning;
	}

	/**
	 * Main method
	 *
	 * @param args arguements passed
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Game Over Window Test");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		GameOverLOSE gameOver = new GameOverLOSE();
		gameOver.setPreferredSize(new Dimension(1000, 800));

		frame.add(gameOver);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}