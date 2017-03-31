package edu.virginia.engine.display;

import engine.events.Event;

public class TweenEvent extends Event{
	Tween tween;
	//final static strings denoting the various events (e.g., TWEEN_COMPLETE_EVENT)
	public static final String TWEEN_COMPLETE_EVENT="Tween Complete Event";
	public static final String TWEEN_START_EVENT="Tween Start Event";
	public static final String TWEEN_UPDATE_EVENT="Tween Update Event";
	public TweenEvent(String eventType, Tween tween){
		this.setEventType(eventType);
		this.tween = tween;
	}
	public Tween getTween(){
		return tween;
	}
}
