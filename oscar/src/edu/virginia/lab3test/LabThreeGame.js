"use strict";

/**
 * Main class. Instantiate or extend Game to create a new game of your own
 */
class LabThreeGame extends Game{

	constructor(canvas, width, height){
		super("Lab Three Game", canvas, width, height);
		/* Initialize Sun */
		var sun = new Sprite("Sun","sun100px.png");
		var scx = width/2; //SunCenterX (arbitrary center of universe)
		var scy = height/2; //SunCenterY (arbitrary center of universe)
		// Set pivot point of game to be center of universe
		this.setPivotPointX(scx);
		this.setPivotPointY(scy);
		// Set pivot point of sun to also be center of unverse
		sun.setPivotPointX(scx);
		sun.setPivotPointY(scy);
		sun.setPosition(scx-100/2, scy-115/2); //100px is sun width, 115px is sun height
		/* Initialize Earth */
		var earth = new Sprite("Earth","earth50px.png");
		earth.setPosition(0,-50/2); // 50px is width of earth
		var mars = new Sprite("Mars","mars50px.png");
		mars.setPosition(0,-50/2); // 50px is width of mars
		var neptune = new Sprite("Neptune","neptune50px.png");
		mars.setPosition(0,-50/2); // 50px is width of neptune
		var jupiter = new Sprite("Jupiter","jupiter50px.png");
		mars.setPosition(0,-50/2); // 50px is width of jupiter
		var moon1 = new Sprite("Moon1","moon25px.png");
		moon1.setPosition(0,-25/2); // 25px is width of moon1
		var moon2 = new Sprite("Moon2","moon25px.png");
		moon2.setPosition(0,-25/2); // 25px is width of moon2
		var moon3 = new Sprite("Moon3","neptune25px.png");
		moon3.setPosition(0,-25/2); // 25px is width of moon2
		var moon4 = new Sprite("Moon4","earth25px.png");
		moon4.setPosition(0,-25/2); // 25px is width of moon2
		var moon5 = new Sprite("Moon5","mars25px.png");
		moon5.setPosition(0,-25/2); // 25px is width of moon2
		//moon2.setScaleX(2);
		//moon2.setScaleY(2);
		/* Create hierachy */
		this.addChild(sun);
		sun.addChild(earth);
		sun.addChild(mars);
		sun.addChild(neptune);
		sun.addChild(jupiter);
		earth.addChild(moon1);
		moon1.addChild(moon2);
		mars.addChild(moon3);
		neptune.addChild(moon4);
		jupiter.addChild(moon5);
	}

	update(pressedKeys){
		super.update(pressedKeys);
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
	var game = new LabThreeGame(drawingCanvas, 1000, 600);
	game.start();
}
