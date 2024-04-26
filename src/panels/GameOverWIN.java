package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * <p>
 * GameOverWIN creates the panel shown when user finds the end of the maze.
 * </p>
 *
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 *
 * @since February 28, 2024
 */
public class GameOverWIN extends Screen {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = -275253873056634627L;

	/**
	 * Main method, used for testing.
	 *
	 * @param args arguements passed.
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame("Next Level Test");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final GameOverWIN gameOverPanel = new GameOverWIN();
		gameOverPanel.setPreferredSize(new Dimension(1000, 800));

		frame.add(gameOverPanel);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Constructs new GameOverWIN panel
	 */
	public GameOverWIN() {
		// Load the image
		try {
			backgroundImage = ImageIO.read(new File("images/YouWin.png"));
		} catch (final IOException e) {
			System.err.println("Failed to load win screen background image!");
		}

		setLayout(new BorderLayout());

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
	@Override
	protected JPanel createMainPanel() {
		final JPanel panel = new JPanel() {
			private static final long serialVersionUID = -3229646309021769985L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};

		panel.setLayout(new BorderLayout()); // Set BorderLayout for the main panel

		final JPanel statsPanel = createStatsPanel();

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
}