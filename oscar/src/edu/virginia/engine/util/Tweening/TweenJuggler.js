"use strict";

/**
 * Object encapsulating the information
 * regarding a single event that occurs.
 * */
class TweenJuggler{
  /* Create empty tweens ArrayList */
  constructor(){
    this.tweenJuggler = new ArrayList();
  }
  /* Call update on every Tween */
  nextFrame(){
    var numberofTweens = this.tweenJuggler.size();
    for	(var i = 0; i < numberofTweens; i++) {
      var tween = this.tweenJuggler.get(i);
      var isComplete = tween.update();
      /* Remove tween if it is complete */
      if (isComplete){
        this.tweenJuggler.removeAt(i);
        /* There are more tweens after this removal */
        if (i + 1 < numberofTweens){
          i -= 1;
          numberofTweens -=1;
        }
        if (tween.event != null){
          var recipient = tween.event.getSource();
          recipient.dispatchEvent(tween.event);
        }
      }
    }
  }
  /* Add Tween to TweenJuggler*/
  addTween(tween){
    this.tweenJuggler.add(tween);
  }
}
