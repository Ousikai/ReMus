"use strict";

/**
 * Listen for quest updates
 *
 * */
class QuestListener extends IEventListener{
  constructor(parent){
    super();
    this.parent = parent;
  }
  handleEvent(event){
    if (event.getEventType() == 'Pickup'){
      this.parent.setText('QUEST COMPLETE!!');
    }
  }
}
