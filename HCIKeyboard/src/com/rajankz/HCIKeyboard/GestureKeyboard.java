package com.rajankz.HCIKeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;

public class GestureKeyboard extends Keyboard {
	
	public static final int KEYCODE_GESTURE = -99;
	
	GestureKeyboard(Context context, int n){
		super(context, n);
	}
}
