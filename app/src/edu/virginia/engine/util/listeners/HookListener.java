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
	private Sprite hookshot;
	private TweenJuggler tweenJuggler;
	private boolean hookablePlatform;
	private Point hookPoint;
	private int timeTweening = 200;
	private double maxWidth = 1.0;
	
	public HookListener(PhysicsSprite parent, TweenJuggler tweenJuggler, Sprite hookshot){
		this.parent = parent;
		this.hookshot = hookshot;
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
		}
		if (event.getEventType().equals(HookEvent.CANNOTHOOK)){
			this.hookablePlatform = false;
			this.setHookPoint(null);
		}
		//Hookshot expanding from player
		if (event.getEventType().equals(HookEvent.HOOKPRESSED)){
			//Set up hookshot expanding
			hookshot.setVisible(true);
			parent.setHookReady(false);
			Event hookRetract = new Event(HookEvent.HOOKRETRACT, this.parent);
			Tween hookExpand = new Tween (hookshot, null, hookRetract);
			this.tweenJuggler.add(hookExpand);
			if (this.hookablePlatform){
				// Animate player flying to platform
				/*
				this.parent.setInPlay(false);
				Point hookPoint = this.hookPoint;
				int newX = (int) hookPoint.getX();
				int newY = (int) hookPoint.getY();
				Event parentHop = new Event(HookEvent.HOOKHOP, this.parent);
				Tween parentFly = new Tween (this.parent, null, parentHop);
				int oldX = (int) this.parent.getPosition().getX();
				int oldY = (int) this.parent.getPosition().getY();
				parentFly.animate(TweenableParams.X, oldX, newX, this.timeTweening);
				parentFly.animate(TweenableParams.Y, oldY, newY, this.timeTweening);
				this.tweenJuggler.add(parentFly);
				*/
				// Change hookshot distance based on proximity
				int newX = (int) hookPoint.getX();
				int newY = (int) hookPoint.getY();
				int hookX = (int) hookshot.getPosition().getX();
				int hookY = (int) hookshot.getPosition().getY();
				double hypot = Math.hypot(hookX-newX, hookY-newY);
				this.maxWidth = hypot/parent.getHookshotLength();
			  // Hookshot will reach max length but hit nothing	
			} else {
				this.maxWidth = 1.0;
				this.timeTweening = 200;
			}
			//Animate hookshot expanding
			hookExpand.animate(TweenableParams.SCALE_X, 0, maxWidth, this.timeTweening);
		}
		// Hookshot retracting to player
		if (event.getEventType().equals(HookEvent.HOOKRETRACT)){
			//Fly with hookshot!
			if (this.hookablePlatform){
				this.parent.setInPlay(false);
				Point hookPoint = this.hookPoint;
				int newX = (int) hookPoint.getX();
				int newY = (int) hookPoint.getY();
				Event parentHop = new Event(HookEvent.HOOKHOP, this.parent);
				Tween parentFly = new Tween (this.parent, null, parentHop);
				int oldX = (int) this.parent.getPosition().getX();
				int oldY = (int) this.parent.getPosition().getY();
				parentFly.animate(TweenableParams.X, oldX, newX, this.timeTweening);
				parentFly.animate(TweenableParams.Y, oldY, newY, this.timeTweening);
				this.tweenJuggler.add(parentFly);				
			}
			Event hookReady = new Event(HookEvent.HOOKREADY, this.parent);
			Tween hookRetract = new Tween (hookshot, null, hookReady);
			hookRetract.animate(TweenableParams.SCALE_X, maxWidth, 0.1, this.timeTweening);
			this.tweenJuggler.add(hookRetract);
		}
		// Can use hookshot again
		if (event.getEventType().equals(HookEvent.HOOKREADY)){
			this.parent.setHookReady(true);
			hookshot.setVisible(false);
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

	public double getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(double maxWidth) {
		this.maxWidth = maxWidth;
	}
}
