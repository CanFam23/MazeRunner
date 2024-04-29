package audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * <p>
 * The AudioPlayer class provides functionality to play audio files.
 * </p>
 * @author Nick Clouse
 * @author Andrew Denegar
 * @author Molly O'Connor
 * @author Kaarin Gaming: base code taken from Kaarin Gaming Youtube, Platform Turtorial episode 25
 * 
 *
 * @since Apr 27, 2024
 *
 */
public class AudioPlayer {

    /** The currently playing audio clip. */
    private Clip clip;

    /** Indicates whether an audio clip is currently playing. */
    private boolean isPlaying = false;

    /**
     * Constructs a new AudioPlayer instance.
     */
    public AudioPlayer() {
        // Initialize arrays for songs and effects
    }

    /**
     * Plays a song continuously.
     * @param fileName The name of the song file to play.
     */
    public void playSong(String fileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(fileName));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the clip continuously
            isPlaying = true;
        } catch (Exception e) {
            System.err.println("Failed to load audio file " + fileName);
        }
    }

    /**
     * Checks if an audio clip is currently active (playing).
     * @return true if an audio clip is currently active, false otherwise.
     */
    public boolean isActive() {
        return isPlaying;
    }

    /**
     * Plays a song once.
     * @param fileName The name of the song file to play.
     */
    public void playSongOnce(String fileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(fileName));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start(); // Play the clip once
            isPlaying = true;
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        isPlaying = false;
                    }
                }
            });
        } catch (Exception e) {
        	System.err.println("Failed to load audio file " + fileName);
        }
    }

    /**
     * Stops the currently playing audio clip.
     */
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            isPlaying = false;
        }
    }
    
    /**
     * Sets the volume of the currently playing song.
     * @param volumeValue The volume value to set (0.0f to 1.0f).
     */
    public void setVolume(float volumeValue) {
        if (clip != null) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volumeValue) + gainControl.getMinimum();
                gainControl.setValue(gain);
            } catch (Exception e) {
            	System.err.println("Failed to set volume! ");
            }
        }
    }
	
    /**
     * Main method, used for testing.
     * 
     * @param args Aruguments passed.
     */
    public static void main(String[] args) {
    	boolean allCasesPassed = true;
        // Create an instance of AudioPlayer
        AudioPlayer audioPlayer = new AudioPlayer();

        // Play a song continuously
        audioPlayer.playSong("menu.wav");

        // Check if the song is active
        System.out.println("Is song active? " + audioPlayer.isActive());
        if (audioPlayer.isActive() != true) {
        	allCasesPassed = false;
        }

        // Wait for a while to observe song playing
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop the song
        audioPlayer.stop();
        
        if (audioPlayer.isActive() != false) {
        	allCasesPassed = false;
        }

        // Check if the song is active after stopping
        System.out.println("Is song active after stopping? " + audioPlayer.isActive());

        // Play a song once
        audioPlayer.playSongOnce("winner.wav");
        if (audioPlayer.isActive() != true) {
        	allCasesPassed = false;
        }

        // Wait for a while to observe song playing
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the song is active after playing once
        System.out.println("Is song active when playing once? " + audioPlayer.isActive());

        // Stop the song
        audioPlayer.stop();
        if (audioPlayer.isActive() != false) {
        	allCasesPassed = false;
        }

        // Check if the song is active after stopping
        System.out.println("Is song active after stopping? " + audioPlayer.isActive());
        if (allCasesPassed == true) {
        	System.out.println("All Cases Passed");
        } else {
        	System.out.println("At least one case failed");
        }
    }
}
