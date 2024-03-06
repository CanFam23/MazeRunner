/*
 * PositionBlock.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: March 2, 2024
 * 
 * Desc:
 * 'TBD'
 */
package src;

import java.awt.Color;
import java.awt.Graphics2D;

public class PositionBlock implements GameVariables {
	protected int x;
	protected int y;

	protected int width;
	protected int height;

	private Color c;

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
		if (this instanceof EmptyBlock)
			return "empt";
		else if (this instanceof Wall)
			return "wall";
		else if (this instanceof StartingBlock)
			return "strt";
		else if (this instanceof EndBlock)
			return "EndB";
		else
			return "????";
	}

	public void draw(Graphics2D g, int chunkXPosition, int chunkYPosition) {
		Color temp = g.getColor();

		g.setColor(c);
		g.fillRect(x + chunkXPosition, y + chunkYPosition, width, height);

		g.setColor(temp);
	}

}
