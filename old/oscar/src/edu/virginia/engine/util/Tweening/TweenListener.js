"use strict";

/**
 * Listen for quest updates
 *
 * */
class TweenListener extends IEventListener{
  constructor(parent){
    super();
    this.parent = parent;
  }
  handleEvent(event){
    var parent = this.parent;
    if (event.getEventType() == 'SunCenter'){
      var source = event.source;
      var growSunX = new Tween(source,
        new TweenParam(source.getScaleX(), 1, 2, 1000),
        parent.transitions.growX,
        null);
      var growSunY = new Tween(source,
        new TweenParam(source.getScaleY(), 1, 2, 1000),
        parent.transitions.growY,
        null);
      var linearMoveSunX = new Tween(source,
        new TweenParam(source.getPositionX(), source.getPositionX(), 400, 1000),
        parent.transitions.linearMoveX,
        null);
      var halfCircleTopSun = new Tween(source,
        new TweenParam(source.getPositionY(), source.getPositionY(), 100, 700),
        parent.transitions.halfCircleTop,
        new Event("SunFall", source));
      parent.tweenJuggler.addTween(growSunX);
      parent.tweenJuggler.addTween(growSunY);
      parent.tweenJuggler.addTween(linearMoveSunX);
      parent.tweenJuggler.addTween(halfCircleTopSun);
    }
    if (event.getEventType() == 'SunFall'){
      var source = event.source;
      var linearMoveYSun = new Tween(source,
  			new TweenParam(source.getPositionY(), source.getPositionY(), 150, 300),
  			parent.transitions.linearMoveY,
  			new Event("SunFade", source));
      parent.tweenJuggler.addTween(linearMoveYSun);
    }
    if (event.getEventType() == 'SunFade'){
      var source = event.source;
      var linearMoveYSun = new Tween(source,
        new TweenParam(source.getPositionY(), source.getPositionY(), -50, 800),
        parent.transitions.linearMoveY,
        null);
      var fadeOutSun = new Tween(source,
        new TweenParam(source.getAlpha(), 1, 0, 800),
        parent.transitions.fadeOut,
        null);
      parent.tweenJuggler.addTween(linearMoveYSun);
      parent.tweenJuggler.addTween(fadeOutSun);

    }
    if (event.getEventType() == 'SpawnPlayer'){
      var source = event.source;
      var fadeInPlayer = new Tween(source,
  			new TweenParam(source.getAlpha(), 0, 1, 800),
  			parent.transitions.fadeIn,
  			new Event("InPlay", source));
      parent.tweenJuggler.addTween(fadeInPlayer);
    }
  }
}
