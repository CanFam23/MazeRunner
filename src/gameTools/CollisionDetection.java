package gameTools;

import java.util.List;

import gameTools.GameVariables.Collision;

public class CollisionDetection implements GameVariables{
	
	//TODO add testing

	/**
	 * Checks for collision between the given coordinates, which represent a square. 
	 * The coordinates are ordered top left, top right, bottom right, bottom left.
	 * Compares the first set to second set, so if it returns left side, that means 
	 * the left side of the first set is colliding with the second set in some way.
	 * 
	 * @param xCoordsOne The first set of x coordinates.
	 * @param yCoordsOne The first set of y coordinates.
	 * @param xCoordsTwo The second set of x coordinates.
	 * @param yCoordsTwo The second set of y coordinates.
	 * @return The collision enumeration which represents what side is being collided.
	 * 
	 * @see Collision
	 */
	public static Collision getCollision(int[] xCoordsOne, int[] yCoordsOne, int[] xCoordsTwo, int[] yCoordsTwo) {

		// if player top left x < wall bottom right x
		if (xCoordsOne[0] <= xCoordsTwo[2]
				// if player bottom right x > wall top left x
				&& xCoordsOne[2] >= xCoordsTwo[0]
				// if player top left y < wall bottom right y
				&& yCoordsOne[0] <= yCoordsTwo[2]
				// if player bottom right y > wall top left y
				&& yCoordsOne[2] >= yCoordsTwo[0]) {

			// find which side of the player is touching a wall
			final int overlapLeft = xCoordsOne[1] - xCoordsTwo[0];
			final int overlapRight = xCoordsTwo[1] - xCoordsOne[0];
			final int overlapTop = yCoordsOne[2] - yCoordsTwo[0];
			final int overlapBottom = yCoordsTwo[2] - yCoordsOne[1];

			// Find the smallest overlap
			final int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

			// this checks if the user is in the general center of the block, a 'full
			// collision'
			if (overlapLeft > FULL_COLLISION_INT && overlapRight > FULL_COLLISION_INT && overlapTop > FULL_COLLISION_INT
					&& overlapBottom > FULL_COLLISION_INT) {
				return Collision.FULL_COLLISION;
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
	
	/**
	 * Handles all collisions passed through in the collisions list. Based on each enumerator in the list, 
	 * it updates dx and dy accordingly with displacement, and returns a 2D array of dx and dy.
	 * 
	 * @param Collisions The collisions to handle.
	 * @param Displacement How much to change dx and dy.
	 * @return A 2D array of dx and dy, which are the final displacements.
	 */
	public static int[] handleCollision(List<Collision> collisions, int displacement) {
		boolean topCollided = false;
		boolean botCollided = false;
		boolean rightCollided = false;
		boolean leftCollided = false;
		int dx = 0;
		int dy = 0;
		
		for (Collision collisionNum : collisions) {
			// Handle collision
			switch (collisionNum) {
			// Depending on what side is colliding, this changes dx or dy by one to make the
			// player 'bounce off' the wall.
			case LEFT_SIDE: // Collided from the left
				if (!leftCollided) {
					dx -= displacement;
				}

				leftCollided = true;
				break;
			case RIGHT_SIDE: // Collided from the right
				if (!rightCollided) {
					dx += displacement;
				}

				rightCollided = true;
				break;
			case TOP_SIDE: // Collided from the top
				if (!topCollided) {
					dy -= displacement;
				}

				topCollided = true;
				break;
			case BOTTOM_SIDE: // Collided from the bottom
				if (!botCollided) {
					dy += displacement;
				}
				botCollided = true;
				break;
			case BOTTOM_RIGHT_CORNER: // Collided from right and bottom
				if (!botCollided && !rightCollided) {
					dy += displacement * 2;
					dx += displacement;
				}
				botCollided = true;
				rightCollided = true;
				break;
			case BOTTOM_LEFT_CORNER: // Collided from left and bottom
				if (!botCollided && !leftCollided) {
					dy += displacement * 2;
					dx -= displacement;
				}
				botCollided = true;
				leftCollided = true;
				break;
			case TOP_RIGHT_CORNER: // Collided from right and top
				if (!topCollided && !rightCollided) {
					dy -= displacement * 2;
					dx += displacement;
				}
				rightCollided = true;
				topCollided = true;
				break;
			case TOP_LEFT_CORNER: // Collided from left and top
				if (!topCollided && !leftCollided) {
					dy -= displacement * 2;
					dx -= displacement;
				}
				leftCollided = true;
				topCollided = true;
				break;
			default:
				break;
			}
		}
		
		return new int[] {dx,dy};
	}
}
