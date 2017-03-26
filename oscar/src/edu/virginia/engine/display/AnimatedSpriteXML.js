

/**
 * This class current only works for the link.ping running animation,
 * but will be updated to support xml files
 * */
class AnimatedSprite extends Sprite{

	constructor(id, filename, xml){
		super(id, filename);
		this.xml = xml;
		this.frameNumber = 0;
		this.frameLimit = 0; //how many animations in a cycle
		// When to move on to the next frame
		this.framesPassed = 0;
		this.frameCount = 20;
		// Location in sprite sheet
		this.spriteX = 0;
		this.spriteY = 0;
		// For parsing sprite in sprite sheet
		this.spriteWidth = 120;
		this.spriteHeight = 130;
	}

	/**
	 * Invoked every frame, manually for now, but later automatically if this DO is in DisplayTree
	 */
	update(pressedKeys){
		var oldX = this.xPos;
		var oldY = this.yPos;
		/* Update Link xPos/yPos based on keypresses */
		if (pressedKeys.size() > 0 && this.id=='Link'){
			// Rate of change for each transformation
			var scaleTranslate = 10;
			/* Translations (Sprite) */
			var vert = 0;
			var hori = 0;
			// up
			if (pressedKeys.contains(40)) {vert += 1;}
			// down
			if (pressedKeys.contains(38)) {vert += -1;}
			// left
			if (pressedKeys.contains(37)) {hori += -1;}
			// right
			if (pressedKeys.contains(39)) {hori += 1;}
			// multiply scalar of movement
			this.xPos += hori*scaleTranslate;
			this.yPos += vert*scaleTranslate;
		}
		// Event trigger for any movement with arrow keys
		if (oldX != this.xPos || oldY != this.yPos){
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

	/**
	 * Draws this image to the screen
	 */
	draw(g){
		//super.draw(g);
		if(this.displayImage){
			var oldSelf = super.applyTransformations(g);
			if(this.loaded && this.visible){
			  //drawImage(image, sx, sy, sWidth, sHeight, dx, dy, dWidth, dHeight)
				// s is location of sprite in source image, d is destination in canvas
				//console.log("%d | %d | %d | %d", this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight);
				g.drawImage(this.displayImage, this.spriteX, this.spriteY, this.spriteWidth, this.spriteHeight,
					this.xPos, this.yPos, this.spriteWidth, this.spriteHeight)
			}
			super.reverseTransformations(g, oldSelf);
		}
	}

	nextFrame(){
		this.framesPassed++;
		// Animate next frame if sufficient in-game update()s have passed
		if (this.framesPassed >= this.frameCount){
			this.currSprite++;
			// End of animation cycle, start over at beginning of cycle
			if (this.currSprite >= this.spriteLimit){
				this.currSprite = 0;
				this.spriteX = 0;
			}
			// Go to next frame in animation cycle
			else {
				this.spriteX = this.currSprite * this.spriteWidth; //Shift to next part of framethis.spriteLimit++;
			}
		} //if (this.framesPassed >= this.frameCount)
	} //nextFrame()

	xmlRequest(){
		var request = new XMLHttpRequest();
		request.open("GET", "resources/"+ this.xml, false);
		request.send();
		var xml = request.responseXML;
		return xml;
	}

	animationType(type, flipped){
		var xml = this.xmlRequest();
		var idle = xml.getElementsByTagName(type);
		var frameNumber = this.frameNumber;
		var thisFrame = idle.getElementsByTagName("frame"+frameNumber);
		this.spriteX = thisFrame.getElementsByTagName("x").childNodes[0].nodeValue;
		this.spriteY = thisFrame.getElementsByTagName("y").childNodes[0].nodeValue;
		this.spriteWidth = thisFrame.getElementsByTagName("width").childNodes[0].nodeValue;
		this.spriteHeight = thisFrame.getElementsByTagName("height").childNodes[0].nodeValue;
		console.log("idle anim: %d | %d | %d | %d |",
				this.spriteX,
				this.spriteY,
				this.spriteWidth,
				this.spriteHeight);
	}// idleAnimation()

	/* Getters and Setters */
	/* Update for scaling */
	getUnscaledWidth(){return this.spriteWidth;}
	getUnscaledHeight(){return this.spriteHeight;}
	getScaledWidth(){return this.spriteWidth*this.scaleX;}
	getScaledHeight(){return this.spriteHeight*this.scaleY;}
}
