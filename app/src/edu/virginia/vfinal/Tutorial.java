//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

package edu.virginia.vfinal;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.util.SoundManager;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.GamePad;
import edu.virginia.engine.display.Note;
import edu.virginia.engine.util.listeners.CollisionEvent;
import edu.virginia.engine.util.listeners.HookListener;
import edu.virginia.engine.util.listeners.PlayerListener;

public class Tutorial extends Game{
	/* Player variables */
	PhysicsSprite player = new PhysicsSprite("Player", "Player.png");
	Sprite crosshairs = new Sprite("Crosshairs", "crosshairs7.png");
	Sprite hookshot = new Sprite("Hookshot", "hookshot.png"); 
	Sprite deathBar = new Sprite("Death Bar", "death bar.png");
	/* Sprite collections */
	DisplayObjectContainer playerSprites = new DisplayObjectContainer("Player Sprites");
	ArrayList<String> backgroundFilePaths = new ArrayList<String>();
	DisplayObjectContainer backgroundCollection = new DisplayObjectContainer("Background Collection");
	DisplayObjectContainer foregroundCollection = new DisplayObjectContainer("Foreground Collection");
	DisplayObjectContainer platformCollection = new DisplayObjectContainer("Platform Collection");
	DisplayObjectContainer notesCollection = new DisplayObjectContainer("Notes Collection");
	//DisplayObjectContainer menu = new DisplayObjectContainer("Menu");
	/* Game variables */
	int livesLeft = 3;
	boolean gameEnded=false;
	boolean inPlay = true;
	//boolean inMenu = true;
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
	int notesToBeatStage = 1;
	int totalNotes=1;
	int missedNotes=0;
	Sprite staff = new Sprite("Staff", "staff.png");
	ArrayList<Note> collected = new ArrayList<Note>();
	SoundManager soundManager = new SoundManager();
	double gameEndedTime;
	boolean toPlay;
	String noteLength="";
	
	public Tutorial() throws IOException {
		super("Restoration of Sound", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set Player Sprite positions */
		player.setPosition(new Point(100, 600));
		crosshairs.setPivotPoint(new Point(-26, -26));
		drawCrosshairsKeyboard(crosshairs, player, hookshot);
		deathBar.setPosition(new Point(0, 900));
		staff.setPosition(new Point(200, 250));
		staff.setVisible(false);
		/* Set up backgrounds */
		this.addChild(backgroundCollection);
		setupBackgrounds();
		/* Set up sprites that will fall in the foreground */
		this.addChild(foregroundCollection);
			// Set up platforms 
			setupPlatforms();
			// Set up notes to collect
			//setupNotes();
		/* Set up hookshot */
		/* Set up player sprites as children of Game */
		setupPlayerSprites(playerSprites,
				    	   crosshairs, 
				    	   player, 
				    	   hookshot);
		//setUpMenu();
		HookListener hookListener = new HookListener(player, tInstance, hookshot);
		PlayerListener playerListener = new PlayerListener(player, tInstance, soundManager);
		this.setupHookshot(player, playerListener, hookListener, hookshot);
		setupAudio();
	}

	public static void main(String[] args) throws IOException {
		Tutorial game = new Tutorial();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		
			super.draw(g);
			/* Draw assets that are not explicity in Display Tree Hierachy */
			if(staff != null){
				staff.draw(g);
			}

		/* Draw HUD */
		g.setColor(Color.WHITE);

		if (gameEnded==true){
			// TODO: Replace with victory splash screen
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			strokeText("Sound Restored!", g, 450, 100);
			//if (tInstance.getSize()==0){
			//	this.stop();
			//}
			g.setFont(new Font("KinoMT", Font.PLAIN, 50));
			if (collected.size()==0) g.drawString("Press 1 to restart or 2 to go back to the menu", 350, 250);
		}
		g.setFont(new Font("KinoMT", Font.BOLD, 30));
		String strNotesCollected = "Notes Collected: " + notesCollected + "/" + notesToBeatStage;
		String strNotesToNextLevel = "Notes To Next Level: " + notesToNextLevel;
		//g.drawString(strNotesCollected, 1260, 50);
		strokeText(strNotesCollected, g, 1260, 50);
		strokeText(strNotesToNextLevel,g, 1200, 90);
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
		if (missedNotes>totalNotes-notesToBeatStage || (gameEnded==true && collected.size()==0)){
			if (pressedKeys.contains(49)){
				String[] args={"0"};
				try {
					BaseGame.main(args);
					soundManager.StopBGM();
					this.exitGame();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (pressedKeys.contains(50)){
				String[] args={"0"};
				try {
					Menu.main(args);
					soundManager.StopBGM();
					this.exitGame();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (inPlay){
			if (gameEnded==true){
				timeElapsed=Math.min(System.currentTimeMillis()-startTime,3000);
			}
			if (tInstance!=null){tInstance.nextFrame();}
			
			/* Update methods */
			super.update(pressedKeys, controllers);
			if (player != null && pressedKeys != null) player.update(pressedKeys, controllers);
			
			/* Update hook status */
			if (crosshairs != null)
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
					//updateHUD();
					notesCollection.removeChild(notesCollection.getCollidedNote());
					updateHUD();
				}
				
				/* Player is on the ground */
				if(player.getPosition().getY()>600){
					player.dispatchEvent(new Event(CollisionEvent.GROUND, null));
				}
			}
			if (notesCollection.collidesWithNoteSound(deathBar, tInstance)){
				Note note = notesCollection.getCollidedNote();
				note.setVisible(false);
				missedNotes++;
			}
			}
		}
		
		if (gameEnded==true){
			notesCollection.removeChildren();
			platformCollection.removeChildren();
			this.removeChild(foregroundCollection);
			}

			
				
			
	}
	
	public void updateHUD(){
		notesCollected += 1; 
		notesToNextLevel -= 1;
		if (notesToNextLevel <= 0){
			currentLevel += 1;
			swapBackground(currentLevel, backgroundFilePaths, 0);
			notesToNextLevel = notesPerLevel;
		}
			// Player has collected all notes 
			if ((notesCollected+missedNotes) >= totalNotes){
				notesToNextLevel = notesToBeatStage;
				gameEnded = true;
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
				int backgroundFallSpeed = 0;
				int newY = y + backgroundFallSpeed;
				toFallNext.setPosition(new Point(x,newY));
			}
			/* Reset timer */
			timeSinceFall.resetGameClock();
		}
		}
	}
	
	public void swapBackground(int currLevel, ArrayList<String> bfp, long timePassed){
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
		int foregroundFallSpeed = 0;
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
		backgroundFilePaths.add("grassy field0.jpg");
		// Level 1
		backgroundFilePaths.add("grassy field1.jpg");
		// Level 2
		backgroundFilePaths.add("grassy field2.jpg");
		// Level 3
		backgroundFilePaths.add("grassy field3.jpg");
		// Level 4
		backgroundFilePaths.add("grassy field4.jpg");
		// Level 5
		backgroundFilePaths.add("grassy field5.jpg");
		// Previous Background
		backgroundCollection.addChild(new Sprite ("Previous Background", "grassy field0.jpg", 0, -332));
		// Current Background
		backgroundCollection.addChild(new Sprite ("Current Background", "grassy field0.jpg", 0, -332));
	}
	
	/** Create platforms here! 
	 * @throws IOException **/
	public void setupPlatforms(){
		/* Set up display hierarchy and platforms */
		foregroundCollection.addChild(platformCollection);
		foregroundCollection.addChild(notesCollection);
		try {
			platformCollection.addChild(new Sprite ("Platform1", "platformBlue.png", 500,500));
			//Note c41 = new Note ("c4", "c.png", "c4.wav", "eighth");
			//c41.setPosition(new Point(590,300));
			//notesCollection.addChild(c41);
			platformCollection.addChild(new Sprite ("Platform2", "platformBlue.png", 800, 200));
			Note c42 = new Note ("c4", "c.png", "c4.wav", "eighth");
			c42.setPosition(new Point(890, 0));
			notesCollection.addChild(c42);
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setupAudio() throws IOException{
		/* Loads into Hash map, integer is level when music is played */
		soundManager.LoadBGM(0, "birdsound.wav");
		soundManager.LoadBGM(1, "birdsound.wav");
		soundManager.LoadBGM(2, "birdsound.wav");
		soundManager.LoadBGM(3, "birdsound.wav");
		soundManager.LoadBGM(4, "birdsound.wav");
		soundManager.LoadBGM(5, "birdsound.wav");
		soundManager.PlayBGM(0, 0);
	}
	


	
}
