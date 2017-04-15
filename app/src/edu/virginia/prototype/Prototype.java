//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

package edu.virginia.prototype;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.IEventDispatcher;
import edu.virginia.engine.util.IEventListener;
import edu.virginia.engine.util.SoundManager;
import edu.virginia.engine.util.listeners.CoingrabbedEvent;
import edu.virginia.engine.util.listeners.CollisionEvent;
import edu.virginia.engine.util.listeners.HookEvent;
import edu.virginia.engine.util.listeners.HookListener;
import edu.virginia.engine.util.listeners.MyQuestManager;
import edu.virginia.engine.util.listeners.PlayerEvent;
import edu.virginia.engine.util.listeners.PlayerListener;

public class Prototype extends Game{
	Sprite coin = new Sprite("Coin", "coin.png");
	Sprite coin2 = new Sprite("Coin", "coin.png");
	Sprite crosshairs = new Sprite("Crosshairs", "crosshairs7.png");
	PhysicsSprite mario = new PhysicsSprite("Mario", "Mario.png");
	DisplayObjectContainer foregroundFalling = new DisplayObjectContainer("Foreground Falling");
	DisplayObjectContainer platformCollection = new DisplayObjectContainer("Platform Collection");
	MyQuestManager myQuestManager = new MyQuestManager();
	SoundManager soundManager = new SoundManager();
	boolean gameEnded=false;
	TweenJuggler tInstance;
	double startTime=System.currentTimeMillis();
	double timeElapsed=0;
	GameClock timeSinceFall = new GameClock();
	public Prototype() throws IOException {
		super("Restoration of Sound", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set initial background */
		this.setImage("background.png");
		/* Set sprite positions */
		mario.setPosition(new Point(100, 600));
		crosshairs.setPivotPoint(new Point(-26, -26));
		coin.setPosition(new Point(400,4));
		coin2.setPosition(new Point(1270, 250));
		drawCrosshairsKeyboard(crosshairs, mario);
		mario.addEventListener(myQuestManager, CollisionEvent.COLLISION);
		/* Set up sprites that will fall in the foreground */
		this.addChild(foregroundFalling);
			//foregroundFalling.addChild(mario);
			/* Set up platforms */
			this.setupPlatforms(foregroundFalling);
		/* Set up hookshot */
		HookListener hookListener = new HookListener(mario, tInstance);
		PlayerListener playerListener = new PlayerListener(mario, tInstance);
		this.setupHookshot(mario, playerListener, hookListener);
		/* Load music */
		//soundManager.LoadMusic("orchestra", "orchestra.wav");
		//soundManager.LoadMusic("canary", "canary.wav");
		//soundManager.LoadMusic("techno", "techno.wav");
		//soundManager.PlayMusic("canary");
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Prototype game = new Prototype();
		//game.addEventListener(listener, eventType);
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		/* Draw all assets */
		super.draw(g);
		
		/* Same, just check for null in case a frame gets thrown in before Mario is initialized*/
		if(coin != null && mario != null) {
			coin.draw(g);
			coin2.draw(g);
			mario.draw(g);
			crosshairs.draw(g);
			//crosshairs2.draw(g);
			/* Draw test rectangle
			 * 
			 */
			int hitboxWidth = (int) crosshairs.getPosition().getX() - mario.getCenterX();
			int hitboxHeight = (int) crosshairs.getPosition().getY() - mario.getCenterY();
			int hitboxX = mario.getCenterX();
			int hitboxY = mario.getCenterY();
			if (hitboxWidth < 0){
				hitboxX += hitboxWidth;
				hitboxWidth *= -1;
			}
			if (hitboxHeight < 0){
				hitboxY += hitboxHeight;
				hitboxHeight *= -1;
			}
			g.setColor(Color.RED);
			/*System.out.format("Rectangle Params: %d %d %d %d \n", 
					hitboxX,
					hitboxY,
					hitboxWidth,
					hitboxHeight);*/
			g.fillRect(
					hitboxX,
					hitboxY,
					hitboxWidth,
					hitboxHeight);
		
		}
		
		
		g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
		if (gameEnded==true){
			g.drawString("Congrats, you got the coin!", 800, 50);
			if (tInstance.getSize()==0){
				this.stop();
			}
		}
		else {
			g.drawString("Get the coin!", 900, 50);
		}
	}
		
	@Override
	public void update(ArrayList<Integer> pressedKeys){
		if (gameEnded==true){
			timeElapsed=Math.min(System.currentTimeMillis()-startTime,3000);
			
		}
		if (tInstance!=null){tInstance.nextFrame();}
		super.update(pressedKeys);
		
		/* Check hook status */
		drawCrosshairsKeyboard(crosshairs, mario);
		platformCollection.hookablePlatform(crosshairs, mario);
		/* Have foreground elements fall */
		updateForegroundFalling(foregroundFalling, timeSinceFall);
		
		/* Make sure mario is not null. Sometimes Swing can auto cause an extra frame to go before everything is initialized */
		if(mario != null) mario.update(pressedKeys);
		if (coin != null && mario != null && mario.isInPlay()){
			
			/* Parent collides with coin */
			if (coin.getInPlay() && mario.collidesWith(coin)){
				Event event = new Event();
				event.setEventType(CoingrabbedEvent.COINGRABBED);
				event.setSource(coin);
				coin.dispatchEvent(event);
				//coin.setVisible(false);
				//soundManager.StopMusic("canary");
				//soundManager.StopMusic("orchestra");
				//soundManager.PlayMusic("techno");
				this.setImage("cool-background.png");
				Tween coinTween = new Tween (coin);
				coin.addEventListener(myQuestManager,"Tween Start Event");
				tInstance.add(coinTween);
				coinTween.animate(TweenableParams.SCALE_X, 1, 4, 2000);
				coinTween.animate(TweenableParams.SCALE_Y, 1, 4, 2000);
				coinTween.animate(TweenableParams.X, 400, 600, 2000);
				coinTween.animate(TweenableParams.Y, 4, 180, 2000);
				//coinTween = new Tween(coin);
				//tInstance.add(coinTween);
				coinTween.animate(TweenableParams.ALPHA, 1, 0, 6000);
				coin.setInPlay(false);
				//gameEnded=true;		
			}
			
			if (coin2.getInPlay() && mario.collidesWith(coin2)){
				Event event = new Event();
				event.setEventType(CoingrabbedEvent.COINGRABBED);
				event.setSource(coin2);
				coin2.dispatchEvent(event);
				//coin.setVisible(false);
				//soundManager.StopMusic("canary");
				//soundManager.StopMusic("techno");
				//soundManager.PlayMusic("orchestra");
				this.setImage("space-bg.jpg");
				Tween coinTween = new Tween (coin2);
				coin.addEventListener(myQuestManager,"Tween Start Event");
				tInstance.add(coinTween);
				coinTween.animate(TweenableParams.SCALE_X, 1, 4, 2000);
				coinTween.animate(TweenableParams.SCALE_Y, 1, 4, 2000);
				coinTween.animate(TweenableParams.X, 1270, 600, 2000);
				coinTween.animate(TweenableParams.Y, 250, 180, 2000);
				//coinTween = new Tween(coin);
				//tInstance.add(coinTween);
				coinTween.animate(TweenableParams.ALPHA, 1, 0, 6000);
				coin2.setInPlay(false);
				//gameEnded=true;		
			}
			
			/* Game ends when you get both coins */
			if (!coin.getInPlay() && !coin2.getInPlay()){
				gameEnded=true;
			}
			/* Player colliding with platform */
			if (platformCollection.collidesWithPlatform(mario)){
				// If true, player will send collision event to all listeners.
			}
			/* Player is in the Air*/
			else {
				mario.dispatchEvent(new Event(CollisionEvent.INAIR, null));
			}
			/* Parent is on the ground */
			if(mario.getPosition().getY()>600){
				mario.dispatchEvent(new Event(CollisionEvent.GROUND, null));
			}
		}
	}

	/*
	public void drawCrosshairsMouse(Sprite crosshairs, PhysicsSprite player){
		Point mouseCoor = MouseInfo.getPointerInfo().getLocation();
		float crosshairsAngle = player.getAngle(mouseCoor);
		int radius = 150;
		int radiusX = (int) (Math.cos(Math.toRadians(crosshairsAngle)) * radius);
		int radiusY = (int) (Math.sin(Math.toRadians(crosshairsAngle)) * radius);
		int crosshairsX = player.getCenterX() + radiusX;
		int crosshairsY = player.getCenterY() + radiusY;
		player.setCrosshairsAngle(crosshairsAngle);
		Point adjustedPoint = new Point(crosshairsX, crosshairsY);
		crosshairs.setPosition(adjustedPoint);
	}*/
	
	public void updateBackgroundFalling(DisplayObjectContainer backgroundRoot){
		/* Only fall if timer value has been passed */
		int timeBetweenFall = 150;
		if (timeSinceFall.getElapsedTime() > timeBetweenFall){
			/* Have your children fall */
			for (int i = 0; i < backgroundRoot.numberOfChildren(); i++){
				DisplayObject toFallNext = backgroundRoot.getChildAtIndex(i);
				int x = (int) toFallNext.getPosition().getX();
				int y = (int) toFallNext.getPosition().getY();
				int foregroundFallSpeed = 1;
				int newY = y + foregroundFallSpeed;
				toFallNext.setPosition(new Point(x,newY));
			}
			/* Reset timer */
			timeSinceFall.resetGameClock();
		}
	}
	
	public void updateForegroundFalling(DisplayObjectContainer foregroundRoot, GameClock timeSinceFall){
		/* Only fall if timer value has been passed */
		int timeBetweenFall = 50;
		if (timeSinceFall.getElapsedTime() > timeBetweenFall){
			/* Have your children fall */
			for (int i = 0; i < foregroundRoot.numberOfChildren(); i++){
				DisplayObject toFallNext = foregroundRoot.getChildAtIndex(i);
				childForegroundFalling((DisplayObjectContainer)toFallNext);
			}
			/* Reset timer */
			timeSinceFall.resetGameClock();
		}
	}
	
	public void childForegroundFalling(DisplayObjectContainer toFallCurrent){
		/* Falling yourself */
		int x = (int) toFallCurrent.getPosition().getX();
		int y = (int) toFallCurrent.getPosition().getY();
		int foregroundFallSpeed = 1;
		int newY = y + foregroundFallSpeed;
		toFallCurrent.setPosition(new Point(x,newY));
		for (int i = 0; i < toFallCurrent.numberOfChildren(); i++){
			DisplayObject toFallNext = toFallCurrent.getChildAtIndex(i);
			childForegroundFalling((DisplayObjectContainer)toFallNext);
		}	
	}
	
	public void setupPlatforms(DisplayObjectContainer foregroundFalling){
		/* Create platforms */
		Sprite platform1 = new Sprite ("Platform1", "platformBlue.png");
		platform1.setPosition(new Point(100, 150));
		Sprite platform2 = new Sprite ("Platform2", "platformBlue.png");
		platform2.setPosition(new Point(600,450));
		Sprite platform3 = new Sprite ("Platform3", "platformBlue.png");
		platform3.setPosition(new Point(1200, 400));
		/* Set up display hierachy */
		foregroundFalling.addChild(platformCollection);
		platformCollection.addChild(platform1);
		platformCollection.addChild(platform2);
		platformCollection.addChild(platform3);
	}
	
	public void setupCollectables(DisplayObjectContainer foregroundFalling){
	}

	
}
