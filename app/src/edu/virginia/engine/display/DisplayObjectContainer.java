package edu.virginia.engine.display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import edu.virginia.engine.display.Note;
import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.GamePad;
import edu.virginia.engine.util.SoundManager;
import edu.virginia.engine.util.listeners.CollisionEvent;
import edu.virginia.engine.util.listeners.HookEvent;

public class DisplayObjectContainer extends DisplayObject{
	
	private ArrayList<DisplayObject> children;
	SoundManager soundManager = new SoundManager();
	private Note collidedNote;

	public DisplayObjectContainer(String id) {
		super(id);
		this.children=new ArrayList<DisplayObject>();
		// TODO Auto-generated constructor stub
	}

	public DisplayObjectContainer(String id, String fileName) {
		// TODO Auto-generated constructor stub
		super(id,fileName);
		this.children=new ArrayList<DisplayObject>();
	}

	public void addChild(DisplayObject o){
		this.children.add(o);
		o.setParent(this);
	}
	
	public void removeChild(DisplayObject o){
		this.children.remove(o);
		o.setParent(null);
	}
	
	public int numberOfChildren(){
		return this.children.size();
	}
	
	public boolean contains(DisplayObject o){
		if (children.contains(o)){
			return true;
		}
		return false;
	}
	
	public ArrayList<DisplayObject> getChildren(){
		return this.children;
	}
	
	public void removeChildren(){
		this.children.clear();
	}
	
	public DisplayObject getChildAtIndex(int i){
		return this.children.get(i);
	}
	
	public DisplayObject getChildById(String id){
		for (int i=0; i<children.size(); i++){
			DisplayObject child = children.get(i);
			if (child.getId().equals(id)){
				return child;
			}
		}
		return null;
	}
	
	public boolean collidesWithPlatform(PhysicsSprite player){
		for (int i=0; i<children.size(); i++){
			DisplayObject platform = children.get(i);
			boolean isColliding = platform.collidesWith(player);
			int playerBottom = (int) player.getPosition().getY() + player.getUnscaledWidth();
			//TODO: Fix this boolean, you pass through the platform sometimes when you shouldn't...
			boolean fallingFromAbove = playerBottom - (int) platform.getPosition().getY() < 30;
			boolean fallingTooFast =  player.getYVel() > 15;
			/* Other is colliding with platform */
			//System.out.format("isColliding: %s && fallingFromAbove: %s\n", isColliding, fallingFromAbove);
			if (isColliding && (fallingFromAbove || fallingTooFast)){
				if (platform.getId().equals(PlatformType.HANG)){
					//Do nothing, can't stand on this platform
				}
				else if (platform.getId().equals(PlatformType.HORIZONTAL)){
					player.dispatchEvent(new Event(CollisionEvent.HORIZONTAL, platform));	
				}
				else if (platform.getId().equals(PlatformType.VERTICAL)){
					player.dispatchEvent(new Event(CollisionEvent.VERTICAL, platform));	
				}
				else if (platform.getId().equals(PlatformType.BOUNCY)){
					player.dispatchEvent(new Event(CollisionEvent.BOUNCY, platform));	
				}
				else {
					player.dispatchEvent(new Event(CollisionEvent.PLATFORM, platform));						
				}			
				return true;
			}
		}
		return false;
	}

	/**public boolean collidesWithNote(PhysicsSprite player){
		for (int i=0; i<children.size(); i++){
			DisplayObject note = children.get(i);
			/* Only collide with notes that are in play */
			/**if (note.getInPlay() && note.collidesWith(player)){
				note.setInPlay(false);
				note.setVisible(false);
				return true;
			}
		}
		return false;
	}**/
	
	public boolean collidesWithNoteSound(Sprite player, TweenJuggler tweenJuggler){
		for (int i=0; i<children.size(); i++){
			DisplayObject note = children.get(i);
			/* Only collide with notes that are in play */
			if (note.getInPlay() && note.collidesWith(player)){
				note.setInPlay(false);
				this.collidedNote=(Note)note;
				if (!player.getId().equals("Death Bar")){
					try {
						soundManager.LoadSoundEffect(((Note)note).getSound(), ((Note)note).getSound());
						soundManager.PlaySoundEffect(((Note)note).getSound());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				//Tween Note!
				int oldY = (int) note.getPosition().getY();
				Tween noteCollected = new Tween (note, null, null);
				noteCollected.animate(TweenableParams.ALPHA, 1, 0, 500);
				noteCollected.animate(TweenableParams.Y, oldY, oldY - 100, 500);
				tweenJuggler.add(noteCollected);
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean hookablePlatform(DisplayObject crosshairs, DisplayObject player){
		/* Set up hookshot Hitbox */
		int hitboxWidth = (int) crosshairs.getPosition().getX() - player.getCenterX();
		int hitboxHeight = (int) crosshairs.getPosition().getY() - player.getCenterY();
		int hitboxX = player.getCenterX();
		int hitboxY = player.getCenterY();
		/* If width or height is negative, 
		 * shift origin by that amount and make value positive */
		if (hitboxWidth < 0){
			hitboxX += hitboxWidth;
			hitboxWidth *= -1;
		}
		else if (hitboxWidth == 0){
			hitboxWidth = 1;
		}
		if (hitboxHeight < 0){
			hitboxY += hitboxHeight;
			hitboxHeight *= -1;
		}
		else if (hitboxHeight == 0){
			hitboxWidth = 1;
		}
		
		Rectangle hitboxHookshot = new Rectangle(
				hitboxX,
				hitboxY,
				hitboxWidth,
				hitboxHeight);
		for (int i=0; i<children.size(); i++){
			DisplayObject platform = children.get(i);
			/* Can only hook platforms above player */
			if (hitboxY < platform.getPosition().getY()){
				boolean canHookshot = platform.collidesWithRectangle(hitboxHookshot);
				/* If player can hookshot to this platform */
				if (canHookshot){
					//System.out.println("Can hook!");
					DisplayObject hookObject = new DisplayObject("Hook Point");
					int hookX = (int) crosshairs.getPosition().getX();
					int hookY = (int) platform.getPosition().getY();
					hookObject.setPosition(new Point(hookX, hookY));
					player.dispatchEvent(new Event(HookEvent.CANHOOK, hookObject));
					//Change crosshairs to green if not green previously
					if (!crosshairs.getId().equals(HookEvent.GREEN)){
						crosshairs.setId(HookEvent.GREEN);
						crosshairs.setImage("crosshairsGreen.png");
					}
					return true;
				}
			}
		}
		player.dispatchEvent(new Event(HookEvent.CANNOTHOOK, null));
		//Change crosshairs to red if not red previously
		if (!crosshairs.getId().equals(HookEvent.RED)){
			crosshairs.setId(HookEvent.RED);
			crosshairs.setImage("crosshairsRed.png");
		}
		return false;
	}
	
	public Note getCollidedNote(){
		return this.collidedNote;
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		for (int i=0; i<children.size(); i++){
			children.get(i).draw(g);
		}
	}
	
	@Override
	public void update(ArrayList<Integer> pressedKeys, ArrayList<GamePad> controllers){
	    super.update(pressedKeys, controllers);
	    // Update Children
	    for	(int i = 0; i < this.numberOfChildren(); i++) {
	      DisplayObject child = this.getChildAtIndex(i);
	      child.update(pressedKeys, controllers);
	    }
	  }
	
	@Override
	public void applyTransformations(Graphics2D g2d) {
		super.applyTransformations(g2d);
		//for (int i=0; i<children.size(); i++){
		//	children.get(i).applyTransformations(g2d);
		//}
	}

}
