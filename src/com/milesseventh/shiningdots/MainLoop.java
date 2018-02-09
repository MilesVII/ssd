package com.milesseventh.shiningdots;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;

public class MainLoop extends Thread {
	public SurfaceHolder sh;
	public boolean running = true, isPaused = false;
	public Game game;
	public SensorManager sm;
	public int dotsAmount, cell, dpc;
	public float radius, spring;
	public int[] color = new int[]{218, 64, 0};
	public SharedPreferences prefs;
	
	@Override
	public void run(){
		reload();
		while (running){
			if (!isPaused){
				Canvas c = sh.lockCanvas();
				if (c == null){
					running = false;
					break;
				}
				c.drawColor(Color.BLACK);
				game.update(c);
				sh.unlockCanvasAndPost(c);
			}
		}
	}
	
	public void reload(){
		Rect r = sh.getSurfaceFrame();
		load();
		game = new Game(dotsAmount, color, cell, dpc, radius, spring, r.width(), r.height(), sm);
	}
	
	public void load(){
		dotsAmount = prefs.getInt("da", 420);
		cell = prefs.getInt("cs", 10);
		dpc = prefs.getInt("dpc", 8);
		color[0] = prefs.getInt("cr", 218);
		color[1] = prefs.getInt("cg", 64);
		color[2] = prefs.getInt("cb", 0);
		radius = prefs.getFloat("rad", 2f);
		spring = prefs.getFloat("sp", .42f);
	}
	
	public void save(){
		Editor e = prefs.edit();
		e.putInt("da", dotsAmount);
		e.putInt("cs", cell);
		e.putInt("dpc", dpc);
		e.putInt("cr", color[0]);
		e.putInt("cg", color[1]);
		e.putInt("cb", color[2]);
		e.putFloat("rad", radius);
		e.putFloat("sp", spring);
		e.commit();
	}
	
	public void resetPrefs(){
		prefs.edit().clear().commit();
	}
}
