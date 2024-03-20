/*
 * PositionBlock.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: March 2, 2024
 * 
 * Desc:
 * 'TBD'
 */
package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PositionBlock implements GameVariables {
	private static BufferedImage backgroundImage;

	protected int x;
	protected int y;

	protected int width;
	protected int height;

	private Color c;
    private static boolean gameIsOver = false;


	public PositionBlock(int x, int y, int width, int height, Color c) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.width = width;
		this.height = height;
	}

	public void updateCoords(int newX, int newY) {
		this.x += newX;
		this.y += newY;
	}

	public int[] getCoords() {
		return new int[] { x, y };
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String toString() {
        if (gameIsOver) {
            return "";
        } else if (this instanceof EmptyBlock)
            return "empt";
        else if (this instanceof Wall)
            return "wall";
        else if (this instanceof StartingBlock)
            return "strt";
        else if (this instanceof EndBlock) {
            // Set the flag to true and call gameOver only once
        	gameIsOver = true;
            GameOverWIN.GameOverWIN();
        	Main.closeMainWindow();
            return "EndB";
        } else
            return "????";
    }

	public void draw(Graphics2D g, int chunkXPosition, int chunkYPosition) {
		Color temp = g.getColor();

		g.setColor(c);
		g.fillRect(x + chunkXPosition, y + chunkYPosition, width, height);

		g.setColor(temp);
	}
}
