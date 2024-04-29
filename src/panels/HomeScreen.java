package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import sprites.Player;

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
	 * Panel to draw the player for selection on.
	 */
	private static JPanel drawingPanel;
	
	// Create the player select buttons.
	/** The default image for the left facing button */
	private final ImageIcon leftButtonDefault = new ImageIcon("images/Left-Default.png");
	/** The default image for the right facing button */
	private final ImageIcon rightButtonDefault = new ImageIcon("images/Right-Default.png");
	/** The mouse hover image for the left facing button */
	private final ImageIcon leftButtonHover = new ImageIcon("images/Left-Hover.png");
	/** The mouse hover image for the right facing button */
	private final ImageIcon rightButtonHover = new ImageIcon("images/Right-Hover.png");
	/** The clicked image for the left facing button */
	private final ImageIcon leftButtonClick = new ImageIcon("images/Left-Click.png");
	/** The clicked image for the right facing button */
	private final ImageIcon rightButtonClick = new ImageIcon("images/Right-Click.png");
	
	/** This timer will control the redrawing of the player. */
	private Timer timer;
	
	/** The player currently being displayed on the home screen. */
	private Player displayPlayer;
	
	
	/**
	 * Main method
	 *
	 * @param args arguments passed
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
		final JPanel panel = new JPanel(new BorderLayout()) {
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
		final JPanel buttonsFlowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		buttonsFlowPanel.setBackground(Color.BLACK); // Adjust as needed

		instructionButton = createButton("INSTRUCTIONS");
		startButton = createButton("START");
		scoreButton = createButton("SCOREBOARD");

		buttonsFlowPanel.add(instructionButton);
		buttonsFlowPanel.add(startButton);
		buttonsFlowPanel.add(scoreButton);

		// Add buttonsFlowPanel to the SOUTH of buttonPanel
		buttonPanel.add(buttonsFlowPanel, BorderLayout.SOUTH);

		// Create a JPanel to hold the player selection buttons and the player display
		final JPanel SelectionPanel = new JPanel(new GridLayout(5, 1));
		
		final JButton leftButton = new JButton(leftButtonDefault);
		final JButton rightButton = new JButton(rightButtonDefault);
		
		// Remove button background
		leftButton.setBorderPainted(false);
		leftButton.setContentAreaFilled(false);
		leftButton.setFocusPainted(false);
		rightButton.setBorderPainted(false);
		rightButton.setContentAreaFilled(false);
		rightButton.setFocusPainted(false);
		
		final List<Player> displayPlayerList = new LinkedList<Player>();
		final Player Civilian1 = new Player();
		Civilian1.load_display_images("Civilian1");
		displayPlayerList.add(Civilian1);
		displayPlayer = Civilian1;
		
		final Player Civilian1Black = new Player();
		Civilian1Black.load_display_images("Civilian1(black)");
		displayPlayerList.add(Civilian1Black);
		
		final Player Civilian2Black = new Player();
		Civilian2Black.load_display_images("Civilian2(black)");
		displayPlayerList.add(Civilian2Black);
		
		final Player Knight1 = new Player();
		Knight1.load_display_images("Knight1");
		displayPlayerList.add(Knight1);
		
		// Add button listeners
		leftButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                leftButton.setIcon(leftButtonHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                leftButton.setIcon(leftButtonDefault);
            }
            @Override
            public void mousePressed(MouseEvent e) {
            	leftButton.setIcon(leftButtonClick);
            	displayPlayerList.add(0, displayPlayerList.remove(displayPlayerList.size() - 1));
            	displayPlayer = displayPlayerList.get(0);
            }
        });
		
		// Add button listeners
		rightButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
            	rightButton.setIcon(rightButtonHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
            	rightButton.setIcon(rightButtonDefault);
            }
            @Override
            public void mousePressed(MouseEvent e) {
            	rightButton.setIcon(rightButtonClick);
            	displayPlayerList.add(displayPlayerList.remove(0));
            	displayPlayer = displayPlayerList.get(0);
            }
        });
		
		//Panel that holds player select buttons and text
		final JPanel playerPanel = new JPanel(new GridBagLayout());

		// Create GridBagConstraints for positioning components
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Row 0
        gbc.gridwidth = 2; // Span two columns
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment
        gbc.insets = new Insets(0, 60, 0, 0); // Padding (top, left, bottom, right)

        // Create the player label
        final JLabel playerSelectLbl = new JLabel("Select a character");
        playerSelectLbl.setFont(new Font("Monospaced", Font.BOLD, 18));
        playerSelectLbl.setForeground(Color.WHITE);

        // Add the player label to playerPanel
        playerPanel.add(playerSelectLbl, gbc);

        // Reset gridwidth for components
        gbc.gridwidth = 1;
        
        // Add button 1
        gbc.gridy = 0; // Row 1
        playerPanel.add(leftButton, gbc);

        // Add button 2
        gbc.gridx = 1; // Column 1
        playerPanel.add(rightButton, gbc);
        
		// Make the panel see through
		playerPanel.setOpaque(false);
		
		// Create Player display
 		initializeGUI();
		
		drawingPanel.setOpaque(false);

		SelectionPanel.setOpaque(false);
		
		// Add panels to the main frame
		//Panels to restrict size of playerPanel
		final JPanel sizingPanel = new JPanel();
		sizingPanel.setBounds(0, 0, 100, 400);
		sizingPanel.setOpaque(false);
		final JPanel sizingPanel2 = new JPanel();
		sizingPanel2.setBounds(0, 0, 100, 400);
		sizingPanel2.setOpaque(false);
		final JPanel sizingPanel3 = new JPanel();
		sizingPanel3.setBounds(0, 0, 100, 400);
		sizingPanel3.setOpaque(false);
		
		SelectionPanel.add(sizingPanel);
		SelectionPanel.add(sizingPanel2);
		SelectionPanel.add(sizingPanel3);
		SelectionPanel.add(drawingPanel);
		SelectionPanel.add(playerPanel);
		
		panel.add(SelectionPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.SOUTH);

		return panel;

	}
	
	/**
	 * We use this to determine which player should be loaded based on the display character selected when the game is started. 
	 * 
	 * @return our display player
	 */
	public Player get_display_player() {
		return displayPlayer;
	}
	
	/**
	 * Initialize a basic GUI for testing.
	 */
	public void initializeGUI() {
		drawingPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			/**
			 * Paint component.
			 *
			 * @param g graphics to draw on.
			 */
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// Draw the current image, if not null
				if (displayPlayer != null) {
					displayPlayer.draw_display((Graphics2D) g);
				}
			}
		};
		startImageTimer();
		}
	
		private void startImageTimer() {
	        timer = new Timer(100, new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                repaint(); // This will call paintComponent method
	            }
	            });
	        timer.start();
	    }
	}
