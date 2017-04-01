//My lab 5 didn't have all the music and animation functionality, so this lab doesn't either. However, I have all the new requirements for this lab.

package edu.virginia.prototype;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.SoundManager;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Note;
import edu.virginia.engine.util.listeners.CollisionEvent;
import edu.virginia.engine.util.listeners.MyQuestManager;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.IEventDispatcher;
import edu.virginia.engine.util.IEventListener;

public class NotesCollection extends Game{
	Sprite mario = new Sprite("Mario", "Mario.png");
	Sprite platform = new Sprite("Platform", "platform.png");
	ArrayList<Note> notes = new ArrayList<Note>();
	Note a4 = new Note("a4", "a.png", "a4.wav");
	Note c4 = new Note("c4", "c.png", "c4.wav");
	Note g4 = new Note("g4", "g.png", "g4.wav");
	Note a5 = new Note("a5", "a.png", "a5.wav");
	Note f4 = new Note("f4", "f.png", "f4.wav");
	Note e4 = new Note("e4", "e.png", "e4.wav");
	Note d4 = new Note("d4", "d.png", "d4.wav");
	ArrayList<Note> collected = new ArrayList<Note>();
	int marioFalling=0;
	boolean onGround=true;
	MyQuestManager myQuestManager = new MyQuestManager();
	SoundManager soundManager = new SoundManager();
	boolean gameEnded=false;
	TweenJuggler tInstance;
	double startTime=System.currentTimeMillis();
	double timeElapsed=0;
	double gameEndedTime;
	boolean toPlay;
	
	public NotesCollection() throws IOException {
		super("Notes Collection", 1600, 768);
		if (TweenJuggler.getInstance() == null){
			tInstance = new TweenJuggler();
		}
		else tInstance = TweenJuggler.getInstance();
		this.setImage("background.png");
		// TODO Auto-generated constructor stub
		c4.setPosition(new Point(600,100));
		platform.setPosition(new Point((int)c4.getPosition().getX()-100, (int)c4.getPosition().getY()+200));
		platform.setScaleX(0.5);
		platform.setScaleY(0.5);
		notes.add(c4);
		notes.add(c4);
		notes.add(g4);
		notes.add(g4);
		notes.add(a5);
		notes.add(a5);
		notes.add(g4);
		notes.add(f4);
		notes.add(f4);
		notes.add(e4);
		notes.add(e4);
		notes.add(d4);
		notes.add(d4);
		notes.add(c4);
		mario.setPosition(new Point(100, 600));
		Tween marioTween = new Tween (mario);
		tInstance.add(marioTween);
		marioTween.animate(TweenableParams.ALPHA, 0.1, 1, 2000);
		marioTween.animate(TweenableParams.SCALE_X, 0.2, 1, 5000);
		marioTween.animate(TweenableParams.SCALE_Y, 0.2, 1, 5000);
		mario.addEventListener(myQuestManager, CollisionEvent.COLLISION);
		
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		NotesCollection game = new NotesCollection();
		//game.addEventListener(listener, eventType);
		game.start();
	}
	
	@Override
	public void draw(Graphics g){
		if (mario != null){
		super.draw(g);
		
		
		/* Same, just check for null in case a frame gets thrown in before Mario is initialized */
			if (!notes.isEmpty()){
				if (notes.get(0) != null && platform != null){
					notes.get(0).draw(g);
					platform.draw(g);
				}
			}
			if(mario != null) {
				mario.draw(g);
				}
		}
		
		g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
		if (gameEnded==true){
			g.drawString("Congrats, you got all the notes!", 800, 50);
			if (tInstance.getSize()==0){
				//this.stop();
			}
		}
		else {
			g.drawString("Get the notes!", 900, 50);
		}
		
		}
		
	@Override
	public void update(ArrayList<Integer> pressedKeys){
		if (gameEnded==true){
			timeElapsed=Math.min(System.currentTimeMillis()-startTime,3000);
			
		}
		if (tInstance!=null){
		
		tInstance.nextFrame();
		//System.out.println(tInstance.getSize());
		}
		//for (int i=0; i<pressedKeys.size(); i++){
		if (mario!=null){
		if ((pressedKeys.contains(37)) && mario.getPosition().getX()>=4){
			mario.getPosition().translate(-10, 0);
			//mario.getPivotPoint().translate(-20, 0);
		}
		if ((pressedKeys.contains(39)) && mario.getPosition().getX()<=1600-mario.getUnscaledWidth()){
			mario.getPosition().translate(10, 0);
			//mario.getPivotPoint().translate(20, 0);
		}
		//if ((pressedKeys.contains("Down"))){
		//	mario.getPosition().translate(0, 4);
		//	//mario.getPivotPoint().translate(0, 10);
		//}
		if ((pressedKeys.contains(38)) && mario.getPosition().getY()>=4 && this.onGround==true){
			this.marioFalling=-25;
			this.onGround=false;
			//mario.getPosition().translate(0, -10);
			//mario.getPivotPoint().translate(0, -10);
		}
		mario.getPosition().translate(0, marioFalling);
		marioFalling++;
		
		
		super.update(pressedKeys);
		
		
		}
		/* Make sure mario is not null. Sometimes Swing can auto cause an extra frame to go before everything is initialized */
		if(mario != null) mario.update(pressedKeys);
		if (mario != null){
		if (mario.getPosition().getY()>=600){
			
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
		
		if (!notes.isEmpty() && mario.collidesWith(notes.get(0))){
			try {
				soundManager.LoadSoundEffect(notes.get(0).getSound(), notes.get(0).getSound());
				soundManager.PlaySoundEffect(notes.get(0).getSound());
				collected.add(notes.get(0));
				notes.remove(0);
				if (!notes.isEmpty()){
					notes.get(0).setPosition(new Point((int)(Math.random()*800),(int)(Math.random()*400+100)));
					platform.setPosition(new Point((int)notes.get(0).getPosition().getX()-100, (int)notes.get(0).getPosition().getY()+200));
					}
				else{
					gameEnded=true;
					toPlay=false;
					gameEndedTime=System.currentTimeMillis();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (gameEnded==true){
			if (collected.isEmpty()){
				this.stop();
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
					collected.remove(0);
					toPlay=false;
					//System.out.println(toPlay);
				}	
				else{
					//System.out.println("um2");
					//System.out.println(System.currentTimeMillis()-gameEndedTime);
					if (System.currentTimeMillis()-gameEndedTime>1000){
						//System.out.println(System.currentTimeMillis()-gameEndedTime);
						gameEndedTime=System.currentTimeMillis();
						toPlay=true;
						}
					}
				}
			}
		}
	}


}
