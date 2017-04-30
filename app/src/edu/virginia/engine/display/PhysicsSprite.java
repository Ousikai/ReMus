package edu.virginia.engine.display;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.GamePad;
import edu.virginia.engine.util.listeners.HookEvent;

public class PhysicsSprite extends Sprite {

	/* Physics variables */
	private boolean inPlay = true;
	private boolean inAir = true;
	private int xVel = 0;
	private int xSlip = 3;
	private int yVel = 0;
	private int jumpHeight = 15;
	private int gravity = 1;
	private GameClock gravityTimer = new GameClock();
	private float crosshairsAngle = 0;
	private boolean hookReady = true;
	private int hookshotLength = 200;
	/* Animation variables */
	private Document rootXML;
	private String stance = AnimationStance.IDLERIGHT;
	private boolean facingRight = true;
	private int currentFrame = 0;
	private int frameLimit = 1; 
	private int timePerFrame = 200;
	private GameClock animationTimer = new GameClock();
	

	public PhysicsSprite(String id, String imageFileName) {
		super(id, imageFileName);
		loadXML();
	}
	
	@Override
	public void update(ArrayList<Integer> pressedKeys, ArrayList<GamePad> controllers) {
		super.update(pressedKeys, controllers);
		/* If player can accept inputs */
		if (this.inPlay){
			/* Gamepad not connected */
			if (controllers.isEmpty()){
				//System.out.println("Keyboard only!");
				keyboardOnly(pressedKeys);
			}
			/* Gamepad connected! */
			else {
				//System.out.println("Gamepad activated!");
				keyboardAndXbox360(pressedKeys, controllers.get(0));
			}
			/* Walking */
			if (this.xVel != 0){
				/* Animate walking */
				continueAnimation();
				this.getPosition().translate(this.xVel, 0);
				//Player is moving left
				if (this.xVel > 0){
					this.xVel -= this.xSlip;
				}
				// Player is moving left
				else{
					this.xVel += this.xSlip;
				}
			}
			/* Idle */
			else {
				if (!stance.equals(AnimationStance.IDLERIGHT) && !stance.equals(AnimationStance.IDLELEFT)){
					if (this.facingRight){startNewAnimation(AnimationStance.IDLERIGHT);}
					else{startNewAnimation(AnimationStance.IDLELEFT);}
				}
			}
			/* If player is in the Air*/
			if (this.inAir){
				//10 is ideal
				if (this.gravityTimer.getElapsedTime() > 10){
					this.gravityTimer.resetGameClock();
					this.yVel += this.gravity;
					this.getPosition().translate(0, this.yVel);
				}
			}	
		}
	}
	
	private void loadXML(){
		try {	
          File inputFile = new File("resources/player.xml");
          DocumentBuilderFactory dbFactory 
             = DocumentBuilderFactory.newInstance();
          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          this.rootXML = dBuilder.parse(inputFile);
          this.rootXML.getDocumentElement().normalize();
          SpriteSheetFrame frameDetails = getFrameDetails(this.stance, this.currentFrame);
          setNextAnimationFrame(frameDetails);
       } catch (Exception e) {
          e.printStackTrace();
       }
	}
	
	private SpriteSheetFrame getFrameDetails(String stance, int currentFrame){
        //Get stance details 
        NodeList nList = rootXML.getElementsByTagName(stance);
        Node nNode = nList.item(0); //Should only be one node for each stance
        Element eElement = (Element) nNode;
        this.setFrameLimit(Integer.parseInt(eElement.getAttribute("frames")));
        //Choose specific frame
        NodeList frameList = eElement.getElementsByTagName("frame" + currentFrame);
        Element frameElement = (Element) frameList.item(0);
        SpriteSheetFrame frameDetails = new SpriteSheetFrame(
        		Integer.parseInt(frameElement.getAttribute("x")),
        		Integer.parseInt(frameElement.getAttribute("y")),
        		Integer.parseInt(frameElement.getAttribute("width")),
        		Integer.parseInt(frameElement.getAttribute("height"))
        		);
        return frameDetails;
	}
	
	private void printSpriteSheetFrame(SpriteSheetFrame frameDetails){
        System.out.println("Number of frames: " + this.frameLimit);
        System.out.println("x: " + frameDetails.getX());
        System.out.println("y: " + frameDetails.getY());
        System.out.println("width: " + frameDetails.getWidth());
        System.out.println("height: " + frameDetails.getHeight());	
	}
	
	private void startNewAnimation(String stance){
		this.stance = stance;
		this.currentFrame = 0;
		SpriteSheetFrame frameDetails = getFrameDetails(this.stance, this.currentFrame);
		setNextAnimationFrame(frameDetails);
		this.animationTimer.resetGameClock();
	}

	private void continueAnimation(){
		/* Get next frame of animation after sufficient time */
		if (this.animationTimer.getElapsedTime() > this.timePerFrame){
			this.currentFrame += 1;
			if (this.currentFrame >= this.frameLimit){this.currentFrame = 0;}
			SpriteSheetFrame frameDetails = getFrameDetails(this.stance, this.currentFrame);
			setNextAnimationFrame(frameDetails);
			this.animationTimer.resetGameClock();
		}
	}
	
	private void setNextAnimationFrame(SpriteSheetFrame frameDetails){
		//printSpriteSheetFrame(frameDetails);
	    BufferedImage img = this.readImage("PlayerSpriteSheet.png");
		img = img.getSubimage(frameDetails.getX(), 
							  frameDetails.getY(), 
							  frameDetails.getWidth(), 
							  frameDetails.getHeight());
		this.setImage(img);
	}
	
	private void moveRight(){
		this.xVel = this.xSlip;
		this.facingRight = true;	
		if (!this.stance.equals(AnimationStance.WALKRIGHT)){
			startNewAnimation(AnimationStance.WALKRIGHT);
		}
	}
	
	private void moveLeft(){
		this.xVel = -this.xSlip;
		this.facingRight = false;
		if (!this.stance.equals(AnimationStance.WALKLEFT)){
			startNewAnimation(AnimationStance.WALKLEFT);
		}
	}
	
	public void keyboardOnly(ArrayList<Integer> pressedKeys){
		/* (Up) Jump into the air */
		if ((pressedKeys.contains(38)) && !this.isInAir()){
			this.yVel = -jumpHeight;
			this.inAir = true;
		}
		/* (Left) Move to the left */
		if ((pressedKeys.contains(37)) && this.getPosition().getX()>=4){
			moveLeft();
		}
		/* (Right) Move to the right */
		if ((pressedKeys.contains(39)) && this.getPosition().getX()<=1600-this.getUnscaledWidth()){
			moveRight();
		}
		/* (Q) Rotate crosshairs counterclockwise */
		if (pressedKeys.contains(81)){
			this.crosshairsAngle -= 1;
		}
		/* (E) Rotate crosshairs clockwise */
		if (pressedKeys.contains(69)){
			this.crosshairsAngle += 1;
		}
		/* (Spacebar) Launch hookshot */
		if (pressedKeys.contains(32) && this.hookReady){
			this.hookReady = false;
			this.dispatchEvent(new Event(HookEvent.HOOKPRESSED,null));
		}		
	}
	
	public void keyboardAndXbox360(ArrayList<Integer> pressedKeys, GamePad xbox360){
		/* Keyboard (Up) || Xbox360 (A) -> Jump into the air */
		if (!this.isInAir() && (pressedKeys.contains(38) || xbox360.isButtonPressed(GamePad.BUTTON_A))){
			this.yVel = -jumpHeight;
			this.inAir = true;
		}
		/* (Left) || Xbox360 (Left Stick) -> Move to the left */
		if ((pressedKeys.contains(37) || xbox360.getLeftStickXAxis() < 0) && this.getPosition().getX()>=4){
			moveLeft();
		}
		/* (Right) || Xbox360 (Left Stick) -> Move to the right */
		if ((pressedKeys.contains(39) || xbox360.getLeftStickXAxis() > 0) && this.getPosition().getX()<=1600-this.getUnscaledWidth()){
			moveRight();
		}
		/* Update crosshairsAngle */
		if(xbox360.isRightStickTilted()){
			this.crosshairsAngle = xbox360.getRightStickAngle();
		}
		
		/* (Q) Rotate crosshairs counterclockwise */
		if (pressedKeys.contains(81) || xbox360.isButtonPressed(GamePad.BUTTON_LB)){
			this.crosshairsAngle -= 1;
		}
		/* (E) Rotate crosshairs clockwise */
		if (pressedKeys.contains(69) || xbox360.isButtonPressed(GamePad.BUTTON_RB)){
			this.crosshairsAngle += 1;
		}
		/* (Spacebar) || Xbox360 (Left Stick) -> Launch hookshot */
		if (this.hookReady && (pressedKeys.contains(32) || xbox360.isRightTriggerPressed())){
			this.dispatchEvent(new Event(HookEvent.HOOKPRESSED,null));
		}		
		xbox360.isRightStickTilted();
	}
	
	
	
	
	
	public boolean isInPlay() {
		return this.inPlay;
	}

	public void setInPlay(boolean newStatus) {
		this.inPlay = newStatus;
	}


	public int getYVel() {
		return this.yVel;
	}

	public void setYVel(int newYVel) {
		this.yVel = newYVel;
	}

	public void resetFall() {
		this.yVel = 0;
		this.gravityTimer.resetGameClock();
		this.inPlay = true;
		this.inAir = true;
	}	

	public boolean isInAir() {
		return inAir;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public float getCrosshairsAngle() {
		return crosshairsAngle;
	}

	public void setCrosshairsAngle(float crosshairsAngle) {
		this.crosshairsAngle = crosshairsAngle;
	}

	public boolean getHookReady() {
		return hookReady;
	}

	public void setHookReady(boolean hookReady) {
		this.hookReady = hookReady;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public int getFrameLimit() {
		return frameLimit;
	}

	public void setFrameLimit(int frameLimit) {
		this.frameLimit = frameLimit;
	}

	public String getStance() {
		return stance;
	}

	public void setStance(String stance) {
		this.stance = stance;
	}

	public int getxVel() {
		return xVel;
	}

	public void setxVel(int xVel) {
		this.xVel = xVel;
	}

	public int getTimePerFrame() {
		return timePerFrame;
	}

	public void setTimePerFrame(int timePerFrame) {
		this.timePerFrame = timePerFrame;
	}

	public GameClock getTimeSinceLastAnimation() {
		return this.animationTimer;
	}

	public void setTimeSinceLastAnimation(GameClock timeSinceLastAnimation) {
		this.animationTimer = timeSinceLastAnimation;
	}

	public boolean isFacingRight() {
		return facingRight;
	}

	public void setFacingRight(boolean facingRight) {
		this.facingRight = facingRight;
	}

	public int getHookshotLength() {
		return hookshotLength;
	}

	public void setHookshotLength(int hookshotLength) {
		this.hookshotLength = hookshotLength;
	}

	public GameClock getGravityTimer() {
		return gravityTimer;
	}

	public void setGravityTimer(GameClock gravityTimer) {
		this.gravityTimer = gravityTimer;
	}

	public int getJumpHeight() {
		return jumpHeight;
	}

	public void setJumpHeight(int jumpHeight) {
		this.jumpHeight = jumpHeight;
	}
	
}
