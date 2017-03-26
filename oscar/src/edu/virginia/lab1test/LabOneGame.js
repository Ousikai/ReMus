"use strict";

/**
 * Main class. Instantiate or extend Game to create a new game of your own
 */
class LabOneGame extends Game{

	constructor(canvas, width, height){
		super("Lab One Game", width, height, canvas);
		this.mario = new AnimatedSprite("Mario", "Mario.png");
		//Set Pivot Point
		this.mario.setPivotPointX(0);
		this.mario.setPivotPointY(0);
		// Set initial timer
		this.gameClock = new GameClock();
		this.timerText = canvas.getContext('2d');
		this.timerVal = 60;
		// Set initial lives
		this.livesText = canvas.getContext('2d');
		this.livesVal = 3;
		// Set endgame text
		this.endText = canvas.getContext('2d');
		// this.endFlag == 0 is game in progress
		// this.endFlag == 1 is Player 1 victory
		// this.endFlag == 2 is Player 2 victory
		this.endFlag = 0;
	}

	update(pressedKeys){
		super.update(pressedKeys, 500, 300);
		this.mario.update(pressedKeys, 500, 300);
		// Deplete 1 second at a time
		if (this.gameClock.getElapsedTime() >= 1000){
			this.timerVal -= 1;
			this.gameClock.resetGameClock();
		}
		// For lab 1, update if mouseclick inside mario sprite
		if (this.xClick != -500 && this.yClick != -500){
			// 120px * 130px is the width and height of a link.png sprite
			var xMarioLeft = this.mario.xPos;
			var xMarioRight = (this.mario.xPos + 120) * this.mario.scaleX;
			var yMarioDown = this.mario.yPos;
			var yMarioUp = (this.mario.yPos + 130) * this.mario.scaleY;
			var xBounds = xMarioLeft < this.xClick && this.xClick < xMarioRight;
			var yBounds = yMarioDown < this.yClick && this.yClick < yMarioUp;
			// Hit if mouseclick inside sprite boundaries
			if (xBounds && yBounds){
				console.log("Hit!!");
				this.livesVal -= 1;
			}
			//else {console.log("Miss!!");}
			// Reset mouseclick after evalation
			this.xClick = -500;
			this.yClick = -500;
		}
		// Player 1 wins!
		if (this.timerVal ==  0){
			this.endFlag = 1;
			super.pause();
		}
		// Player 2 wins!
		if (this.livesVal <= 0){
			this.endFlag = 2;
			super.pause();
		}
		// End of game variables
		if (this.endFlag == 1 || this.endFlag == 2){
			this.mario.visible = true; //Show mario if he was invisible
		}
	}

	draw(g){
		//Draw Canvas
		g.clearRect(0, 0, this.width, this.height);
		g.fillStyle = 'black';
		g.fillRect(0,0,this.width,this.height);
		super.draw(g);
		//Draw mario
	 	this.mario.draw(g);
		// Draw pivot point
		g.fillStyle = 'cyan';
		var ppx = this.mario.getPivotPointX();
		var ppy = this.mario.getPivotPointY();
		g.fillRect(ppx,ppy,10,10);
		g.strokeRect(ppx,ppy,10,10);
		//Draw timer
		this.timerText.fillStyle = 'white';
		this.timerText.font = "30px Arial";
		this.timerText.fillText("Timer: " + this.timerVal,360,40);
		//Draw Lives
		this.livesText.fillText("Lives: " + this.livesVal,360,80);
		//Draw endText
		this.endText.font = "60px Arial";
		// Player 1 Victory
		if (this.endFlag == 1) {
			this.timerText.fillStyle = 'red';
			this.endText.fillText("Player 1 Wins! ",60,160);
		}
		// Player 2 Victory
		if (this.endFlag == 2) {
			this.timerText.fillStyle = 'green';
			this.endText.fillText("Player 2 Wins! ",60,160);
		}
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
	var game = new LabOneGame(drawingCanvas, 500, 300);
	game.start();
}
