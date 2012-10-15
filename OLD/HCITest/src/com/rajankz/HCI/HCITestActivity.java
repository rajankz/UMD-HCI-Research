package com.rajankz.HCI;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Toast;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;


public class HCITestActivity extends Activity implements OnKeyListener, OnGesturePerformedListener {
	
	public static final String TAG = "HCITestActivity";
	private GestureLibrary mLibrary;
	private HCIEditText textBox;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textBox = (HCIEditText)findViewById(R.id.inputText);
        textBox.setOnKeyListener(this);
        
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
              finish();
        }
        
        GestureOverlayView gov = (GestureOverlayView)findViewById(R.id.gesturesOverlay); 
		gov.bringToFront();
		gov.addOnGesturePerformedListener(this);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	//Log.i(TAG, event.);
    	Log.i(TAG, "KeyDown: "+String.valueOf(keyCode));
    	switch(keyCode){
    		case -99:
    			Toast.makeText(HCITestActivity.this, "specialKey", Toast.LENGTH_LONG).show();
    			return true;
    		default:
    			Toast.makeText(HCITestActivity.this, String.valueOf(keyCode) , Toast.LENGTH_LONG).show();
    			return super.onKeyDown(keyCode, event);
    	}    	
    }

	//@Override
	public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
		//Log.i(TAG, "KeyDown: "+String.valueOf(keyCode));
		/*if(keyCode==KeyEvent.KEYCODE_1)
		{
			
		}*/
		return false;
	}

	//@Override
	public void onGesturePerformed(GestureOverlayView gov, Gesture gesture) {
		Log.i(TAG, "some gesture");
		//Editable text;
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
	    //Log.v("performed","performed");
	    // We want at least one prediction
	    if (predictions.size() > 0) {
	        Prediction prediction = predictions.get(0);
	        // We want at least some confidence in the result
	        if (prediction.score > 1.0) {
	        	//if(prediction.name.equalsIgnoreCase("#")){
	        		Log.i(TAG, prediction.name);
	        		textBox.getEditableText().append(prediction.name);
	            //}
	        }
	    }
	}
}