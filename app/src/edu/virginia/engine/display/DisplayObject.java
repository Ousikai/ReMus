package edu.virginia.engine.display;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import edu.virginia.engine.util.EventDispatcher;
import edu.virginia.engine.util.listeners.CollisionEvent;

/**
 * A very basic display object for a java based gaming engine
 * 
 * */
public class DisplayObject extends EventDispatcher{

	/* All DisplayObject have a unique id */
	private String id;

	/* The image that is displayed by this object */
	private BufferedImage displayImage;
	
	private boolean visible;
	private Point position;
	private Point pivotPoint;
	private double scaleX;
	private double scaleY;
	private double rotation;
	private float alpha;
	private Rectangle hitbox;
	
	private DisplayObject parent;
	

	/**
	 * Constructors: can pass in the id OR the id and image's file path and
	 * position OR the id and a buffered image and position
	 */
	public DisplayObject(String id) {
		this.setId(id);
		this.visible=true;
		this.position=new Point(0,0);
		this.pivotPoint=new Point(0,0);
		this.scaleX=1;
		this.scaleY=1;
		this.rotation=0;
		this.alpha=1;
		//this.addEventListener(myQuestManager, CollisionEvent.COLLISION);
	}

	public DisplayObject(String id, String fileName) {
		this.setId(id);
		this.setImage(fileName);
		this.visible=true;
		this.position=new Point(0,0);
		this.pivotPoint=new Point(0,0);
		this.scaleX=1;
		this.scaleY=1;
		this.rotation=0;
		this.alpha=1;
		//this.addEventListener(myQuestManager, CollisionEvent.COLLISION);
	}

	
	//Getters, Setters___________________________________________________________________________________
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}


	/**
	 * Returns the unscaled width and height of this display object
	 * */
	public int getUnscaledWidth() {
		if(displayImage == null) return 0;
		return displayImage.getWidth();
	}

	public int getUnscaledHeight() {
		if(displayImage == null) return 0;
		return displayImage.getHeight();
	}

	public BufferedImage getDisplayImage() {
		return this.displayImage;
	}

	protected void setImage(String imageName) {
		if (imageName == null) {
			return;
		}
		displayImage = readImage(imageName);
		if (displayImage == null) {
			System.err.println("[DisplayObject.setImage] ERROR: " + imageName + " does not exist!");
		}
	}
	
	public boolean getVisible(){
		return this.visible;
	}
	
	public void setVisible(boolean b){
		this.visible=b;
	}
	
	public Point getPosition(){
		if (this.parent==null)
		return this.position;
		
		else {
			Point p=new Point(this.position);
			p.translate(-(int)this.parent.getPosition().getX(), -(int)this.parent.getPosition().getY());
			return p;
		}
	}
	
	public void setPosition(Point p){
		//if (this.parent==null){
		this.position=p;
		//}
		
		//else {
		//	p.translate((int)this.parent.getPosition().getX(), (int)this.parent.getPosition().getY());
			this.position=p;
			
		//}
	}
	
	public Point getPivotPoint(){
		return this.pivotPoint;
	}
	
	public void setPivotPoint(Point p){
		this.pivotPoint=p;
	}
	
	public double getScaleX(){
		return this.scaleX;
	}
	
	public void setScaleX(double d){
		this.scaleX=d;
	}

	public double getScaleY(){
		return this.scaleY;
	}
	
	public void setScaleY(double d){
		this.scaleY=d;
	}
	public double getRotation(){
		return this.rotation;
	}
	
	public void setRotation(double i){
		this.rotation=i%360;
	}
	
	public float getAlpha(){
		return this.alpha;
	}
	
	public void setAlpha(float i){
		this.alpha=i;
	}
	
	public Rectangle getHitbox(){
		Rectangle hitbox = new Rectangle((int)this.position.getX(),(int)this.position.getY(),this.getUnscaledWidth(), this.getUnscaledHeight());
		return hitbox;
	}
	
	public DisplayObject getParent(){
		if (this.parent != null){
			return this.parent;
		}
		return null;
	}
	
	public void setParent (DisplayObject o){
		this.parent=o;
	}
	//___________________________________________________________________________________________________-
	
	public boolean collidesWith(DisplayObject other){
		if (this.getHitbox().intersects(other.getHitbox())){
			return true;
		}
		return false;
	}
	
	/**
	 * Helper function that simply reads an image from the given image name
	 * (looks in resources\\) and returns the bufferedimage for that filename
	 * */
	public BufferedImage readImage(String imageName) {
		BufferedImage image = null;
		try {
			String file = ("resources" + File.separator + imageName);
			image = ImageIO.read(new File(file));
		} catch (IOException e) {
			System.out.println("[Error in DisplayObject.java:readImage] Could not read image " + imageName);
			e.printStackTrace();
		}
		return image;
	}

	public void setImage(BufferedImage image) {
		if(image == null) return;
		displayImage = image;
	}


	/**
	 * Invoked on every frame before drawing. Used to update this display
	 * objects state before the draw occurs. Should be overridden if necessary
	 * to update objects appropriately.
	 * */
	protected void update(ArrayList<Integer> pressedKeys) {
		
	}

	/**
	 * Draws this image. This should be overloaded if a display object should
	 * draw to the screen differently. This method is automatically invoked on
	 * every frame.
	 * */
	//from http://www.informit.com/articles/article.aspx?p=26349&seqNum=5
	 private AlphaComposite makeComposite(float alpha) {
		  int type = AlphaComposite.SRC_OVER;
		  return(AlphaComposite.getInstance(type, alpha));
		 }
	 
	public void draw(Graphics g) {
		
		if (displayImage != null) {
			
			/*
			 * Get the graphics and apply this objects transformations
			 * (rotation, etc.)
			 */
			Graphics2D g2d = (Graphics2D) g;
			applyTransformations(g2d);
			/* Actually draw the image, perform the pivot point translation here */
			g2d.drawImage(displayImage, 0, 0,
					(int) (getUnscaledWidth()),
					(int) (getUnscaledHeight()), null);
			
			/*
			 * undo the transformations so this doesn't affect other display
			 * objects
			 */
			reverseTransformations(g2d);
		}
	}

	/**
	 * Applies transformations for this display object to the given graphics
	 * object
	 * */
	protected void applyTransformations(Graphics2D g2d) {
		g2d.translate(this.position.getX(), this.position.getY());
		g2d.rotate(this.rotation*(3.1416)/180, this.pivotPoint.getX(), this.pivotPoint.getY());
		g2d.scale(this.scaleX, this.scaleY);
		g2d.setComposite(makeComposite(this.getAlpha()));
		if (this.getVisible()==false){
			g2d.setComposite(makeComposite(0));
		}
	}

	/**
	 * Reverses transformations for this display object to the given graphics
	 * object
	 * */
	protected void reverseTransformations(Graphics2D g2d) {
		g2d.setComposite(makeComposite(1));
		g2d.scale(1/this.scaleX, 1/this.scaleY);
		g2d.rotate(-(this.rotation*(3.1416)/180), this.pivotPoint.getX(), this.pivotPoint.getY());
		g2d.translate(-this.position.getX(), -this.position.getY());
	}

}