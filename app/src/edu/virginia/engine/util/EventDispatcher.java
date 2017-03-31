package edu.virginia.engine.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EventDispatcher implements IEventDispatcher{

	private HashMap<String, Set<IEventListener>> eventListeners; 
	
	public EventDispatcher(){
		eventListeners=new HashMap<String, Set<IEventListener>>();
	}
	public void addEventListener(IEventListener listener, String eventType) {
		// TODO Auto-generated method stub
		if (eventListeners.get(eventType) != null){
			eventListeners.get(eventType).add(listener);
		}
		else {
			Set<IEventListener> s = new HashSet<IEventListener>();
			s.add(listener);
			
			eventListeners.put(eventType, s);
		}
		
	}

	public void removeEventListener(IEventListener listener, String eventType) {
		// TODO Auto-generated method stub
		if (eventListeners.containsValue(listener)){
			eventListeners.remove(eventType, listener);
		}
		
	}

	public void dispatchEvent(Event event) {
		// TODO Auto-generated method stub
		
		Set<IEventListener> s = eventListeners.get(event.getEventType());
		
		if (s != null){
			for (IEventListener listener : s){
				listener.handleEvent(event);
			}
		}
		//event.setSource(this);
		//event.setEventType(eventType);
	}

	public boolean hasEventListener(IEventListener listener, String eventType) {
		// TODO Auto-generated method stub
		if (!eventListeners.isEmpty()){
			return true;
		}
		return false;
	}

}
