package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
public class finalWinScreen extends JPanel {
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
	public finalWinScreen() {
		// Load the image
		if (!Main.addedToLeaderboard) {
			try {
				backgroundImage = ImageIO.read(new File("images/winnerNoScoreboard.png"));
			} catch (IOException e) {
				System.err.println("Failed to load game over screen background image!");
			}		
		}
		else if (Main.addedToLeaderboard) {
			try {
				backgroundImage = ImageIO.read(new File("images/winnerYesScoreboard.png"));
			} catch (IOException e) {
				System.err.println("Failed to load game over screen background image!");
			}
		}

		setLayout(new BorderLayout());
		
		mainPanel = createMainPanel();
		scoreboardPanel = createScoreboardPanel();

		add(mainPanel, BorderLayout.CENTER);
		currentPanel = mainPanel;

	}

	private JPanel createMainPanel() {
		JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        };

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

        panel.setLayout(new BorderLayout());
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;

    }

    private JButton createButton(String text) {
    	Dimension buttonSize = new Dimension(225,50);
        JButton button = new JButton(text);
        button.setPreferredSize(buttonSize);
        button.setFont(new Font("Monospaced", Font.PLAIN, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);

        // Set the content area background color
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        // Create a line border with white color and 2 pixels thickness
        Color brighterPurple = new Color(120, 0, 200); // Adjusted RGB values for brighter purple
        Border border = BorderFactory.createLineBorder(brighterPurple, 1);

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
        	button.addActionListener(e -> Main.restartGame());
        }

        if (text.equals("EXIT")) {
        	button.addActionListener(e -> System.exit(0));
        }
        return button;
    }
    
    private JPanel createScoreboardPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.BLACK);

		// Create panel for the instructions box
		JPanel scoreboardBoxPanel = new JPanel(new BorderLayout());
		scoreboardBoxPanel.setBackground(Color.BLACK);
		scoreboardBoxPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 50, 50)); // Add padding

		String levelName = Main.leaderboard.getleaderboardName();
		String[] entries = Main.leaderboard.leaderboardToString();

		String scoreboard = "<html>";
		scoreboard += "<br>" + levelName + "<br>";

		for (int i = 0; i < entries.length; i++) {
			scoreboard += "<br>" + (i + 1) + ". " + entries[i] + "<br>";
		}
		scoreboard += "<html>";

		// Create label for scoreboard text
		JLabel scoreboardLabel = new JLabel(scoreboard);
		scoreboardLabel.setForeground(Color.WHITE);
		scoreboardLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
		scoreboardLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreboardLabel.setVerticalAlignment(SwingConstants.TOP); // Align text to the top

		JLabel scoreboardTxtLabel = new JLabel(
				"<html><br>-The leaderboard keeps track of the fastest time (In seconds) it takes"
						+ " to complete all 3 levels.<br>"
						+ "<br>-Want to be among the greats? Go back to the home page and press start!<br><html>");
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

		finalWinScreen finalScreen = new finalWinScreen();
		finalScreen.setPreferredSize(new Dimension(1000, 800));

		frame.add(finalScreen);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}