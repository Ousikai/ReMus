package edu.virginia.engine.display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class DisplayObjectContainer extends DisplayObject{
	
	private ArrayList<DisplayObject> children;

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
	/**
	@Override
	public void setAlpha(float i) {
		// TODO Auto-generated method stub
		super.setAlpha(i);
		for (int j=0; j<children.size(); j++){
			children.get(j).setAlpha(i);
		}
	}
	
	@Override
	public void setRotation(double i) {
		// TODO Auto-generated method stub
		super.setRotation(i);
		for (int j=0; j<children.size(); j++){
			children.get(j).setRotation(i);
		}
	}
	
	@Override
	public void setScaleX(double d) {
		// TODO Auto-generated method stub
		super.setScaleX(d);
	}
	
	@Override
	public void setScaleY(double d) {
		// TODO Auto-generated method stub
		super.setScaleY(d);
	}
	
	@Override
	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		super.setVisible(b);
	}
	
	@Override
	public void setPosition(Point p) {
		// TODO Auto-generated method stub
		super.setPosition(p);
	}
	
	**/
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		Graphics2D g2d = (Graphics2D) g;
		applyTransformations(g2d);
		for (int i=0; i<children.size(); i++){
				children.get(i).draw(g);
				}
		reverseTransformations(g2d);
	}
	
	@Override
	public void applyTransformations(Graphics2D g2d) {
		super.applyTransformations(g2d);
		//for (int i=0; i<children.size(); i++){
		//	children.get(i).applyTransformations(g2d);
		//}
	}

}
