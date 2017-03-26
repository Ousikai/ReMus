"use strict";

/**
 * Main class. Instantiate or extend Game to create a new game of your own
 */
class LabSixGame extends Game{

	constructor(canvas, width, height){
		super("Lab Six Game", canvas, width, height);
		/* Set up BGM and background */
    this.playBGM();
		var background = new DisplayObject("Background","background0.gif");
		background.setScaleX(2.0);
		background.setScaleY(1.5);
		background.setAlpha(0.7);
		this.frameCurrent = 0;
		this.framesPassed = 0;
		/* Create sprites */
		var sun = new Sprite("Sun","sun100px.png");
		sun.setPosition(850, 25);
		var player = new PhysicsSprite("Player","link.png");
    player.setPosition(0, 350);
		player.setAlpha(0);
		player.inPlay = false;
    //TODO: Fix Scaling with Sprite Sheets
    //player.setScaleX(0.5);
    //player.setScaleY(0.5);
		var questText = new Text('QuestText', 'white', '40px Arial', 'Press R to respawn!');
		questText.setPosition(350,50);
    /* Set up platforms */
    var blockCollection = this.blockCollection();
		/* Set up observer design pattern */
		var sunListener = new ItemListener(sun);
		var questTracker = new QuestListener(questText);
    var playerListener = new PlayerListener(player);
		var tweenListener = new TweenListener(this);
		player.addEventListener(sunListener, 'Pickup');
		player.addEventListener(questTracker, 'Pickup');
    player.addEventListener(playerListener, 'CollisionBlock');
    player.addEventListener(playerListener, 'Jump');
    player.addEventListener(playerListener, 'InAir');
		player.addEventListener(playerListener, 'Respawn');
		player.addEventListener(tweenListener, 'SpawnPlayer');
		player.addEventListener(playerListener, 'InPlay');
		sun.addEventListener(tweenListener, 'SunCenter');
		sun.addEventListener(tweenListener, 'SunFall');
		sun.addEventListener(tweenListener, 'SunFade');
		/* Create hierachy */
		this.addChild(background);
    this.addChild(blockCollection);
		this.addChild(sun);
    this.addChild(player);
		this.addChild(questText);
		/* Set up tweens */
		this.tweenJuggler = new TweenJuggler();
		this.transitions = new TweenTransition();
		/* Add tweens to TweenJuggler */
		player.dispatchEvent(new Event("Respawn", player));
	}

	update(pressedKeys){
		super.update(pressedKeys);
    var player = this.getChildById('Player');
		var sun = this.getChildById('Sun');
    var blockCollection = this.getChildById('BlockCollection');
  if (sun.inPlay){
      player.collidesWith(sun);
		}
    var onGround = blockCollection.collidesWithCollection(player);
    if (!onGround){
      var inAir = new Event("InAir", player);
      player.dispatchEvent(inAir);
    }
		/* Backgroud animation */
		this.framesPassed += 1;
		if (this.framesPassed > 20){
			var bg = this.getChildById('Background');
			this.frameCurrent += 1;
			if (this.frameCurrent > 2){this.frameCurrent = 0;}
			bg.loadImage('background'+this.frameCurrent+'.gif');
			this.framesPassed = 0;
		}
	}

	draw(g){
		//Draw Canvas
		g.clearRect(0, 0, this.width, this.height);
		g.fillStyle = 'white';
		g.fillRect(0,0,this.width,this.height);
		super.draw(g);
		this.tweenJuggler.nextFrame();
	}

  /* Creates the platform for the level */
  blockCollection(){
    var blockCollection = new DisplayObjectContainer("BlockCollection", null);
    var xPos = 0;
    var yPos = 575;
    var i = 0;
    /* Bottom boundary */
    for (i=0; i < 20; i++){
      var block = new Sprite("Block","block50px.png");
      block.setPosition(xPos,yPos);
      blockCollection.addChild(block);
      xPos += 50;
    }
    /* Platform 1 */
    xPos = 150;
    yPos = 450;
    for (i=0; i < 3; i++){
      var block = new Sprite("Block","block50px.png");
      block.setPosition(xPos,yPos);
      blockCollection.addChild(block);
      xPos += 50;
    }
    /* Platform 2 */
    xPos = 350;
    yPos = 325;
    for (i=0; i < 2; i++){
      var block = new Sprite("Block","block50px.png");
      block.setPosition(xPos,yPos);
      blockCollection.addChild(block);
      xPos += 50;
    }
    /* Platform 3 */
    xPos = 500;
    yPos = 200;
    for (i=0; i < 2; i++){
      var block = new Sprite("Block","block50px.png");
      block.setPosition(xPos,yPos);
      blockCollection.addChild(block);
      xPos += 50;
    }
    /* Platform 4 */
    xPos = 760;
		yPos = 300;
    for (i=0; i < 1; i++){
      var block = new Sprite("Block","block50px.png");
      block.setPosition(xPos,yPos);
      blockCollection.addChild(block);
      xPos += 50;
    }
    /* Return collection of blocks */
    return blockCollection;
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
	var game = new LabSixGame(drawingCanvas, 1000, 600);
	game.start();
}
