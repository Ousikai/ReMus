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
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.SoundManager;
import edu.virginia.engine.util.listeners.CollisionEvent;
import edu.virginia.engine.util.listeners.HookListener;
import edu.virginia.engine.util.listeners.PlayerListener;

public class TwinkleTwinkle extends Game{
	/* Player variables */
	PhysicsSprite player = new PhysicsSprite("Player", "Player.png");
	Sprite crosshairs = new Sprite("Crosshairs", "crosshairs7.png");
	/* Sprite collections */
	ArrayList<String> backgroundFilePaths = new ArrayList<String>();
	DisplayObjectContainer backgroundCollection = new DisplayObjectContainer("Background Collection");
	DisplayObjectContainer foregroundCollection = new DisplayObjectContainer("Foreground Collection");
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
	/* Audio assets */
	SoundManager soundManager = new SoundManager();
	GameClock timeToSwapBGM = new GameClock();
	
	/* Set up game */
	public TwinkleTwinkle() throws IOException {
		super("Twinkle Twinkle Little Star", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set Player Sprite positions */
		player.setPosition(new Point(100, 500));
		crosshairs.setPivotPoint(new Point(-26, -26));
		drawCrosshairsKeyboard(crosshairs, player);
		/* Set up backgrounds */
		this.addChild(backgroundCollection);
		setupBackgrounds();
		/* Set up sprites that will fall in the foreground */
		this.addChild(foregroundCollection);
			// Set up platforms and notes
			setupPlatformsAndNotes();
		/* Set up hookshot */
		HookListener hookListener = new HookListener(player, tInstance);
		PlayerListener playerListener = new PlayerListener(player, tInstance);
		this.setupHookshot(player, playerListener, hookListener);
		/* Set up audio */
		setupAudio();
	}

	public static void main(String[] args) throws IOException {
		TwinkleTwinkle game = new TwinkleTwinkle();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		super.draw(g);
		/* Draw assets that are not explicity in Display Tree Hierachy */
		player.draw(g);
		crosshairs.draw(g);
		// Draw HUD
		if (gameLoss){
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			g.drawString("You Lose!", 500, 350);
			if (tInstance.getSize()==0){
				this.stop();
			}			
		}
		if (gameEnded==true && gameLoss==false){
			// TODO: Tween victory text
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			g.drawString("Sound Restored!", 450, 400);
			if (tInstance.getSize()==0){
				this.stop();
			}
		}
		g.setFont(new Font("KinoMT", Font.BOLD, 30));
		g.drawString("Notes Collected: " + notesCollected, 1260, 50);
		g.drawString("Notes To Next Level: " + notesToNextLevel, 1200, 90);
	}
		
	@Override
	public void update(ArrayList<Integer> pressedKeys){
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
			super.update(pressedKeys);
			player.update(pressedKeys);
			
			/* Update hook status */
			drawCrosshairsKeyboard(crosshairs, player);
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
				if (notesCollection.collidesWithNote(player)){
					updateHUD();
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
		// Increase threshold for achieve next level
		if (notesCollected >= notesToNextLevel){
			currentLevel += 1;
			swapBackground(currentLevel, backgroundFilePaths, 0);
			notesToNextLevel += notesPerLevel;
			// Player has collected all notes 
			if (notesCollected >= notesToBeatStage){
				notesToNextLevel = notesToBeatStage;
				gameEnded = true;
			}
		}	
	}
	
	public void updateBackgroundCollection(DisplayObjectContainer backgroundRoot, GameClock timeSinceFall){
		/** Change how often the background will update **/
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
		int foregroundFallSpeed = 1;
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
		
		
		platformCollection.addChild(new Sprite ("Platform1", "platformBlue.png", 50, 400));
		platformCollection.addChild(new Sprite ("Platform2", "platformBlue.png", 500, 400));
		platformCollection.addChild(new Sprite ("Platform3", "platformBlue.png", 500, 100));
		platformCollection.addChild(new Sprite ("Platform4", "platformBlue.png", 900, 100));
		platformCollection.addChild(new Sprite ("Platform5", "platformBlue.png", 1300, 100));
		platformCollection.addChild(new Sprite ("Platform5", "platformBlue.png", 1300, -150));
		platformCollection.addChild(new Sprite ("Platform5", "platformBlue.png", 900, -400));

		notesCollection.addChild(new Sprite ("d4", "d.png", 150, 150));
		notesCollection.addChild(new Sprite ("a4", "a.png", 600, 150));
		notesCollection.addChild(new Sprite ("g4", "g.png", 600, -150));
		notesCollection.addChild(new Sprite ("e4", "e.png", 1400, -400));
		notesCollection.addChild(new Sprite ("f4", "f.png", 1000, -650));
	}
	
	public void setupAudio() throws IOException{
		/* Loads into Hash map, integer is level when music is played */
		soundManager.LoadSoundEffect("Game Over", "GameOver.wav");
		/*
		soundManager.LoadBGM(0, "TwinkleTwinkle0.wav");
		soundManager.LoadBGM(1, "TwinkleTwinkle1.wav");
		soundManager.LoadBGM(2, "TwinkleTwinkle2.wav");
		soundManager.LoadBGM(3, "TwinkleTwinkle3.wav");
		soundManager.LoadBGM(4, "TwinkleTwinkle4.wav");
		soundManager.LoadBGM(5, "TwinkleTwinkle5.wav");
		*/
		soundManager.LoadBGM(0, "TempleOfTimeLevel0.wav");
		soundManager.LoadBGM(2, "TempleOfTimeLevel1.wav");
		soundManager.LoadBGM(3, "TempleOfTimeLevel2.wav");
		soundManager.LoadBGM(1, "TempleOfTimeLevel3.wav");
		soundManager.LoadBGM(4, "TempleOfTimeLevel4.wav");
		soundManager.LoadBGM(5, "TempleOfTimeLevel5.wav");
		soundManager.PlayBGM(0, 0);
	}
	
}
