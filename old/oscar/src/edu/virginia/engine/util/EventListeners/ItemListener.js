"use strict";

/**
 * Listen for quest updates
 *
 * */
class ItemListener extends IEventListener{
  constructor(parent){
    //Adapt in subclass
    super();
    this.parent = parent;
  }
  handleEvent(event){
    if (event.getEventType() == 'Pickup'){
      this.pauseBGM();
      var audio = new Audio('resources/music_VictoryFanfare.mp3');
      audio.play();
      this.parent.inPlay = false;
      this.parent.dispatchEvent(new Event("SunCenter", this.parent));
      //this.parent.visible = false;
    }
  }
}
