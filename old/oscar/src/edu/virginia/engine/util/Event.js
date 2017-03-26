"use strict";

/**
 * Object encapsulating the information
 * regarding a single event that occurs.
 * */
class Event{
  constructor(eventType, source){
    this.eventType = eventType;
    this.source = source;
  }
  /* Getters and Setters */
  getEventType(){return this.eventType;}
  setEventType(newEventType){this.eventType = newEventType;}
  getSource(){return this.source;}
  setSource(newSource){this.source = newSource;}
}
