package src;

import java.awt.Graphics2D;

public class Chunk {
	
	// This has been stored in each file which isn't great design and could be improved later.
	private final int screenWidth = 1000;
	private final int screenHeight = 800;
	private final int WALL_WIDTH = screenWidth/5;
	private final int WALL_HEIGHT = screenHeight/5;
	
	PositionBlock[][] blocks;
	int xPosition;
	int yPosition;
	
	public Chunk(int xDimension, int yDimension, int xPosition, int yPosition) {
		blocks = new PositionBlock[yDimension][xDimension];
		this.xPosition = WALL_WIDTH * xPosition;
		this.yPosition = WALL_HEIGHT * yPosition;
	}
	
	public void add(int xPosition, int yPosition, PositionBlock block) {
		blocks[yPosition][xPosition] = block;
	}
	
	public void updateCoords(int dx, int dy) {
		xPosition += dx;
		yPosition += dy;
	}
	
	public void draw(Graphics2D g) {
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j].draw(g, xPosition, yPosition);
			}
		}
	}

	public String toString() {
		String ret = "";
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[y].length; x++) {
				ret += blocks[y][x].toString();
			}
			ret += "\n";
		}
		return ret;
	}
	
}
