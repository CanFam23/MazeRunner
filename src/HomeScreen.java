/*
 * HomeScreen.java
 * 
 * Description: This class represents the home screen of the maze runner game.
 * It displays a background image with a start button that allows
 * the player to begin the game.
 * 
 * Date: 3.21.24
 * 
 * Author: Molly O'Connor, Nick Clouse, Andrew Denegar
 */

package src;

import javax.swing.*;

//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.fail;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HomeScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	private BufferedImage backgroundImage;
	private JButton startButton;

	// Constructor to initialize the home screen
	public HomeScreen() {
		initialize(); // Set up frame properties
		loadImage(); // Load background image
		createComponents(); // Create UI components
	}

	// Method to initialize frame properties
	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 800);
		setLocationRelativeTo(null); // Center the frame on screen
		setLayout(new BorderLayout()); // Set BorderLayout for the frame
	}

	// Method to load the background image
	private void loadImage() {
		try {
			// change image if needed
			backgroundImage = ImageIO.read(new File("images/HomeScreen4.png"));
		} catch (IOException e) {
			System.err.println("Failed to load home screen background image!");
		}
	}

	// Method to create UI components
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
		panel.setLayout(new BorderLayout()); // Set BorderLayout for the panel

		// Create a start button
		startButton = new JButton("Start Game");
		startButton.setText("CLICK HERE TO START");
		startButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Set font for button text
		startButton.setForeground(Color.BLACK); // Set text color
		startButton.setBackground(Color.BLACK); // Set background color

		startButton.setMargin(new Insets(10, 20, 10, 20)); // Set margin for button
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Add your code to start the game here
				JOptionPane.showMessageDialog(HomeScreen.this,
						"Get ready for this race against time.\nReach the end of the maze before time runs up to go to the next level!\nClick 'OK' when you're ready");
				HomeScreen.this.dispose(); // Close the home screen after starting the game
			}
		});

		// Add mouse listeners to change button color on hover
		startButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				startButton.setForeground(Color.RED);
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				startButton.setBackground(Color.WHITE);
				startButton.setForeground(Color.BLACK);
			}
		});

		// Add the start button to the panel
		panel.add(startButton, BorderLayout.SOUTH);

		// Add the panel to the JFrame
		add(panel);
	}
}

	// Main method to launch the home screen
//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				HomeScreen homeScreen = new HomeScreen();
//				homeScreen.setVisible(true);
//			}
//		});
//		boolean testPassed = false;
//		// Test whether the background image is loaded successfully
//		BufferedImage backgroundImage = null;
//		try {
//			backgroundImage = ImageIO.read(new File("images/GameOver2.png"));
//			assertNotNull("Background image should not be null", backgroundImage);
//			testPassed = true;
//		} catch (IOException e) {
//			fail("Failed to load game over screen background image!");
//			testPassed = false;
//		}
//
//		if (!testPassed) {
//			System.out.println("The class has issues.");
//		} else {
//			System.out.println("Test cases passed!");
//		}
//	}
//}