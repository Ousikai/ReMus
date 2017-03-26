

/**
 * This class current only works for the link.ping running animation,
 * but will be updated to support xml files
 * */
class PhysicsSprite extends AnimatedSprite{

	constructor(id, filename, xml){
		super(id, filename, xml);
    this.gravity = 10;
		this.xVel = 0.0;
    this.yVel = 0.0;
    this.yAccel = 0.0;
    this.yMaxFall = 20;
		this.jumpHeld = false;
		this.respawnHeld = false;
    this.inAir = true;
    this.inAirTime = new GameClock();
	}

	/**
	 * Invoked every frame, manually for now, but later automatically if this DO is in DisplayTree
	 */
	update(pressedKeys){
    //super.update(pressedKeys);
		/* Only move if player is inPlay */
		if (this.inPlay){
			/* Respawn with R */
			if (pressedKeys.contains(82) && !this.respawnHeld){
				this.inAirTime.resetGameClock();
				this.xVel = 0;
				this.yVel = 0;
				this.inPlay = false;
				this.respawnHeld = true;
				this.dispatchEvent(new Event("Respawn", this));
			}
			/* On the ground, can jump */
	    if (pressedKeys.contains(38) && !this.inAir && !this.heldJump) {
	      var jumpEvent = new Event("Jump", null);
	      this.dispatchEvent(jumpEvent);
	      this.yVel -= 60;
				this.jumpHeld = true;
	      this.inAir = true;
	    }

			//TODO: Find out why superjumps are happening
			//console.log("this.yVel:%d", this.yVel);

			if (!pressedKeys.contains(38)){this.jumpHeld = false;}
			if (!pressedKeys.contains(82)){this.respawnHeld = false;}
			/* Handle left/right movement */
	    // Go left
	    if (pressedKeys.contains(37)) {
				this.xVel = -5;
			}
	    // Go right
	    if (pressedKeys.contains(39)) {
				this.xVel = 5;
			}
			/* Add velocity to xPos */
			this.xPos += this.xVel;
			if (this.xVel > 0){this.xVel -= 1;}
			else if (this.xVel < 0) {this.xVel += 1;}
	    /* Handle gravity */
	    if (this.inAir){
	      //this.inAirTime += 0.01;
	      var elapsedTime = this.inAirTime.getElapsedTime()/1000;
	      var yDiff = this.yVel * elapsedTime;
	      this.yVel += this.gravity * elapsedTime;
	      // Terminal velocity
	      if (this.yVel > this.yMaxFall){this.yVel = this.yMaxFall;}
	      this.yPos += yDiff;
	    }
			/* Temporary method for animating player */
			if (this.inAir){
				this.currSprite = 0;
				this.spriteX = 840;
				this.spriteY = 520;
			}
			else if (this.xVel != 0 && !this.inAir){
				if (this.currSprite == -1){
					this.currSprite = 0;
					this.spriteY = 520;
				}
				this.nextFrame();
			}
			// Go back to idle frame if no movement
			else {
				this.currSprite = -1;
				this.spriteX = 0;
				this.spriteY = 0;
			}
		}
	}

	/**
	 * Draws this image to the screen
	 */
	draw(g){
		super.draw(g);
	}

  onGround(){
    this.yVel = 0.0;
    this.yAccel = 0.0
    this.inAir = false;
    this.inAirTime.resetGameClock();
  }
}
