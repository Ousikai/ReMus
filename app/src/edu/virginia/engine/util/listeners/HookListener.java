package edu.virginia.engine.util.listeners;

import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.Timer;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenEvent;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.EventDispatcher;
import edu.virginia.engine.util.IEventListener;

public class HookListener implements IEventListener {

	private PhysicsSprite parent;
	private TweenJuggler tweenJuggler;
	private boolean hookablePlatform;
	private Point hookPoint;
	
	public HookListener(PhysicsSprite parent, TweenJuggler tweenJuggler){
		this.parent = parent;
		this.tweenJuggler = tweenJuggler;
		this.hookablePlatform = false;
	}
	
	@Override
	public void handleEvent(Event event) {
		/* Parent initially flies to hooked platform */
		if (event.getEventType().equals(HookEvent.CANHOOK)){
			this.hookablePlatform = true;
			DisplayObject hookObject = (DisplayObject) event.getSource();
			this.setHookPoint(hookObject.getPosition());
			int hookX = (int) this.hookPoint.getX();
			int hookY = (int) this.hookPoint.getY();
			
		}
		if (event.getEventType().equals(HookEvent.CANNOTHOOK)){
			this.hookablePlatform = false;
			this.setHookPoint(null);
		}
		if (event.getEventType().equals(HookEvent.HOOKPRESSED) && this.hookablePlatform){
			this.parent.setHookReady(false);
			this.parent.setInPlay(false);
			Point hookPoint = this.hookPoint;
			int newX = (int) hookPoint.getX();
			int newY = (int) hookPoint.getY();
			Event parentHop = new Event(HookEvent.HOOKHOP, this.parent);
			Tween parentFly = new Tween (this.parent, null, parentHop);
			this.tweenJuggler.add(parentFly);
			int oldX = (int) this.parent.getPosition().getX();
			int oldY = (int) this.parent.getPosition().getY();
			/*TODO: changed timeTweening to reflect distance of hookshot
			(shorter distances will have shorter tween times) */
			int timeTweening = 200;
			parentFly.animate(TweenableParams.X, oldX, newX, timeTweening);
			parentFly.animate(TweenableParams.Y, oldY, newY, timeTweening);
		}
		/* Parent hops onto platform after end of hookshot */
		if (event.getEventType().equals(HookEvent.HOOKHOP)){
			Event resetFall = new Event(PlayerEvent.ResetFall, this.parent);
			Tween parentHop = new Tween (this.parent, null, resetFall);
			this.tweenJuggler.add(parentHop);
			int oldY = (int) this.parent.getPosition().getY();
			int newY = oldY - 200;
			int timeTweening = 250;
			parentHop.animate(TweenableParams.Y, oldY, newY, timeTweening);
		}
	}

	public Point getHookPoint() {
		return hookPoint;
	}

	public void setHookPoint(Point hookPoint) {
		this.hookPoint = hookPoint;
	}
}
