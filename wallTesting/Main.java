package se;

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

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);


        // starts game
        gamePanel.startGameThread();


    }

}
