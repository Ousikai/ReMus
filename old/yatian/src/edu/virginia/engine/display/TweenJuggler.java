package edu.virginia.engine.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TweenJuggler {
	private static TweenJuggler instance;
	private static ArrayList<Tween> tweens;
	
	public TweenJuggler(){
		if(instance != null) {
			System.out.println("ERROR: Cannot re-initialize singleton class!");
		}
		
		else {instance = this;
		tweens = new ArrayList<Tween>();
		}
	}
	
	public static TweenJuggler getInstance(){
		return instance;
		}
	
	public static void add(Tween tween){
		//if (!(tweens.contains(tween))){
		//	tween.setStartTime();
		
		tweens.add(tween);
		//}
	}
	public void nextFrame(){ //invoked every frame by Game, calls update() on every Tween and cleans up old /complete Tweens
		//try {
		//System.out.println("Nextframe Called");
		ArrayList<Tween> tweensCopy = (ArrayList<Tween>)tweens.clone();
		for (int i = 0; i< tweensCopy.size(); i++){
			Tween tween = tweensCopy.get(i);
			tween.update();
		
			if (tween.isComplete()==true){
				tweens.remove(tween);
			}
		}		
	//} catch (Exception e) {
	//	System.out
	//			.println("Exception in nextFrame of game. Stopping game (no frames will be drawn anymore");
	//	e.printStackTrace();
	//}
	}
	
	public int getSize(){
		return tweens.size();
	}

}
