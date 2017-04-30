//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

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

public class BaseGame extends Game{
	/* Player variables */
	PhysicsSprite player = new PhysicsSprite("Player", "Player.png");
	Sprite crosshairs = new Sprite("Crosshairs", "crosshairs7.png");
	Sprite hookshot = new Sprite("Hookshot", "hookshot.png"); 
	Sprite deathBar = new Sprite("Death Bar", "death bar.png");
	Sprite lossScreen = new Sprite ("Loss Screen", "End Screen Lost.jpg");
	Sprite winScreen = new Sprite ("Win Screen", "End Screen Won.jpg");
	/* Sprite collections */
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
	int notesToNextLevel = 3;
	int notesPerLevel = 3;
	int notesToBeatStage = 13;
	int totalNotes=15;
	int missedNotes=0;
	Sprite staff = new Sprite("Staff", "staff.png");
	ArrayList<Note> collected = new ArrayList<Note>();
	SoundManager soundManager = new SoundManager();
	double gameEndedTime;
	boolean toPlay;
	String noteLength="";
	
	public BaseGame() throws IOException {
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
		winScreen.setVisible(false);
			// Set up platforms 
			setupPlatforms();
			// Set up notes to collect
			//setupNotes();
		/* Set up hookshot */
		//setUpMenu();
		HookListener hookListener = new HookListener(player, tInstance, hookshot);
		PlayerListener playerListener = new PlayerListener(player, tInstance, soundManager);
		this.setupHookshot(player, playerListener, hookListener, hookshot);
		setupAudio();
	}

	public static void main(String[] args) throws IOException {
		BaseGame game = new BaseGame();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		
			super.draw(g);
			/* Draw assets that are not explicity in Display Tree Hierachy */
			if (player != null && crosshairs != null){
			player.draw(g);
			crosshairs.draw(g);
			}
			if (winScreen != null){
				winScreen.draw(g);
			}
			if(staff != null){
				staff.draw(g);
			}
		
		if (gameEnded == true && tInstance.getSize()==0){
			staff.setVisible(true);
			for (Note note : collected){
				note.setAlpha(1);
				note.draw(g);
			}
		}
		/* Draw HUD */
		g.setColor(Color.WHITE);
		if (missedNotes>totalNotes-notesToBeatStage){
			g.setFont(new Font("KinoMT", Font.PLAIN, 50));
			lossScreen.draw(g);
			g.drawString("You have missed too many notes!", 450, 150);
			g.drawString("Press 1 to restart or 2 to go back to the menu", 350, 200);
			
		}
		if (gameEnded==true){
			// TODO: Replace with victory splash screen
			g.setFont(new Font("KinoMT", Font.PLAIN, 80));
			winScreen.setVisible(true);
			strokeText("Sound Restored!", g, 450, 100);
			//if (tInstance.getSize()==0){
			//	this.stop();
			//}
			g.setFont(new Font("KinoMT", Font.PLAIN, 50));
			if (collected.size()==0) g.drawString("Press 1 to restart, 2 to go back to the menu, 3 to go to the next level", 50, 250);
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
			if (tInstance!=null){tInstance.nextFrame();}
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
					Note note = notesCollection.getCollidedNote();
					collected.add(note);
					note.setVisible(true);
					note.setInPlay(true);
					int height=300;
					if (note.getId().equals("c4") || note.getId().equals("c4q")) height=384;
					else if (note.getId().equals("d4") || note.getId().equals("d4q")) height=370;
					else if (note.getId().equals("e4") || note.getId().equals("e4q")) height=356;
					else if (note.getId().equals("f4") || note.getId().equals("f4q")) height=342;
					else if (note.getId().equals("g4") || note.getId().equals("g4q")) height=328;
					else if (note.getId().equals("a5") || note.getId().equals("a5q")) height=314;
					else if (note.getId().equals("b5") || note.getId().equals("b5q")) height=300;
					
				    note.setPosition(new Point((250+(collected.size()+1)*1100/5), height));
					note.setScaleX(0.4);
					note.setScaleY(0.4);
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
			if (collected.isEmpty()){
				//System.out.println("Collected is empty");
				//this.stop();
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
					int t=500;
					if (noteLength.equals("quarter")) t=1000; 
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
					else if (collected.get(i).getId().equals("g3") || collected.get(i).getId().equals("g3q")) height=440;
				
					collected.get(i).setPosition(new Point((250+(i+1)*1100/collected.size()), height));
					collected.get(i).setScaleX(0.4);
					collected.get(i).setScaleY(0.4);
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
				/** Change how much the background will fall **/
				int backgroundFallSpeed = 1;
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
			platformCollection.addChild(new Sprite ("Platform1", "platformBlue.png", 100,-150));
			Note c41 = new Note ("c4", "c.png", "c4.wav", "eighth");
			c41.setPosition(new Point(190,-350));
			notesCollection.addChild(c41);
			platformCollection.addChild(new Sprite ("Platform2", "platformBlue.png", 300, -400));
			Note c42 = new Note ("c4", "c.png", "c4.wav", "eighth");
			c42.setPosition(new Point(390,-600));
			notesCollection.addChild(c42);
			platformCollection.addChild(new Sprite ("Platform3", "platformBlue.png", 500, -650));
			Note c43 = new Note ("c4", "c.png", "c4.wav", "quarter");
			c43.setPosition(new Point(590,-850));
			notesCollection.addChild(c43);
			platformCollection.addChild(new Sprite ("Platform4", "platformBlue.png", 800, -900));
			Note g31 = new Note ("g3", "g.png", "g3.wav", "quarter");
			g31.setPosition(new Point(890,-1100));
			notesCollection.addChild(g31);
			platformCollection.addChild(new Sprite ("Platform5", "platformBlue.png", 700, -1200));
			Note e41 = new Note ("e4", "e.png", "e4.wav", "eighth");
			e41.setPosition(new Point(790,-1400));
			notesCollection.addChild(e41);
			platformCollection.addChild(new Sprite ("Platform6", "platformBlue.png", 900, -1500));
			Note e42 = new Note ("e4", "e.png", "e4.wav", "eighth");
			e42.setPosition(new Point(990,-1700));
			notesCollection.addChild(e42);
			platformCollection.addChild(new Sprite ("Platform7", "platformBlue.png", 1000, -1700));
			Note e43 = new Note ("e4", "e.png", "e4.wav", "quarter");
			e43.setPosition(new Point(1090,-1900));
			notesCollection.addChild(e43);
			platformCollection.addChild(new Sprite ("Platform8", "platformBlue.png", 800, -1900));
			Note c44 = new Note ("c4", "c.png", "c4.wav", "quarter");
			c44.setPosition(new Point(890,-2100));
			notesCollection.addChild(c44);
			platformCollection.addChild(new Sprite ("Platform9", "platformBlue.png", 500, -2150));
			Note c45 = new Note ("c4", "c.png", "c4.wav", "eighth");
			c45.setPosition(new Point(590,-2350));
			notesCollection.addChild(c45);
			platformCollection.addChild(new Sprite ("Platform10", "platformBlue.png", 300, -2350));
			Note e44 = new Note ("e4", "e.png", "e4.wav", "eighth");
			e44.setPosition(new Point(390,-2550));
			notesCollection.addChild(e44);
			platformCollection.addChild(new Sprite ("Platform11", "platformBlue.png", 450, -2500));
			Note g41 = new Note ("g4", "g.png", "g4.wav", "quarter");
			g41.setPosition(new Point(540,-2700));
			notesCollection.addChild(g41);
			platformCollection.addChild(new Sprite ("Platform12", "platformBlue.png", 550, -2700));
			Note g42 = new Note ("g4", "g.png", "g4.wav", "quarter");
			g42.setPosition(new Point(640,-2900));
			notesCollection.addChild(g42);
			platformCollection.addChild(new Sprite ("Platform13", "platformBlue.png", 700, -2900));
			Note f41 = new Note ("f4", "f.png", "f4.wav", "eighth");
			f41.setPosition(new Point(790,-3100));
			notesCollection.addChild(f41);
			platformCollection.addChild(new Sprite ("Platform14", "platformBlue.png", 600, -3100));
			Note e45 = new Note ("e4", "e.png", "e4.wav", "eighth");
			e45.setPosition(new Point(690,-3300));
			notesCollection.addChild(e45);
			platformCollection.addChild(new Sprite ("Platform15", "platformBlue.png", 700, -3300));
			Note d41 = new Note ("d4", "d.png", "d4.wav", "quarter");
			d41.setPosition(new Point(790,-3500));
			notesCollection.addChild(d41);
		
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
