"use strict";

/**
 * Listen for quest updates
 *
 * */
class PlayerListener extends IEventListener{
  constructor(parent){
    super();
    this.parent = parent;
  }
  handleEvent(event){
    if (event.getEventType() == 'CollisionBlock'){
      /* Player center of rectangles */
      var playerHitbox = event.source.get('PlayerHitbox');
      var pLeft = playerHitbox.get('left');
      var pRight = playerHitbox.get('right');
      var pTop = playerHitbox.get('top');
      var pBottom = playerHitbox.get('bottom');
      /* Block center of rectangles */
      var blockHitbox = event.source.get('BlockHitbox');
      var bLeft = blockHitbox.get('left');
      var bRight = blockHitbox.get('right');
      var bTop = blockHitbox.get('top');
      var bBottom = blockHitbox.get('bottom');
      //console.log("left: %d \n right: %d \n top: %d \n bottom: %d \n", playerHitbox.get('left'),playerHitbox.get('right'),playerHitbox.get('top'),playerHitbox.get('bottom'));
      //TODO: Better collision detection for sides
      // SMALLEST POSITIVE SIDE OF COLLISION
      var collTop = pBottom - bTop;
      var collBottom = bBottom - pTop;
      var collLeft = pRight - bLeft;
      var collRight = bRight - pLeft;
      var arrColl = new ArrayList();
      arrColl.push(collTop);
      arrColl.push(collBottom);
      arrColl.push(collLeft);
      arrColl.push(collRight);
      var collSide = Number.MAX_SAFE_INTEGER;
      for	(var i = 0; i < 4; i++) {
        var num = arrColl.get(i);
        if (num < collSide && num > 0){collSide = num;}
      }
      /* In the air and traveling upwards */
      if (this.parent.inAir && this.parent.yVel < 0){
        //console.log("Bottom Collision");
        this.parent.yPos += (50);
        var momentum = Math.ceil(-1 * (this.parent.yVel)/2);
        this.parent.yVel = momentum;
      }
      else if (collSide = collTop && this.parent.inAir && this.parent.yVel >= 0){
        //console.log("Top Collision");
        var vertAdjust = pBottom - bTop;
        this.parent.yPos -= (vertAdjust);
        /* Update inAirBool */
        this.parent.onGround();
      }
      else if (collSide = collLeft && pBottom > bBottom && pTop < bTop){
        //console.log("Left Collision");
        this.parent.xPos -= (50);
        var momentum = Math.ceil(-1 * (this.parent.xVel)/2);
        this.parent.xVel = momentum;
      }
      else if (collSide = collRight && pBottom > bBottom && pTop < bTop){
        //console.log("Right Collision");
        this.parent.xPos += (50);
        var momentum = Math.ceil(-1 * (this.parent.xVel)/2);
        this.parent.xVel = momentum;
      }
    }
    if (event.getEventType() == 'Respawn'){
      this.parent.setPosition(0, 350);
      var audio = new Audio('resources/sfx_Grow.wav');
      audio.play();
      this.parent.dispatchEvent(new Event("SpawnPlayer",this.parent));
    }
    if (event.getEventType() == 'InPlay'){
      this.parent.inPlay = true;
      this.parent.inAirTime.resetGameClock()
    }
    if (event.getEventType() == 'Jump'){
      var audio = new Audio('resources/Jump.mp3');
      audio.play();
    }
    if (event.getEventType() == 'InAir'){
      this.parent.inAir = true;
    }
  }
}
