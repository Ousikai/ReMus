package edu.virginia.engine.util.listeners;

import java.awt.Point;

import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.IEventDispatcher;
import edu.virginia.engine.util.IEventListener;

public class PlayerListener implements IEventListener {

	private PhysicsSprite parent;
	private TweenJuggler tweenJuggler;
	
	public PlayerListener(PhysicsSprite parent, TweenJuggler tweenJuggler){
		this.parent = parent;
		this.tweenJuggler = tweenJuggler;
	}
	
	@Override
	public void handleEvent(Event event) {
		/* Start falling at 0 yVel */
		if (event.getEventType().equals(PlayerEvent.ResetFall)){
			this.parent.setHookReady(true);
			this.parent.resetFall();
		}
		/* Fall ontop of platform */
		if (event.getEventType().equals(CollisionEvent.PLATFORM)){
			Sprite source = (Sprite) event.getSource();
			int parentX = (int) parent.getPosition().getX();
			int parentYOverlap = (int) parent.getPosition().getY() + parent.getUnscaledHeight();
			int platformY = (int) source.getPosition().getY();
			int overlapY = parentYOverlap - platformY;
			int newY = (int) parent.getPosition().getY() - overlapY;
			parent.setPosition(new Point(parentX, newY));
			parent.setInAir(false);
			parent.setYVel(0);
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
