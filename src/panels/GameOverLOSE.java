package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

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
public class GameOverLOSE extends Screen {
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = -6221325201334468759L;

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
		scoreboardPanel = createScoreboardPanel();

		add(mainPanel, BorderLayout.CENTER);
		currentPanel = mainPanel;

	}

	/**
	 * Creates the main JPanel and its contents.
	 * 
	 * @return The main JPanel.
	 */
	protected JPanel createMainPanel() {
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 7206196828465176362L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};

		final Font f = new Font("Monospaced", Font.PLAIN, 17);
		panel.setLayout(new BorderLayout()); // Set BorderLayout for the main panel

		final JPanel statsPanel = new JPanel();
		statsPanel.setBackground(Color.BLACK);
		statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // top, left, bottom, right
		statsPanel.setLayout(new GridBagLayout());

		final JLabel enemiesKilled = new JLabel("Enemies Killed: " + Main.enemiesKilled);
		enemiesKilled.setForeground(Color.WHITE);
		enemiesKilled.setFont(f);
		enemiesKilled.setHorizontalAlignment(SwingConstants.CENTER);

		final JLabel totalEnemiesKilled = new JLabel("Total Enemies Killed: " + Main.totalEnemiesKilled);
		totalEnemiesKilled.setForeground(Color.WHITE);
		totalEnemiesKilled.setFont(f);
		totalEnemiesKilled.setHorizontalAlignment(SwingConstants.CENTER);

		final JLabel timeLevel = new JLabel("Level time: " + Main.secondsLevel + " seconds");
		timeLevel.setForeground(Color.WHITE);
		timeLevel.setFont(f);
		timeLevel.setHorizontalAlignment(SwingConstants.CENTER);

		final JLabel timeTotal = new JLabel("Total Time: " + Main.totalTimePlayed + " seconds");
		timeTotal.setForeground(Color.WHITE);
		timeTotal.setFont(f);
		timeTotal.setHorizontalAlignment(SwingConstants.CENTER);

		final JLabel score = new JLabel("Final Score: " + Main.calculateScore());
		score.setForeground(Color.WHITE);
		score.setFont(f);
		score.setHorizontalAlignment(SwingConstants.CENTER);

		// Create constraints
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0; // Expand horizontally
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Add labels to the panel
		statsPanel.add(enemiesKilled, gbc);
		gbc.gridx++;
		statsPanel.add(timeLevel, gbc);
		gbc.gridy++;
		gbc.gridx--;
		statsPanel.add(totalEnemiesKilled, gbc);
		gbc.gridx++;
		statsPanel.add(timeTotal, gbc);
		gbc.gridx = 0;

		// Center the score label in the column below the last one with text
		gbc.gridy += 2; // Skip one row to move below the last row with text
		gbc.gridwidth = 2; // Make the score label span 2 columns
		gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill to horizontally center the score label
		statsPanel.add(score, gbc);

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

		// Create a Box container with vertical orientation
		final Box box = Box.createVerticalBox();

		// Add statsPanel and buttonPanel to the Box container
		box.add(statsPanel);
		box.add(buttonPanel);

		// Add the Box container to the main panel in BorderLayout.SOUTH
		panel.add(box, BorderLayout.SOUTH);

		return panel;

	}

	/**
	 * Main method, used for testing
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