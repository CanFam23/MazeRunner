package gameTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * <p>
 * Leaderboard is used to load and update the leaderboards for each level, and
 * the leaderboard that tracks the fastest times to complete the game.
 * </p>
 *
 * @author Nick Clouse
 *
 * @since April 11, 2024
 */
public class Leaderboard implements GameVariables {

	/**
	 * Stores the entries in the leaderboard.
	 */
	private final String[] leaderboard = new String[5];
	/**
	 * Stores the file name for the leaderboard file.
	 */
	private String fileName;
	/**
	 * Stores the name of the leaderboard, which is the first line of the file.
	 */
	private String levelName;

	/**
	 * Main method, used for testing.
	 *
	 * @param args Arguments passed.
	 */
	public static void main(String[] args) {
		boolean allPassed = true;

		final String fileName = "leaderboards/overall_time_leaderboard.txt";
		final Leaderboard leaders = new Leaderboard(fileName);
		final String[] entries = Arrays.copyOf(leaders.getleaderboard(), leaders.getleaderboard().length);
		final String name = leaders.getleaderboardName();

		if (name == null || entries == null) {
			System.err.println("Failed to load leaderboard!");
			allPassed = false;
		}

		// This entry shouldn't be added because its higher than the last score
		leaders.addEntry("MazeRunner", 100);

		String[] newEntries = leaders.getleaderboard();
		// Make sure no entries have been changed
		for (int i = 0; i < entries.length; i++) {
			if (!entries[i].equals(newEntries[i])) {
				System.err.println("Leaderboard was updated when it shouldn't have been!");
				allPassed = false;
			}
		}

		// This entry should be added
		leaders.addEntry("ConnorMarl", 73);
		newEntries = leaders.getleaderboard();
		// Make sure entries have been changed
		boolean noneChanged = true;
		for (int i = 0; i < entries.length; i++) {
			if (!entries[i].equals(newEntries[i])) {
				noneChanged = false;
			}
		}
		// If no entries were changed
		if (noneChanged) {
			System.err.println("Leaderboard wasn't updated when it should've have been!");
			allPassed = false;
		}

		// Making sure there's no duplicates in the leaderboard now
		for (int i = 1; i < newEntries.length; i++) {
			if (newEntries[i] == newEntries[i - 1]) {
				System.err.println("Leaderboard was updated incorrectly!");
				allPassed = false;
			}
		}

		// Making sure file was updated
		leaders.updateleaderboardFile();
		try (final Scanner input = new Scanner(new File(fileName))) {
			while (input.hasNextLine()) {
				final String levelName = input.nextLine();
				if (!levelName.equals(leaders.getleaderboardName())) {
					System.err.println("Updated leaderboard with the wrong title!");
					allPassed = false;
				}

				for (int i = 0; i < entries.length; i++) {
					String currentEntry = input.nextLine().replace(";", " ");
					if (!newEntries[i].equals(currentEntry)) {
						System.err.println("Updated leaderboard with a incorrect entry!");
						allPassed = false;
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println(fileName + " was not found!");
		}

		// Making sure the leaderboard is sorted from lowest to highest score
		for (int i = 0; i < newEntries.length - 1; i++) {
			final String entryOne = newEntries[i];
			final String entryTwo = newEntries[i + 1];

			final int playerOneScore = Integer.parseInt(entryOne.substring(entryOne.length() - 2));
			final int playerTwoScore = Integer.parseInt(entryTwo.substring(entryTwo.length() - 2));

			if (playerOneScore > playerTwoScore) {
				System.err.println("Player scores not sorted!");
				allPassed = false;
				break;
			}
		}

		// Set file back to what it was originally
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

			// Add title
			writer.write(name);
			writer.newLine();

			// Write each leaderboard entry to file
			for (int i = 0; i < entries.length; i++) {
				writer.write(entries[i].replace(" ", ";"));
				if (i < entries.length - 1) {
					writer.newLine();
				}
			}

		} catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}

		if (allPassed) {
			System.out.println("All cases passed! :)");
		} else {
			System.err.println("At least one case failed! :(");
		}
	}

	/**
	 * Constructs a new leaderboard object.
	 *
	 * @param fileName The fileName of the file that stores the leaderboard.
	 */
	public Leaderboard(String fileName) {
		this.fileName = fileName;
		loadleaderboard(fileName);
	}

	/**
	 * Loads the leaderboard under the file name.
	 */
	public void loadleaderboard(String fileName) {
		try (final Scanner input = new Scanner(new File(fileName))) {
			while (input.hasNextLine()) {
				levelName = input.nextLine();

				for (int i = 0; i < 5; i++) {
					leaderboard[i] = input.nextLine().replace(";", " ");
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println(fileName + " was not found!");
		}
	}

	/**
	 * Updates the leaderboard file with the leaderboard array.
	 */
	public void updateleaderboardFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

			// Add title
			writer.write(levelName);
			writer.newLine();

			// Write each leaderboard entry to file
			for (int i = 0; i < leaderboard.length; i++) {
				writer.write(leaderboard[i].replace(" ", ";"));
				if (i < leaderboard.length - 1) {
					writer.newLine();
				}
			}

		} catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}
	}

	/**
	 * Adds a new entry to the leaderboard, if the score is low enough.
	 *
	 * @param name  The name to add.
	 * @param score The score to add.
	 * @return true if the entry was added to the leaderboard.
	 */
	public int addEntry(String name, int score) {
		int index = -1;

		// Check each entry in the array
		for (int i = 0; i < leaderboard.length; i++) {
			final String temp = leaderboard[i];

			final int playerScore = Integer.parseInt(temp.substring(temp.length() - 2));
			/*
			 * if new score is less than the score at i, thats where the new score will be
			 * inserted, so no need to look any farther.
			 */
			if (score <= playerScore) {
				index = i;
				break;
			}
		}
		// If a spot for the score isn't found, no need to go past this point
		if (index == -1) {
			return index;
		}

		// Shift each entry after the index to the right
		for (int i = leaderboard.length - 1; i > index; i--) {
			leaderboard[i] = leaderboard[i - 1];
		}

		// Add new entry
		final String newEntry = name + " " + score;
		leaderboard[index] = newEntry;

		return index;
	}

	/**
	 * Gets the leaderboard array.
	 *
	 * @return The leaderboard.
	 */
	public String[] getleaderboard() {
		return leaderboard;
	}

	/**
	 * Gets the levelName String, which is the title for the leaderboard.
	 *
	 * @return The name of the leaderboard.
	 */
	public String getleaderboardName() {
		return levelName;
	}
}
