/*
 * HomeScreen.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: February 28, 2024
 * 
 * Desc:
 * 'TBD'
 */
package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class HomeScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	private BufferedImage backgroundImage;
	private JButton startButton;

	public HomeScreen() {
		initialize();
		loadImage();
		createComponents();
	}

	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(632, 665);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
	}

	private void loadImage() {
		try {
			// Replace "FinalProject/background.jpg" with the correct path to your image
			// file
			URL imageUrl = getClass().getResource("../data/HomeScreen2.png");
			backgroundImage = ImageIO.read(imageUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createComponents() {
		// Create a transparent JPanel to overlay on top of the background image
		JPanel panel = new JPanel() {
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
		startButton.setForeground(Color.BLACK);
		startButton.setBackground(Color.BLACK); // Why isn't this working!!!!

		startButton.setMargin(new Insets(10, 20, 10, 20)); // top, left, bottom, right
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Add your code to start the game here
				JOptionPane.showMessageDialog(HomeScreen.this,
						"Get ready for this race against time.\nReach the end of the maze before time runs up to go to the next level!\nClick 'OK' when your ready");
				Main.runMainCode();
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
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new HomeScreen().setVisible(true);
			}
		});
	}
}
