"use strict";

/**
 * Object encapsulating the information
 * regarding a single event that occurs.
 * */
class TweenEvent extends Event{
  constructor(eventType, tween){
    this.eventType = eventType;
    this.tween = tween;
  }
  /* Getters and Setters */
  getTween(){return this.tween;}
  setTween(tween){this.tween = tween;}
}
