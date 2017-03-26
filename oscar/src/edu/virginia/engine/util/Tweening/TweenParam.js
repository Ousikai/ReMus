"use strict";

class TweenParam{
  /* Create empty tweens ArrayList */
  constructor(paramToTween, startVal, endVal, endTime){
    this.paramToTween = paramToTween;
    this.startVal = startVal;
    this.endVal = endVal;
    this.endTime = endTime;
  }
  getParam(){return this.paramToTween;}
  getStartVal(){return this.startVal;}
  getEndVal(){return this.endVal;}
  getEndTime(){return this.endTime;}
}
