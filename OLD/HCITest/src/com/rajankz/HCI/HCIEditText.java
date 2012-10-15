package com.rajankz.HCI;

import android.content.Context;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnKeyListener;

public class HCIEditText extends EditText implements OnKeyListener{

	public static final String TAG = "HCIEditText";
	public HCIEditText(Context context) {
		super(context);
		this.requestFocus();
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnKeyListener(this);
	}

	public HCIEditText(Context context, AttributeSet attrs){
		super(context, attrs);
		this.requestFocus();
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnKeyListener(this);
	}
	
	public HCIEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		this.requestFocus();
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnKeyListener(this);
	}
	
	//@Override
	public boolean onKey(View view, int keyCode, KeyEvent keyEvent){
		if(keyEvent.getAction() == KeyEvent.ACTION_DOWN)
			Log.i(TAG, "Event Detected: "+keyCode);

		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		Log.i(TAG, event.getKeyCode()+"");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_O)
			Log.i(TAG,"O down");
		Log.i(TAG,"KeyPress: "+String.valueOf(keyCode));
		return super.onKeyPreIme(keyCode, event);
	}

}
