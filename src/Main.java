/*
 * Main.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: Febuary 20, 2024
 * 
 * Desc:
 * 'TBD'
 */
package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Main {

	public static void main(String[] args) {
		runMainCode();
	}

	public static void runMainCode() {

		// Creates window
		JFrame window = new JFrame();

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// window.setResizable(false);
		window.setTitle("Project 1");

		// Creates new gamepanel object
		GamePanel gamePanel = new GamePanel();
		window.add(gamePanel);

		// Timer to track seconds
		Timer timer = new Timer(1000, new ActionListener() {
			int seconds = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				seconds++;
				window.setTitle("Project 1 - Elapsed Time: " + seconds + " seconds");
			}
		});
		timer.start();

		// window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.pack();

		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// Add window listener to handle window closing event
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Average FPS: " + gamePanel.getFPS());
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.dispose(); // Close the window

			}
		});

		// starts game
		gamePanel.startGameThread();
	}

}
