package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import main.Main;


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
    private JPanel mainPanel;
    private JPanel instructionsPanel;
    private JPanel scoreboardPanel;
    private JButton startButton;
    private JButton instructionButton;
    private JButton scoreButton;
    private JButton backButton;
    
    private JPanel currentPanel;
    
    JTextField nameField;


    public HomeScreen() {
        try {
            backgroundImage = ImageIO.read(new File("images/HomeScreen.png"));
        } catch (IOException e) {
            System.err.println("Failed to load home screen background image!");
        }

        setLayout(new BorderLayout());

        mainPanel = createMainPanel();
        instructionsPanel = createInstructionsPanel();
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
        
        JPanel buttonPanel = new JPanel(new BorderLayout()); // Use BorderLayout for buttonPanel
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // top, left, bottom, right

        // Create a panel for the nameLabel and nameField
        JPanel nameInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        nameInputPanel.setBackground(Color.BLACK); // Adjust as needed
        JLabel nameLabel = new JLabel("Enter Your Name: ");
		nameLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
        nameLabel.setForeground(Color.WHITE); // Set label text color
        nameField = new JTextField(10);; // 10 columns for the text field
        nameInputPanel.add(nameLabel);
        nameInputPanel.add(nameField);

        // Add nameInputPanel to the CENTER of buttonPanel
        buttonPanel.add(nameInputPanel, BorderLayout.CENTER);

        // Create a panel for the buttons with FlowLayout (horizontal)
        JPanel buttonsFlowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
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
	
	private JPanel createInstructionsPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.BLACK);

		// Create panel for the instructions box
		JPanel instructionsBoxPanel = new JPanel(new BorderLayout());
		instructionsBoxPanel.setBackground(Color.BLACK);
		instructionsBoxPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 50, 50)); // Add padding

		// Create label for instructions text
		JLabel instructionsLabel = new JLabel("<html>Welcome to MazeRunner!<br> <br> In this game, your objective is to navigate through three challenging levels of mazes, find the exit, and escape before the time runs out. Here are the rules and tips to help you succeed:<br>"
				+ "<br>- Use the arrow keys keys to move your player through the maze and the space bar to attack enemies."
		        + " <br><br>- You have a total of two minutes to complete each level. A timer will be displayed on the top of the screen to keep track of the remaining time. If the time runs out before you find the exit, you lose the game.<br><br>"
		        + "- Along the way, you may encounter enemies lurking in the maze. Your player has a health bar, which decreases if you collide with enemies. If your player's health reaches zero, you'll respawn at the beginning of the maze. However, fighting enemies also grants you an extra 15 seconds of time if you hit them 5 times. Use your health wisely to balance speed and safety."
		        + "<br><br>- Your score is determined by the time taken to complete each level. The faster you complete the three mazes, the higher ranking you will have. Aim for the best time and challenge yourself to improve with each playthrough."
		        + "<br><br>Are you ready to embark on this thrilling maze adventure? Good luck, and may the odds be in your favor!</html>");
		instructionsLabel.setForeground(Color.WHITE);
		instructionsLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
		instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		instructionsLabel.setVerticalAlignment(SwingConstants.TOP); // Align text to the top
		instructionsLabel.setPreferredSize(new Dimension(600, 0)); // Set preferred width, 0 for unlimited height (auto-wrap)

		// Add instructions label to the center of the instructions box panel
		instructionsBoxPanel.add(instructionsLabel, BorderLayout.CENTER);
		
	    // Create exit button
	    backButton = createButton("BACK");


	    backButton.addActionListener(e -> showMainPanel());

	    // Create a panel to hold the back button and center it
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
	    buttonPanel.setBackground(Color.BLACK);
	    buttonPanel.add(backButton);

	    // Add button panel to the bottom of the instructions box panel
	    instructionsBoxPanel.add(buttonPanel, BorderLayout.SOUTH);
	    // Add instructions box panel to the center of the main panel
	    panel.add(instructionsBoxPanel, BorderLayout.CENTER);

	    return panel;
    }
	
	private JPanel createScoreboardPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.BLACK);

		// Create panel for the instructions box
		JPanel scoreboardBoxPanel = new JPanel(new BorderLayout());
		scoreboardBoxPanel.setBackground(Color.BLACK);
		scoreboardBoxPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 50, 50)); // Add padding

		String levelName = Main.leaderboard.getleaderboardName();
		String[] entries = Main.leaderboard.getleaderboard();
		
		String scoreboard = "<html>";
		scoreboard += "<br>" + levelName + "<br>";
		
		for(int i = 0; i < entries.length; i++) {
			scoreboard += "<br>" + (i+1) + ". " + entries[i] + "<br>";
		}
		scoreboard += "<html>";
		
		// Create label for scoreboard text
		JLabel scoreboardLabel = new JLabel(scoreboard);
		scoreboardLabel.setForeground(Color.WHITE);
		scoreboardLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
		scoreboardLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreboardLabel.setVerticalAlignment(SwingConstants.TOP); // Align text to the top

		JLabel scoreboardTxtLabel = new JLabel("<html><br>-The leaderboard keeps track of the fastest time (In seconds) it takes"
				+ " to complete all 3 levels.<br>" +"<br>-Want to be among the greats? Go back to the home page and press start!<br><html>");
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
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
	    buttonPanel.setBackground(Color.BLACK);
	    buttonPanel.add(backButton);

	    // Add button panel to the bottom of the instructions box panel
	    scoreboardBoxPanel.add(buttonPanel, BorderLayout.SOUTH);
	    // Add instructions box panel to the center of the main panel
	    panel.add(scoreboardBoxPanel, BorderLayout.CENTER);

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
        Color lightYellow = new Color(255, 255, 153);
        Border border = BorderFactory.createLineBorder(lightYellow, 1);
        
        // Set the border for the button
        button.setBorder(border);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.BLUE);
                button.setBackground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
        
        if (text.equals("INSTRUCTIONS")) {
            button.addActionListener(e -> showInstructionsPanel());
        }else if(text.equals("SCOREBOARD")) {
        	 button.addActionListener(e -> showScoreboardPanel());
        }
        
        return button;
    }
    
    private void showInstructionsPanel() {
        remove(mainPanel);
        add(instructionsPanel, BorderLayout.CENTER);
        currentPanel = instructionsPanel;
        revalidate();
        repaint();
        instructionButton.setForeground(Color.WHITE);
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
    

	// Method to get the startButton instance
	public JButton getStartButton() {
		return startButton;
	}
	
	public String getName() {
		return nameField.getText();
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
		homePanel.setPreferredSize(new Dimension(1000, 800));

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