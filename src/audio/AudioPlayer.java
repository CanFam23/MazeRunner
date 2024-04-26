package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	
	public static int MENU_1 = 0;
	public static int LEVEL_1 = 1;
	public static int LEVEL_2 = 2;

	public static Clip DIE;
	public static int JUMP = 1;
	public static int GAMEOVER = 2;
	public static int LVL_COMPLETED = 3;
	public static int ATTACK_ONE = 4;
	public static int ATTACK_TWO = 5;
	public static int ATTACK_THREE = 6;
	
	private Clip[] songs, effects;
	private int currentSongId;
	private boolean songMute, effectMute;
	private Random rand = new Random();
	
    private Clip clip;

	
    public AudioPlayer() {
        loadSongs();
        loadEffects();
    }

    private void loadSongs() {
        String[] names = { "menu", "level1", "level2" };
        songs = new Clip[names.length];
        for (int i = 0; i < songs.length; i++)
            songs[i] = getClip(names[i]);
    }

    private void loadEffects() {
    	DIE = getClip("die");
        String[] effectNames = { "die", "jump", "gameover", "lvlcompleted", "attack1", "attack2", "attack3" };
        effects = new Clip[effectNames.length];
        for (int i = 0; i < effects.length; i++)
            effects[i] = getClip(effectNames[i]);
    }

    private Clip getClip(String name) {
        URL url = getClass().getResource(name + ".wav");
        AudioInputStream audio;

        try {
            audio = AudioSystem.getAudioInputStream(url);
            Clip c = AudioSystem.getClip();

            c.open(audio);
            return c;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        	System.out.print("Couldn't load");
            e.printStackTrace();
        }

        return null;
    }

    public void stopSong() {
        if (songs[currentSongId].isActive())
            songs[currentSongId].stop();
    }

//    public void setLevelSong(int lvlIndex) {
//        if (lvlIndex % 2 == 0)
//            playSong(LEVEL_1);
//        else
//            playSong(LEVEL_2);
//    }

    public void lvlCompleted() {
        stopSong();
        playEffect(LVL_COMPLETED);
    }

    public void playAttackSound() {
        int start = 4;
        start += Math.random() * 3;
        playEffect(start);
    }

    public void playEffect(int effect) {
        effects[effect].setMicrosecondPosition(0);
        effects[effect].start();
        System.out.println("Playing effect: " + effect);
    }

//    public void playSong(int song) {
//        currentSongId = song;
//        songs[currentSongId].stop();
//        songs[currentSongId].setMicrosecondPosition(0);
//        songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
//        
//        System.out.println("Playing song: " + song);
//        // Block until the song finishes playing
//        while (songs[currentSongId].isActive()) {
//            try {
//                Thread.sleep(100); // Sleep for a short duration
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    
    public void playSong(String fileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(fileName));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            System.out.println("Playing song: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSongOnce(String fileName) {
	   try {
	        if (clip != null && clip.isActive()) {
	            clip.stop();  // Stop the currently playing clip
	            clip.close();
	        }

	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(fileName));
	        clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start(); // Play the clip once
	        System.out.println("Playing song: " + fileName);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
    
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
	
   public static void main(String[] args) throws InterruptedException {
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.playSongOnce("attack1.wav");
        Thread.sleep(10000); // Play for 10 seconds
//        audioPlayer.stop();

   }
}
