package edu.virginia.engine.display;

import java.awt.Point;
import java.util.ArrayList;

import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.GamePad;

public class Platform extends Sprite {
	
	//Animated Platforms
	TweenJuggler tweenJuggler;
	private double updateTimer;
	GameClock gameClock;
	//Moving platforms 
	private int shiftsPerUpdate;
	private int radius;
	private int displacement;
	private boolean isPositive = true;
	
	// Platforms that do not need to move 
	public Platform(String id, int x, int y) {
		super(id);
		String imageName = "";
		if (id.equals(PlatformType.DEFAULT)){
			imageName = "platformBlue.png";
		}
		if (id.equals(PlatformType.BOUNCY)){
			imageName = "platformOrange.png";
		}
		if (id.equals(PlatformType.HANG)){
			imageName = "platformLightGreen.png";
		}
		if (id.equals(PlatformType.STATIONARY)){
			imageName = "platformPink.png";
		}	
		this.setImage(imageName);
		this.setPosition(new Point(x,y));
	}
	
	//Basically only for invisible platforms 
	public Platform(String id, int x, int y, TweenJuggler tweenJuggler, int updateTimer) {
		super(id);
		String imageName = "";
		if (id.equals(PlatformType.INVISIBLE)){
			imageName = "platformLightBlue.png";
			this.setAlpha(0);
		}		
		this.setImage(imageName);
		this.setPosition(new Point(x,y));
		//Animation variables 
		this.updateTimer = updateTimer;
		this.gameClock = new GameClock();
		this.tweenJuggler = tweenJuggler;
	}
	
	// For moving platforms! 
	public Platform(String id, int x, int y, int shiftsPerUpdate, int radius) {
		super(id);
		String imageName = "";
		if (id.equals(PlatformType.HORIZONTAL)){
			imageName = "platformRed.png";
		}
		if (id.equals(PlatformType.VERTICAL)){
			imageName = "platformYellow.png";
		}			
		this.setImage(imageName);
		this.setPosition(new Point(x,y));
		//Moving platform variables
		this.setShiftsPerUpdate(shiftsPerUpdate);
		this.radius = radius;
		this.displacement = 0;
		this.isPositive = true;
		
	}
	
	@Override
	public void update(ArrayList<Integer> pressedKeys, ArrayList<GamePad> controllers) {
		super.update(pressedKeys, controllers);
		// Extra functionality for unique platforms
		if (this.getId().equals(PlatformType.HORIZONTAL)){
			Point currentPoint = this.getPosition();
			int x = (int) currentPoint.getX();
			int y = (int) currentPoint.getY();
			//Go up
			if (this.isPositive){
				this.setPosition(new Point(x - this.shiftsPerUpdate, y));
				this.displacement += this.shiftsPerUpdate;
				// Reached max lift 
				if (Math.abs(this.displacement) >= this.radius){
					this.displacement = 0;
					this.isPositive = false;						
				}					
			}
			//Else go down
			else {
				this.setPosition(new Point(x + this.shiftsPerUpdate, y));
				this.displacement += this.shiftsPerUpdate;
				//Reached max descent
				if (Math.abs(this.displacement) >= this.radius){
					this.displacement = 0;
					this.isPositive = true;			
				}
			}			}
		if (this.getId().equals(PlatformType.VERTICAL)){
			Point currentPoint = this.getPosition();
			int x = (int) currentPoint.getX();
			int y = (int) currentPoint.getY();
			//Go up
			if (this.isPositive){
				this.setPosition(new Point(x, y - this.shiftsPerUpdate));
				this.displacement += this.shiftsPerUpdate;
				// Reached max lift 
				if (Math.abs(this.displacement) >= this.radius){
					this.displacement = 0;
					this.isPositive = false;						
				}					
			}
			//Else go down
			else {
				this.setPosition(new Point(x, y + this.shiftsPerUpdate));
				this.displacement += this.shiftsPerUpdate;
				//Reached max descent
				if (Math.abs(this.displacement) >= this.radius){
					this.displacement = 0;
					this.isPositive = true;			
				}
			}				
		}
		if (this.getId().equals(PlatformType.INVISIBLE)){
			if (this.gameClock.getElapsedTime() > this.updateTimer){
				//Fade out if fully visible 
				if (this.getAlpha() == 1){
					Tween fadeOut = new Tween (this, null, null);
					fadeOut.animate(TweenableParams.ALPHA, 1, 0, 500);
					tweenJuggler.add(fadeOut);					
				}
				//Else fade in!
				else {
					Tween fadeIn = new Tween (this, null, null);
					fadeIn.animate(TweenableParams.ALPHA, 0, 1, 500);
					tweenJuggler.add(fadeIn);			
				}				
				this.gameClock = new GameClock();
			}
		}
	}

	public double getUpdateTimer() {
		return updateTimer;
	}

	public void setUpdateTimer(double updateTimer) {
		this.updateTimer = updateTimer;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

	public int getDisplacement() {
		return displacement;
	}

	public void setDisplacement(int displacement) {
		this.displacement = displacement;
	}

	public int getShiftsPerUpdate() {
		return shiftsPerUpdate;
	}

	public void setShiftsPerUpdate(int shiftsPerUpdate) {
		this.shiftsPerUpdate = shiftsPerUpdate;
	}

}
