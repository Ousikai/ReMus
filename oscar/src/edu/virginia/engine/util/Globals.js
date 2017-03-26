"use strict";

/**
 * Global variables and functions
 *
 * */
class Globals {
  constructor(){
  }

  playBGM(){
    var bgm = document.getElementById('bgm');
    bgm.play();
  }
  pauseBGM(){
    var bgm = document.getElementById('bgm');
    bgm.pause();
    bgm.currentTime = 0
  }
}
