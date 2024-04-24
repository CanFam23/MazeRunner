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
 * Abstract class used for the screens of our game. Made so we didn't have
 * repeated code in each panel class.
 *
 * @author Nick Clouse
 *
 * @since April 23, 2024
 */
public abstract class Screen extends JPanel {

	/**
	 * Serial Version IUD.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Image to use for background.
	 */
	protected BufferedImage backgroundImage;

	/**
	 * Keeps track of if this panel is being shown.
	 */
	protected boolean isRunning = true;

	/**
	 * Main panel for this screen.
	 */
	protected JPanel mainPanel;

	/**
	 * Scoreboard panel for this screen.
	 */
	protected JPanel scoreboardPanel;

	/**
	 * Current panel being shown.
	 */
	protected JPanel currentPanel;

	/**
	 * Next level button.
	 */
	protected JButton nextLevelButton;

	/**
	 * Exit button.
	 */
	protected JButton exitButton;

	/**
	 * Score button.
	 */
	protected JButton scoreButton;

	/**
	 * Back button.
	 */
	protected JButton backButton;

	/**
	 * Play again button.
	 */
	protected JButton againButton;

	/**
	 * Instructions panel.
	 */
	protected JPanel instructionsPanel;

	/**
	 * Start button.
	 */
	protected JButton startButton;

	/**
	 * Instructions button.
	 */
	protected JButton instructionButton;

	/**
	 * Updates the main panel contents, used so the player's stats and update.
	 */
	public void updatePanel() {
		remove(mainPanel);
		mainPanel = createMainPanel();
		scoreboardPanel = createScoreboardPanel();
		add(mainPanel, BorderLayout.CENTER);
		currentPanel = mainPanel;
	}

	/**
	 * Creates the main panel.
	 * 
	 * @return The main JPanel.
	 */
	protected JPanel createMainPanel() {
		final JPanel panel = new JPanel() {
			private static final long serialVersionUID = -3229646309021769985L;

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

		final JLabel timeLevel = new JLabel("Level Completed in: " + Main.secondsLevel + " seconds");
		timeLevel.setForeground(Color.WHITE);
		timeLevel.setFont(f);
		timeLevel.setHorizontalAlignment(SwingConstants.CENTER);

		final JLabel timeTotal = new JLabel("Total Time: " + Main.totalTimePlayed + " seconds");
		timeTotal.setForeground(Color.WHITE);
		timeTotal.setFont(f);
		timeTotal.setHorizontalAlignment(SwingConstants.CENTER);

		final JLabel score = new JLabel("Current Score: " + Main.calculateScore());
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

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // top, left, bottom, right

		exitButton = createButton("EXIT");
		nextLevelButton = createButton("NEXT LEVEL");
		scoreButton = createButton("SCOREBOARD");

		// Add buttons with horizontal gap of 20 pixels
		buttonPanel.add(exitButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(30, 0))); // Add space between buttons
		buttonPanel.add(nextLevelButton);
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
	 * Creates a new JButton with given text.
	 * 
	 * @param text Text to put on JButton.
	 * @return The new JButton.
	 */
	protected JButton createButton(String text) {
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

		if (text.equals("INSTRUCTIONS")) {
			button.addActionListener(e -> showInstructionsPanel());
		}

		if (text.equals("NEXT LEVEL")) {
			button.addActionListener(e -> setRunning(false));
		}

		if (text.equals("EXIT")) {
			button.addActionListener(e -> System.exit(0));
		}
		if (text.equals("PLAY AGAIN")) {
			button.addActionListener(e -> newGame());
		}
		return button;
	}

	/**
	 * Starts a new game
	 */
	private static void newGame() {
		Main.restartGame();
	}

	/**
	 * Creates the scoreboard panel.
	 * 
	 * @return The scoreboard JPanel.
	 */
	protected JPanel createScoreboardPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.BLACK);

		// Create panel for the instructions box
		final JPanel scoreboardBoxPanel = new JPanel(new BorderLayout());
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
						+ "<br>-Want to be among the greats? Go back to the home page and play again!<br><html>");
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
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.add(backButton);

		// Add button panel to the bottom of the instructions box panel
		scoreboardBoxPanel.add(buttonPanel, BorderLayout.SOUTH);
		// Add instructions box panel to the center of the main panel
		panel.add(scoreboardBoxPanel, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Gets if the game over panel is running.
	 * 
	 * @return true if panel is running.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Sets isRunning to giveb boolean.
	 * 
	 * @param running Boolean to set isRunning to.
	 */
	public void setRunning(boolean running) {
		isRunning = running;
	}

	/**
	 * Shows the scoreboard panel.
	 */
	private void showScoreboardPanel() {
		remove(mainPanel);
		add(scoreboardPanel, BorderLayout.CENTER);
		currentPanel = scoreboardPanel;
		revalidate();
		repaint();
		scoreButton.setForeground(Color.WHITE);
	}

	/**
	 * Shows the main panel.
	 */
	private void showMainPanel() {
		backButton.setForeground(Color.WHITE);
		remove(currentPanel);
		currentPanel = mainPanel;
		add(mainPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/**
	 * Creates instruction panel.
	 * 
	 * @return The instruction panel.
	 */
	protected JPanel createInstructionsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.BLACK);

		// Create panel for the instructions box
		JPanel instructionsBoxPanel = new JPanel(new BorderLayout());
		instructionsBoxPanel.setBackground(Color.BLACK);
		instructionsBoxPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 50, 50)); // Add padding

		// Create label for instructions text
		JLabel instructionsLabel = new JLabel(
				"<html>Welcome to MazeRunner!<br> <br> In this game, your objective is to navigate through three challenging levels of mazes, find the exit, and escape before the time runs out. Here are the rules and tips to help you succeed:<br>"
						+ "<br>- Use the arrow keys to move your player through the maze and the space bar to attack enemies."
						+ " <br><br>- You have a total of two minutes to complete each level. A timer will be displayed on the top of the screen to keep track of the remaining time. If the time runs out before you find the exit, you lose the game."
						+ "<br><br>- Each level has 5 different mazes. Each time you start the game, a random maze is picked for each level.<br><br>"
						+ "- Along the way, you may encounter enemies lurking in the maze. Your player has a health bar, which decreases if you collide with enemies. If your player's health reaches zero, you'll respawn at the beginning of the maze. However, fighting enemies also grants you an extra 15 seconds of time if you hit them 3 times. Use your health wisely to balance speed and safety."
						+ "<br><br>- Your score is calculated by the time taken to complete each level and the number of enemies you kill. The faster you complete the three mazes and the more enemies you kill, the higher ranking you will have. Aim for the best time, kill as many enemies as you can, and challenge yourself to improve with each playthrough!");
		instructionsLabel.setForeground(Color.WHITE);
		instructionsLabel.setFont(new Font("Monospaced", Font.PLAIN, 17));
		instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		instructionsLabel.setVerticalAlignment(SwingConstants.TOP); // Align text to the top
		instructionsLabel.setPreferredSize(new Dimension(600, 0)); // Set preferred width, 0 for unlimited height
																	// (auto-wrap)

		// Add instructions label to the center of the instructions box panel
		instructionsBoxPanel.add(instructionsLabel, BorderLayout.CENTER);

		// Create exit button
		backButton = createButton("BACK");

		backButton.addActionListener(e -> showMainPanel());

		// Create a panel to hold the back button and center it
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.add(backButton);

		// Add button panel to the bottom of the instructions box panel
		instructionsBoxPanel.add(buttonPanel, BorderLayout.SOUTH);
		// Add instructions box panel to the center of the main panel
		panel.add(instructionsBoxPanel, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Shows intruction panel.
	 */
	private void showInstructionsPanel() {
		remove(mainPanel);
		add(instructionsPanel, BorderLayout.CENTER);
		currentPanel = instructionsPanel;
		revalidate();
		repaint();
		instructionButton.setForeground(Color.WHITE);
	}

	/**
	 * Main method, used for testing.
	 *
	 * @param args arguements passed
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Screen Test");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Screen finalScreen = new finalWinScreen();
		finalScreen.setPreferredSize(new Dimension(1000, 800));

		frame.add(finalScreen);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
