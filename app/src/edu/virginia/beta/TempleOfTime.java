//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

// Notes missed decreasing when no notes have gone down yet?

package edu.virginia.beta;


import java.awt.Color;
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
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.GamePad;
import edu.virginia.engine.util.SoundManager;
import edu.virginia.engine.util.listeners.CollisionEvent;
import edu.virginia.engine.util.listeners.HookListener;
import edu.virginia.engine.util.listeners.PlayerListener;

public class TempleOfTime extends Game{
	/* Player variables */
	PhysicsSprite player = new PhysicsSprite("Player", "Player.png");
	Sprite crosshairs = new Sprite("CrosshairsRed", "crosshairsRed.png");
	Sprite hookshot = new Sprite("Hookshot", "hookshot.png"); 
	Sprite deathBar = new Sprite("Death Bar", "death bar.png");

	Sprite lossScreen = new Sprite ("Loss Screen", "End Screen Lost.jpg");
	Sprite winScreen = new Sprite ("Win Screen", "End Screen Won.jpg");
	/* Sprite collections */
	DisplayObjectContainer playerSprites = new DisplayObjectContainer("Player Sprites");
	ArrayList<String> backgroundFilePaths = new ArrayList<String>();
	DisplayObjectContainer backgroundCollection = new DisplayObjectContainer("Background Collection");
	int backgroundFallSpeed = 1;
	DisplayObjectContainer foregroundCollection = new DisplayObjectContainer("Foreground Collection");
	int foregroundFallSpeed = 2;
	DisplayObjectContainer platformCollection = new DisplayObjectContainer("Platform Collection");
	DisplayObjectContainer notesCollection = new DisplayObjectContainer("Notes Collection");
	/* Game variables */
	int livesLeft = 3;
	boolean gameEnded=false;
	boolean gameLoss=false;
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
	
	//I DONT KNOW WHAT THE TOTAL NOTES ARE
	int totalNotes=15;
	int missedNotes=0;
	ArrayList<Note> collected = new ArrayList<Note>();
	/* Audio assets */
	SoundManager soundManager = new SoundManager();
	GameClock timeToSwapBGM = new GameClock();
	
	/* Set up game */
	public TempleOfTime() throws IOException {
		super("Temple of Time", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set Player Sprite positions */
		player.setPosition(new Point(100, 500));
		player.setCrosshairsAngle(270);
		crosshairs.setPivotPoint(new Point(-26, -26));
		drawCrosshairsKeyboard(crosshairs, player, hookshot);
		/* Set up backgrounds */
		this.addChild(backgroundCollection);
		setupBackgrounds();
		/* Set up sprites that will fall in the foreground */
		this.addChild(foregroundCollection);
		// Set up platforms and notes
		setupPlatformsAndNotes();
		/* Set up player sprites as children of Game */
		setupPlayerSprites(playerSprites,
				    	   crosshairs, 
				    	   player, 
				    	   hookshot);
		foregroundCollection.addChild(playerSprites);
		/* Set up hookshot */
		HookListener hookListener = new HookListener(player, tInstance, hookshot);
		PlayerListener playerListener = new PlayerListener(player, tInstance, soundManager);
		this.setupHookshot(player, playerListener, hookListener, hookshot);
		/* Set up audio */
		setupAudio();
	}

	public static void main(String[] args) throws IOException {
		TempleOfTime game = new TempleOfTime();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		super.draw(g);
		/* Draw HUD */
		g.setColor(Color.WHITE);
		if (gameLoss){
			lossScreen.draw(g);
			g.setColor(Color.MAGENTA);
			g.setFont(new Font("KinoMT", Font.PLAIN, 40));
			//g.drawString("You have missed too many notes!", 400, 150);
			g.drawString("1: Restart    2: Menu", 1175, 710);
			
			if (tInstance.getSize()==0){
				this.stop();
			}			
			
		}
		if (gameEnded==true && gameLoss==false){
			// TODO: Replace with victory splash screen
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			winScreen.setVisible(true);
			//strokeText("Sound Restored!", g, 450, 100);
			//if (tInstance.getSize()==0){
			//	this.stop();
			//}
			g.setFont(new Font("KinoMT", Font.PLAIN, 30));
			g.setColor(Color.BLACK);
			if (collected.size()==0) g.drawString("1: Restart   2: Menu   3: Next Level", 40, 710);
		}
		g.setFont(new Font("KinoMT", Font.BOLD, 30));
		String strNotesCollected = "Notes Collected: " + notesCollected + "/" + notesToBeatStage;
		String strNotesToNextLevel = "Notes To Next Level: " + notesToNextLevel;
		String strNotesMissed = "Note Lives Left: " + (3 - missedNotes);
		//g.drawString(strNotesCollected, 1260, 50);
		strokeText(strNotesCollected, g, 1260, 50);
		strokeText(strNotesToNextLevel,g, 1200, 90);
		strokeText(strNotesMissed, g, 10, 50);
		if (missedNotes >= 3) {
			g.setColor(Color.RED);
			g.drawString("Note Lives Left: 0", 10, 50);
			
		}
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
					TempleOfTime.main(args);
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
					this.exitGame();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (gameEnded==true && collected.size()==0){
				if (pressedKeys.contains(51)){
					String[] args={"0"};
					try {
						//NOT BRAMBLE WHAT'S AFTER THIS???
						BrambleBlast.main(args);
						this.exitGame();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
			player.update(pressedKeys, controllers);
			
			/* Update hook status */
			drawCrosshairsKeyboard(crosshairs, player, hookshot);
			platformCollection.hookablePlatform(crosshairs, player);
		
			/* Have foreground and background elements fall */
			updateBackgroundCollection(backgroundCollection, backgroundInterlude);
			updateForegroundCollection(foregroundCollection, foregroundInterlude);
		
			/* Collisions only occur if player can be interacted with */
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
				}
				
				if (notesCollection.collidesWithNoteSound(deathBar, tInstance)){
					Note note = notesCollection.getCollidedNote();
					note.setVisible(false);
					if (missedNotes >= 3) {
						missedNotes = 3;
					} else {
					missedNotes++;
					}
				}
				
				/* If player falls to the bottom of the screen, lose */
				if(player.getPosition().getY()>750){
					gameEnded = true;
					gameLoss = true;
					soundManager.StopBGM();
					soundManager.PlaySoundEffect("Game Over");
					player.setVisible(false);
					crosshairs.setVisible(false);
				}
			}
		}
	}
	
	public void updateHUD(){
		notesCollected += 1; 
		notesToNextLevel -= 1;
		// Increase threshold for achieve next level
		if (notesToNextLevel <= 0){
			currentLevel += 1;
			swapBackground(currentLevel, backgroundFilePaths, 0);
			notesToNextLevel = notesPerLevel;
			// Player has collected all notes 
			if (notesCollected >= notesToBeatStage){
				notesToNextLevel = notesToBeatStage;
				gameEnded = true;
				player.setInPlay(false);
			}
		}	
	}
	
	public void updateBackgroundCollection(DisplayObjectContainer backgroundRoot, GameClock timeSinceFall){
		/** Change how often the background will update **/
		int timeBetweenFall = 300;
		if (timeSinceFall.getElapsedTime() > timeBetweenFall){
			/* Have your children fall */
			for (int i = 0; i < backgroundRoot.numberOfChildren(); i++){
				DisplayObject toFallNext = backgroundRoot.getChildAtIndex(i);
				int x = (int) toFallNext.getPosition().getX();
				int y = (int) toFallNext.getPosition().getY();
				/** Change how much the background will fall **/
				int newY = y + backgroundFallSpeed;
				toFallNext.setPosition(new Point(x,newY));
			}
			/* Reset timer */
			timeSinceFall.resetGameClock();
		}
	}
	
	public void swapBackground(int currLevel, ArrayList<String> bfp, long timePassed){
		// Swap the background to the next image
		DisplayObject currBackground = backgroundCollection.getChildAtIndex(0);
		currBackground.setImage(bfp.get(currLevel));
		currBackground.setAlpha(0);
		// Tween the backgrounds
		Tween fadeIn = new Tween (currBackground, null, null);
		tInstance.add(fadeIn);
		int timeTweening = 500;
		fadeIn.animate(TweenableParams.ALPHA, 0, 1, timeTweening);
		//Update the BGM
		soundManager.updateBGM(currLevel, timeToSwapBGM.getElapsedTime());
		/* Have the previous background fade out 
		int prevLevel = currLevel - 1;
		DisplayObject prevBackground = backgroundCollection.getChildAtIndex(0);
		prevBackground.setImage(bfp.get(prevLevel));
		prevBackground.setAlpha(1);
		prevBackground.setVisible(false);
		Tween fadeOut = new Tween (prevBackground, null, null);
		tInstance.add(fadeOut);
		fadeOut.animate(TweenableParams.ALPHA, 1, 0, timeTweening);
		*/
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
		int newY = y + 2;
		toFallCurrent.setPosition(new Point(x,newY));
		for (int i = 0; i < toFallCurrent.numberOfChildren(); i++){
			DisplayObject toFallNext = toFallCurrent.getChildAtIndex(i);
			foregroundFallSpeed1((DisplayObjectContainer)toFallNext);
		}	
	}
	
	/** Set backgrounds here! **/
	public void setupBackgrounds(){
		// Level 0
		backgroundFilePaths.add("TempleOfTimeLevel0.png");
		// Level 1
		backgroundFilePaths.add("TempleOfTimeLevel1.png");
		// Level 2
		backgroundFilePaths.add("TempleOfTimeLevel2.png");
		// Level 3
		backgroundFilePaths.add("TempleOfTimeLevel3.png");
		// Level 4
		backgroundFilePaths.add("TempleOfTimeLevel4.png");
		// Level 5
		backgroundFilePaths.add("TempleOfTimeLevel5.png");
		/* Previous Background
		backgroundCollection.addChild(new Sprite ("Previous Background", "TempleOfTimeLevel1.png", 0, -200));*/
		// Current Background
		backgroundCollection.addChild(new Sprite ("Current Background", "TempleOfTimeLevel0.png", 0, -200));
	}
	
	/** Create platforms here! **/
	public void setupPlatformsAndNotes(){
		/* Set up display hierarchy and platforms */
		foregroundCollection.addChild(platformCollection);
		foregroundCollection.addChild(notesCollection);
		platformCollection.addChild(new Sprite ("PlatformBase1", "platformBlue.png", 0, 600));
		platformCollection.addChild(new Sprite ("PlatformBase2", "platformBlue.png", 250, 600));
		platformCollection.addChild(new Sprite ("PlatformBase3", "platformBlue.png", 500, 600));
		platformCollection.addChild(new Sprite ("PlatformBase4", "platformBlue.png", 750, 600));
		platformCollection.addChild(new Sprite ("PlatformBase5", "platformBlue.png", 1000, 600));
		platformCollection.addChild(new Sprite ("PlatformBase6", "platformBlue.png", 1250, 600));
		platformCollection.addChild(new Sprite ("PlatformBase7", "platformBlue.png", 1500, 600));
		platformCollection.addChild(new Sprite ("Platform1", "platformBlue.png", 100,150));
		platformCollection.addChild(new Sprite ("Platform2", "platformBlue.png", 600, 450));
		platformCollection.addChild(new Sprite ("Platform2.5", "platformBlue.png", 300, 400));
		platformCollection.addChild(new Sprite ("Platform3", "platformBlue.png", 1200, 300));
		platformCollection.addChild(new Sprite ("Platform4", "platformBlue.png", 600, 100));
		platformCollection.addChild(new Sprite ("Platform5", "platformBlue.png", 700, -100));
		platformCollection.addChild(new Sprite ("Platform6", "platformBlue.png", 300, -300));
		platformCollection.addChild(new Sprite ("Platform7", "platformBlue.png", 700, -500));
		platformCollection.addChild(new Sprite ("Platform8", "platformBlue.png", 200, -700));
		platformCollection.addChild(new Sprite ("Platform9", "platformBlue.png", 100,-900));
		platformCollection.addChild(new Sprite ("Platform10", "platformBlue.png", 600, -1100));
		platformCollection.addChild(new Sprite ("Platform11", "platformBlue.png", 1200, -1300));
		platformCollection.addChild(new Sprite ("Platform12", "platformBlue.png", 600, -1500));
		platformCollection.addChild(new Sprite ("Platform13", "platformBlue.png", 700, -1700));
		platformCollection.addChild(new Sprite ("Platform14", "platformBlue.png", 300, -1900));
		platformCollection.addChild(new Sprite ("Platform15", "platformBlue.png", 700, -2100));
		platformCollection.addChild(new Sprite ("Platform16", "platformBlue.png", 200, -2300));
		try {
			notesCollection.addChild(new Note ("d4", "d.png", "d4.wav", "eighth", 700, 250));
			notesCollection.addChild(new Note ("a4","a.png", "a4.wav", "eighth", 200, -100));
			notesCollection.addChild(new Note ("g4","g.png", "g4.wav", "eighth", 800, -300));
			notesCollection.addChild(new Note ("e4", "e.png", "e4.wav", "eighth", 800, -750));
			notesCollection.addChild(new Note ("f4", "f.png", "f4.wav", "eighth", 700, -1300));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void setupAudio() throws IOException{
		/* Loads into Hash map, integer is level when music is played */
		soundManager.LoadSoundEffect("Game Over", "GameOver.wav");
		soundManager.LoadBGM(0, "TempleOfTimeLevel0.wav");
		soundManager.LoadBGM(2, "TempleOfTimeLevel1.wav");
		soundManager.LoadBGM(3, "TempleOfTimeLevel2.wav");
		soundManager.LoadBGM(1, "TempleOfTimeLevel3.wav");
		soundManager.LoadBGM(4, "TempleOfTimeLevel4.wav");
		soundManager.LoadBGM(5, "TempleOfTimeLevel5.wav");
		soundManager.PlayBGM(0, 0);
	}
	
}
