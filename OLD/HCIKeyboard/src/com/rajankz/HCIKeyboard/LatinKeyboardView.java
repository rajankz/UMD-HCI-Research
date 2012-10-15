/*
 * Copyright (C) 2008-2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.rajankz.HCIKeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.*;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;

import android.util.Log;

public class LatinKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener, android.view.KeyEvent.Callback{

    static final int KEYCODE_OPTIONS = -100;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onLongPress(Key key) {
    	Log.d("LKV","onLongPress"+key.toString());
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }
    
    @Override
    public void swipeDown(){
    	super.swipeDown();
    }
    
    @Override
    public void swipeRight(){
    	super.swipeRight();
    }
    
    @Override
    public void swipeLeft(){
    	super.swipeLeft();
    }
    
    @Override
    public void swipeUp(){
    	super.swipeUp();
    }
/*
	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		// TODO Auto-generated method stub
		Log.d("LKV","onKey"+primaryCode);
		
	}

	@Override
	public void onPress(int keyCode) {
		// TODO Auto-generated method stub
		Log.d("LKV","onPress"+keyCode);
		
	}

	@Override
	public void onRelease(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onText(CharSequence arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		Log.d("LKV", "onKeyDown"+keyCode);
		return true;
	}
*/

	public void onKey(int arg0, int[] arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onPress(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRelease(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onText(CharSequence arg0) {
		// TODO Auto-generated method stub
		
	}
}
