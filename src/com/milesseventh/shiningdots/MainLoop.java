package com.milesseventh.shiningdots;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;

public class MainLoop extends Thread {
	public SurfaceHolder sh;
	public boolean running = true, isPaused = false;
	public Game game;
	public SensorManager sm;
	public int dotsAmount = 420, cell = 32, dpc = 16, sup = 16;
	public boolean showFPS = false;
	public long lastTime;
	public int[] color = new int[]{218, 64, 0};
	
	@Override
	public void run(){
		reload();
		lastTime = System.nanoTime();
		while (running){
			if (!isPaused){
				Canvas c = sh.lockCanvas();
				if (c == null){
					running = false;
					break;
				}
				c.drawColor(Color.BLACK);
				game.update(c, System.nanoTime() - lastTime);
				lastTime = System.nanoTime();
				sh.unlockCanvasAndPost(c);
			}
		}
	}
	
	public void reload(){
		Rect r = sh.getSurfaceFrame();
		game = new Game(dotsAmount, showFPS, color, cell, dpc, sup, r.width(), r.height(), sm);
	}
}
