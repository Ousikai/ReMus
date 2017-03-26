"use strict";

/**
 * Main class. Instantiate or extend Game to create a new game of your own
 */
class LabFourGame extends Game{

	constructor(canvas, width, height){
		super("Lab Four Game", canvas, width, height);

		/* Create sprites */
		var sun = new Sprite("Sun","sun100px.png");
		sun.setPosition(700,300-115/2); //100px is sun width, 115px is sun height
		var link = new AnimatedSprite("Link","link.png");
		var questText = new Text('QuestText', 'white', '40px Arial', 'Quest In Progress...');
		questText.setPosition(300,50);
		/* Set up observer design pattern */
		var sunListener = new ItemListener(sun);
		var questTracker = new QuestListener(questText);
		this.addEventListener(sunListener, 'Pickup');
		this.addEventListener(questTracker, 'Pickup');
		/* Create hierachy */
		this.addChild(link);
		this.addChild(sun);
		this.addChild(questText);
	}

	update(pressedKeys){
		super.update(pressedKeys);
		var sun = this.getChildById('Sun');
		var player = this.getChildById('Link');
		if (sun.visible){
			//console.log("player touching sun: %s",player.isTouching(sun));
			if (player.isTouching(sun)){
				var pickup = new Event('Pickup', player);
				this.dispatchEvent(pickup);
				this.pause();
			}
		}
	}

	draw(g){
		//Draw Canvas
		g.clearRect(0, 0, this.width, this.height);
		g.fillStyle = 'black';
		g.fillRect(0,0,this.width,this.height);
		super.draw(g);
	}
}


/**
 * THIS IS THE BEGINNING OF THE PROGRAM
 * YOU NEED TO COPY THIS VERBATIM ANYTIME YOU CREATE A GAME
 */
function tick(){
	game.nextFrame();
}

/* Get the drawing canvas off of the  */
var drawingCanvas = document.getElementById('game');
if(drawingCanvas.getContext) {
	var game = new LabFourGame(drawingCanvas, 1000, 600);
	game.start();
}
