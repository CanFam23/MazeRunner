package gameTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 * <p>
 * Loads and updates the leaderboard that tracks the fastest times to complete
 * the game.
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
	private final Entry[] leaderboard = new Entry[5];
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

		final Entry[] entries = Arrays.copyOf(leaders.getleaderboard(), leaders.getleaderboard().length);
		final String name = leaders.getleaderboardName();

		if (name == null || entries == null) {
			System.err.println("Failed to load leaderboard!");
			allPassed = false;
		}

		// This entry shouldn't be added because its higher than the last score
		leaders.addEntry("MazeRunner", 300);

		Entry[] newEntries = leaders.getleaderboard();
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
					final String line = input.nextLine();
					final String[] currentEntry = line.split(";");

					// New entries should match currentEntry
					if (!newEntries[i].name.equals(currentEntry[0])
							|| newEntries[i].score != Integer.parseInt(currentEntry[1])) {
						System.err.println("Updated leaderboard with a incorrect entry!");
						allPassed = false;
					}

					// Testing toString
					final String tempStr = line.replace(";", " ");
					if (!newEntries[i].toString().equals(tempStr)) {
						System.err.println("ToString failed!");
						allPassed = false;
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println(fileName + " was not found!");
		}

		// Making sure the leaderboard is sorted from lowest to highest score
		for (int i = 0; i < newEntries.length - 1; i++) {
			final Entry entryOne = newEntries[i];
			final Entry entryTwo = newEntries[i + 1];

			final int playerOneScore = entryOne.score;
			final int playerTwoScore = entryTwo.score;

			if (playerOneScore > playerTwoScore) {
				System.err.println("Player scores not sorted!");
				allPassed = false;
				break;
			}
		}

		// Sort the array by score using a Comparator
		Arrays.sort(entries, Comparator.comparingInt(entry -> entry.score));

		// Print the sorted array
		System.out.println("Sorted by score:");
		for (Entry entry : entries) {
			System.out.println(entry);
		}

		// Set file back to what it was originally
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

			// Add title
			writer.write(name);
			writer.newLine();

			// Write each leaderboard entry to file
			// Write each leaderboard entry to file
			for (int i = 0; i < entries.length; i++) {
				final String entry = entries[i].name + ";" + entries[i].score;
				writer.write(entry);
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
	 * 
	 * @param fileName The file to use.
	 */
	public void loadleaderboard(String fileName) {
		try (final Scanner input = new Scanner(new File(fileName))) {
			Entry defaultEntry = new Entry("Name", Integer.MAX_VALUE);
			while (input.hasNextLine()) {
				levelName = input.nextLine();

				for (int i = 0; i < 5; i++) {

					try {
						final String[] tempEntry = input.nextLine().split(";");
						final String name = tempEntry[0];

						final int score = Integer.parseInt(tempEntry[1]);
						Entry newEntry = new Entry(name, score);
						leaderboard[i] = newEntry;
					} catch (NumberFormatException e) {
						System.err.println("Error loading leaderboard scores! Using default value.");
						leaderboard[i] = defaultEntry;
					}
				}
			}

			// Sort leaderboard array by score of each entry object
			Arrays.sort(leaderboard, Comparator.comparingInt(Entry -> Entry.score));
		} catch (FileNotFoundException e) {
			System.err.println(fileName + " was not found!");
		}
	}

	/**
	 * Updates the leaderboard file with the leaderboard array.
	 */
	public void updateleaderboardFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			// Sort leaderboard array by score of each entry object
			Arrays.sort(leaderboard, Comparator.comparingInt(Entry -> Entry.score));

			// Add title
			writer.write(levelName);
			writer.newLine();

			// Write each leaderboard entry to file
			for (int i = 0; i < leaderboard.length; i++) {
				final String entry = leaderboard[i].name + ";" + leaderboard[i].score;
				writer.write(entry);
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

		// Sort leaderboard array by score of each entry object
		Arrays.sort(leaderboard, Comparator.comparingInt(Entry -> Entry.score));

		// Check each entry in the array
		for (int i = 0; i < leaderboard.length; i++) {
			final Entry temp = leaderboard[i];
			final int playerScore = temp.score;
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
		final Entry newEntry = new Entry(name, score);
		leaderboard[index] = newEntry;

		return index;
	}

	/**
	 * Gets the leaderboard array.
	 *
	 * @return The leaderboard.
	 */
	public Entry[] getleaderboard() {
		return leaderboard;
	}

	/**
	 * Gets the leaderboard array, but converts every entry to a string.
	 * 
	 * @return The leaderboard, where each entry is a string.
	 */
	public String[] leaderboardToString() {
		final String[] leaderboardStr = new String[leaderboard.length];

		for (int i = 0; i < leaderboard.length; i++) {
			final String entry = leaderboard[i].toString();
			leaderboardStr[i] = entry;
		}

		return leaderboardStr;
	}

	/**
	 * Gets the levelName String, which is the title for the leaderboard.
	 *
	 * @return The name of the leaderboard.
	 */
	public String getleaderboardName() {
		return levelName;
	}

	/**
	 * Used to store entries in the leaderboard. Each entry has a 'name' and
	 * 'score.'
	 */
	private class Entry {
		/**
		 * Name associated with the entry.
		 */
		public String name;
		/**
		 * Score associated with the entry.
		 */
		public int score;

		/**
		 * Makes a new Entry object.
		 * 
		 * @param name  Name associated with entry.
		 * @param score Score associated with entry.
		 */
		Entry(String name, int score) {
			this.name = name;
			this.score = score;
		}

		/**
		 * Converts Entry object to string.
		 *
		 * @return String format of entry object.
		 */
		public String toString() {
			return name + " " + score;
		}
	}
}
