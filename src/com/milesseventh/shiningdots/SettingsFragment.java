package com.milesseventh.shiningdots;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;;

public class SettingsFragment extends DialogFragment {
	private MainLoop ml;
	
	public SettingsFragment(MainLoop _ml){
		ml = _ml;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder builder = new Builder(getActivity());
		LinearLayout ch = new LinearLayout(getActivity());
		LinearLayout ll = new LinearLayout(getActivity());
		LinearLayout cl = new LinearLayout(getActivity());
		LinearLayout vl = new LinearLayout(getActivity());
		vl.setOrientation(LinearLayout.VERTICAL);
		
		final EditText su = new EditText(getActivity());
		su.setHint("Sensors update period (ms)");
		su.setText("" + ml.sup);
		su.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_NUMBER);
		ch.addView(su);
		
		final CheckBox cs = new CheckBox(getActivity());
		cs.setText("Show FPS");
		cs.setChecked(ml.showFPS);
		ch.addView(cs);
		
		vl.addView(ch);
		
		final EditText de = new EditText(getActivity());
		de.setHint("Dots amount");
		de.setText("" + ml.dotsAmount);
		de.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_NUMBER);
		ll.addView(de);
		
		final EditText ps = new EditText(getActivity());
		ps.setHint("Cell side size");
		ps.setText("" + ml.cell);
		ps.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_NUMBER);
		ll.addView(ps);
		
		final EditText bv = new EditText(getActivity());
		bv.setHint("Dots per cell for max brightness");
		bv.setText("" + ml.dpc);
		bv.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_NUMBER);
		ll.addView(bv);
		
		vl.addView(ll);

		final EditText cr = new EditText(getActivity());
		cr.setHint("Red value [0; 255]");
		cr.setText("" + ml.color[0]);
		cr.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_NUMBER);
		cl.addView(cr);
		final EditText cg = new EditText(getActivity());
		cg.setHint("Green value [0; 255]");
		cg.setText("" + ml.color[1]);
		cg.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_NUMBER);
		cl.addView(cg);
		final EditText cb = new EditText(getActivity());
		cb.setHint("Blue value [0; 255]");
		cb.setText("" + ml.color[2]);
		cb.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_NUMBER);
		cl.addView(cb);
		
		vl.addView(cl);
		
		builder.setView(vl).setTitle("Settings").setPositiveButton("Apply", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					ml.dotsAmount = Integer.parseInt(de.getText().toString());
					ml.cell = Integer.parseInt(ps.getText().toString());
					ml.dpc = Integer.parseInt(bv.getText().toString());
					ml.sup = Integer.parseInt(su.getText().toString());
					ml.color[0] = Integer.parseInt(cr.getText().toString());
					ml.color[1] = Integer.parseInt(cg.getText().toString());
					ml.color[2] = Integer.parseInt(cb.getText().toString());
				} catch(Exception e){}
				ml.showFPS = cs.isChecked();
				ml.reload();
				ml.isPaused = false;
			}
		});
		return builder.create();
	}
}
