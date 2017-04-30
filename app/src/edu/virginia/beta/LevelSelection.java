//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

package edu.virginia.beta;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import edu.virginia.alpha.NotesCollection;
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

public class LevelSelection extends Game{
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
	public LevelSelection() throws IOException {
		super("Restoration of Sound", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set Player Sprite positions */
		setUpMenu();
	}

	public static void main(String[] args) throws IOException {
		LevelSelection game = new LevelSelection();
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		if (menu != null && inMenu == true){
			menu.draw(g);
			g.setFont(new Font("KinoMT", Font.PLAIN, 50));
			g.setColor(Color.magenta);
			g.drawString("1", 520, 170);
			g.drawString("2", 520, 290);
			g.drawString("3", 520, 410);
			g.drawString("4", 520, 530);
			g.drawString("5", 520, 650);
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
						BrambleBlast.main(args);
						this.exitGame();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (pressedKeys.contains(51)){
					String[] args={"0"};
					try {
						LisaRiccia.main(args);
						this.exitGame();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (pressedKeys.contains(52)){
					String[] args={"0"};
					try {
						TheTower.main(args);
						this.exitGame();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (pressedKeys.contains(53)){
					String[] args={"0"};
					try {
						TempleOfTime.main(args);
						this.exitGame();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (pressedKeys.contains(66)){
					String[] args={"0"};
					try {
						Menu.main(args);
						this.exitGame();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}
	
	public void updateHUD(){
		
	}
	
	
	public void setUpMenu(){
		Sprite menuBackground = new Sprite("Menu Background", "menu background.jpg", 0,0);
		menu.addChild(menuBackground);
		Sprite menuLvl1 = new Sprite ("Menu Level 1", "menu lvl1.png", 570, 100);
		menu.addChild(menuLvl1);
		Sprite menuLvl2 = new Sprite ("Menu Level 2", "menu lvl2.png", 570, 220);
		menu.addChild(menuLvl2);
		Sprite menuLvl3 = new Sprite ("Menu Level 3", "menu lvl3.png", 570, 340);
		menu.addChild(menuLvl3);
		Sprite menuLvl4 = new Sprite ("Menu Level 4", "menu lvl4.png", 570, 460);
		menu.addChild(menuLvl4);
		Sprite menuLvl5 = new Sprite ("Menu Level 5", "menu lvl5.png", 570, 580);
		menu.addChild(menuLvl5);
		Sprite menuBack = new Sprite ("Menu Back", "menu back.png", 20, 20);
		menu.addChild(menuBack);
	}
	


	
}
