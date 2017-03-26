package edu.virginia.engine.display;

public class TweenParam {
	private TweenableParams paramToTween;
	private double startVal;
	private double endVal;
	private double time;
	private double percent;
	
	public TweenParam(TweenableParams paramToTween, double startVal, double endVal, double time){
		this.paramToTween=paramToTween;
		this.startVal=startVal;
		this.endVal=endVal;
		this.time=time;
		this.percent=0;
	}
	public TweenableParams getParam(){
		return paramToTween;
	}
	public double getStartVal(){
		return startVal;
	}
	public double getEndVal(){
		return endVal;
	}
	public double getTweenTime(){
		return time;
	}
	public double getPercent(){
		return percent;
	}
}
