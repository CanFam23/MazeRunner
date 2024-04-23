package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
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
	private static final long serialVersionUID = -6221325201334468759L;

	/**
	 * Image to use for background.
	 */
	private BufferedImage backgroundImage;

	private boolean isRunning = true;

	private JPanel mainPanel;
	private JPanel scoreboardPanel;
	private JPanel currentPanel;
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
		scoreboardPanel = createScoreboardPanel();

		add(mainPanel, BorderLayout.CENTER);
		currentPanel = mainPanel;

	}
	
	public void updatePanel() {
		remove(mainPanel);
		mainPanel = createMainPanel();
		add(mainPanel,BorderLayout.CENTER);
		currentPanel = mainPanel;
	}

	private JPanel createMainPanel() {
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 7206196828465176362L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};
		
		final Font f  = new Font("Monospaced", Font.PLAIN, 17);
	    panel.setLayout(new BorderLayout()); // Set BorderLayout for the main panel

	    final JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Color.BLACK);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // top, left, bottom, right
        statsPanel.setLayout(new GridBagLayout());

        final JLabel enemiesKilled = new JLabel("Enemies Killed: " + Main.enemiesKilled);
	    enemiesKilled.setForeground(Color.WHITE);
	    enemiesKilled.setFont(f);
	    enemiesKilled.setHorizontalAlignment(SwingConstants.CENTER);
	    
	    final JLabel totalEnemiesKilled = new JLabel("Total Enemies Killed: " +  Main.totalEnemiesKilled);
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

	private JButton createButton(String text) {
		final Dimension buttonSize = new Dimension(225, 50);
		final JButton button = new JButton(text);
		button.setPreferredSize(buttonSize);
		button.setFont(new Font("Monospaced", Font.PLAIN, 24));
		button.setForeground(Color.WHITE);
		button.setBackground(Color.BLACK);
		button.setFocusable(false);

		// Set the content area background color
		button.setContentAreaFilled(false);
		button.setOpaque(false);

		// Create a line border with white color and 2 pixels thickness
		final Color brighterPurple = new Color(120, 0, 200); // Adjusted RGB values for brighter purple
		final Border border = BorderFactory.createLineBorder(brighterPurple, 1);

		// Set the border for the button
		button.setBorder(border);

		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setForeground(Color.RED);
				button.setBackground(Color.WHITE);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setForeground(Color.WHITE);
			}
		});

		if (text.equals("SCOREBOARD")) {
          button.addActionListener(e -> showScoreboardPanel());
		}

		if (text.equals("PLAY AGAIN")) {
			button.addActionListener(e -> newGame());
		}

		if (text.equals("EXIT")) {
			button.addActionListener(e -> System.exit(0));
		}
		return button;
	}
	
	private JPanel createScoreboardPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.BLACK);

		// Create panel for the instructions box
		final JPanel scoreboardBoxPanel  = new JPanel(new BorderLayout());
		scoreboardBoxPanel.setBackground(Color.BLACK);
		scoreboardBoxPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 50, 50)); // Add padding

		final String levelName = Main.leaderboard.getleaderboardName();
		final String[] entries = Main.leaderboard.leaderboardToString();

		String scoreboard = "<html>";
		scoreboard += "<br>" + levelName + "<br>";

		for (int i = 0; i < entries.length; i++) {
			scoreboard += "<br>" + (i + 1) + ". " + entries[i] + "<br>";
		}
		scoreboard += "<html>";

		// Create label for scoreboard text
		final JLabel scoreboardLabel = new JLabel(scoreboard);
		scoreboardLabel.setForeground(Color.WHITE);
		scoreboardLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
		scoreboardLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreboardLabel.setVerticalAlignment(SwingConstants.TOP); // Align text to the top

		final JLabel scoreboardTxtLabel = new JLabel(
				"<html><br>-The leaderboard keeps track of the fastest time (In seconds) it takes"
						+ " to complete all 3 levels.<br>"
						+ "<br>-Want to be among the greats? Go back to the home page and try again!<br><html>");
		scoreboardTxtLabel.setForeground(Color.WHITE);
		scoreboardTxtLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
		scoreboardTxtLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreboardTxtLabel.setVerticalAlignment(SwingConstants.CENTER); // Align text to the top

		// Add instructions label to the center of the instructions box panel
		scoreboardBoxPanel.add(scoreboardLabel, BorderLayout.NORTH);
		scoreboardBoxPanel.add(scoreboardTxtLabel, BorderLayout.CENTER);

		// Create exit button
		backButton = createButton("BACK");

		backButton.addActionListener(e -> showMainPanel());

		// Create a panel to hold the back button and center it
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.add(backButton);

		// Add button panel to the bottom of the instructions box panel
		scoreboardBoxPanel.add(buttonPanel, BorderLayout.SOUTH);
		// Add instructions box panel to the center of the main panel
		panel.add(scoreboardBoxPanel, BorderLayout.CENTER);

		return panel;
	}

	private static void newGame() {
		GamePanel.resetLevel();
		Main.resetTime();
		Main.disablePanels();
		Main.runMainCode();

	}
	
	private void showScoreboardPanel() {
		remove(mainPanel);
		add(scoreboardPanel, BorderLayout.CENTER);
		currentPanel = scoreboardPanel;
		revalidate();
		repaint();
		scoreButton.setForeground(Color.WHITE);
	}

	private void showMainPanel() {
		backButton.setForeground(Color.WHITE);
		remove(currentPanel);
		currentPanel = mainPanel;
		add(mainPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
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