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
			this.parent.resetFall();
		}
		/* Fall ontop of platform */
		if (event.getEventType().equals(CollisionEvent.PLATFORM)){
			Sprite source = (Sprite) event.getSource();
			int sourceX = (int) source.getPosition().getX();
			int sourceY = (int) source.getPosition().getY();
			parent.setInAir(false);
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
