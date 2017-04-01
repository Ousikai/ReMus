package edu.virginia.engine.util;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.Sprite;
import sun.audio.*;
import java.io.*;

public class Note extends DisplayObject{
	
	//private AudioStream sound;
	private String sound;
	

	public Note(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	public Note(String id, String imageName) {
		super(id, imageName);
		// TODO Auto-generated constructor stub
	}
	
	public Note(String id, String imageName, String soundName) throws IOException {
		super(id, imageName);
		//InputStream in = new FileInputStream(soundName);
		//AudioStream audioStream = new AudioStream(in);
		//this.sound=audioStream;
		this.sound=soundName;
		// TODO Auto-generated constructor stub
	}
	
	public String getSound(){
		return sound;
	}
	

}
