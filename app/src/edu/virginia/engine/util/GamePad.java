package edu.virginia.engine.util;

import java.util.HashMap;

import net.java.games.input.*;
import net.java.games.input.Component.Identifier;

/**
 * Wrapper class for Controllers (variable names chosen to reflect ps3 controllers)
 * */
public class GamePad {
	
	/* The controller that was detected */
	private Controller controller;
	private double lastTilt = 0;
	
	/* Constants defining the various buttons on a typical controller */
	/* Might be necessary to change the string bindings depending on your particular gamepad configuration */
	/*
	public static final String BUTTON_CROSS = "0"; //X on ps3
	public static final String BUTTON_CIRCLE = "1"; //Cross on ps3
	public static final String BUTTON_SQUARE = "2"; //Square on ps3
	public static final String BUTTON_TRIANGLE = "3"; //Triangle on ps3
	
	public static final String BUTTON_L1 = "4"; //L1 hammer on ps3
	public static final String BUTTON_R1 = "5"; //R1 hammer on ps3
	
	public static final String BUTTON_L3 = "8";
	public static final String BUTTON_R3 = "9";
	
	public static final String BUTTON_SELECT = "6";
	public static final String BUTTON_START = "7";
	
	public static final String DPAD_UP = "DPAD_UP"; //X on ps3
	public static final String DPAD_RIGHT = "DPAD_RIGHT"; //X on ps3
	public static final String DPAD_LEFT = "DPAD_LEFT"; //X on ps3
	public static final String DPAD_DOWN = "DPAD_DOWN"; //X on ps3
	*/
	
	/* Xbox 360 Mappings */
	public static final String BUTTON_A = "0";  
	public static final String BUTTON_B = "1"; 
	public static final String BUTTON_X = "2"; 
	public static final String BUTTON_Y = "3"; 
	
	public static final String BUTTON_LB = "4"; 
	public static final String BUTTON_RB = "5";
	public static final String BUTTON_TRIGGER = "z";
	public static final double BUTTON_LT = 0.1; //Left trigger if above 0.1
	public static final double BUTTON_RT = -0.1; //Right trigger if below -0.1
	
	public static final String BUTTON_BACK = "6";
	public static final String BUTTON_START = "7";
	
	public static final String LEFTSTICK_PRESS = "8";
	public static final String RIGHTSTICK_START = "9";
	
	public static final String DPAD = "pov";
	public static final double DPAD_UP = 0.25;
	public static final double DPAD_RIGHT = 0.5;
	public static final double DPAD_DOWN = 0.75;
	public static final double DPAD_LEFT = 1.0;
	
	/* Stores id to GamePadComponent pairs */
	private HashMap<String, GamePadComponent> components;
	
	public GamePad(Controller controller){
		if(controller == null) System.out.println("WARNING in GamePad.java [Constructor]: Trying to initialize a GamePad with a NULL controller");
		
		this.controller = controller;
		this.components = new HashMap<String, GamePadComponent>();
	}
	
	/**
	 * Use the constants at the top of this class as the parameter to this method
	 * */
	public boolean isButtonPressed(String buttonId){
		if(this.components.containsKey(buttonId))
			return this.components.get(buttonId).getData() != 0.0;
		return false;
	}

	public boolean isLeftTriggerPressed(){
		if(this.components.containsKey(GamePad.BUTTON_TRIGGER))
			return this.components.get(GamePad.BUTTON_TRIGGER).getData() > GamePad.BUTTON_LT;
		return false;
	}
	
	public boolean isRightTriggerPressed(){
		if(this.components.containsKey(GamePad.BUTTON_TRIGGER))
			return this.components.get(GamePad.BUTTON_TRIGGER).getData() < GamePad.BUTTON_RT;
		return false;
	}
	
	/**
	 * Use the constants at the top of this class as the parameter to this method
	 * */
	public boolean isDPadPressed(String id){
		if(this.components.containsKey("pov")){
			double data = this.components.get("pov").getData();
			if(id.equals(GamePad.DPAD_UP)) return (data>0.0 && data<0.5);
			if(id.equals(GamePad.DPAD_RIGHT)) return (data>0.25 && data<0.75);
			if(id.equals(GamePad.DPAD_DOWN)) return (data>0.5 && data<1.0);
			if(id.equals(GamePad.DPAD_LEFT)) return (data>0.75 || (data<0.25 && data>0.0));
		}
		return false;
	}
	
	private final double thresholdLeftStick = 0.5;
	public double getLeftStickXAxis(){
		if(this.components.containsKey("x")){
			double toReturn = this.components.get("x").getData();
			if(Math.abs(toReturn) > thresholdLeftStick) return toReturn;
		}
		return 0.0;
	}
	
	public double getLeftStickYAxis(){
		if(this.components.containsKey("y")){
			double toReturn = this.components.get("y").getData();
			if(Math.abs(toReturn) > thresholdLeftStick) return toReturn;
		}
		return 0.0;
	}
	
	private final double thresholdRightStick = 0.2;
	public double getRightStickXAxis(){
		if(this.components.containsKey("rx")){
			double toReturn = this.components.get("rx").getData();
			if(Math.abs(toReturn) > thresholdRightStick) return toReturn;
		}
		return 0.0;
	}
	
	public double getRightStickYAxis(){
		if(this.components.containsKey("ry")){
			double toReturn = this.components.get("ry").getData();
			if(Math.abs(toReturn) > thresholdRightStick) return toReturn;
		}
		return 0.0;
	}
	
	public boolean isRightStickTilted(){
		double xAxisAbs = Math.abs(getRightStickXAxis());
		double yAxisAbs = Math.abs(getRightStickYAxis());
		double minTilt = 0.3;
		double maxTilt = 0.8;
		double currTilt  = xAxisAbs*xAxisAbs + yAxisAbs*yAxisAbs;
		//boolean isTilted = currTilt > minTilt*minTilt;
		boolean isTilted = (currTilt > minTilt*minTilt) 
							&& ((currTilt > this.lastTilt) || (currTilt > maxTilt*maxTilt));
		this.lastTilt = currTilt;
		if (isTilted){
			return true;
		}
		//System.out.println("Not Tilted");
		return false;
	}
	
	public float getRightStickAngle(){
		double x = getRightStickXAxis();
		double y = getRightStickYAxis();
		float updatedAngle = 0;
		// Cannot make a triangle without sides!
		if (x == 0){x = 0.01;}
		if (y == 0){y = 0.01;}
		updatedAngle = (float) (Math.toDegrees(Math.atan2(y, x)));
		//System.out.println("updatedAngle: " + updatedAngle);
		return updatedAngle;
	}
	
	public void update(){
		controller.poll();
		/* Go through all components of the controller. */
        Component[] components = controller.getComponents();
        for(int i=0; i < components.length; i++)
        {
            Component component = components[i];
            Identifier id = component.getIdentifier();
            this.components.put(id.getName(), new GamePadComponent(component));
        }
	}
	
	/**
	 * DIAGNOSTIC METHOD. Invoke this if you want to see all of the buttons on your controller
	 * That are being detected and checked every frame. This will print out their name, their id, and
	 * the current value for that button.
	 * 
	 * You can use this to figure out what to set the string bindings at the top of this class to
	 * */
	public void printButtonSummary(){
		controller.poll();
		Component[] components = controller.getComponents();
		for(int i=0; i < components.length; i++)
		{
		    Component component = components[i];
		    System.out.println(component.getName() + ": " + component.getIdentifier().getName() + "; Data: " + component.getPollData());
		}
		System.out.println("---------------------");
	}

	public double getLastTilt() {
		return this.lastTilt;
	}

	public void setLastTitle(double lastTilt) {
		this.lastTilt = lastTilt;
	}

}
