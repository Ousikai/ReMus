//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

package edu.virginia.lab6test;

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
import edu.virginia.lab6test.CollisionEvent;
import engine.events.Event;
import engine.events.IEventDispatcher;
import engine.events.IEventListener;

public class MainGame extends Game{
	Sprite coin = new Sprite("Coin", "coin.png");
	Sprite crosshairs = new Sprite("Coin", "crosshairs7.png");
	PhysicsSprite mario = new PhysicsSprite("Mario", "Mario.png");
	Sprite platform1 = new Sprite ("Platform1", "platformBlue.png");
	Sprite platform2 = new Sprite ("Platform2", "platformBlue.png");
	Sprite platform3 = new Sprite ("Platform3", "platformBlue.png");
	//Sprite platform2 = new Sprite ("Platform2", "platform.png");
	int marioFalling=0;
	MyQuestManager myQuestManager = new MyQuestManager();
	SoundManager soundManager = new SoundManager();
	boolean gameEnded=false;
	TweenJuggler tInstance;
	HookListener hookListener = new HookListener(mario, tInstance);
	PlayerListener playerListener = new PlayerListener(mario, tInstance);
	double startTime=System.currentTimeMillis();
	double timeElapsed=0;
	
	public MainGame() throws IOException {
		super("Restoration of Sound", 1600, 768);
		if (TweenJuggler.getInstance() == null){tInstance = new TweenJuggler();}
		else {tInstance = TweenJuggler.getInstance();}
		//soundManager.LoadMusic("dvorak", "dvorak.mp3");
		soundManager.LoadSoundEffect("coin", "coin.wav");
		//soundManager.PlayMusic("dvorak");
		this.setImage("background.png");
		// TODO Auto-generated constructor stub
		mario.setPosition(new Point(100, 600));
		platform1.setPosition(new Point(100, 150));
		platform2.setPosition(new Point(550,450));
		platform3.setPosition(new Point(1200, 400));
		coin.setPosition(new Point(400,4));
		drawCrosshairs(crosshairs);
		/*
		Tween marioTween = new Tween (mario);
		tInstance.add(marioTween);
		marioTween.animate(TweenableParams.ALPHA, 0.1, 1, 2000);
		marioTween.animate(TweenableParams.SCALE_X, 0.2, 1, 5000);
		marioTween.animate(TweenableParams.SCALE_Y, 0.2, 1, 5000);*/
		
		//platform2.setPosition(new Point(900, 400));
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
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		MainGame game = new MainGame();
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
