//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

package edu.virginia.lab6test;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.SoundManager;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.lab6test.CollisionEvent;
import engine.events.Event;
import engine.events.IEventDispatcher;
import engine.events.IEventListener;

public class CoinGrabber extends Game{
	Sprite coin = new Sprite("Coin", "coin.png");
	Sprite coin2 = new Sprite("Coin", "coin.png");
	Sprite mario = new Sprite("Mario", "Mario.png");
	Sprite platform1 = new Sprite ("Platform1", "platform.png");
	Sprite platform2 = new Sprite ("Platform2", "platform.png");
	int marioFalling=0;
	boolean onGround=true;
	MyQuestManager myQuestManager = new MyQuestManager();
	SoundManager soundManager = new SoundManager();
	boolean gameEnded=false;
	TweenJuggler tInstance;
	double startTime=System.currentTimeMillis();
	double timeElapsed=0;
	
	public CoinGrabber() throws IOException {
		super("Coin Grabber", 1600, 768);
		if (TweenJuggler.getInstance() == null){
			tInstance = new TweenJuggler();
		}
		else tInstance = TweenJuggler.getInstance();
		
		soundManager.LoadMusic("orchestra", "orchestra.wav");
		soundManager.LoadMusic("canary", "canary.wav");
		soundManager.LoadMusic("techno", "techno.wav");
		//soundManager.PlayMusic("coin");
		//soundManager.LoadSoundEffect("coin", "coin.wav");
		soundManager.PlayMusic("canary");
		
		
		
		
		
		
		
		
		
		this.setImage("background.png");
		
		coin.setPosition(new Point(400,4));
		coin2.setPosition(new Point(300, 600));
		mario.setPosition(new Point(100, 600));
		Tween marioTween = new Tween (mario);
		tInstance.add(marioTween);
		marioTween.animate(TweenableParams.ALPHA, 0.1, 1, 2000);
		marioTween.animate(TweenableParams.SCALE_X, 0.2, 1, 5000);
		marioTween.animate(TweenableParams.SCALE_Y, 0.2, 1, 5000);
		
		platform1.setPosition(new Point(400, 150));
		platform2.setPosition(new Point(900, 400));
		mario.addEventListener(myQuestManager, CollisionEvent.COLLISION);
		
	}

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		// TODO Auto-generated method stub
		CoinGrabber game = new CoinGrabber();
		//game.addEventListener(listener, eventType);
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		if (coin != null){
		super.draw(g);
		
		
		/* Same, just check for null in case a frame gets thrown in before Mario is initialized */
		if(coin != null && mario != null && platform1 != null && platform2 != null && coin2 != null) {
			platform1.draw(g);
			platform2.draw(g);
			coin.draw(g);
			coin2.draw(g);
			mario.draw(g);
			}
		}
		
		g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
		if (gameEnded==true){
			g.drawString("Congrats, you got the coins!", 800, 50);
			if (tInstance.getSize()==0){
				this.stop();
			}
		}
		else {
			g.drawString("Get the coins!", 900, 50);
		}
		
		}
		
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
		//soundManager.update();
		
		if (gameEnded==true){
			timeElapsed=Math.min(System.currentTimeMillis()-startTime,3000);
			
		}
		if (tInstance!=null){
		
		tInstance.nextFrame();
		//System.out.println(tInstance.getSize());
		}
		//for (int i=0; i<pressedKeys.size(); i++){
		if (mario!=null){
		if ((pressedKeys.contains("Left")) && mario.getPosition().getX()>=4){
			mario.getPosition().translate(-10, 0);
			//mario.getPivotPoint().translate(-20, 0);
		}
		if ((pressedKeys.contains("Right")) && mario.getPosition().getX()<=1600-mario.getUnscaledWidth()){
			mario.getPosition().translate(10, 0);
			//mario.getPivotPoint().translate(20, 0);
		}
		//if ((pressedKeys.contains("Down"))){
		//	mario.getPosition().translate(0, 4);
		//	//mario.getPivotPoint().translate(0, 10);
		//}
		if ((pressedKeys.contains("Up")) && mario.getPosition().getY()>=4 && this.onGround==true){
			this.marioFalling=-25;
			this.onGround=false;
			//mario.getPosition().translate(0, -10);
			//mario.getPivotPoint().translate(0, -10);
		}
		mario.getPosition().translate(0, marioFalling);
		marioFalling++;
		
		
		
		
		}
		/* Make sure mario is not null. Sometimes Swing can auto cause an extra frame to go before everything is initialized */
		if(mario != null) mario.update(pressedKeys);
		if (coin != null && mario != null && platform1 != null && platform2 != null){
		if (mario.collidesWith(platform1) || mario.collidesWith(platform2) || mario.getPosition().getY()>=600){
			if (mario.collidesWith(platform1)){
				if (mario.getPosition().getY()+mario.getUnscaledHeight()>platform1.getPosition().getY() && mario.getPosition().getY()<platform1.getPosition().getY()){
					mario.setPosition(new Point((int)mario.getPosition().getX(),(int)platform1.getPosition().getY()-mario.getUnscaledHeight()));
					this.marioFalling=0;
					this.onGround=true;
				}
				else if (mario.getPosition().getY()<platform1.getPosition().getY()+platform1.getUnscaledHeight() && mario.getPosition().getY() + mario.getUnscaledHeight()>platform1.getPosition().getY() + platform1.getUnscaledHeight()){
					mario.setPosition(new Point((int)mario.getPosition().getX(),(int)platform1.getPosition().getY()+platform1.getUnscaledHeight()));
				}
				
				else if (mario.getPosition().getX()+mario.getUnscaledWidth()>platform1.getPosition().getX() && mario.getPosition().getX()<platform1.getPosition().getX()){
					mario.setPosition(new Point((int)platform1.getPosition().getX()-mario.getUnscaledWidth(), (int)mario.getPosition().getY()));
				}
				else if (mario.getPosition().getX()<platform1.getPosition().getX()+platform1.getUnscaledWidth() && mario.getPosition().getX() + mario.getUnscaledWidth()>platform1.getPosition().getX() +platform1.getUnscaledWidth()){
					mario.setPosition(new Point((int)platform1.getPosition().getX()+platform1.getUnscaledWidth(), (int)mario.getPosition().getY()));
				}
			}
			if (mario.collidesWith(platform2)){
				if (mario.getPosition().getY()+mario.getUnscaledHeight()>platform2.getPosition().getY() && mario.getPosition().getY()<platform2.getPosition().getY()){
					mario.setPosition(new Point((int)mario.getPosition().getX(),(int)platform2.getPosition().getY()-mario.getUnscaledHeight()));
					this.marioFalling=0;
					this.onGround=true;
				}
				else if (mario.getPosition().getY()<platform2.getPosition().getY()+platform2.getUnscaledHeight() && mario.getPosition().getY() + mario.getUnscaledHeight()>platform2.getPosition().getY() + platform2.getUnscaledHeight()){
					mario.setPosition(new Point((int)mario.getPosition().getX(),(int)platform2.getPosition().getY()+platform2.getUnscaledHeight()));
				}
				
				else if (mario.getPosition().getX()+mario.getUnscaledWidth()>platform2.getPosition().getX() && mario.getPosition().getX()<platform2.getPosition().getX()){
					mario.setPosition(new Point((int)platform2.getPosition().getX()-mario.getUnscaledWidth(), (int)mario.getPosition().getY()));
				}
				else if (mario.getPosition().getX()<platform2.getPosition().getX()+platform2.getUnscaledWidth() && mario.getPosition().getX() + mario.getUnscaledWidth()>platform2.getPosition().getX() +platform2.getUnscaledWidth()){
					mario.setPosition(new Point((int)platform2.getPosition().getX()+platform2.getUnscaledWidth(), (int)mario.getPosition().getY()));
				}
			}
			
			if(mario.getPosition().getY()>600){
				mario.setPosition(new Point((int)mario.getPosition().getX(), 600));
				this.marioFalling=0;
				this.onGround=true;
			}
			
			
			Event event = new Event();
			event.setEventType(CollisionEvent.COLLISION);
			event.setSource(mario);
			mario.dispatchEvent(event);	
		}
		
		if (coin2.getInPlay() && mario.collidesWith(coin2)){
			Event event = new Event();
			event.setEventType(CoingrabbedEvent.COINGRABBED);
			event.setSource(coin2);
			coin2.dispatchEvent(event);
			//coin.setVisible(false);
			soundManager.StopMusic("canary");
			soundManager.PlayMusic("orchestra");
			this.setImage("purple-background.png");
			Tween coinTween = new Tween (coin2);
			coin.addEventListener(myQuestManager,"Tween Start Event");
			tInstance.add(coinTween);
			coinTween.animate(TweenableParams.SCALE_X, 1, 4, 2000);
			coinTween.animate(TweenableParams.SCALE_Y, 1, 4, 2000);
			coinTween.animate(TweenableParams.X, 300, 600, 2000);
			coinTween.animate(TweenableParams.Y, 600, 180, 2000);
			//coinTween = new Tween(coin);
			//tInstance.add(coinTween);
			coinTween.animate(TweenableParams.ALPHA, 1, 0, 6000);
			coin2.setInPlay(false);
			//gameEnded=true;		
		}
		if (coin.getInPlay() && mario.collidesWith(coin)){
			Event event = new Event();
			event.setEventType(CoingrabbedEvent.COINGRABBED);
			event.setSource(coin);
			coin.dispatchEvent(event);
			//coin.setVisible(false);
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
//			gameEnded=true;		
		}
		}
	}


}
