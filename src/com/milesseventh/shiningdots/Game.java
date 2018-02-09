package com.milesseventh.shiningdots;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Game {
	class Dot{
		Vector pos, acc = new Vector(), spd = new Vector();
		
		public Dot(float x, float y){
			pos = new Vector(x, y);
		}
		
		public void update(float xa, float ya, float w, float h){
			acc.x = xa;
			acc.y = ya;
			acc.scale(r.nextFloat() * .02f + .98f);
			accelerate();
			
			if (spd.x > 0 && pos.x + spd.x + R > w){
				spd.x *= -spring;
				pos.x = w - R;
			} else if (spd.x < 0 && pos.x + spd.x - R < 0){
				spd.x *= -spring;
				pos.x = R;
			}
			if (spd.y > 0 && pos.y + spd.y + R > h){
				spd.y *= -spring;
				pos.y = h - R;
			} else if (spd.y < 0 && pos.y + spd.y - R < 0){
				spd.y *= -spring;
				pos.y = R;
			}
			
			if (Float.isNaN(pos.x) || Float.isNaN(pos.y) || Float.isNaN(spd.x) || Float.isNaN(spd.y)){
				pos.x = r.nextInt((int)w);
				pos.y = r.nextInt((int)h);
				spd.x = 0;
				spd.y = 0;
			}
			
			pos.add(spd);
		}
		
		public void accelerate(){
			spd.add(acc);
		}
	}

	public static Random r = new Random();
	public float G = 9.8f, R, cellSize;
	public int dotsAmount, dpc;
	public Dot[] dots;
	public SensorManager sm;
	public Sensor gyro;
	public Paint pain      = new Paint();
	public int[] color;
	public float spring;
	
	public Game(int da, int[] _color, int _cell, int _dpc, float _r, float _spring, int w, int h, SensorManager _sm){
		dotsAmount = da;
		dots = new Dot[dotsAmount];
		color = _color;
		cellSize = _cell;
		dpc = _dpc;
		R = _r;
		spring = _spring;
		
		pain.setColor(Color.BLACK);
		pain.setTextAlign(Align.LEFT);
		pain.setTextSize(20);
		rendermx = new int[(int)((float)w / cellSize)][(int)((float)h / cellSize)];
		clearmx();
		
		sm = _sm;
		gyro = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(new SensorEventListener(){
			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSensorChanged(SensorEvent se) {
				if (se.sensor == gyro){
					acceleration[0] = se.values[0];
					acceleration[1] = se.values[1];
					acceleration[2] = se.values[2];
				}
			}
		}, gyro, 16 * 1000);
		
		for (int i = 0; i < dotsAmount; ++i){
			dots[i] = new Dot(r.nextInt(w), r.nextInt(h));
		}
	}
	
	float[] acceleration = new float[]{0, 0, 0};
	public void update(Canvas canvas){
		collision();
		for (Dot d : dots)
			d.update(acceleration[1] / 5f, acceleration[0] / 5f, canvas.getWidth(), canvas.getHeight());
		render(canvas);
	}
	
	public float clamp(float x, float min, float max){
		return Math.min(Math.max(min, x), max);
	}
	
	class Contact{
		int dot1, dot2;
		float distance;
		
		public Contact(int b1, int b2, float d){
			dot1 = b1;
			dot2 = b2;
			distance = d;
		}
	}
	
	public ArrayList<Contact> slam = new ArrayList<Contact>();
	public void collision(){
		for (int i = 0; i < dotsAmount; ++i)
			for (int j = i + 1; j < dotsAmount; ++j){
				float d = dist2(dots[i].pos.x, dots[i].pos.y, dots[j]);
				if (d < R * R){
					slam.add(new Contact(i, j, (float)Math.sqrt(d)));
				}
			}
		
		for (Contact c : slam){
			Vector aim = Vector.getVector(dots[c.dot2].pos);
			aim.sub(dots[c.dot1].pos);
			if(c.distance != 0)
				aim.normalize();
			else
				continue;//aim.x = 1f;
			aim.scale((2 * R) - c.distance).scale(.5f);
			dots[c.dot2].pos.add(aim);
			aim.scale(-1);
			dots[c.dot1].pos.add(aim);
		}
		slam.clear();
	}
	
	public int[][] rendermx;
	public void clearmx(){
		rendermx[0][0] = 0;
		for (int x = 0; x < rendermx.length; ++x)
			for (int y = 0; y < rendermx[0].length; ++y)
				rendermx[x][y] = 0;
	}
	
	public boolean RENDER_DEBUG = false;
	public void render(Canvas canvas){
		if (RENDER_DEBUG)
			for (Dot d : dots){
				pain.setColor(Color.WHITE);
				canvas.drawCircle(d.pos.x, d.pos.y, R, pain);
			}
		else {
			for (Dot d : dots){
				int x, y;
				x = (int)clamp(d.pos.x / canvas.getWidth() * rendermx.length, 0, rendermx.length - 1);
				y = (int)clamp(d.pos.y / canvas.getHeight() * rendermx[0].length, 0, rendermx[0].length - 1);
				
				++rendermx[x][y];
			}
			for (int x = 0; x < rendermx.length; ++x)
				for (int y = 0; y < rendermx[0].length; ++y){
					float v = clamp(rendermx[x][y] / (float)dpc, 0, 1);
					pain.setColor(Color.rgb((int)(color[0] * v), 
					                        (int)(color[1] * v), 
					                        (int)(color[2] * v)));
					canvas.drawRect(x * cellSize, y * cellSize, (x + 1) * cellSize, (y + 1) * cellSize, pain);
				}
			clearmx();
		}
	}
	
	public float dist(float x, float y, Dot d){
		return Vector.distance(Vector.getVector(x, y), d.pos);
	}
	
	public float dist2(float x, float y, Dot d){
		return Vector.distance2(Vector.getVector(x, y), d.pos);
	}
}
