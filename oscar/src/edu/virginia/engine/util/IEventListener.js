"use strict";

/**
 * Defines what all listeners must do.
 *
 * */
class IEventListener extends IEventDispatcher{
  constructor(){
    //Adapt in subclass
    super();
  }
  handleEvent(event){
    //Adapt in subclass
  }
}
