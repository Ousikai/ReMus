package edu.virginia.lab6test;

import engine.events.Event;
import engine.events.IEventListener;

public class MyQuestManager implements IEventListener{

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		if (event.getEventType().equals(CollisionEvent.COLLISION)){
			//((DisplayObject) event.getSource()).setVisible(false);
			//System.out.println("Collision Detected");
		}
		
		if (event.getEventType().equals(CoingrabbedEvent.COINGRABBED)){
			//((DisplayObject) event.getSource()).setVisible(false);
			System.out.println("Got the coin!");
		}
	}

}
