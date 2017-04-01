package edu.virginia.engine.display;

import java.util.HashMap;
import java.util.Set;

import sun.audio.*;

import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import edu.virginia.engine.util.IEventListener;

public class SoundManager {

	private HashMap<String, AudioStream> soundEffects; 
	public String currentMusicID;
	public String currentFile;
	public SoundManager(){
		soundEffects = new HashMap<String, AudioStream>();
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
	public void LoadMusic(String id, String filename) throws IOException{
		InputStream in = new FileInputStream("resources/" + filename);
		AudioStream audioStream = new AudioStream(in);
		soundEffects.put(id, audioStream);
		currentMusicID = id;
		setCurrentFile(filename);
	}
	public void PlayMusic(String id){ //music loops and plays forever, consider adding a parameter for looping
		AudioPlayer.player.start(soundEffects.get(id)); 
	}
	
	public void StopMusic(String id) {
		AudioPlayer.player.stop(soundEffects.get(id));
	}
	
//	public void update() {
//	//	System.out.println(AudioPlayer.player.isInterrupted());
//		
////		
////		
//	}

}