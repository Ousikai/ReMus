package edu.virginia.engine.display;

import java.util.ArrayList;

import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.listeners.HookEvent;

public class PhysicsSprite extends Sprite {

	private boolean inPlay = true;
	private boolean inAir = true;
	private int yVel = 0;
	private int gravity = 1;
	private float crosshairsAngle = 0;
	private boolean hookReady = true;
	//private GameClock inAirTime = new GameClock();
	
	public PhysicsSprite(String id) {
		super(id);
	}

	public PhysicsSprite(String id, String imageFileName) {
		super(id, imageFileName);
	}
	
	@Override
	public void update(ArrayList<Integer> pressedKeys) {
		super.update(pressedKeys);
		/* If player can accept inputs */
		
		if (this.inPlay){
			/* (Up) Jump into the air */
			if ((pressedKeys.contains(38)) && !this.isInAir()){
				this.yVel = -15;
				this.inAir = true;
			}
			/* (Left) Move to the left */
			if ((pressedKeys.contains(37)) && this.getPosition().getX()>=4){
				this.getPosition().translate(-10, 0);
				//mario.getPivotPoint().translate(-20, 0);
			}
			/* (Right) Move to the right */
			if ((pressedKeys.contains(39)) && this.getPosition().getX()<=1600-this.getUnscaledWidth()){
				this.getPosition().translate(10, 0);
				//mario.getPivotPoint().translate(20, 0);
			}
			/* (Q) Rotate crosshairs counterclockwise */
			if (pressedKeys.contains(81)){
				this.crosshairsAngle -= 5;
			}
			/* (E) Rotate crosshairs clockwise */
			if (pressedKeys.contains(69)){
				this.crosshairsAngle += 5;
			}
			/* (Spacebar) Launch hookshot */
			if (pressedKeys.contains(32) && this.hookReady){
				this.dispatchEvent(new Event(HookEvent.HOOKPRESSED,null));
			}
			/* Down (which is 40) */
			/* If player is in the Air*/
			if (this.inAir){
				this.yVel += this.gravity;
				this.getPosition().translate(0, this.yVel);
			}
		}
	}

	public boolean isInPlay() {
		return this.inPlay;
	}

	public void setInPlay(boolean newStatus) {
		this.inPlay = newStatus;
	}


	public int getYVel() {
		return this.yVel;
	}

	public void setYVel(int newYVel) {
		this.yVel = newYVel;
	}

	public void resetFall() {
		this.yVel = 0;
		this.inPlay = true;
		this.inAir = true;
	}	

	public boolean isInAir() {
		return inAir;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public float getCrosshairsAngle() {
		return crosshairsAngle;
	}

	public void setCrosshairsAngle(float crosshairsAngle) {
		this.crosshairsAngle = crosshairsAngle;
	}

	public boolean getHookReady() {
		return hookReady;
	}

	public void setHookReady(boolean hookReady) {
		this.hookReady = hookReady;
	}
	
}
