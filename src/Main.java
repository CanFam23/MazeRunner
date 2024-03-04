package src;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        // Creates window
        JFrame window = new JFrame();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //window.setResizable(false);
        window.setTitle("Project 1");

        // Creates new gamepanel object
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);



        //window.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
