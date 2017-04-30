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

public class Menu extends Game{
	DisplayObjectContainer menu = new DisplayObjectContainer("Menu");
	/* Game variables */
	boolean inMenu = true;
	TweenJuggler tInstance;
	double startTime=System.currentTimeMillis();
	double timeElapsed=0;
	GameClock backgroundInterlude = new GameClock();
	SoundManager soundManager = new SoundManager();
	double gameEndedTime;
	boolean toPlay;
	String noteLength="";
	
	/* Set up game */
	public Menu() throws IOException {
		super("Restoration of Sound", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set Player Sprite positions */
		setUpMenu();
	}

	public static void main(String[] args) throws IOException {
		Menu game = new Menu();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		if (menu != null && inMenu == true){
			menu.draw(g);
			g.setFont(new Font("KinoMT", Font.PLAIN, 50));
			g.setColor(Color.magenta);
			g.drawString("1", 520, 270);
			g.drawString("2", 520, 370);
			g.drawString("3", 520, 470);
			g.drawString("4", 520, 570);
		}
	
		g.setFont(new Font("KinoMT", Font.BOLD, 30));
	}
		
	@Override
	public void update(ArrayList<Integer> pressedKeys, ArrayList<GamePad> controllers){
		// Hold 'P' to pause (for debugging)
			
			
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
						LevelSelection.main(args);
						this.exitGame();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (pressedKeys.contains(51)){
					String[] args={"0"};
					try {
						Tutorial.main(args);
						this.exitGame();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (pressedKeys.contains(52)){
					this.exitGame();
				}
	}
	
	public void updateHUD(){
		
	}
	
	
	public void setUpMenu(){
		Sprite menuBackground = new Sprite("Menu Background", "menu background.jpg", 0,0);
		menu.addChild(menuBackground);
		Sprite menuStart = new Sprite ("Menu Start", "menu start.png", 570, 200);
		menu.addChild(menuStart);
		Sprite menuSelect = new Sprite ("Menu Select", "menu select.png", 570, 300);
		menu.addChild(menuSelect);
		Sprite menuTutorial = new Sprite ("Menu Tutorial", "menu tutorial.png", 570, 400);
		menu.addChild(menuTutorial);
		Sprite menuExit = new Sprite ("Menu Exit", "menu exit.png", 570, 500);
		menu.addChild(menuExit);
	}
	


	
}
