"use strict";

/**
 * A very basic display object for a javascript based gaming engine
 *
 * */
class DisplayObject extends IEventDispatcher{

	constructor(id, filename){
		super();
		this.id = id;
		this.displayImage = new Image();
		this.loaded = false;
		this.loadImage(filename);
		this.parent = null; //new DisplayObject();
		// Transformation variables
		this.visible = true;
		this.xPos = 0;
		this.yPos = 0;
		this.xPivot = 0;
		this.yPivot = 0;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
		this.rotation = 0;
		this.alpha = 1.0;
		// Transformation booleans for toggles
		this.visibleHeld = false;
		this.inPlay = true;
	}

	/**
	 * Loads the image, sets a flag called 'loaded' when the image is ready to be drawn
	 */
	loadImage(filename){
		// Get file, if it exists
		if (filename != null){
			try {
	    	this.displayImage.src = 'resources/' + filename;
				this.loaded = true;
			}
			catch(err) {
				alert("Incorrect Filename: " + filename);
			}
		}
	}

	/**
	 * Invoked every frame, manually for now, but later automatically if this DO is in DisplayTree
	 */
	update(pressedKeys){
	}

	/**
	 * Draws this image to the screen
	 */
	draw(g){
		if(this.displayImage){
			var oldSelf = this.applyTransformations(g);
			if(this.loaded && this.visible){
				g.drawImage(this.displayImage,this.xPos,this.yPos);
			}
			this.reverseTransformations(g, oldSelf);
		}
	}

	/* For debugging */
	printSelf(){
		console.log(this.visible);
		console.log(this.xPos);
		console.log(this.yPos);
		console.log(this.xPivot);
		console.log(this.yPivot);
		console.log(this.scaleX);
		console.log(this.scaleY);
		console.log(this.rotation);
		console.log(this.alpha);
		console.log("~~~~~~~~~~~~~~~~~");
	}

	/* Save old values before apply transformations */
	getOldSelf(){
		var oldSelf = new ArrayList();
		oldSelf.push(this.visible);
		oldSelf.push(this.xPos);
		oldSelf.push(this.yPos);
		oldSelf.push(this.xPivot);
		oldSelf.push(this.yPivot);
		oldSelf.push(this.scaleX);
		oldSelf.push(this.scaleY);
		oldSelf.push(this.rotation);
		oldSelf.push(this.alpha);
		return oldSelf;
	}

	/* Restore values before apply transformations */
	restoreSelf(oldSelf){
		this.visible = oldSelf.get(0);
		this.xPos = oldSelf.get(1);
		this.yPos = oldSelf.get(2);
		this.xPivot = oldSelf.get(3);
		this.yPivot = oldSelf.get(4);
		this.scaleX = oldSelf.get(5);
		this.scaleY = oldSelf.get(6);
		this.rotation = oldSelf.get(7);
		this.alpha = oldSelf.get(8);
	}


	/* Create a temporary child based on its parent attributes */
	getParentAttributes(parent){
		// Another parent exists, keep using recursion
		if (parent.parent != null){
			this.getParentAttributes(parent.parent);
		}
		/* Parent's pivot point is considered 0,0 of sprite's universe */
		this.xPos += parent.xPivot;
		this.yPos += parent.yPivot;
		/* To find out your parent's global pivot point */
		this.xPivot += parent.xPivot;
		this.yPivot += parent.yPivot;
		/* Scale by mutiplication */
		this.scaleX *= parent.scaleX;
		this.scaleY *= parent.scaleY;
	}

	/**
	 * Applies transformations for this display object to the given graphics
	 * object
	 * */
	applyTransformations(g) {
		g.save(); // ends with g.restore() in reverseTransformations()
		var oldSelf = this.getOldSelf();
		// rotate/scale around parent
		if (this.parent != null){
			this.getParentAttributes(this.parent);
			/* Lab Six Game is root, no attributes to obtain */
			if (this.parent.id == "Lab Six Game"){
				var xPivot = this.xPos;
				var yPivot = this.yPos;
			}
			/* Pivot around the global x,y position of your parent pivots,
			 * which does NOT include your personal pivot point */
			else {
				 var xPivot = this.xPivot - oldSelf.get(3);
				 var yPivot = this.yPivot - oldSelf.get(4);
			}

			g.translate(xPivot, yPivot); //translate to pivot point
			g.rotate(this.rotation*Math.PI/180); // rotation
			g.scale(this.scaleX, this.scaleY); // grow/shrink
			g.translate(-xPivot, -yPivot); //translate back to origin
			// increase/decrease alpha (not needed for lab)
			g.globalAlpha = this.alpha;
		}
		/* Needed for reverse Transformations */
		return oldSelf;
	}

	/**
	 * Reverses transformations for this display object to the given graphics
	 * object
	 * */
	reverseTransformations(g, oldSelf) {
		g.restore(); //to end g.save() in applyTransformations()
		/* Restore old variable values */
		//this.printSelf();
		this.restoreSelf(oldSelf);
	}

	/* For collision detection,
		gets global coordinates of DisplayObject */
	getHitbox(parent, hitbox){
		var root = false;
		if (hitbox == null){
			var hitbox = new Map();
			hitbox.set('left', this.xPos);
			hitbox.set('top', this.yPos);
			root = true;
		}
		/* Another parent exists, keep using recursion */
		if (parent.parent != null){
			hitbox = this.getHitbox(parent.parent);
		}

		/* Variables used by both conditionals */
		var left = hitbox.get('left');
		var top = hitbox.get('top');
		/* Return final hitbox coordinates */
		if (root){
			hitbox.set('right', left + this.getScaledWidth());
			hitbox.set('bottom', top + this.getScaledHeight());
			return hitbox;
		}
		/* Continue discovering global coordinates */
		else {
			/* Parent's pivot point is considered 0,0 of sprite's universe */
			hitbox.set('left', left + parent.xPivot);
			hitbox.set('top', top + parent.yPivot);
			return hitbox;
		}
	}
	/* Boolean for rectangle collision */
	collidesWith(other){
		/* My boundaries */
		var mhb = this.getHitbox(this.parent); //myHitbox
		var aLeft = mhb.get('left');
		var aRight = mhb.get('right');
		var aTop = mhb.get('top');
		var aBottom = mhb.get('bottom');
		/* Other Boundaries*/
		var ohb = other.getHitbox(other.parent); //otherHitBox
		var bLeft = ohb.get('left');
		var bRight = ohb.get('right');
		var bTop = ohb.get('top');
		var bBottom = ohb.get('bottom');
		/* Dispatch events on collisions  */
		if (aLeft <= bRight && bLeft <= aRight && aTop <= bBottom && bTop <= aBottom){
				if(other.id == "Sun"){
					var pickup = new Event('Pickup', other);
					this.dispatchEvent(pickup);
					this.inPlay = false;
				}
				/* Generic collisions */
				else{
					var collision = new Event('Collision', other);
					this.dispatchEvent(pickup);
				}
		}
	}

	/* Boolean for rectangle collision */
	isTouching(other){
		/* My boundaries */
		var aLeft = this.xPos;
		var aRight = this.xPos + this.getScaledWidth();
		var aTop = this.yPos;
		var aBottom = this.yPos + this.getScaledHeight();
		/* Other Boundaries*/
		var bLeft = other.xPos;
		var bRight = other.xPos + other.getScaledWidth();
		var bTop = other.yPos;
		var bBottom = other.yPos + other.getScaledHeight();
		/* Collision bools */
		return (aLeft <= bRight &&
          bLeft <= aRight &&
          aTop <= bBottom &&
          bTop <= aBottom);
	}

	/**
	 * THIS AREA CONTAINS MOSTLY GETTERS AND SETTERS!
	 *
	 */

	setId(id){this.id = id;}
	getId(){return this.id;}

	setDisplayImage(image){this.displayImage = image;} //image needs to already be loaded!
	getDisplayImage(){return this.displayImage;}

	getUnscaledHeight(){return this.displayImage.height;}
	getUnscaledWidth(){return this.displayImage.width;}

	getScaledHeight(){
		var unscaledHeight = this.getUnscaledHeight();
		return unscaledHeight*this.scaleY;
	}
	getScaledWidth(){
		var unscaledWidth = this.getUnscaledWidth();
		return unscaledWidth*this.scaleX;
	}

	/* Getters and Setters */
	// Position
	getPositionX(){return this.xPos;}
	getPositionY(){return this.yPos;}
	setPositionX(xPos){this.xPos = xPos;}
	setPositionY(yPos){this.yPos = yPos;}
	setPosition(xPos, yPos){
		this.xPos = xPos;
		this.yPos = yPos;
	}

	setEllipseRadius(base, extension){
		this.xPos = base + Math.abs(extension*Math.cos(this.rotation*Math.PI/180));
		if (this.xPos - base < 10){this.xPos = base+10;}
	}

	// Pivot Point
	getPivotPointX(){return this.xPivot;}
	getPivotPointY(){return this.yPivot;}
	setPivotPointX(xPivot){this.xPivot = xPivot;}
	setPivotPointY(yPivot){this.yPivot = yPivot;}

	// ScaleX
	getScaleX(){return this.scaleX;}
	setScaleX(scaleX){
		var limitX = scaleX;
		if (scaleX > 2.0){limitX = 2.0;}
		if (scaleX < 0.1){limitX = 0.1;}
		this.scaleX = limitX;
	}

	// ScaleX
	getScaleY(){return this.scaleY;}
	setScaleY(scaleY){
		var limitY = scaleY;
		if (scaleY > 2.0){limitY = 2.0;}
		if (scaleY < 0.1){limitY = 0.1;}
		this.scaleY = limitY;
	}

	// Rotation
	getRotation(){return this.rotaion;}
	setRotation(newRotation){
		var limitRotation = currentRotation;
		/* No angles beyond 0 or 360 degrees */
		if (limitRotation < 0){limitRotation += 360;}
		if (limitRotation > 360){limitRotation -= 360;}
		this.rotation = limitRotation;
	}
	addRotation(newRotation){
		var currentRotation = this.rotation;
		var limitRotation = currentRotation + newRotation;
		/* No angles beyond 0 or 360 degrees */
		if (limitRotation < 0){limitRotation += 360;}
		if (limitRotation > 360){limitRotation -= 360;}
		this.rotation = limitRotation;
	}

	// Alpha
	getAlpha(){return this.alpha;}
	setAlpha(alpha){
		var newAlpha = alpha;
		if (newAlpha < 0){newAlpha = 0;}
		else if (newAlpha > 1){newAlpha = 1;}
		this.alpha = newAlpha;
	}
}
