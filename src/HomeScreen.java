package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
public class HomeScreen extends JFrame implements GameVariables {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Background image to use.
	 */
	private BufferedImage backgroundImage;
	
    /**
     * The Panel used for testing.
     */
    private static JPanel testPanel;
    
	/**
	 * Start button
	 */
	private JButton startButton;

	/**
	 * Constructs new HomeScreen
	 */
	public HomeScreen() {
		initialize();
		loadImage();
		createComponents();
	}

	/**
	 * Sets some default settings
	 */
	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
	}

	/**
	 * Loads background image.
	 */
	private void loadImage() {
		try {
			backgroundImage = ImageIO.read(new File("images/HomeScreen4.png"));
		} catch (IOException e) {
			System.err.println("Failed to load home screen background image!");

		}
	}

	/**
	 * Creates components and adds them to screen.
	 */
	private void createComponents() {
		// Create a transparent JPanel to overlay on top of the background image
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 3736745325593940536L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};
		panel.setLayout(new BorderLayout());

		// Create a start button
		startButton = new JButton("Start Game");
		startButton.setText("CLICK HERE TO START");
		startButton.setFont(new Font("Arail", Font.PLAIN, 24));
		startButton.setForeground(Color.BLACK);
		startButton.setBackground(Color.BLACK);

		startButton.setMargin(new Insets(10, 20, 10, 20)); // top, left, bottom, right
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Add your code to start the game here
				JOptionPane.showMessageDialog(HomeScreen.this,
						"Get ready for this race against time.\nReach the end of the maze before time runs up to go to the next level!\nClick 'OK' when your ready");
				HomeScreen.this.dispose();
			}
		});

		startButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				startButton.setForeground(Color.RED);
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				startButton.setBackground(Color.WHITE);
				startButton.setForeground(Color.BLACK);
			}
		});

		// Add components to the panel
		panel.add(startButton, BorderLayout.SOUTH);

		// Add the panel to the JFrame
		add(panel);
		testPanel = panel;
	}

	/**
	 * Main method
	 * 
	 * @param args arguements passed
	 */
	public static void main(String[] args) {
        boolean allCasesPassed = true;

        // Set up the test panel
        JPanel testPanel = new JPanel();
        
        // Call the method under test
        HomeScreen homeScreen = new HomeScreen();
        homeScreen.setVisible(true);

        // Check if the panel has any child components (i.e., if it's displaying the home screen)
        Container contentPane = homeScreen.getContentPane();
        if (contentPane.getComponentCount() <= 0) {
            allCasesPassed = false;
            System.err.println("Home screen not displayed!");
        }

        // Check if the home screen is visible
        if (!homeScreen.isVisible()) {
            allCasesPassed = false;
            System.err.println("Home screen is not visible!");
        }

        if (allCasesPassed) {
            System.out.println("All cases passed!");
        } else {
            System.err.println("At least one case failed!");
        }
    }
}