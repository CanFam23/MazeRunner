package se;

import java.awt.Color;
import java.awt.Graphics2D;

public class Wall {
	
	private static final int SCREEN_HEIGHT = 700;
	private static final int SCREEN_WIDTH = 1000;
	
	
	private int x;
	private int y;
	
	private int width;
	private int height;
	
	private Color c;
	
	public Wall(int x, int y,int width, int height, Color c){
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
		return new int[]{x,y};
	}
	
	public boolean isVisible() {
		return (this.x > 0-width/2 && this.x < SCREEN_WIDTH-width/2) && (this.y > 0 && this.y < SCREEN_HEIGHT+width/2);
	}
	
	public void draw(Graphics2D g) {
		Color temp = g.getColor();
		
		g.setColor(c);
		g.fillRect(x, y, width, height);
		
		g.setColor(temp);
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
