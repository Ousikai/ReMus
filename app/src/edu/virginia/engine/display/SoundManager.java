package edu.virginia.engine.display;

import java.util.HashMap;
import java.util.Set;
import sun.audio.*;
import java.io.*;

import engine.events.IEventListener;

public class SoundManager {

	private HashMap<String, AudioStream> soundEffects; 
	public SoundManager(){
		soundEffects = new HashMap<String, AudioStream>();
	}
	public void LoadSoundEffect(String id, String filename) throws IOException{
		System.out.println(filename);
		InputStream in = new FileInputStream(filename);
		AudioStream audioStream = new AudioStream(in);
		soundEffects.put(id, audioStream);

	}
	public void PlaySoundEffect(String id){ //sound effects are short and are removed once complete
		 AudioPlayer.player.start(soundEffects.get(id));
	}
	public void LoadMusic(String id, String filename) throws IOException{
		InputStream in = new FileInputStream(filename);
		AudioStream audioStream = new AudioStream(in);
		soundEffects.put(id, audioStream);
	}
	public void PlayMusic(String id){ //music loops and plays forever, consider adding a parameter for looping
		 AudioPlayer.player.start(soundEffects.get(id));
	}

}