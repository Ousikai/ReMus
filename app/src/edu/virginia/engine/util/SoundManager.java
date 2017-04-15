package edu.virginia.engine.util;

import java.util.HashMap;
import java.util.Set;

import sun.audio.*;

import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager implements LineListener{

	/* Sound effects */
	private HashMap<String, AudioStream> soundEffects; 
	public String currentFile;
	/* Background Music */
	private HashMap<Integer, File> backgroundMusic; 
	private int currentLevel;
	private Clip audioClip; 
	
	
	public SoundManager(){
		soundEffects = new HashMap<String, AudioStream>();
		backgroundMusic = new HashMap<Integer, File>();
		currentLevel = 0;
	}
	public String getCurrentFile() {
		return currentFile;
	}
	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}
	public void LoadSoundEffect(String id, String filename) throws IOException{
		InputStream in = new FileInputStream("resources/" + filename);
		AudioStream audioStream = new AudioStream(in);
		soundEffects.put(id, audioStream);

	}
	public void PlaySoundEffect(String id){ //sound effects are short and are removed once complete
		 AudioPlayer.player.start(soundEffects.get(id));
	}
	public void LoadBGM(int level, String filename) throws IOException{
		File audioFile = new File("resources/" + filename);
		backgroundMusic.put(level, audioFile);
	}
	
	public void PlayBGM(int currentLevel, double timePassed){ //music loops and plays forever, consider adding a parameter for looping
		//AudioPlayer.player.start(soundEffects.get(id)); 
		File audioFile = backgroundMusic.get(currentLevel);
		try {
	        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
	        AudioFormat format = audioStream.getFormat();
	        DataLine.Info info = new DataLine.Info(Clip.class, format);
	        audioClip = (Clip) AudioSystem.getLine(info);
	        audioClip.addLineListener((LineListener) this);
	        audioClip.open(audioStream);
	        //FrameLength: 2489534
	        audioClip.setMicrosecondPosition((long) (timePassed * 1000));
	        audioClip.start();
		} catch (UnsupportedAudioFileException ex) {
	        System.out.println("The specified audio file is not supported.");
	        ex.printStackTrace();
	    } catch (LineUnavailableException ex) {
	        System.out.println("Audio line for playing back is unavailable.");
	        ex.printStackTrace();
	    } catch (IOException ex) {
	        System.out.println("Error playing the audio file.");
	        ex.printStackTrace();
	    }		
		
	}
	
	public void StopBGM(){
		audioClip.close();
	}
	
	public void updateBGM(int currentLevel, double timePassed){
		// Track has been updated
		if (this.currentLevel != currentLevel){
			// Stop playing current track
			StopBGM();
			// Setup next track
			this.currentLevel = currentLevel;
			PlayBGM(currentLevel, timePassed);
		}
		// Else continue playing current track 
	}

	public int getCurrentLevel() {
		return currentLevel;
	}
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	@Override
	public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        //Game is over
        if (type == LineEvent.Type.STOP) {
        	audioClip.close();
        }
	}
	
//	public void update() {
//	//	System.out.println(AudioPlayer.player.isInterrupted());
//		
////		
////		
//	}

}