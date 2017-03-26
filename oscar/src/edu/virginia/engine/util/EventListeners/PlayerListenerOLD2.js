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
      var player = event.source.get('Player');
      var playerHitbox = event.source.get('PlayerHitbox');
      var playerCenterX = player.xPos + player.getScaledWidth()/2;
      var playerCenterY = player.yPos + player.getScaledHeight()/2;
      /* Block center of rectangles */
      var block = event.source.get('Block');
      var blockHitbox = event.source.get('BlockHitbox');
      var blockCenterX = block.xPos + block.getScaledWidth()/2;
      var blockCenterY = block.yPos + block.getScaledHeight()/2;
      // Calculate the vertical and horizontal
      // length between the centres of rectangles
      w = 0.5 * (player.getScaledWidth() + block.getScaledWidth());
      h = 0.5 * (player.getScaledHeight() + block.getScaledHeight());
      dx = A.centerX() - B.centerX();
      dy = A.centerY() - B.centerY();
      //console.log("pcx:%d pcy:%d bcx:%d bcy:%d ",playerCenterX, playerCenterY, blockCenterX, blockCenterY);
      //console.log("hd: %d | vd: %s", hd, vd);
      //console.log("left: %d \n right: %d \n top: %d \n bottom: %d \n", playerHitbox.get('left'),playerHitbox.get('right'),playerHitbox.get('top'),playerHitbox.get('bottom'));
      if (){
        if () {
          console.log("Right Collision");
          var horiAdjust = blockHitbox.get('right') - playerHitbox.get('left');
          console.log("blockHitbox: %d, playerHitbox: %d, horiAdjust: %d", blockHitbox.get('right'), playerHitbox.get('left'), horiAdjust);
          this.parent.xPos += (horiAdjust);
        }
        else {
          console.log("Left Collision");
          var horiAdjust = playerHitbox.get('right') - blockHitbox.get('left');
          this.parent.xPos -= (horiAdjust);
        }
      }
      else if () {
        if () {
          console.log("Bottom Collision");
          var vertAdjust = blockHitbox.get('bottom') - playerHitbox.get('top') ;
          this.parent.yPos += (vertAdjust);
        }
        else {
          console.log("Top Collision");
          var vertAdjust = playerHitbox.get('bottom') - blockHitbox.get('top');
          this.parent.yPos -= (vertAdjust);
          this.parent.onGround();
        }
      }
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
