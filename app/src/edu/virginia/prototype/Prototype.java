//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

package edu.virginia.prototype;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.display.SoundManager;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.IEventDispatcher;
import edu.virginia.engine.util.IEventListener;
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
	Sprite crosshairs = new Sprite("Coin", "crosshairs7.png");
	PhysicsSprite mario = new PhysicsSprite("Mario", "Mario.png");
	Sprite platform1 = new Sprite ("Platform1", "platformBlue.png");
	Sprite platform2 = new Sprite ("Platform2", "platformBlue.png");
	Sprite platform3 = new Sprite ("Platform3", "platformBlue.png");
	int marioFalling=0;
	MyQuestManager myQuestManager = new MyQuestManager();
	SoundManager soundManager = new SoundManager();
	boolean gameEnded=false;
	TweenJuggler tInstance;
	HookListener hookListener = new HookListener(mario, tInstance);
	PlayerListener playerListener = new PlayerListener(mario, tInstance);
	double startTime=System.currentTimeMillis();
	double timeElapsed=0;
	
	public Prototype() throws IOException {
		super("Restoration of Sound", 1600, 768);
		/* Set TweenJuggler */
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		/* Set initial background */
		this.setImage("background.png");
		/* Set sprite positions */
		mario.setPosition(new Point(100, 600));
		platform1.setPosition(new Point(100, 150));
		platform2.setPosition(new Point(550,450));
		platform3.setPosition(new Point(1200, 400));
		coin.setPosition(new Point(400,4));
		coin2.setPosition(new Point(1270, 250));
		drawCrosshairs(crosshairs);
		/* Set event listeners */
		mario.addEventListener(myQuestManager, CollisionEvent.COLLISION);
		mario.addEventListener(playerListener, CollisionEvent.PLATFORM);
		mario.addEventListener(playerListener, CollisionEvent.GROUND);
		mario.addEventListener(playerListener, CollisionEvent.INAIR);
		mario.addEventListener(playerListener, PlayerEvent.ResetFall);
		this.addEventListener(hookListener, HookEvent.HOOKPRESSED);
		this.addEventListener(hookListener, HookEvent.HOOKRELEASED);
		this.addEventListener(hookListener, HookEvent.CANHOOK);
		this.addEventListener(hookListener, HookEvent.CANNOTHOOK);
		mario.addEventListener(hookListener, HookEvent.HOOKHOP);
		/* Load music */
		soundManager.LoadMusic("orchestra", "orchestra.wav");
		soundManager.LoadMusic("canary", "canary.wav");
		soundManager.LoadMusic("techno", "techno.wav");
		soundManager.PlayMusic("canary");
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Prototype game = new Prototype();
		//game.addEventListener(listener, eventType);
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		if (coin != null){
		super.draw(g);
		
		
		/* Same, just check for null in case a frame gets thrown in before Mario is initialized*/
		if(coin != null && mario != null) {
			platform1.draw(g);
			platform2.draw(g);
			platform3.draw(g);
			coin.draw(g);
			coin2.draw(g);
			mario.draw(g);
			crosshairs.draw(g);
			}
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
	public void update(ArrayList<String> pressedKeys){
		if (gameEnded==true){
			timeElapsed=Math.min(System.currentTimeMillis()-startTime,3000);
			
		}
		if (tInstance!=null){tInstance.nextFrame();}
		super.update(pressedKeys);
		
		/* Draw sprites */
		drawCrosshairs(crosshairs);
		if (crosshairs.collidesWith(platform1) || 
			crosshairs.collidesWith(platform2) || 
			crosshairs.collidesWith(platform3)){
			this.dispatchEvent(new Event(HookEvent.CANHOOK, this));
		}
		else {
			this.dispatchEvent(new Event(HookEvent.CANNOTHOOK, this));
		}
		
		/* Make sure mario is not null. Sometimes Swing can auto cause an extra frame to go before everything is initialized */
		if(mario != null) mario.update(pressedKeys);
		if (coin != null && mario != null){
			
			/* Parent collides with coin */
			if (coin.getInPlay() && mario.collidesWith(coin)){
				Event event = new Event();
				event.setEventType(CoingrabbedEvent.COINGRABBED);
				event.setSource(coin);
				coin.dispatchEvent(event);
				//coin.setVisible(false);
				soundManager.StopMusic("canary");
				soundManager.StopMusic("orchestra");
				soundManager.PlayMusic("techno");
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
				soundManager.StopMusic("canary");
				soundManager.StopMusic("techno");
				soundManager.PlayMusic("orchestra");
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
			
			/* Parent is colliding with platform */
			if (mario.collidesWith(platform1)){
				mario.dispatchEvent(new Event(CollisionEvent.PLATFORM, platform1));
			}
			else if (mario.collidesWith(platform2)){
				mario.dispatchEvent(new Event(CollisionEvent.PLATFORM, platform2));
			}
			else if (mario.collidesWith(platform3)){
				mario.dispatchEvent(new Event(CollisionEvent.PLATFORM, platform3));
			}
			/* Parent is in the Air*/
			else {
				mario.dispatchEvent(new Event(CollisionEvent.INAIR, null));
			}
			
			/* Parent is on the ground */
			if(mario.getPosition().getY()>600){
				mario.dispatchEvent(new Event(CollisionEvent.GROUND, null));
			}
		}
	}

	public void drawCrosshairs(Sprite crosshairs){
		Point mouseCoor = MouseInfo.getPointerInfo().getLocation();
		int mouseX = (int) mouseCoor.getX();
		mouseX -= 28;
		int mouseY = (int) mouseCoor.getY();
		mouseY -= 50;
		Point adjustedPoint = new Point(mouseX, mouseY);
		crosshairs.setPosition(adjustedPoint);
	}
	
}
