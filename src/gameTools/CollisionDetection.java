package gameTools;

/**
 * <p>
 * Stores the two collision functions we use for our game, MazeRunner. We put
 * these files in a separate class and made them static, so other classes can
 * use them without needed their own methods or having to create a
 * CollisionDetection object.
 * </p>
 *
 * @author Nick Clouse
 *
 * @since March 26, 2024
 */
public class CollisionDetection implements GameVariables {

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

		// if x1 top left x < x2 bottom right x
		if (xCoordsOne[0] <= xCoordsTwo[2]
				// if x1 bottom right x > x2 top left x
				&& xCoordsOne[2] >= xCoordsTwo[0]
				// if y1 top left y < y2 bottom right y
				&& yCoordsOne[0] <= yCoordsTwo[2]
				// if y1 bottom right y > y2 top left y
				&& yCoordsOne[2] >= yCoordsTwo[0]) {
			return true;
		}

		return false;
	}

	/**
	 * Checks for collision between the given coordinates, which represent a square.
	 * The coordinates are ordered top left, top right, bottom right, bottom left.
	 * Compares the first set to second set, this checks if there is a full
	 * collision, which is when all of x/yCoordsTwo are inside of x/yCoords one.
	 *
	 * @param xCoordsOne The first set of x coordinates.
	 * @param yCoordsOne The first set of y coordinates.
	 * @param xCoordsTwo The second set of x coordinates.
	 * @param yCoordsTwo The second set of y coordinates.
	 * @return true if there is a full collision.
	 *
	 */
	public static boolean fullCollision(int[] xCoordsOne, int[] yCoordsOne, int[] xCoordsTwo, int[] yCoordsTwo) {
		// if x1 top left x < x2 bottom right x
		if (xCoordsOne[0] <= xCoordsTwo[2]
				// if x1 bottom right x > x2 top left x
				&& xCoordsOne[2] >= xCoordsTwo[0]
				// if y1 top left y < y2 bottom right y
				&& yCoordsOne[0] <= yCoordsTwo[2]
				// if y1 bottom right y > y2 top left y
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

	/**
	 * Main method, used for testing.
	 * 
	 * @param args Arguments passed
	 */
	public static void main(String[] args) {
		boolean allPassed = true;

		int[] xCoordsOne = new int[] { 0, 10, 10, 0 };
		int[] yCoordsOne = new int[] { 0, 0, 10, 10 };

		int[] xCoordsTwo = new int[] { 11, 21, 21, 11 };
		int[] yCoordsTwo = new int[] { 0, 0, 10, 10 };

		// Shouldn't be a collision here
		if (CollisionDetection.getCollision(xCoordsOne, yCoordsOne, xCoordsTwo, yCoordsTwo)) {
			System.err.println("A collision was detected when there wasn't one!");
			allPassed = false;
		}

		// Decrease x coords so they overlap with over x coords
		for (int i = 0; i < xCoordsTwo.length; i++) {
			xCoordsTwo[i]--;
		}

		// Now there should be a collision
		if (!CollisionDetection.getCollision(xCoordsOne, yCoordsOne, xCoordsTwo, yCoordsTwo)) {
			System.err.println("A collision wasn't detected when there was one!");
			allPassed = false;
		}

		// Decrease x coords more so they overlap with over x coords
		for (int i = 0; i < xCoordsTwo.length; i++) {
			xCoordsTwo[i] -= 5;
		}

		// Checking for full collision, shouldn't be one
		if (CollisionDetection.fullCollision(xCoordsOne, yCoordsOne, xCoordsTwo, yCoordsTwo)) {
			System.err.println("A full collision was detected when there wasn't one!");
			allPassed = false;
		}

		xCoordsOne = new int[] { 0, 100, 100, 0 };
		yCoordsOne = new int[] { 0, 0, 100, 100 };

		xCoordsTwo = new int[] { 10, 90, 90, 10 };
		yCoordsTwo = new int[] { 10, 10, 90, 90 };

		// Now that all of the Two coords are inside of the One coords, there should be
		// a full collision here
		if (!CollisionDetection.fullCollision(xCoordsOne, yCoordsOne, xCoordsTwo, yCoordsTwo)) {
			System.err.println("A full collision wasn't detected when there was one!");
			allPassed = false;
		}

		if (allPassed) {
			System.out.println("All cases passed! :)");
		} else {
			System.out.println("At least one case failed! :(");
		}
	}
}
