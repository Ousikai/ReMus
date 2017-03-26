"use strict";

class TweenTransition{
  /* Create empty tweens ArrayList */
  constructor(){
  }

  linearMoveX(displayObject, tweenParam, percentDone){
    var distance = tweenParam.getEndVal() - tweenParam.getStartVal();
    var newPos = tweenParam.getStartVal() + distance * percentDone;
    displayObject.setPositionX(newPos);
  }

  linearMoveY(displayObject, tweenParam, percentDone){
    var distance = tweenParam.getEndVal() - tweenParam.getStartVal();
    var newPos = tweenParam.getStartVal() + distance * percentDone;
    displayObject.setPositionY(newPos);
  }

  halfCircleTop(displayObject, tweenParam, percentDone){
    var radiusMax = tweenParam.getEndVal();
    if (percentDone < 0.5){
      var firstHalf =  percentDone * 2;
      var radiusNow = radiusMax * firstHalf;
      var distance =  Math.sqrt(radiusNow * radiusNow);
      var newPos = tweenParam.getStartVal() - distance;
    }
    else {
      var secondHalf = (percentDone - 0.5) * 2;
      var radiusNow = radiusMax * secondHalf;
      var distance =  Math.sqrt(radiusMax*radiusMax - radiusNow*radiusNow);
      var newPos = tweenParam.getStartVal() - distance;
    }
    displayObject.setPositionY(newPos);
  }

  fadeIn(displayObject, tweenParam, percentDone){
    var alpha = percentDone;
    displayObject.setAlpha(alpha);
  }

  fadeOut(displayObject, tweenParam, percentDone){
    var alpha = 1 - percentDone;
    displayObject.setAlpha(alpha);
  }

  growX(displayObject, tweenParam, percentDone){
    var diff = tweenParam.getEndVal() - tweenParam.getStartVal();
    var newScaleX = tweenParam.getStartVal() + diff * percentDone;
    displayObject.setScaleX(newScaleX);
  }

  growY(displayObject, tweenParam, percentDone){
    var diff = tweenParam.getEndVal() - tweenParam.getStartVal();
    var newScaleY = tweenParam.getStartVal() + diff * percentDone;
    displayObject.setScaleY(newScaleY);
  }
}
