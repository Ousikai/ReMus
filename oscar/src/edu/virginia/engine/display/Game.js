"use strict";

/**
 * Main class. Instantiate or extend Game to create a new game of your own
 */
class Game extends DisplayObjectContainer{

	constructor(gameId, canvas, width, height){
		super();
		Game.instance = this;
		this.gameId = gameId;
		this.setId(gameId);
		this.width = width;
		this.height = height;
		this.canvas = canvas;
		this.g = canvas.getContext('2d'); //the graphics object
		this.playing = false;
		this.pressedKeys = new ArrayList();
		this.xClick = -500;
		this.yClick = -500;


		/* Setup a key listener */
		window.addEventListener("keydown", onKeyDown, true);
		window.addEventListener("keyup", onKeyUp, true);
		this.canvas.addEventListener("click", onClick, true);
	}

	static getInstance(){ return Game.instance; }

	// Respond to keypresses in game engine
	update(pressedKeys){
		super.update(pressedKeys);
		//if (pressedKeys.size() > 0){this.playing = !this.playing;}
	}

	// Draw canvas
	draw(g){
		super.draw(g);
	}

	nextFrame(){
		game.update(this.pressedKeys);
		game.draw(this.g);
		if(this.playing) window.requestAnimationFrame(tick);
	}

	start(){
		this.playing = true;
		window.requestAnimationFrame(tick); //Notice that tick() MUST be defined somewhere! See LabOneGame.js for an example
	}

	pause(){
		this.playing = false;
	}

	/**
	 * For dealing with keyCodes
	 */
	addKey(keyCode){
		//console.log("Key Code DOWN: " + keyCode); //for your convenience, you can see what the keyCode you care about is
		if(this.pressedKeys.indexOf(keyCode) == -1) this.pressedKeys.push(keyCode);
	}

	removeKey(keyCode){
		//console.log("Key Code UP: " + keyCode);
		this.pressedKeys.remove(keyCode);
		}

		setClickPos(xClick, yClick){
			this.xClick = xClick;
			this.yClick = yClick;
		}

}

function onKeyDown(e){
	Game.getInstance().addKey(e.keyCode);

  }
function onKeyUp(e){
	Game.getInstance().removeKey(e.keyCode);
}

// Get x and y location of mouse clicks
function onClick(e){
	//console.log("Hi there2");
	//var xClick = e.offsetX;
	var xClick = e.offsetX;
	var yClick = e.offsetY;
	Game.getInstance().setClickPos(xClick, yClick);
}
