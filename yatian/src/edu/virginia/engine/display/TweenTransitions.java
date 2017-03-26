package edu.virginia.engine.display;

public class TweenTransitions {
	public static void applyTransition(double percentDone){
		
	}
	public static double easeInOut(double percentDone){ //applies a specific transition function, can have more of these for each transition your engine supports. I will only list one here.
		return (Math.sin(Math.PI*percentDone/2)*Math.sin(Math.PI*percentDone/2));
	}
	
	public static double linear(double percentDone){ //applies a specific transition function, can have more of these for each transition your engine supports. I will only list one here.
		return (percentDone);
	}
	
	public static double easeIn(double percentDone){ //applies a specific transition function, can have more of these for each transition your engine supports. I will only list one here.
		if (percentDone<0.3){
			return 0;
		}
		return (((percentDone-0.3)/0.7)*((percentDone-0.3)/0.7));
	}
	
	public static double easeOut(double percentDone){ //applies a specific transition function, can have more of these for each transition your engine supports. I will only list one here.
		return (1-(1-percentDone)*(1-percentDone));
	}
}
