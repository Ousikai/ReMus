//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

package edu.virginia.alpha;


import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.Note;
import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.util.SoundManager;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.GamePad;
import edu.virginia.engine.util.listeners.CollisionEvent;
import edu.virginia.engine.util.listeners.HookListener;
import edu.virginia.engine.util.listeners.PlayerListener;

public class NotesFreeFall extends Game{
	/* Player variables */
	PhysicsSprite player = new PhysicsSprite("Player", "Player.png");
	Sprite crosshairs = new Sprite("Crosshairs", "crosshairs7.png");
	Sprite hookshot = new Sprite("Hookshot", "hookshot.png"); 
	/* Sprite collections */
	DisplayObjectContainer playerSprites = new DisplayObjectContainer("Player Sprites");
	ArrayList<String> backgroundFilePaths = new ArrayList<String>();
	DisplayObjectContainer backgroundCollection = new DisplayObjectContainer("Background Collection");
	DisplayObjectContainer foregroundCollection = new DisplayObjectContainer("Foreground Collection");
	DisplayObjectContainer platformCollection = new DisplayObjectContainer("Platform Collection");
	DisplayObjectContainer notesCollection = new DisplayObjectContainer("Notes Collection");
	/* Game variables */
	int livesLeft = 3;
	boolean gameEnded=false;
	boolean inPlay = true;
	TweenJuggler tInstance;
	double startTime=System.currentTimeMillis();
	double timeElapsed=0;
	GameClock backgroundInterlude = new GameClock();
	GameClock foregroundInterlude = new GameClock();
	/* HUD variables */
	int notesCollected = 0;
	int currentLevel = 0;
	int notesToNextLevel = 1;
	int notesPerLevel = 1;
	int notesToBeatStage = 5;
	Sprite staff = new Sprite("Staff", "staff.png");
	ArrayList<Note> collected = new ArrayList<Note>();
	SoundManager soundManager = new SoundManager();
	double gameEndedTime;
	boolean toPlay;
	String noteLength="";
	
	/* Set up game */
	public NotesFreeFall() throws IOException {
		super("Restoration of Sound", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set Player Sprite positions */
		player.setPosition(new Point(100, 600));
		drawCrosshairsKeyboard(crosshairs, player, hookshot);
		staff.setPosition(new Point(200, 250));
		staff.setVisible(false);
		/* Set up backgrounds */
		this.addChild(backgroundCollection);
		setupBackgrounds();
		/* Set up sprites that will fall in the foreground */
		this.addChild(foregroundCollection);
			// Set up platforms 
			setupPlatforms();
			/* Set up player sprites as children of Game */
			setupPlayerSprites(playerSprites,
					    	   crosshairs, 
					    	   player, 
					    	   hookshot);
		/* Set up hookshot */
		HookListener hookListener = new HookListener(player, tInstance, hookshot);
		PlayerListener playerListener = new PlayerListener(player, tInstance, soundManager);
		this.setupHookshot(player, playerListener, hookListener, hookshot);
	}

	public static void main(String[] args) throws IOException {
		NotesFreeFall game = new NotesFreeFall();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		super.draw(g);
		if(staff != null){
			staff.draw(g);
		}
		
		if (gameEnded == true){
			staff.setVisible(true);
			for (Note note : collected){
				note.draw(g);
			}
		}
		// Draw HUD
		if (gameEnded==true){
			// TODO: Tween victory text
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			g.drawString("Sound Restored!", 450, 150);
			if (collected.size()==0){
				//this.stop();
			}
		}
		g.setFont(new Font("KinoMT", Font.BOLD, 30));
		g.drawString("Notes Collected: " + notesCollected, 1260, 50);
		g.drawString("Notes To Next Level: " + notesToNextLevel, 1200, 90);
	}
		
	@Override
	public void update(ArrayList<Integer> pressedKeys, ArrayList<GamePad> controllers){
		// Hold 'P' to pause (for debugging)
		if ((pressedKeys.contains(80))){
			inPlay = false;
		}
		else{
			inPlay = true;
		}
		
		if (inPlay){
			if (gameEnded==true){
				timeElapsed=Math.min(System.currentTimeMillis()-startTime,3000);
			}
			if (tInstance!=null){tInstance.nextFrame();}
			
			/* Update methods */
			super.update(pressedKeys, controllers);
			player.update(pressedKeys, controllers);
			
			/* Update hook status */
			drawCrosshairsKeyboard(crosshairs, player, hookshot);
			if (player != null){
			platformCollection.hookablePlatform(crosshairs, player);}
		
			/* Have foreground and background elements fall */
			if (backgroundCollection != null && backgroundInterlude != null){
			updateBackgroundCollection(backgroundCollection, backgroundInterlude);
			updateForegroundCollection(foregroundCollection, foregroundInterlude);
			}
		
			/* Collisions only occur if player can be interacted with */
			if (player != null){
			if (player.isInPlay()){
				/* Player colliding with platform */
				if (platformCollection.collidesWithPlatform(player)){
					// If true, player will send collision event to all listeners.
				}
				/* Player is in the Air*/
				else {
					player.dispatchEvent(new Event(CollisionEvent.INAIR, null));
				}
				// Player collides with note
				if (notesCollection.collidesWithNoteSound(player, tInstance)){
					updateHUD();
					Note note = notesCollection.getCollidedNote();
					collected.add(note);
					note.setVisible(true);
					int height=300;
					if (note.getId().equals("c4") || note.getId().equals("c4q")) height=384;
					else if (note.getId().equals("d4") || note.getId().equals("d4q")) height=370;
					else if (note.getId().equals("e4") || note.getId().equals("e4q")) height=356;
					else if (note.getId().equals("f4") || note.getId().equals("f4q")) height=342;
					else if (note.getId().equals("g4") || note.getId().equals("g4q")) height=328;
					else if (note.getId().equals("a5") || note.getId().equals("a5q")) height=314;
					else if (note.getId().equals("b5") || note.getId().equals("b5q")) height=300;
					
				    note.setPosition(new Point((250+(collected.size()+1)*900/5), height));
					note.setScaleX(0.4);
					note.setScaleY(0.4);
					notesCollection.removeChild(notesCollection.getCollidedNote());
				}
				
				/* Player is on the ground */
				if(player.getPosition().getY()>600){
					player.dispatchEvent(new Event(CollisionEvent.GROUND, null));
				}
			}
			}
		}
		
		if (gameEnded==true){
			notesCollection.removeChildren();
			platformCollection.removeChildren();
			this.removeChild(foregroundCollection);
			if (collected.isEmpty()){
				this.stop();
			}
			else {
				if (toPlay==true){
					try {
						soundManager.LoadSoundEffect(collected.get(0).getSound(), collected.get(0).getSound());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					soundManager.PlaySoundEffect(collected.get(0).getSound());
					noteLength=collected.get(0).getLength();
					collected.remove(0);
					toPlay=false;
					//System.out.println(toPlay);
				}	
				else{
					//System.out.println("um2");
					//System.out.println(System.currentTimeMillis()-gameEndedTime);
					int t=1000;
					if (noteLength.equals("quarter")) t=2000; 
					if (System.currentTimeMillis()-gameEndedTime>t){
						//System.out.println(System.currentTimeMillis()-gameEndedTime);
						gameEndedTime=System.currentTimeMillis();
						toPlay=true;
						}
					}
				}
			}
	}
	
	public void updateHUD(){
		if (notesCollected < 5) notesCollected += 1; 
		// Increase threshold for achieve next level
		if (notesCollected >= notesToNextLevel){
			if (currentLevel < 5) currentLevel += 1;
			swapBackground(currentLevel, backgroundFilePaths);
			notesToNextLevel += notesPerLevel;
			// Player has collected all notes 
			if (notesCollected >= notesToBeatStage){
				notesToNextLevel = notesToBeatStage;
				gameEnded = true;
				toPlay=false;
				gameEndedTime=System.currentTimeMillis();
				staff.setVisible(true);
				int height=300;
				this.removeChild(foregroundCollection);
				for (int i=0; i<collected.size(); i++){
					if (collected.get(i).getId().equals("c4") || collected.get(i).getId().equals("c4q")) height=384;
					else if (collected.get(i).getId().equals("d4") || collected.get(i).getId().equals("d4q")) height=370;
					else if (collected.get(i).getId().equals("e4") || collected.get(i).getId().equals("e4q")) height=356;
					else if (collected.get(i).getId().equals("f4") || collected.get(i).getId().equals("f4q")) height=342;
					else if (collected.get(i).getId().equals("g4") || collected.get(i).getId().equals("g4q")) height=328;
					else if (collected.get(i).getId().equals("a5") || collected.get(i).getId().equals("a5q")) height=314;
					else if (collected.get(i).getId().equals("b5") || collected.get(i).getId().equals("b5q")) height=300;
				
					collected.get(i).setPosition(new Point((250+(i+1)*900/collected.size()), height));
					collected.get(i).setScaleX(0.4);
					collected.get(i).setScaleY(0.4);
			}
		}	
		}
	}
	
	public void updateBackgroundCollection(DisplayObjectContainer backgroundRoot, GameClock timeSinceFall){
		/** Change how often the background will update **/
		if (backgroundRoot != null){
		int timeBetweenFall = 150;
		if (timeSinceFall.getElapsedTime() > timeBetweenFall){
			/* Have your children fall */
			for (int i = 0; i < backgroundRoot.numberOfChildren(); i++){
				DisplayObject toFallNext = backgroundRoot.getChildAtIndex(i);
				int x = (int) toFallNext.getPosition().getX();
				int y = (int) toFallNext.getPosition().getY();
				/** Changw how much the background will fall **/
				int backgroundFallSpeed = 1;
				int newY = y + backgroundFallSpeed;
				toFallNext.setPosition(new Point(x,newY));
			}
			/* Reset timer */
			timeSinceFall.resetGameClock();
		}
		}
	}
	
	public void swapBackground(int currLevel, ArrayList<String> bfp){
		// Swap the background to the next image
		int prevLevel = currLevel - 1;
		DisplayObject currBackground = backgroundCollection.getChildAtIndex(1);
		currBackground.setImage(bfp.get(currLevel));
		currBackground.setAlpha(0);
		DisplayObject prevBackground = backgroundCollection.getChildAtIndex(0);
		prevBackground.setImage(bfp.get(prevLevel));
		prevBackground.setAlpha(1);
		// Tween the backgrounds
		Tween fadeOut = new Tween (prevBackground, null, null);
		Tween fadeIn = new Tween (currBackground, null, null);
		tInstance.add(fadeOut);
		tInstance.add(fadeIn);
		int timeTweening = 500;
		fadeOut.animate(TweenableParams.ALPHA, 1, 0, timeTweening);
		fadeIn.animate(TweenableParams.ALPHA, 0, 1, timeTweening);
		
	}
	
	public void updateForegroundCollection(DisplayObjectContainer foregroundRoot, GameClock timeSinceFall){
		/** Change how often the foreground and background will update **/
		int timeBetweenFall = 20;
		if (timeSinceFall.getElapsedTime() > timeBetweenFall){
			// Have foreground elements fall 
			for (int i = 0; i < foregroundRoot.numberOfChildren(); i++){
				DisplayObject toFallNext = foregroundRoot.getChildAtIndex(i);
				foregroundFallSpeed1((DisplayObjectContainer)toFallNext);
			}
			// Reset timer 
			timeSinceFall.resetGameClock();
		}
	}
	
	public void foregroundFallSpeed1(DisplayObjectContainer toFallCurrent){
		/** Change how much the foreground will fall **/
		int x = (int) toFallCurrent.getPosition().getX();
		int y = (int) toFallCurrent.getPosition().getY();
		int foregroundFallSpeed = 3;
		int newY = y + foregroundFallSpeed;
		toFallCurrent.setPosition(new Point(x,newY));
		for (int i = 0; i < toFallCurrent.numberOfChildren(); i++){
			DisplayObject toFallNext = toFallCurrent.getChildAtIndex(i);
			foregroundFallSpeed1((DisplayObjectContainer)toFallNext);
		}	
	}
	
	/** Set backgrounds here! **/
	public void setupBackgrounds(){
		// Level 0
		backgroundFilePaths.add("background.png");
		// Level 1
		backgroundFilePaths.add("cool-background.png");
		// Level 2
		backgroundFilePaths.add("background.png");
		// Level 3
		backgroundFilePaths.add("cool-background.png");
		// Level 4
		backgroundFilePaths.add("background.png");
		// Level 5
		backgroundFilePaths.add("cool-background.png");
		// Previous Background
		backgroundCollection.addChild(new Sprite ("Previous Background", "cool-background.png"));
		// Current Background
		backgroundCollection.addChild(new Sprite ("Current Background", "background.png"));
	}
	
	/** Create platforms here! 
	 * @throws IOException **/
	public void setupPlatforms(){
		/* Set up display hierarchy and platforms */
		foregroundCollection.addChild(platformCollection);
		foregroundCollection.addChild(notesCollection);
		try {
		platformCollection.addChild(new Sprite ("Platform1", "platformBlue.png", 100,150));
		Note c41;
			c41 = new Note ("c4", "c.png", "c4.wav", "eighth");
		c41.setPosition(new Point(130,50));
		notesCollection.addChild(c41);
		platformCollection.addChild(new Sprite ("Platform2", "platformBlue.png", 600, 450));
		Note c42 = new Note ("c4", "c.png", "c4.wav", "eighth");
		c42.setPosition(new Point(630,350));
		notesCollection.addChild(c42);
		platformCollection.addChild(new Sprite ("Platform2.5", "platformBlue.png", 300, 400));
		Note g41 = new Note ("g4", "g.png", "g4.wav", "eighth");
		g41.setPosition(new Point(330,300));
		notesCollection.addChild(g41);
		platformCollection.addChild(new Sprite ("Platform3", "platformBlue.png", 1200, 300));
		Note g42 = new Note ("g4", "g.png", "g4.wav", "eighth");
		g42.setPosition(new Point(1230,200));
		notesCollection.addChild(g42);
		platformCollection.addChild(new Sprite ("Platform4", "platformBlue.png", 600, 100));
		Note a51 = new Note ("a5", "a.png", "a5.wav", "eighth");
		a51.setPosition(new Point(630,0));
		notesCollection.addChild(a51);
		platformCollection.addChild(new Sprite ("Platform5", "platformBlue.png", 700, -100));
		Note a52 = new Note ("a5", "a.png", "a5.wav", "eighth");
		a52.setPosition(new Point(730,-200));
		notesCollection.addChild(a52);
		platformCollection.addChild(new Sprite ("Platform6", "platformBlue.png", 300, -300));
		Note g43 = new Note ("g4", "g.png", "g4.wav", "quarter");
		g43.setPosition(new Point(330,-400));
		notesCollection.addChild(g43);
		platformCollection.addChild(new Sprite ("Platform7", "platformBlue.png", 700, -500));
		Note f41 = new Note ("f4", "f.png", "f4.wav", "eighth");
		f41.setPosition(new Point(730,-600));
		notesCollection.addChild(f41);
		platformCollection.addChild(new Sprite ("Platform8", "platformBlue.png", 200, -700));
		Note f42 = new Note ("f4", "f.png", "f4.wav", "eighth");
		f42.setPosition(new Point(230,-800));
		notesCollection.addChild(f42);
		platformCollection.addChild(new Sprite ("Platform9", "platformBlue.png", 100,-900));
		Note e41 = new Note ("e4", "e.png", "e4.wav", "eighth");
		e41.setPosition(new Point(130,-1000));
		notesCollection.addChild(e41);
		platformCollection.addChild(new Sprite ("Platform10", "platformBlue.png", 600, -1100));
		Note e42 = new Note ("e4", "e.png", "e4.wav", "eighth");
		e42.setPosition(new Point(630,-1200));
		notesCollection.addChild(e42);
		platformCollection.addChild(new Sprite ("Platform11", "platformBlue.png", 1200, -1300));
		Note d41 = new Note ("d4", "d.png", "d4.wav", "eighth");
		d41.setPosition(new Point(1230,-1400));
		notesCollection.addChild(d41);
		platformCollection.addChild(new Sprite ("Platform12", "platformBlue.png", 600, -1500));
		Note d42 = new Note ("d4", "d.png", "d4.wav", "eighth");
		d42.setPosition(new Point(630,-1600));
		notesCollection.addChild(d42);
		platformCollection.addChild(new Sprite ("Platform13", "platformBlue.png", 700, -1700));
		Note c43 = new Note ("c4", "c.png", "c4.wav", "quarter");
		c43.setPosition(new Point(730,-1800));
		notesCollection.addChild(c43);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	
}
