package edu.virginia.engine.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ArrayList;

import javax.swing.Timer;

public class AnimatedSprite extends Sprite implements ActionListener {
	
	private ArrayList <ArrayList<String>> imgNames;
	private ArrayList<String> currentAnimation;
	private int currentFrame;
	private int startIndex;
	private int endIndex;
	private int fps;
	private Timer timer;
	
	public AnimatedSprite(String id) {
		super(id);
		// TODO Auto-generated constructor stub
		
		ArrayList<String> run = new ArrayList<String>();
		ArrayList<String> walk = new ArrayList<String>();
		ArrayList<String> jump = new ArrayList<String>();
		run.add("mariowalk1.png");
		run.add("mariowalk2.png");
		run.add("mariowalk3.png");
		run.add("mariowalk4.png");
		run.add("mariowalk5.png");
		run.add("mariowalk6.png");
		run.add("mariowalk7.png");
		run.add("mariowalk8.png");
		run.add("mariowalk9.png");
		walk.add("mariowalk1.png");
		walk.add("mariowalk2.png");
		walk.add("mariowalk3.png");
		walk.add("mariowalk4.png");
		walk.add("mariowalk5.png");
		walk.add("mariowalk6.png");
		walk.add("mariowalk7.png");
		walk.add("mariowalk8.png");
		walk.add("mariowalk9.png");
		jump.add("mariojump1.png");
		jump.add("mariojump2.png");
		jump.add("mariojump3.png");
		jump.add("mariojump4.png");
		jump.add("mariojump5.png");
		jump.add("mariojump6.png");
		jump.add("mariojump7.png");
		jump.add("mariojump8.png");
		jump.add("mariojump9.png");
		
		fps=9;
		
		timer = new Timer(1000/fps, this);
		timer.start();
	}

	public AnimatedSprite(String id, String imageFileName) {
		super(id, imageFileName);
		imgNames= new ArrayList();
		ArrayList<String> run = new ArrayList<String>();
		ArrayList<String> walk = new ArrayList<String>();
		ArrayList<String> jump = new ArrayList<String>();
		run.add("mariowalk1.png");
		run.add("mariowalk2.png");
		run.add("mariowalk3.png");
		run.add("mariowalk4.png");
		run.add("mariowalk5.png");
		run.add("mariowalk6.png");
		run.add("mariowalk7.png");
		run.add("mariowalk8.png");
		run.add("mariowalk9.png");
		walk.add("mariowalk1.png");
		walk.add("mariowalk2.png");
		walk.add("mariowalk3.png");
		walk.add("mariowalk4.png");
		walk.add("mariowalk5.png");
		walk.add("mariowalk6.png");
		walk.add("mariowalk7.png");
		walk.add("mariowalk8.png");
		walk.add("mariowalk9.png");
		jump.add("mariojump1.png");
		jump.add("mariojump2.png");
		jump.add("mariojump3.png");
		jump.add("mariojump4.png");
		jump.add("mariojump5.png");
		jump.add("mariojump6.png");
		jump.add("mariojump7.png");
		jump.add("mariojump8.png");
		jump.add("mariojump9.png");
		
		imgNames.add(run);
		imgNames.add(walk);
		imgNames.add(jump);
		
		fps=9;
		
		currentFrame=0;
		endIndex=8;
		
		timer = new Timer(1000/fps, this);
		timer.start();
	}
	
	
	
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
	}
	
	public void animate (String action){
		//ArrayList<String> a = new ArrayList<String>();
		if (action == "run"){
			currentAnimation=this.imgNames.get(0);
			fps=15;
		}
		if (action == "walk"){
			currentAnimation=this.imgNames.get(1);
			fps=8;
		}
		if (action == "jump"){
			currentAnimation=this.imgNames.get(2);
			fps=9;
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		if (currentFrame<endIndex)
		currentFrame++;
		else
			currentFrame=0;
		this.setImage(this.currentAnimation.get(currentFrame));
	}
	
	public void setFps(int i){
		this.fps=i;
	}
	
	public void stopAnimation(){
		timer.stop();
	}
	
	public void startAnimation(){
		timer.start();
	}
	
}