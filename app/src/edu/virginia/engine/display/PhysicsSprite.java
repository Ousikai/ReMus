package edu.virginia.engine.display;

import java.util.ArrayList;

import edu.virginia.engine.util.GameClock;

public class PhysicsSprite extends Sprite {

	private boolean inPlay = true;
	private boolean inAir = true;
	private int yVel = 0;
	private int gravity = 1;
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
			/* Up */
			if ((pressedKeys.contains(38)) && !this.isInAir()){
				this.yVel = -15;
				this.inAir = true;
			}
			/* Left */
			if ((pressedKeys.contains(37)) && this.getPosition().getX()>=4){
				this.getPosition().translate(-10, 0);
				//mario.getPivotPoint().translate(-20, 0);
			}
			/* Right */
			if ((pressedKeys.contains(39)) && this.getPosition().getX()<=1600-this.getUnscaledWidth()){
				this.getPosition().translate(10, 0);
				//mario.getPivotPoint().translate(20, 0);
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
		return inPlay;
	}

	public void setInPlay(boolean inPlay) {
		this.inPlay = inPlay;
	}


	public int getYVel() {
		return yVel;
	}

	public void setYVel(int yVel) {
		this.yVel = yVel;
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
	
}
