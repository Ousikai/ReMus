package edu.virginia.lab6test;

import java.awt.MouseInfo;

import javax.swing.Timer;

import edu.virginia.engine.display.PhysicsSprite;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenEvent;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import engine.events.Event;
import engine.events.EventDispatcher;
import engine.events.IEventListener;

public class HookListener implements IEventListener {

	private PhysicsSprite parent;
	private TweenJuggler tweenJuggler;
	private boolean hookablePlatform;
	
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
		}
		if (event.getEventType().equals(HookEvent.CANNOTHOOK)){
			this.hookablePlatform = false;
		}
		if (event.getEventType().equals(HookEvent.HOOKPRESSED) && this.hookablePlatform){
			java.awt.Point mouseCoor = MouseInfo.getPointerInfo().getLocation();
			Event parentHop = new Event(HookEvent.HOOKHOP, this.parent);
			Tween parentFly = new Tween (this.parent, null, parentHop);
			this.tweenJuggler.add(parentFly);
			int oldX = (int) this.parent.getPosition().getX();
			int oldY = (int) this.parent.getPosition().getY();
			int newX = (int) mouseCoor.getX() - 50;
			int newY = (int) mouseCoor.getY();
			/*TODO: changed timeTweening to reflect distance of hookshot
			(shorter distances will have shorter tween times) */
			int timeTweening = 500;
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

}
