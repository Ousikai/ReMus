package edu.virginia.engine.util.listeners;

import java.awt.Point;

import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.display.Platform;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.IEventDispatcher;
import edu.virginia.engine.util.IEventListener;
import edu.virginia.engine.util.SoundManager;

public class PlayerListener implements IEventListener {

	private PhysicsSprite parent;
	private TweenJuggler tweenJuggler;
	SoundManager soundManager;
	
	public PlayerListener(PhysicsSprite parent, TweenJuggler tweenJuggler, SoundManager soundManager){
		this.parent = parent;
		this.tweenJuggler = tweenJuggler;
		this.soundManager = soundManager;
	}
	
	@Override
	public void handleEvent(Event event) {
		/* Start falling at 0 yVel */
		if (event.getEventType().equals(PlayerEvent.ResetFall)){
			this.parent.resetFall();
		}
		/* Fall ontop of platform */
		if (event.getEventType().equals(CollisionEvent.PLATFORM)){
			Sprite source = (Sprite) event.getSource();
			int parentX = (int) parent.getPosition().getX();
			int parentYOverlap = (int) parent.getPosition().getY() + parent.getUnscaledHeight();
			int platformY = (int) source.getPosition().getY();
			int overlapY = parentYOverlap - platformY - 1;
			int newY = (int) parent.getPosition().getY() - overlapY;
			parent.setPosition(new Point(parentX, newY));
			parent.setInAir(false);
			parent.setYVel(0);
		}
		/* Fall ontop of horizontal moving platform  */
		if (event.getEventType().equals(CollisionEvent.HORIZONTAL)){
			Platform source = (Platform) event.getSource();
			int parentX = (int) parent.getPosition().getX();
			int parentYOverlap = (int) parent.getPosition().getY() + parent.getUnscaledHeight();
			int platformY = (int) source.getPosition().getY();
			int overlapY = parentYOverlap - platformY - 1;
			int newY = (int) parent.getPosition().getY() - overlapY;
			//Move with horizontal platform
			int shiftsPerUpdate = source.getShiftsPerUpdate();
			if (source.isPositive()){shiftsPerUpdate *= -1;}
			int newX = parentX + shiftsPerUpdate;
			//Set everything up
			parent.setPosition(new Point(newX, newY));
			parent.setInAir(false);
			parent.setYVel(0);
		}
		/* Fall ontop of vertical moving platform  */
		if (event.getEventType().equals(CollisionEvent.VERTICAL)){
			Platform source = (Platform) event.getSource();
			int parentX = (int) parent.getPosition().getX();
			int parentYOverlap = (int) parent.getPosition().getY() + parent.getUnscaledHeight();
			int platformY = (int) source.getPosition().getY();
			int overlapY = parentYOverlap - platformY - 1;
			int newY = (int) parent.getPosition().getY() - overlapY;
			//Move with horizontal platform
			int shiftsPerUpdate = source.getShiftsPerUpdate();
			if (source.isPositive()){shiftsPerUpdate *= -1;}
			newY += shiftsPerUpdate;
			//Set everything up
			parent.setPosition(new Point(parentX, newY));
			parent.setInAir(false);
			parent.setYVel(0);
		}
		// Jump on a bouncy pad!
		if (event.getEventType().equals(CollisionEvent.BOUNCY)){
			//soundManager.PlaySoundEffect("Bounce");
			parent.resetFall();
			parent.setYVel(-30);
		}
		/* Fall to the bottom of the screen */
		if (event.getEventType().equals(CollisionEvent.GROUND)){
			parent.setPosition(new Point((int)parent.getPosition().getX(), 600));
			parent.resetFall();
			parent.setInAir(false);
		}
		if (event.getEventType().equals(CollisionEvent.INAIR)){
			parent.setInAir(true);
			
		}
	}

}
