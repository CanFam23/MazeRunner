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
public class HomeScreen extends JPanel {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Background image to use.
	 */
	private BufferedImage backgroundImage;
    private boolean isRunning = true;
    private JButton startButton;

	
	public HomeScreen() {
		try {
			backgroundImage = ImageIO.read(new File("images/HomeScreen4.png"));
		} catch (IOException e) {
			System.err.println("Failed to load home screen background image!");

		}
		
        setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };


		// Create a start button
		startButton = new JButton("START GAME");
		startButton.setText("CLICK HERE TO START");
		startButton.setFont(new Font("Arail", Font.PLAIN, 24));
		startButton.setForeground(Color.BLACK);
		startButton.setBackground(Color.BLACK);
		startButton.addActionListener (e -> {
			isRunning = false;
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
		add(startButton, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        
	}
	
    /**
     * Returns true if the game over panel is running, false otherwise.
     */
    public boolean isGameOverRunning() {
        return isRunning;
    }
    
    // Method to get the startButton instance
    public JButton getStartButton() {
        return startButton;
    }

	/**
	 * Main method
	 * 
	 * @param args arguements passed
	 */
	public static void main(String[] args) {
	    JFrame frame = new JFrame("HomeScreen Window Test");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	    HomeScreen homePanel = new HomeScreen();
	    homePanel.setPreferredSize(new Dimension(800, 600));
	
	    frame.add(homePanel);
	
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
}	
		
//        boolean allCasesPassed = true;

//        // Set up the test panel
//        JPanel testPanel = new JPanel();
//        
//        // Call the method under test
//        HomeScreen homeScreen = new HomeScreen();
//        homeScreen.setVisible(true);
//
//        // Check if the panel has any child components (i.e., if it's displaying the home screen)
//        Container contentPane = homeScreen.getContentPane();
//        if (contentPane.getComponentCount() <= 0) {
//            allCasesPassed = false;
//            System.err.println("Home screen not displayed!");
//        }
//
//        // Check if the home screen is visible
//        if (!homeScreen.isVisible()) {
//            allCasesPassed = false;
//            System.err.println("Home screen is not visible!");
//        }
//
//        if (allCasesPassed) {
//            System.out.println("All cases passed!");
//        } else {
//            System.err.println("At least one case failed!");
//        }
//    }
//}