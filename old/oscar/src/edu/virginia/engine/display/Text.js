"use strict";

/**
 * A very basic Sprite. For now, does not do anything.
 *
 * */
class Text extends DisplayObjectContainer{

	constructor(id, fillStyle, font, text){
		super(id, null);
    this.fillStyle = fillStyle;
    this.font = font;
    this.text = text;
	}

	/**
   * Invoked every frame, manually for now, but later automatically if this DO is in DisplayTree
   */
  update(pressedKeys){
      super.update(pressedKeys);
  }

	/**
	 * Draws this image to the screen
	 */
	draw(g){
    var oldSelf = this.applyTransformations(g);
    if(this.visible){
      g.fillStyle = this.fillStyle;
		  g.font = this.font;
      g.fillText(this.text,this.xPos,this.yPos);
    }
    this.reverseTransformations(g, oldSelf);
	}

  /* Set text to be displayed */
  setFillStyle(fillStyle){
    this.fillStyle = fillStyle;
  }

  /* Set text to be displayed */
  setText(text){
    this.text = text;
  }
}
