"use strict";

/**
 * Equivalent to “observable”.
 * Lists the methods necessary for any object that throws
 * events.
 * */
class IEventDispatcher extends Globals{
  constructor(){
    super();
    this.map = {};
  }

  hasEventListener(listener, eventType){
    if (eventType in this.map){
      var listenerList = this.map[eventType]; //listenerlist
      for (var i=0; i<listenerList.size();i++){
        var currListener = listenerList.get(i);
        if (currListener == listener){console.log("key DOES contains listener");return true;}
      }
      return false;
    }
    /* event does not exist */
    else {
     return false;
    }
  }
  addEventListener(listener, eventType){
    if (!this.hasEventListener(listener, eventType)){
      /* Event type already has listeners */
      if (eventType in this.map){
        var listenerList = this.map[eventType];
        delete this.map[eventType];
        listenerList.push(listener);
        this.map[eventType] = listenerList;
      }
      /* Add new event listener! */
      else {
        var listenerList = new ArrayList();
        listenerList.push(listener);
        this.map[eventType] = listenerList;
      }
    }
  }
  /* Removes event listener,
   * and eventType if list of listeners is 0*/
  removeEventListener(listener, eventType){
    if (this.hasEventListener(listener, eventType)){
      var listenerList = this.map[eventType];
      listenerList.remove(listener);
      /* Update listenerList for eventType */
      if (listenerList.size() > 0){
        listenerList.push(listener);
        this.map[eventType] = listenerList;
      }
      /* Remove event type as key */
      else {delete this.map[eventType];}
    }
  }
  dispatchEvent(event){
    var eventType = event.getEventType();
    var listenerList = this.map[eventType];
    /* Send event to all listeners */
    for (var i=0; i<listenerList.size();i++){
      var listener = listenerList.get(i);
      listener.handleEvent(event);
    }
  }
}
