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

public class BrambleBlast extends Game{
	/* Player variables */
	PhysicsSprite player = new PhysicsSprite("Player", "Player.png");
	Sprite crosshairs = new Sprite("CrosshairsRed", "crosshairsRed.png");
	Sprite hookshot = new Sprite("Hookshot", "hookshot.png"); 
	Sprite staff = new Sprite("Staff", "staff.png");
	Sprite deathBar = new Sprite("Death Bar", "death bar.png");
	Sprite lossScreen = new Sprite ("Loss Screen", "End Screen Lost.jpg");
	Sprite winScreen = new Sprite ("Win Screen", "End Screen Won.jpg");
	ArrayList<Note> collected = new ArrayList<Note>();
	/* Sprite collections */
	DisplayObjectContainer playerSprites = new DisplayObjectContainer("Player Sprites");
	ArrayList<String> backgroundFilePaths = new ArrayList<String>();
	DisplayObjectContainer backgroundCollection = new DisplayObjectContainer("Background Collection");
	int backgroundFallRate = 200;
	DisplayObjectContainer foregroundCollection = new DisplayObjectContainer("Foreground Collection");
	int foregroundFallRate = 15; //how often platforms should move
	DisplayObjectContainer platformCollection = new DisplayObjectContainer("Platform Collection");
	DisplayObjectContainer notesCollection = new DisplayObjectContainer("Notes Collection");
	/* Game variables */
	int livesLeft = 3;
	boolean gameEnded=false;
	boolean gameLoss=false;
	boolean inPlay = true;
	TweenJuggler tInstance;
	double startTime=System.currentTimeMillis();
	GameClock timeElapsed = new GameClock();
	GameClock backgroundInterlude = new GameClock();
	GameClock foregroundInterlude = new GameClock();
	/* HUD variables */
	int notesCollected = 0;
	int currentLevel = 0;
	int notesToNextLevel = 2;
	int notesPerLevel = 2;
	int notesToBeatStage = 10;
	int totalNotes=10;
	int missedNotes=0;
	/* Audio assets */
	SoundManager soundManager = new SoundManager();
	GameClock timeToSwapBGM = new GameClock();
	private long gameEndedTime;
	private boolean toPlay;
	
	/* Set up game */
	public BrambleBlast() throws IOException {
		super("Bramble Blast", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set Player Sprite positions */
		player.setPosition(new Point(100, 500));
		player.setCrosshairsAngle(270);
		crosshairs.setPivotPoint(new Point(-26, -26));
		deathBar.setPosition(new Point(0, 900));
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
		BrambleBlast game = new BrambleBlast();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
//		g.setColor(Color.WHITE);
//		if (missedNotes>totalNotes-notesToBeatStage){
//			//g.setFont(new Font("KinoMT", Font.PLAIN, 50));
//			
//			lossScreen.draw(g);
//			g.setColor(Color.MAGENTA);
//			g.setFont(new Font("KinoMT", Font.PLAIN, 40));
//			//g.drawString("You have missed too many notes!", 400, 150);
//			g.drawString("1: Restart    2: Menu", 1175, 710);
//			
//		}
//		if (gameEnded==true){
//			// TODO: Replace with victory splash screen
//			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
//			winScreen.setVisible(true);
//			//strokeText("Sound Restored!", g, 450, 100);
//			//if (tInstance.getSize()==0){
//			//	this.stop();
//			//}
//			g.setFont(new Font("KinoMT", Font.PLAIN, 30));
//			g.setColor(Color.BLACK);
//			if (collected.size()==0) g.drawString("1: Restart   2: Menu   3: Next Level", 40, 710);
//		}
//		g.setFont(new Font("KinoMT", Font.BOLD, 30));
//		String strNotesCollected = "Notes Collected: " + notesCollected + "/" + notesToBeatStage;
//		String strNotesToNextLevel = "Notes To Next Level: " + notesToNextLevel;
//		String strNotesMissed = "Note Lives Left: " + (3 - missedNotes);
//		//g.drawString(strNotesCollected, 1260, 50);
//		strokeText(strNotesCollected, g, 1260, 50);
//		strokeText(strNotesToNextLevel,g, 1200, 90);
//		strokeText(strNotesMissed, g, 10, 50);
//		if (missedNotes >= 3) {
//			g.setColor(Color.RED);
//			g.drawString("Note Lives Left: 0", 10, 50);
//			
//		}
		
		
		
		/* Draw all assets */
		super.draw(g);
		/* Draw HUD */
		if (winScreen != null){
			winScreen.draw(g);
		}
		g.setColor(Color.WHITE);
		if (gameLoss){
			// TODO: Replace with victory splash screen
			g.setFont(new Font("KinoMT", Font.PLAIN, 50));
			lossScreen.draw(g);
			//g.drawString("You Lose!", 550, 350);
			g.setColor(Color.MAGENTA);
			g.setFont(new Font("KinoMT", Font.PLAIN, 40));
			//g.drawString("You have missed too many notes!", 400, 150);
			g.drawString("1: Restart    2: Menu", 1175, 710);	
		}

		if (gameEnded==true && gameLoss==false){
			// TODO: Replace with victory splash screen
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			winScreen.setVisible(true);
			//strokeText("Sound Restored!", g, 450, 400);
			g.setFont(new Font("KinoMT", Font.PLAIN, 30));
			if (collected.size()==0) g.drawString("1: Restart   2: Menu   3: Next Level", 40, 710);
			if(staff != null){
				staff.draw(g);
			}
		
			staff.setVisible(true);
			for (Note note : collected){
				note.setAlpha(1);
				note.draw(g);
			}
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
		if (missedNotes>totalNotes-notesToBeatStage || (gameEnded==true && collected.size()==0) || gameLoss){
			if (pressedKeys.contains(49)){
				String[] args={"0"};
				try {
					BrambleBlast.main(args);
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
						LisaRiccia.main(args);
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
				if (tInstance.getSize()==0){
					if (collected.size()>0 ){
						Note note =collected.get(collected.size()-1);
						note.setVisible(true);
						note.setAlpha(1);
						int height=300;
						if (note.getId().equals("c4") || note.getId().equals("c4q")) height=384;
						else if (note.getId().equals("d4") || note.getId().equals("d4q")) height=370;
						else if (note.getId().equals("e4") || note.getId().equals("e4q")) height=356;
						else if (note.getId().equals("f4") || note.getId().equals("f4q")) height=342;
						else if (note.getId().equals("g4") || note.getId().equals("g4q")) height=328;
						else if (note.getId().equals("a5") || note.getId().equals("a5q")) height=314;
						else if (note.getId().equals("b5") || note.getId().equals("b5q")) height=300;
						//note.setPosition(new Point((250+(totalNotes-missedNotes-1) * 1100/5), height));
						note.setPosition(new Point((int)(note.getPosition().getX()), height));
					}
			}
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
		}
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
	
	public void updateBackgroundCollection(DisplayObjectContainer backgroundRoot, GameClock timeSinceFall){
		/** Change how often the background will update **/
		if (timeSinceFall.getElapsedTime() > backgroundFallRate){
			/* Have your children fall */	
			if (timeElapsed.getElapsedTime() > 36000){
				for (int i = 0; i < backgroundRoot.numberOfChildren(); i++){
					DisplayObject toFallNext = backgroundRoot.getChildAtIndex(i);
					int x = (int) toFallNext.getPosition().getX();
					int y = (int) toFallNext.getPosition().getY();
					int newX = x - 1;
					toFallNext.setPosition(new Point(newX,y));
				}
			} else {
				for (int i = 0; i < backgroundRoot.numberOfChildren(); i++){
					DisplayObject toFallNext = backgroundRoot.getChildAtIndex(i);
					int x = (int) toFallNext.getPosition().getX();
					int y = (int) toFallNext.getPosition().getY();
					int newY = y + 1;
					toFallNext.setPosition(new Point(x,newY));
				}
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
		if (timeSinceFall.getElapsedTime() > foregroundFallRate){
			// Have foreground elements fall 
			for (int i = 0; i < foregroundRoot.numberOfChildren(); i++){
				DisplayObject toFallNext = foregroundRoot.getChildAtIndex(i);
				if (timeElapsed.getElapsedTime() > 36000){
					foregroundScrollRight((DisplayObjectContainer)toFallNext);
				} else{
					foregroundScrollDown((DisplayObjectContainer)toFallNext);
				}
			}
			// Reset timer 
			timeSinceFall.resetGameClock();
		}
	}
	
	public void foregroundScrollDown(DisplayObjectContainer toFallCurrent){
		/** Change how much the foreground will fall **/
		int x = (int) toFallCurrent.getPosition().getX();
		int y = (int) toFallCurrent.getPosition().getY();
		int newY = y + 1;
		toFallCurrent.setPosition(new Point(x,newY));
		for (int i = 0; i < toFallCurrent.numberOfChildren(); i++){
			DisplayObject toFallNext = toFallCurrent.getChildAtIndex(i);
			foregroundScrollDown((DisplayObjectContainer)toFallNext);
		}	
	}	
	
	public void foregroundScrollRight(DisplayObjectContainer toFallCurrent){
		/** Change how much the foreground will fall **/
		int x = (int) toFallCurrent.getPosition().getX();
		int y = (int) toFallCurrent.getPosition().getY();
		int newX = x - 2;
		toFallCurrent.setPosition(new Point(newX,y));
		for (int i = 0; i < toFallCurrent.numberOfChildren(); i++){
			DisplayObject toFallNext = toFallCurrent.getChildAtIndex(i);
			foregroundScrollRight((DisplayObjectContainer)toFallNext);
		}	
	}	
	
	/** Set backgrounds here! **/
	public void setupBackgrounds(){
		// Level 0
		backgroundFilePaths.add("BrambleBlastLevel5.jpg");
		// Level 1
		backgroundFilePaths.add("BrambleBlastLevel5.jpg");
		// Level 2
		backgroundFilePaths.add("BrambleBlastLevel5.jpg");
		// Level 3
		backgroundFilePaths.add("BrambleBlastLevel5.jpg");
		// Level 4
		backgroundFilePaths.add("BrambleBlastLevel5.jpg");
		// Level 5
		backgroundFilePaths.add("BrambleBlastLevel5.jpg");
		/* Previous Background
		backgroundCollection.addChild(new Sprite ("Previous Background", "TempleOfTimeLevel1.png", 0, -200));*/
		// Current Background
		backgroundCollection.addChild(new Sprite ("Current Background", "BrambleBlastLevel5.jpg", -200, -400));
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
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 100, 300));
		platformCollection.addChild(new Platform(PlatformType.HORIZONTAL, 700, 300, 4, 300));
		platformCollection.addChild(new Platform(PlatformType.HORIZONTAL, 1300, 300, 4, 200));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 1300, 0));
		platformCollection.addChild(new Platform(PlatformType.HANG, 950, -200));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 600, -100));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 100, -100));
		platformCollection.addChild(new Platform(PlatformType.VERTICAL, 100, -400, 2, 200));
		platformCollection.addChild(new Platform(PlatformType.VERTICAL, 500, -500, 3, 200));
		platformCollection.addChild(new Platform(PlatformType.VERTICAL, 900, -600, 2, 150));
		platformCollection.addChild(new Platform(PlatformType.VERTICAL, 1300, -700, 2, 300));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 900, -1000));
		platformCollection.addChild(new Platform(PlatformType.HORIZONTAL, 600, -1000, 3, 400));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 100, -1300));
		platformCollection.addChild(new Platform(PlatformType.HANG, 400, -1550));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 800, -1500));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 800, -1800));
		platformCollection.addChild(new Platform(PlatformType.HORIZONTAL, 1300, -1500, 3, 150));
		platformCollection.addChild(new Platform(PlatformType.VERTICAL, 1600, -1600, 2, 300));
		platformCollection.addChild(new Platform(PlatformType.BOUNCY, 2000, -1350));
		platformCollection.addChild(new Platform(PlatformType.DEFAULT, 2500, -1800));
		/* Collectable notes */
		notesCollection.addChild(new Note ("a4", "a.png", "a4.wav", "eighth", 700, 0)); //On first horizontal
		notesCollection.addChild(new Note ("b4", "b.png", "b4.wav", "eighth", 1040, -450)); // On first hang
		notesCollection.addChild(new Note ("c4", "c.png", "c4.wav", "eighth", 240, -950)); //On first vertical 
		notesCollection.addChild(new Note ("d4", "d.png", "d4.wav", "eighth", 1370, -1250)); //Second hang
		notesCollection.addChild(new Note ("e4", "e.png", "e4.wav", "eighth", 1020, -1250)); //On default after vertical
		notesCollection.addChild(new Note ("f4", "f.png", "f4.wav", "eighth", 170, -1550)); //Random platform before horizontal segment
		notesCollection.addChild(new Note ("g4", "g.png", "g4.wav", "eighth", 900, -2050)); //Random platform 2 before horizontal segment
		notesCollection.addChild(new Note ("a5", "a.png", "a5.wav", "eighth", 1650, -2100)); // Last vertical
		notesCollection.addChild(new Note ("b5", "b.png", "b5.wav", "eighth", 2000, -2000)); //Above first bouncy
		notesCollection.addChild(new Note ("c5", "c.png", "c5.wav", "eighth", 2550, -2100)); //Final platform
		
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
		//platformCollection.addChild(new Sprite ("Platform", "platformPink.png", 300, 400));
	}
	
	public void setupAudio() throws IOException{
		/* Loads into Hash map, integer is level when music is played */
		soundManager.LoadSoundEffect("Game Over", "GameOver.wav");
		soundManager.LoadSoundEffect("Bounce", "Bounce.wav");
		soundManager.LoadBGM(0, "BrambleBlastLevel5.wav");
		soundManager.LoadBGM(1, "BrambleBlastLevel5.wav");
		soundManager.LoadBGM(2, "BrambleBlastLevel5.wav");
		soundManager.LoadBGM(3, "BrambleBlastLevel5.wav");
		soundManager.LoadBGM(4, "BrambleBlastLevel5.wav");
		soundManager.LoadBGM(5, "BrambleBlastLevel5.wav");
		soundManager.PlayBGM(0, 0);
	}
	
}
