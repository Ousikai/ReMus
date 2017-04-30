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
import edu.virginia.engine.display.Platform;
import edu.virginia.engine.display.PlatformType;
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

public class LisaRiccia extends Game{
	/* Player variables */
	PhysicsSprite player = new PhysicsSprite("Player", "Player.png");
	Sprite crosshairs = new Sprite("CrosshairsRed", "crosshairsRed.png");
	Sprite hookshot = new Sprite("Hookshot", "hookshot.png"); 
	Sprite staff = new Sprite("Staff", "staff.png");
	Sprite lossScreen = new Sprite ("Loss Screen", "End Screen Lost.jpg");
	Sprite winScreen = new Sprite ("Win Screen", "End Screen Won.jpg");
	ArrayList<Note> collected = new ArrayList<Note>();
	/* Sprite collections */
	DisplayObjectContainer playerSprites = new DisplayObjectContainer("Player Sprites");
	ArrayList<String> backgroundFilePaths = new ArrayList<String>();
	DisplayObjectContainer backgroundCollection = new DisplayObjectContainer("Background Collection");
	int backgroundFallRate = 200;
	DisplayObjectContainer foregroundCollection = new DisplayObjectContainer("Foreground Collection");
	int foregroundFallSpeed = 4; //how often platforms should move
	DisplayObjectContainer platformCollection = new DisplayObjectContainer("Platform Collection");
	DisplayObjectContainer notesCollection = new DisplayObjectContainer("Notes Collection");
	/* Game variables */
	int livesLeft = 3;
	boolean gameEnded=false;
	boolean gameLoss=false;
	boolean inPlay = true;
	boolean drawStationaryPlatforms = false;
	boolean drawNotes = false;
	TweenJuggler tInstance;
	double startTime=System.currentTimeMillis();
	GameClock timeElapsed = new GameClock();
	GameClock backgroundInterlude = new GameClock();
	GameClock foregroundInterlude = new GameClock();
	/* HUD variables */
	int notesCollected = 0;
	int currentLevel = 0;
	int notesToNextLevel = 3;
	int notesPerLevel = 3;
	int notesToBeatStage = 15;
	int missedNotes=0;
	int totalNotes=15;
	private long gameEndedTime;
	private boolean toPlay;
	/* Audio assets */
	SoundManager soundManager = new SoundManager();
	GameClock timeToSwapBGM = new GameClock();
	
	/* Set up game */
	public LisaRiccia() throws IOException {
		super("Lisa RICCIA", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set Player Sprite positions */
		player.setPosition(new Point(100, 500));
		player.setCrosshairsAngle(270);
		player.setJumpHeight(20);
		crosshairs.setPivotPoint(new Point(-26, -26));
		winScreen.setVisible(false);
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
		staff.setPosition(new Point(200, 250));
		staff.setVisible(false);
	}

	public static void main(String[] args) throws IOException {
		LisaRiccia game = new LisaRiccia();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		super.draw(g);
		/* Draw HUD */
		g.setColor(Color.WHITE);
		if (winScreen != null){
			winScreen.draw(g);
		}
		if (gameLoss){
			// TODO: Replace with victory splash screen
			lossScreen.draw(g);
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			g.setColor(Color.MAGENTA);
			g.setFont(new Font("KinoMT", Font.PLAIN, 40));
			//g.drawString("You have missed too many notes!", 400, 150);
			g.drawString("1: Restart    2: Menu", 1175, 710);
		}
		if (gameEnded==true && gameLoss==false){
			// TODO: Replace with victory splash screen
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			winScreen.setVisible(true);
			if(staff != null){
				staff.draw(g);
			}
		
			staff.setVisible(true);
			for (Note note : collected){
				note.setAlpha(1);
				note.draw(g);
			}
			//strokeText("Sound Restored!", g, 450, 400);
			if (collected.size()==0) g.drawString("1: Restart   2: Menu   3: Next Level", 40, 710);
		}
		g.setFont(new Font("KinoMT", Font.BOLD, 30));
		String strNotesCollected = "Notes Collected: " + notesCollected + "/" + notesToBeatStage;
		String strNotesToNextLevel = "Notes To Next Level: " + notesToNextLevel;
		String strNotesMissed = "Note Lives Left: " + (3 - missedNotes);
		//g.drawString(strNotesCollected, 1260, 50);
		strokeText(strNotesCollected, g, 1260, 50);
		strokeText(strNotesToNextLevel,g, 1200, 90);
		//strokeText(strNotesMissed, g, 10, 50);
//		if (missedNotes >= 3) {
//			g.setColor(Color.RED);
//			g.drawString("Note Lives Left: 0", 10, 50);
//			s
//		}
		
		String strTimeElapsed="";
		if (!gameEnded==true){
			strTimeElapsed = "Time Elapsed: " + timeElapsed.getElapsedTime();
		}
		strokeText(strTimeElapsed, g, 1200, 130);
		
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
		if (missedNotes>totalNotes-notesToBeatStage || (gameEnded==true && collected.size()==0) || gameLoss){
			if (pressedKeys.contains(49)){
				String[] args={"0"};
				try {
					LisaRiccia.main(args);
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
						TheTower.main(args);
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
				//TODO: Something 
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
					Note note = notesCollection.getCollidedNote();
					collected.add(note);
				
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
				
					collected.get(i).setPosition(new Point((250+(i+1)*1100/collected.size()), height));
					collected.get(i).setScaleX(0.4);
					collected.get(i).setScaleY(0.4);
			
				}	
			}
		}	
	}
	
	public void updateBackgroundCollection(DisplayObjectContainer backgroundRoot, GameClock timeSinceFall){
		/** Change how often the background will update **/
		if (timeSinceFall.getElapsedTime() > backgroundFallRate){
			/* Have your children fall */	
			for (int i = 0; i < backgroundRoot.numberOfChildren(); i++){
				DisplayObject toFallNext = backgroundRoot.getChildAtIndex(i);
				int x = (int) toFallNext.getPosition().getX();
				int y = (int) toFallNext.getPosition().getY();
				int newY = y + 1;
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
		//if (timeSinceFall.getElapsedTime() > foregroundFallRate){
			// Have foreground elements fall 
			if (timeElapsed.getElapsedTime() > 22000){
				for (int i = 0; i < foregroundRoot.numberOfChildren(); i++){
					DisplayObject toFallNext = foregroundRoot.getChildAtIndex(i);
					foregroundScrollDown((DisplayObjectContainer)toFallNext);
				}
				if (!drawNotes){
					drawNotes = true;
					/* Collectable notes */  
					try {
						notesCollection.addChild(new Note ("a4", "a.png", "a4.wav", "eighth", 700, -500));
						notesCollection.addChild(new Note ("a4", "a.png", "a4.wav", "eighth", 200, -750));
						notesCollection.addChild(new Note ("b4", "b.png", "b4.wav", "eighth", 1040, -1000));
						notesCollection.addChild(new Note ("b4", "b.png", "b4.wav", "eighth", 500, -1250));
						notesCollection.addChild(new Note ("c4", "c.png", "c4.wav", "eighth", 240, -1500));
						notesCollection.addChild(new Note ("c4", "c.png", "c4.wav", "eighth", 1300, -1750));
						notesCollection.addChild(new Note ("d4", "d.png", "d4.wav", "eighth", 1300, -2000));
						notesCollection.addChild(new Note ("d4", "d.png", "d4.wav", "eighth", 500, -2250));
						notesCollection.addChild(new Note ("e4", "e.png", "e4.wav", "eighth", 700, -2500)); 
						notesCollection.addChild(new Note ("e4", "e.png", "e4.wav", "eighth", 200, -2750));
						notesCollection.addChild(new Note ("f4", "f.png", "f4.wav", "eighth", 100, -3000)); 
						notesCollection.addChild(new Note ("f4", "f.png", "f4.wav", "eighth", 500, -3250)); 
						notesCollection.addChild(new Note ("g4", "g.png", "g4.wav", "eighth", 700, -3500));
						notesCollection.addChild(new Note ("g4", "g.png", "g4.wav", "eighth", 1000, -3750));
						notesCollection.addChild(new Note ("b4", "b.png", "b4.wav", "eighth", 250, -4000));
						notesCollection.addChild(new Note ("b4", "b.png", "b4.wav", "eighth", 567, -4250));
						notesCollection.addChild(new Note ("c4", "c.png", "c4.wav", "eighth", 100, -4500));
						notesCollection.addChild(new Note ("c4", "c.png", "c4.wav", "eighth", 1000, -4750));
						notesCollection.addChild(new Note ("d4", "d.png", "d4.wav", "eighth", 1300, -5000));
						notesCollection.addChild(new Note ("d4", "d.png", "d4.wav", "eighth", 1000, -5250));
						notesCollection.addChild(new Note ("e4", "e.png", "e4.wav", "eighth", 700, -5500));
						notesCollection.addChild(new Note ("e4", "e.png", "e4.wav", "eighth", 300, -5750));
						notesCollection.addChild(new Note ("f4", "f.png", "f4.wav", "eighth", 100, -6000));
						notesCollection.addChild(new Note ("f4", "f.png", "f4.wav", "eighth", 700, -6000));
						notesCollection.addChild(new Note ("g4", "g.png", "g4.wav", "eighth", 900, -6500));
						notesCollection.addChild(new Note ("g4", "g.png", "g4.wav", "eighth", 900, -6500));
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				
			} 
			else if (timeElapsed.getElapsedTime() > 20000){
				//Do nothing, take a break :)
				if (!drawStationaryPlatforms){
					drawStationaryPlatforms = true;	
					drawStationaryPlatforms();
				}
			}
			else {
				for (int i = 0; i < foregroundRoot.numberOfChildren(); i++){
					DisplayObject toFallNext = foregroundRoot.getChildAtIndex(i);
					foregroundScrollDown((DisplayObjectContainer)toFallNext);
				}
			} 
			// Reset timer 
			timeSinceFall.resetGameClock();
		//}
	}
	
	public void foregroundScrollDown(DisplayObjectContainer toFallCurrent){
		/** Change how much the foreground will fall **/
		int x = (int) toFallCurrent.getPosition().getX();
		int y = (int) toFallCurrent.getPosition().getY();
		int newY = y + foregroundFallSpeed;
		if (toFallCurrent.getId().equals(PlatformType.STATIONARY)){
			newY = y;
		}
		toFallCurrent.setPosition(new Point(x,newY));
		for (int i = 0; i < toFallCurrent.numberOfChildren(); i++){
			DisplayObject toFallNext = toFallCurrent.getChildAtIndex(i);
			foregroundScrollDown((DisplayObjectContainer)toFallNext);
		}	
	}	
	
	public void foregroundScrollDownFAST(DisplayObjectContainer toFallCurrent){
		/** Change how much the foreground will fall **/
		int x = (int) toFallCurrent.getPosition().getX();
		int y = (int) toFallCurrent.getPosition().getY();
		int newY = y + 8;
		if (toFallCurrent.getId().equals(PlatformType.STATIONARY)){
			newY = y;
		}
		toFallCurrent.setPosition(new Point(x,newY));
		for (int i = 0; i < toFallCurrent.numberOfChildren(); i++){
			DisplayObject toFallNext = toFallCurrent.getChildAtIndex(i);
			foregroundScrollDownFAST((DisplayObjectContainer)toFallNext);
		}	
	}	
	
	/** Set backgrounds here! **/
	public void setupBackgrounds(){
		// Level 0
		backgroundFilePaths.add("LisaRicciaLevel5.jpg");
		// Level 1
		backgroundFilePaths.add("LisaRicciaLevel5.jpg");
		// Level 2
		backgroundFilePaths.add("LisaRicciaLevel5.jpg");
		// Level 3
		backgroundFilePaths.add("LisaRicciaLevel5.jpg");
		// Level 4
		backgroundFilePaths.add("LisaRicciaLevel5.jpg");
		// Level 5
		backgroundFilePaths.add("LisaRicciaLevel5.jpg");
		/* Previous Background
		backgroundCollection.addChild(new Sprite ("Previous Background", "TempleOfTimeLevel1.png", 0, -200));*/
		// Current Background
		backgroundCollection.addChild(new Sprite ("Current Background", "LisaRicciaLevel5.jpg", -300, -600));
	}
	
	/** Create platforms here! 
	 * @throws IOException **/
	public void setupPlatformsAndNotes() throws IOException{
		/* Set up display hierarchy and platforms */
		foregroundCollection.addChild(platformCollection);
		foregroundCollection.addChild(notesCollection);
		/* Base platform */
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 0, 600));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 250, 600));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 500, 600));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 750, 600));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 1000, 600));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 1250, 600));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 1500, 600));
		/* Level begins */
		platformCollection.addChild(new Platform(PlatformType.HANG, 100, 400));
		platformCollection.addChild(new Platform(PlatformType.HANG, 300, 200));
		platformCollection.addChild(new Platform(PlatformType.HANG, 500, 50));
		platformCollection.addChild(new Platform(PlatformType.HANG, 700, -50));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 700, -300));
		//Divergence
		platformCollection.addChild(new Platform(PlatformType.HANG, 500, -500));
		platformCollection.addChild(new Platform(PlatformType.HANG, 900, -500));
		platformCollection.addChild(new Platform(PlatformType.HANG, 300, -700));
		platformCollection.addChild(new Platform(PlatformType.HANG, 1100, -700));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 100, -900));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 1300, -900));
		platformCollection.addChild(new Platform(PlatformType.HANG, 100, -1100));
		platformCollection.addChild(new Platform(PlatformType.HANG, 1300, -1100));
		platformCollection.addChild(new Platform(PlatformType.HANG, 100, -1300));
		platformCollection.addChild(new Platform(PlatformType.HANG, 1300, -1300));
		platformCollection.addChild(new Platform(PlatformType.HANG, 100, -1500));
		platformCollection.addChild(new Platform(PlatformType.HANG, 1300, -1500));
		platformCollection.addChild(new Platform(PlatformType.HANG, 300, -1700));
		platformCollection.addChild(new Platform(PlatformType.HANG, 1100, -1700));
		platformCollection.addChild(new Platform(PlatformType.HANG, 500, -1900));
		platformCollection.addChild(new Platform(PlatformType.HANG, 900, -1900));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 700, -2100));
		platformCollection.addChild(new Platform(PlatformType.HANG, 700, -2300));
		platformCollection.addChild(new Platform(PlatformType.HANG, 500, -2500));
		platformCollection.addChild(new Platform(PlatformType.HANG, 300, -2700));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 100, -2900));
		platformCollection.addChild(new Platform(PlatformType.HANG, 300, -3100));
		platformCollection.addChild(new Platform(PlatformType.HANG, 500, -3300));
		platformCollection.addChild(new Platform(PlatformType.HANG, 700, -3500));
		platformCollection.addChild(new Platform(PlatformType.HANG, 700, -3700));
		//Pause at 19000 ms
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 0, -3900));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 250, -3900));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 500, -3900));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 750, -3900));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 1000, -3900));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 1250, -3900));
		platformCollection.addChild(new Platform (PlatformType.DEFAULT, 1500, -3900));
		
		/** Special platforms **/
		/*
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 1300, 400));
		platformCollection.addChild(new Platform(PlatformType.HANG, 300, 300));
		platformCollection.addChild(new Platform(PlatformType.BOUNCY, 1000, 200));
		platformCollection.addChild(new Platform(PlatformType.INVISIBLE, 500, 400, tInstance, 1000));
		platformCollection.addChild(new Platform(PlatformType.HORIZONTAL, 300, 100, 2, 150));
		platformCollection.addChild(new Platform(PlatformType.VERTICAL, 800, 400, 2, 150));
		*/
		//TODO: Remove debug platform
		//platformCollection.addChild(new Platform(PlatformType.STATIONARY, -100, 600));
	}
	
	public void setupAudio() throws IOException{
		/* Loads into Hash map, integer is level when music is played */
		soundManager.LoadSoundEffect("Game Over", "GameOver.wav");
		soundManager.LoadSoundEffect("Bounce", "Bounce.wav");
		soundManager.LoadBGM(0, "LisaRicciaLevel5.wav");
		soundManager.LoadBGM(1, "LisaRicciaLevel5.wav");
		soundManager.LoadBGM(2, "LisaRicciaLevel5.wav");
		soundManager.LoadBGM(3, "LisaRicciaLevel5.wav");
		soundManager.LoadBGM(4, "LisaRicciaLevel5.wav");
		soundManager.LoadBGM(5, "LisaRicciaLevel5.wav");
		soundManager.PlayBGM(0, 0);
	}
	
	private void drawStationaryPlatforms(){
		Platform platform1 = new Platform(PlatformType.STATIONARY, 0, 600);
		Platform platform2 = new Platform(PlatformType.STATIONARY, 675, 600);
		Platform platform3 = new Platform(PlatformType.STATIONARY, 1350, 600);
		Platform platform4 = new Platform(PlatformType.STATIONARY, 350, 400);
		Platform platform5 = new Platform(PlatformType.STATIONARY, 1000, 400);
		platform1.setAlpha(0);
		platform2.setAlpha(0);
		platform3.setAlpha(0);
		platform4.setAlpha(0);
		platform5.setAlpha(0);
		platformCollection.addChild(platform1);
		platformCollection.addChild(platform2);
		platformCollection.addChild(platform3);	
		platformCollection.addChild(platform4);	
		platformCollection.addChild(platform5);	
		Tween fadeIn1 = new Tween (platform1, null, null);
		Tween fadeIn2 = new Tween (platform2, null, null);
		Tween fadeIn3 = new Tween (platform3, null, null);
		Tween fadeIn4 = new Tween (platform4, null, null);
		Tween fadeIn5 = new Tween (platform5, null, null);
		fadeIn1.animate(TweenableParams.ALPHA, 0, 1, 1000);
		fadeIn2.animate(TweenableParams.ALPHA, 0, 1, 1000);
		fadeIn3.animate(TweenableParams.ALPHA, 0, 1, 1000);
		fadeIn4.animate(TweenableParams.ALPHA, 0, 1, 1000);
		fadeIn5.animate(TweenableParams.ALPHA, 0, 1, 1000);
		tInstance.add(fadeIn1);
		tInstance.add(fadeIn2);
		tInstance.add(fadeIn3);		
		tInstance.add(fadeIn4);		
		tInstance.add(fadeIn5);			
	}
}
