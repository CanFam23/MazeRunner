package gameTools;

public class CollisionDetection implements GameVariables {

	// TODO add testing

	/**
	 * Checks for collision between the given coordinates, which represent a square.
	 * The coordinates are ordered top left, top right, bottom right, bottom left.
	 *
	 * @param xCoordsOne The first set of x coordinates.
	 * @param yCoordsOne The first set of y coordinates.
	 * @param xCoordsTwo The second set of x coordinates.
	 * @param yCoordsTwo The second set of y coordinates.
	 * @return true if there is a collision.
	 */
	public static boolean getCollision(int[] xCoordsOne, int[] yCoordsOne, int[] xCoordsTwo, int[] yCoordsTwo) {

		// if player top left x < wall bottom right x
		if (xCoordsOne[0] <= xCoordsTwo[2]
				// if player bottom right x > wall top left x
				&& xCoordsOne[2] >= xCoordsTwo[0]
				// if player top left y < wall bottom right y
				&& yCoordsOne[0] <= yCoordsTwo[2]
				// if player bottom right y > wall top left y
				&& yCoordsOne[2] >= yCoordsTwo[0]) {
			return true;
		}

		return false;
	}

	/**
	 * Checks for collision between the given coordinates, which represent a square.
	 * The coordinates are ordered top left, top right, bottom right, bottom left.
	 * Compares the first set to second set, this checks if there is a full collision,
	 * which is when all of x/yCoordsTwo are inside of x/yCoords one.
	 *
	 * @param xCoordsOne The first set of x coordinates.
	 * @param yCoordsOne The first set of y coordinates.
	 * @param xCoordsTwo The second set of x coordinates.
	 * @param yCoordsTwo The second set of y coordinates.
	 * @return true if there is a full collision.
	 *
	 */
	public static boolean fullCollision(int[] xCoordsOne, int[] yCoordsOne, int[] xCoordsTwo, int[] yCoordsTwo) {
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
			// this checks if the user is in the general center of the block, a 'full
			// collision'
			if (overlapLeft > FULL_COLLISION_INT && overlapRight > FULL_COLLISION_INT && overlapTop > FULL_COLLISION_INT
					&& overlapBottom > FULL_COLLISION_INT) {
				return true;
			}

		}
		return false;
	}
}
