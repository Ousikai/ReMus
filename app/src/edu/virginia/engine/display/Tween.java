package edu.virginia.engine.display;

import java.awt.Point;
import java.util.ArrayList;

import engine.events.Event;

public class Tween {
	private DisplayObject object;
	private TweenTransitions transition;
	private ArrayList<TweenParam> params;
	private double startTime;
	private Event event;
	
	public Tween(DisplayObject object){
		this.object=object;
		params = new ArrayList<TweenParam>();
		startTime=System.currentTimeMillis();
	}
	public Tween(DisplayObject object, TweenTransitions transition){
		this.object=object;
		this.transition=transition;
		params = new ArrayList<TweenParam>();
		startTime=System.currentTimeMillis();
		
	}
	
	public Tween(DisplayObject object, TweenTransitions transition, Event event){
		this.object=object;
		this.transition=transition;
		params = new ArrayList<TweenParam>();
		startTime=System.currentTimeMillis();
		this.event = event;
	}
	public void animate(TweenableParams fieldToAnimate, double startVal, double endVal, double time){
		TweenParam param= new TweenParam (fieldToAnimate, startVal, endVal, time);
		params.add(param);
		
	}
	
	public void setStartTime(){
		this.startTime = System.currentTimeMillis();
	}
	public void update(){ //invoked once per frame by the TweenJuggler. Updates this tween / DisplayObject
		//System.out.println("Update called");
		ArrayList<TweenParam> paramsCopy = (ArrayList<TweenParam>)params.clone();
		for (TweenParam param : paramsCopy){
			if (param.getParam()==TweenableParams.X){
				Point p = new Point ((int)param.getStartVal(), (int)object.getPosition().getY());
				object.setPosition(p);
				//double percent=param.getPercent();
				double percent = Math.min(1,(System.currentTimeMillis()-startTime)/param.getTweenTime());
				double distance = param.getEndVal()-param.getStartVal();
				double movement = TweenTransitions.linear(percent)*distance;
				object.getPosition().translate((int)movement, 0);
			}
			
			else if (param.getParam()==TweenableParams.Y){
				Point p = new Point ((int)object.getPosition().getX(), (int)param.getStartVal());
				object.setPosition(p);
				//double percent=param.getPercent();
				double percent = Math.min(1,(System.currentTimeMillis()-startTime)/param.getTweenTime());
				double distance = param.getEndVal()-param.getStartVal();
				double movement = TweenTransitions.linear(percent)*distance;
				object.getPosition().translate(0, (int)movement);
			}
			
			else if (param.getParam()==TweenableParams.SCALE_X){
				object.setScaleX(param.getStartVal());
				//double percent=param.getPercent();
				double percent = Math.min(1,(System.currentTimeMillis()-startTime)/param.getTweenTime());
				double distance = param.getEndVal()-param.getStartVal();
				double movement = TweenTransitions.easeInOut(percent)*distance;
				//System.out.println(percent);
				//System.out.println(startTime);
				//System.out.println(System.currentTimeMillis());
				//System.out.println(percent);
				object.setScaleX(param.getStartVal()+movement);
			}
			
			else if (param.getParam()==TweenableParams.SCALE_Y){
				object.setScaleY(param.getStartVal());
				//double percent=param.getPercent();
				double percent = Math.min(1,(System.currentTimeMillis()-startTime)/param.getTweenTime());
				double distance = param.getEndVal()-param.getStartVal();
				double movement = TweenTransitions.easeInOut(percent)*distance;
				object.setScaleY(param.getStartVal()+movement);
			}
			
			else if (param.getParam().equals(TweenableParams.ROTATION)){
				object.setRotation(param.getStartVal());
				//double percent=param.getPercent();
				double percent = Math.min(1,(System.currentTimeMillis()-startTime)/param.getTweenTime());
				double distance = param.getEndVal()-param.getStartVal();
				double movement = TweenTransitions.linear(percent)*distance;
				object.setRotation(movement);
			}
			
			else if (param.getParam().equals(TweenableParams.ALPHA)){
				//object.setAlpha((float) param.getStartVal());
				//double percent=param.getPercent();
				//System.out.println(System.currentTimeMillis()-startTime);
				double percent = Math.min(1,(System.currentTimeMillis()-startTime)/param.getTweenTime());
				//System.out.println(percent);
				double distance = param.getEndVal()-param.getStartVal();
				double movement = TweenTransitions.easeIn(percent)*distance;
				//System.out.println(movement);
				object.setAlpha((float)(movement+param.getStartVal()));
			}
			if ((System.currentTimeMillis()-startTime) / param.getTweenTime() > 1){
				params.remove(param);
			}
		}
	}
	public boolean isComplete(){
		if (params.size()==0){
			return true;
			}
		return false;
	}
	public void setValue(TweenableParams param, double value){
		if (param == TweenableParams.X){
			Point p = new Point ((int)value, (int)object.getPosition().getY());
			object.setPosition(p);
		}
		
		else if (param == TweenableParams.Y){
			Point p = new Point ((int)object.getPosition().getX(), (int)value);
			object.setPosition(p);
		}
		
		else if (param == TweenableParams.SCALE_X){
			object.setScaleX(value);
		}
		
		else if (param == TweenableParams.SCALE_Y){
			object.setScaleY(value);
		}
		
		else if (param == TweenableParams.ROTATION){
			object.setRotation(value);
		}
		
		else if (param == TweenableParams.ALPHA){
			object.setAlpha((float) value);
		}
	}
	
	public Event getEvent(){
		return this.event;
	}
}
