package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * <p>
 * HomeScreen creates the home screen.
 * </p>
 *
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 *
 * @since February 28, 2024
 */
public class HomeScreen extends Screen {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Main method
	 *
	 * @param args arguements passed
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame("HomeScreen Window Test");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final HomeScreen homePanel = new HomeScreen();
		homePanel.setPreferredSize(new Dimension(1000, 800));

		frame.add(homePanel);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Text field used to store players name.
	 */
	JTextField nameField;

	/**
	 * Creates a new home screen.
	 */
	public HomeScreen() {
		try {
			backgroundImage = ImageIO.read(new File("images/HomeScreen.png"));
		} catch (final IOException e) {
			System.err.println("Failed to load home screen background image!");
		}

		setLayout(new BorderLayout());

		mainPanel = createMainPanel();
		instructionsPanel = createInstructionsPanel();
		scoreboardPanel = createScoreboardPanel();

		add(mainPanel, BorderLayout.CENTER);
		currentPanel = mainPanel;
	}

	@Override
	public String getName() {
		return nameField.getText();
	}

	/**
	 * Get the startButton instance
	 *
	 * @return The start button
	 */
	public JButton getStartButton() {
		return startButton;
	}

	@Override
	protected JPanel createMainPanel() {
		final JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};

		final JPanel buttonPanel = new JPanel(new BorderLayout()); // Use BorderLayout for buttonPanel
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // top, left, bottom, right

		// Create a panel for the nameLabel and nameField
		final JPanel nameInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		nameInputPanel.setBackground(Color.BLACK); // Adjust as needed
		final JLabel nameLabel = new JLabel("Enter Your Name: ");
		nameLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
		nameLabel.setForeground(Color.WHITE); // Set label text color
		nameField = new JTextField(10); // 10 columns for the text field
		nameInputPanel.add(nameLabel);
		nameInputPanel.add(nameField);

		// Add nameInputPanel to the CENTER of buttonPanel
		buttonPanel.add(nameInputPanel, BorderLayout.CENTER);

		// Create a panel for the buttons with FlowLayout (horizontal)
		final JPanel buttonsFlowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		buttonsFlowPanel.setBackground(Color.BLACK); // Adjust as needed

		instructionButton = createButton("INSTRUCTIONS");
		startButton = createButton("START");
		scoreButton = createButton("SCOREBOARD");

		buttonsFlowPanel.add(instructionButton);
		buttonsFlowPanel.add(startButton);
		buttonsFlowPanel.add(scoreButton);

		// Add buttonsFlowPanel to the SOUTH of buttonPanel
		buttonPanel.add(buttonsFlowPanel, BorderLayout.SOUTH);

		// Set BorderLayout for the main panel
		panel.setLayout(new BorderLayout());

		// Add the buttonPanel to the SOUTH
		panel.add(buttonPanel, BorderLayout.SOUTH);

		return panel;

	}
}
