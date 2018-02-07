package com.milesseventh.shiningdots;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class GameActivity extends Activity{
	class HarvesterOfEyes extends SurfaceView implements SurfaceHolder.Callback{
		MainLoop ml;
		
		public HarvesterOfEyes(Context context) {
			super(context);
			setFocusable(true);
			getHolder().addCallback(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

		@Override
		public void surfaceCreated(SurfaceHolder sh){
			ml = new MainLoop();
			ml.sh = sh;
			ml.sm = ((SensorManager) me.getSystemService(Activity.SENSOR_SERVICE));
			this.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					ml.game.RENDER_DEBUG = !ml.game.RENDER_DEBUG;
				}
			});
			ml.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0){
			ml.running = false;
		}
	}
	
	public HarvesterOfEyes eyeless;
	public static Activity me;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		me = this;
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		eyeless = new HarvesterOfEyes(this);
		setContentView(eyeless);
	}
	
	@Override
	public void onBackPressed(){
		eyeless.ml.isPaused = true;
		SettingsFragment sf = new SettingsFragment(eyeless.ml);
		sf.show(getFragmentManager(), "...");
	}
}
