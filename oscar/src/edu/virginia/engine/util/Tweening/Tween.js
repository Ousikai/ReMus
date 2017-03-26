"use strict";

class Tween{
  /* Create empty tweens ArrayList */
  constructor(displayObject, tweenParam, tweenTransition, tweenEvent){
    this.object = displayObject;
    this.tweenParam = tweenParam;
    this.transition = tweenTransition;
    this.event = tweenEvent;
    this.time = new GameClock();
  }

  update(){
    var percentDone = this.getTweenTime()/this.tweenParam.getEndTime();
    if (percentDone > 1){percentDone = 1;}
    this.transition(this.object, this.tweenParam, percentDone);
    return this.isComplete();
  }

  getTweenTime(){return this.time.getElapsedTime();}

  isComplete(){return this.getTweenTime() >= this.tweenParam.getEndTime();}
}
