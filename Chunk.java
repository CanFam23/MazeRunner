/*
 * Chunk.java
 * Authors: Nick Clouse, Andrew Denegar, Molly O'Connor
 * Date: March 2, 2024
 * 
 * Desc:
 * 'TBD'
 */

package src;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Chunk implements GameVariables {
	
	private final int HITBOX_BUFFER_AMOUNT = 6;
	private static final int COLLISION_INT = 5;
	private static final int FULL_COLLISION_INT = 75;

	private PositionBlock[][] blocks;

	private int ChunkNumber;

	private int chunkWidth;
	private int chunkHeight;

	private boolean isStartChunk = false;
	private boolean isEndChunk = false;

	public int xPosition;
	public int yPosition;

	public Chunk(int xDimension, int yDimension, int xPosition, int yPosition) {
		blocks = new PositionBlock[yDimension][xDimension];
		this.xPosition = WALL_WIDTH * xPosition * xDimension;
		this.yPosition = WALL_HEIGHT * yPosition * yDimension;

		this.chunkWidth = xDimension * WALL_WIDTH;
		this.chunkHeight = yDimension * WALL_HEIGHT;
	}

	public void add(int xPosition, int yPosition, PositionBlock block) {
		blocks[yPosition][xPosition] = block;

		if (block instanceof StartingBlock) {
			isStartChunk = true;
		}
		if (block instanceof EndBlock) {
			isEndChunk = true;
		}
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

		// Draw a Circle in top left of all chunks, used for testing, can delete now or
		// later
//		Color temp = g.getColor();
//		g.setColor(Color.red);
//		g.fillOval(xPosition, yPosition, 10, 10);
//		
//		g.setColor(temp);
	}

	public int getChunkNumber() {
		return ChunkNumber;
	}

	public boolean isStartChunk() {
		return isStartChunk;
	}

	public boolean isEndChunk() {
		return isEndChunk;
	}

	// Returns a 2D array of coordinates, which represent each 'corner' of a chunk
	public int[][] getCoords() {
		return new int[][] { { xPosition, xPosition + chunkWidth, xPosition + chunkWidth, xPosition },
				{ yPosition, yPosition, yPosition + chunkHeight, yPosition + chunkHeight } };
	}

	// Returns a List of integers, which represent what side of the player is
		// colliding with a wall
		public List<Collision> checkCollision() {

			List<Collision> Collisions = new ArrayList<>();

			// Check each wall in the chunk, if a wall chunk and colliding, add it's
			// returned int value to list
			for (int r = 0; r < blocks.length; r++) {
				for (int c = 0; c < blocks[0].length; c++) {
					PositionBlock temp = blocks[r][c];
					if (temp instanceof Wall) {
						Collision collided = collision(temp);

						if (collided != Collision.NO_COLLISION) {
							Collisions.add(collided);
						}

					}
				}
			}

			return Collisions;
		}

		// Checks for collision between player and wall
		public Collision collision(PositionBlock w) {
			// Convert 2D array of coords into arrays of x coords and y coords
			final int[] otherCoords = w.getCoords();
			final int x = otherCoords[0] + xPosition;
			final int y = otherCoords[1] + yPosition;

			final int[] playerXCoords = new int[] { PLAYER_X, PLAYER_X + PLAYER_WIDTH, PLAYER_X + PLAYER_WIDTH, PLAYER_X };
			final int[] playerYCoords = new int[] { PLAYER_Y, PLAYER_Y, PLAYER_Y + PLAYER_HEIGHT,
					PLAYER_Y + PLAYER_HEIGHT };

			final int[] otherXCoords = new int[] { x - HITBOX_BUFFER_AMOUNT, x + WALL_WIDTH + HITBOX_BUFFER_AMOUNT,
					x + WALL_WIDTH + HITBOX_BUFFER_AMOUNT, x - HITBOX_BUFFER_AMOUNT };
			final int[] otherYCoords = new int[] { y - HITBOX_BUFFER_AMOUNT, y - HITBOX_BUFFER_AMOUNT,
					y + WALL_HEIGHT + HITBOX_BUFFER_AMOUNT, y + WALL_HEIGHT + HITBOX_BUFFER_AMOUNT };

			// if player top left x < wall bottom right x
			if (playerXCoords[0] <= otherXCoords[2]
					// if player bottom right x > wall top left x
					&& playerXCoords[2] >= otherXCoords[0]
					// if player top left y < wall bottom right y
					&& playerYCoords[0] <= otherYCoords[2]
					// if player bottom right y > wall top left y
					&& playerYCoords[2] >= otherYCoords[0]) {

				// find which side of the player is touching a wall
				int overlapLeft = otherXCoords[1] - playerXCoords[0];
				int overlapRight = playerXCoords[1] - otherXCoords[0];
				int overlapTop = otherYCoords[2] - playerYCoords[0];
				int overlapBottom = playerYCoords[2] - otherYCoords[1];

				// Find the smallest overlap
				int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

				
				//If the block being checked is the end block, we want to check if the user is fully on the block or not
				//this checks if the user is in the general center of the block, aka a 'full collision'
				if(w instanceof EndBlock) {
					if(overlapLeft > FULL_COLLISION_INT && overlapRight > FULL_COLLISION_INT && overlapTop > FULL_COLLISION_INT && overlapBottom > FULL_COLLISION_INT) {
						return Collision.FULL_COLLISION;
					}
				}
				
				// If two sides are colliding at the same time
				// Bottom right corner
				if (overlapRight < COLLISION_INT && overlapBottom < COLLISION_INT) {
					return Collision.BOTTOM_RIGHT_CORNER;
					// Bottom left corner
				} else if (overlapLeft < COLLISION_INT && overlapBottom < COLLISION_INT) {
					return Collision.BOTTOM_LEFT_CORNER;
					// Top Right corner
				} else if (overlapRight < COLLISION_INT && overlapTop < COLLISION_INT) {
					return Collision.TOP_RIGHT_CORNER;
					// Top left corner
				} else if (overlapLeft < COLLISION_INT && overlapTop < COLLISION_INT) {
					return Collision.TOP_LEFT_CORNER;
				}

				// Return the side of the collision
				if (minOverlap == overlapLeft) {
					return Collision.LEFT_SIDE; // Collided from the left
				} else if (minOverlap == overlapRight) {
					return Collision.RIGHT_SIDE; // Collided from the right
				} else if (minOverlap == overlapTop) {
					return Collision.TOP_SIDE; // Collided from the top
				} else {
					return Collision.BOTTOM_SIDE; // Collided from the bottom
				}
			} else {
				// no collision
				return Collision.NO_COLLISION;
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