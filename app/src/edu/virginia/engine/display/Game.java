package edu.virginia.engine.display;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.virginia.engine.util.Event;
import edu.virginia.engine.util.listeners.CollisionEvent;
import edu.virginia.engine.util.listeners.HookEvent;
import edu.virginia.engine.util.listeners.HookListener;
import edu.virginia.engine.util.listeners.PlayerEvent;
import edu.virginia.engine.util.listeners.PlayerListener;


/**
 * Highest level class for creating a game in Java.
 * 
 * */
public class Game extends DisplayObjectContainer implements ActionListener, KeyListener, MouseListener {

	/* Frames per second this game runs at */
	private int FRAMES_PER_SEC = 60;

	/* The main JFrame that holds this game */
	private JFrame mainFrame;

	/* Timer that this game runs on */
	private Timer gameTimer;
	
	/* The JPanel for this game */
	private GameScenePanel scenePanel;
	
	TweenJuggler tInstance;

	public Game(String gameId, int width, int height) {
		super(gameId);
		if (TweenJuggler.getInstance() == null){
			TweenJuggler tInstance = new TweenJuggler();
		}
		setUpMainFrame(gameId, width, height);
		
		setScenePanel(new GameScenePanel(this));
		
		/* Use an absolute layout */
		scenePanel.setLayout(null);
	}
	
	
	public void setFramesPerSecond(int fps){
		if(fps > 0) this.FRAMES_PER_SEC = fps;
	}

	public void setUpMainFrame(String gameId, int width, int height) {
		this.mainFrame = new JFrame();
		getMainFrame().setTitle(gameId);
		getMainFrame().setResizable(false);
		getMainFrame().setVisible(true);
		getMainFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getMainFrame().setBounds(0, 0, width, height);
		getMainFrame().addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		getMainFrame().addKeyListener(this);
		getMainFrame().addMouseListener(this);
	}

	/**
	 * Starts the game
	 */
	public void start() {
		if (gameTimer == null) {
			gameTimer = new Timer(1000 / FRAMES_PER_SEC, this);
			gameTimer.start();
		} else {
			gameTimer.start();
		}
	}

	/**
	 * Stops the animation.
	 */
	public void stop() {
		pause();
		gameTimer = null;
	}

	public void pause() {
		if (gameTimer != null) {
			gameTimer.stop();
		}
	}
	
	public void exitGame(){
		stop();
		this.mainFrame.setVisible(false);
		this.mainFrame.dispose();
	}
	
	/**
	 * Close the window
	 * */
	public void closeGame(){
		this.stop();
		if(this.getMainFrame() != null){
			this.getMainFrame().setVisible(false);
			this.getMainFrame().dispose();
		}
	}


	/**
	 * Called once per frame. updates the game, redraws the screen, etc. May
	 * need to optimize this if games get too slow.
	 * */
	@Override
	public void actionPerformed(ActionEvent e) {
		repaintGame();
	}
	
	/**
	 * Forces a repaint
	 * */
	public void repaint(){repaintGame();}
	public void repaintGame(){
		if(getScenePanel() != null){
			getScenePanel().validate();
			getScenePanel().repaint();
		}
	}

	protected void nextFrame(Graphics g) {
		try {
			if (tInstance!=null){
				
				tInstance.nextFrame();
				//System.out.println(tInstance.getSize());
				}
			/* Update all objects on the stage */
			this.update(pressedKeys);
			/* Draw everything on the screen */
			this.draw(g);
		} catch (Exception e) {
			System.out
					.println("Exception in nextFrame of game. Stopping game (no frames will be drawn anymore");
			stop();
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics g){
		/* Start with no transparency */
		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				1.0f));
		
		super.draw(g);
	}

	public JFrame getMainFrame() {
		return this.mainFrame;
	}
	
	public void setScenePanel(GameScenePanel scenePanel) {
		this.scenePanel = scenePanel;
		this.getMainFrame().add(this.scenePanel);
		getMainFrame().setFocusable(true);
		getMainFrame().requestFocusInWindow();
	}

	public GameScenePanel getScenePanel() {
		return scenePanel;
	}

	ArrayList<Integer> pressedKeys = new ArrayList<Integer>();
	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println("keyPressed: " + (Integer)e.getKeyCode());
		if(!pressedKeys.contains((Integer)e.getKeyCode()))
			pressedKeys.add((Integer)e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(pressedKeys.contains((Integer)e.getKeyCode()))
			pressedKeys.remove((Integer)e.getKeyCode());
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void setupHookshot(PhysicsSprite player, 
							  PlayerListener playerListener, 
							  HookListener hookListener){
		/* Set event listeners */
		/* Set player event listeners */
		player.addEventListener(playerListener, CollisionEvent.PLATFORM);
		player.addEventListener(playerListener, CollisionEvent.GROUND);
		player.addEventListener(playerListener, CollisionEvent.INAIR);
		player.addEventListener(playerListener, PlayerEvent.ResetFall);
		/* Set hookshot event listeners */
		player.addEventListener(hookListener, HookEvent.HOOKPRESSED);
		player.addEventListener(hookListener, HookEvent.HOOKRELEASED);
		player.addEventListener(hookListener, HookEvent.CANHOOK);
		player.addEventListener(hookListener, HookEvent.CANNOTHOOK);
		player.addEventListener(hookListener, HookEvent.HOOKHOP);		
	}
	
	public void drawCrosshairsKeyboard(Sprite crosshairs, PhysicsSprite player){
		/* Find where crosshair should be based on mouse location */
		float crosshairsAngle = player.getCrosshairsAngle();
		/* Set crosshair position, where center is player Center */
		int radius = 200;
		int radiusX = (int) (Math.cos(Math.toRadians(crosshairsAngle)) * radius);
		int radiusY = (int) (Math.sin(Math.toRadians(crosshairsAngle)) * radius);
		int crosshairsX = player.getCenterX() + radiusX;
		int crosshairsY = player.getCenterY() + radiusY;
		//System.out.format("cX: %d | cY: %d \n",crosshairsX, crosshairsY);
		/* Adjust crosshairs to (pivot?) point */
		player.setCrosshairsAngle(crosshairsAngle);
		Point adjustedPoint = new Point(crosshairsX, crosshairsY);
		crosshairs.setPosition(adjustedPoint);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		/*
		System.out.println("Pressed"); 
		Event hookPressed = new Event();
		hookPressed.setEventType(HookEvent.HOOKPRESSED);
		hookPressed.setSource(null);
		this.dispatchEvent(hookPressed);
		*/
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
}
