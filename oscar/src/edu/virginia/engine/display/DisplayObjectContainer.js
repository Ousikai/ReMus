"use strict";

/**
 * A very basic display object for a javascript based gaming engine
 *
 * */
class DisplayObjectContainer extends DisplayObject{

  constructor(id, filename){
    super(id, filename);
    this.children = new ArrayList();
  }

  /**
   * Invoked every frame, manually for now, but later automatically if this DO is in DisplayTree
   */
  update(pressedKeys){
    super.update(pressedKeys);
    // Update Children
    for	(var i = 0; i < this.numberOfChildren(); i++) {
      var child = this.getChildAtIndex(i);
      child.update(pressedKeys);
    }
  }

  /**
   * Draws this image to the screen
   */
  draw(g){
    // Draw Self
    super.draw(g);
    // Draw Children
    for	(var i = 0; i < this.numberOfChildren(); i++) {
      var child = this.getChildAtIndex(i);
      child.draw(g);
    }
  }
  /* Check individual collisions with children */
  /* PROTOTYPE */
  collidesWithCollection(other){
    var collidesSomewhere = false;
    for	(var i = 0; i < this.numberOfChildren(); i++) {
      var child = this.getChildAtIndex(i);
      /* My boundaries */
  		var mhb = child.getHitbox(child.parent); //myHitbox
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
          var newEvent;
          if (child.id == "Block"){
            var hitboxAdjust = new Map();
            hitboxAdjust.set('Player', other);
            hitboxAdjust.set('PlayerHitbox', ohb);
            hitboxAdjust.set('Block', child);
            hitboxAdjust.set('BlockHitbox', mhb);
            newEvent = new Event('CollisionBlock', hitboxAdjust);
          }
          /* Generic collisions */
          else{
            newEvent = new Event('Collision', other);
          }
          other.dispatchEvent(newEvent);
          collidesSomewhere =  true;
  		}
    }
    return collidesSomewhere;
  }

  /**
  * Children Methods
  */

  /* Returns number of children */
  numberOfChildren(){
   return this.children.size();
  }

  /* Returns index of first instance of child */
  indexOf(child){
   return this.children.indexOf(child);
  }

  /* Boolean method for whether a child already has THIS as a parent */
  contains(DisplayObject){return (this.indexOf(DisplayObject) != -1); }

  // You're a parent now!
  addChild(child){
   // Only add unique children (recurring sprites have different ids)
   if (!this.contains(child)){
     child.parent = this;
     this.children.push(child);
   }
  }

  /* Returns child at index (if child exists)*/
  getChildAtIndex(index){
    var noc = this.numberOfChildren();
    if (index < noc){return this.children.itemAt(index);}
    return -1; //no such index
  }

  /* Returns child by name/id (if child exists) */
  getChildById(id){
    var noc = this.numberOfChildren();
    for	(var i = 0; i < noc; i++) {
      var child = this.getChildAtIndex(i);
      var bool = child.id == id;
      if (child.id == id){return child;}
    }
    return -1; //no such child exists
  }

  /* Returns array list of children objects */
  getChildren(){
    return this.children;
  }

  /* A tragic accident */
  removeChildById(id){
    var children = new ArrayList();
    var noc = this.numberOfChildren();
		for(var i = 0; i < noc; i++){
      var child = this.getChildAtIndex(i);
			if (child.id != id){children.push(child);}
		}
    this.removeAllChildren(); //Remove current set of children
		this.children = children; //Updated arraylist of children!
  }

  /* A tragically specific accident */
  removeChildAtIndex(index){
   this.children.removeAt(index);
  }

  /* A Series of Unfortunate Events */
  removeAllChildren(){
    var noc = this.numberOfChildren();
    for	(var i = 0; i < noc; i++) {
      this.removeChildAtIndex(0);
    }
  }

}
