package edu.virginia.engine.display;

import sun.audio.*;

import java.awt.Point;
import java.io.*;

public class Note extends Sprite{
	
	//private AudioStream sound;
	private String sound;
	private boolean goodbad;
	private String imageName;
	private String length;
	

	public Note(String id) {
		super(id);
		this.goodbad=true;
		// TODO Auto-generated constructor stub
	}
	
	public Note(String id, String imageName) {
		super(id, imageName);
		this.goodbad=true;
		this.imageName=imageName;
		this.length=length;
		// TODO Auto-generated constructor stub
	}
	
	public Note(String id, String imageName, String soundName, String length) throws IOException {
		super(id, imageName);
		//InputStream in = new FileInputStream(soundName);
		//AudioStream audioStream = new AudioStream(in);
		//this.sound=audioStream;
		this.sound=soundName;
		this.goodbad=true;
		this.imageName=imageName;
		this.length=length;
		// TODO Auto-generated constructor stub
	}
	
	public Note(String id, String imageName, String soundName, String length, int x, int y) throws IOException {
		super(id, imageName);
		//InputStream in = new FileInputStream(soundName);
		//AudioStream audioStream = new AudioStream(in);
		//this.sound=audioStream;
		this.sound=soundName;
		this.goodbad=true;
		this.imageName=imageName;
		this.length=length;
		this.setPosition(new Point(x,y));
	}
	
	public Note(Note n){
		super(n.getId(), n.getImageName());
		this.sound=n.getSound();
		this.goodbad=n.isGood();
		this.imageName=n.getImageName();
		this.length=n.getLength();
	}
	
	public String getSound(){
		return sound;
	}
	
	public String getImageName(){
		return this.imageName;
	}
	
	public String getLength(){
		return this.length;
	}
	
	public boolean isGood(){
		return goodbad;
	}
	
	public void setGoodbad(boolean goodbad){
		this.goodbad=goodbad;
	}
	

}
