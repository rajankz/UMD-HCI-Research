package com.example.hcigestuer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HCITouchView extends View {
	
	private Drawable mIcon;
	private static final int INVALID_POINTER_ID=-1;
	private int mEnterGesturePointerID = INVALID_POINTER_ID;
	private int mDrawGesturePointerID = INVALID_POINTER_ID;
	
	private static final String TAG = "HCITouchView";
	
	private float mPosX;
	private float mPosY;
	
	private float mLastTouchX;
	private float mLastTouchY;

	public HCITouchView(Context context) {
		//this(context, null, 0);
		super(context);
	}

	public HCITouchView(Context context, AttributeSet attrs) {
		//this(context, attrs, 0);
		super(context, attrs);
	}

	public HCITouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//mIcon = context.getResources().getDrawable(defStyle);
		//mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());
		//this.isInEditMode();
	}

	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		//canvas.save();
		//canvas.translate(mPosX, mPosY);
		//mIcon.draw(canvas);
		//canvas.restore();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		final int action = ev.getAction();
		switch(action & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:{
			final float x = ev.getX();
			final float y = ev.getY();
			
			mLastTouchX = x;
			mLastTouchY = y;
			
			Log.d(TAG,"Down: x="+x+" y="+y);
			mEnterGesturePointerID = ev.getPointerId(0);
			break;
		}
		case MotionEvent.ACTION_MOVE:{
			if((mDrawGesturePointerID == INVALID_POINTER_ID)||(mDrawGesturePointerID == mEnterGesturePointerID))
				break;
			final int mDrawPointerIndex = ev.findPointerIndex(mDrawGesturePointerID);
			final float x = ev.getX( mDrawPointerIndex);
			final float y = ev.getY(mDrawPointerIndex);
			
			final float dx = x - mLastTouchX;
			final float dy = y - mLastTouchY;
			
			mPosX += dx;
			mPosY += dy;
			
			mLastTouchX = x;
			mLastTouchY = y;
			
			Log.d(TAG, "MOVE x="+x+" and y="+y);
			
			invalidate();
			break;
		}
		
		case MotionEvent.ACTION_POINTER_DOWN:{
			final int mDrawPointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
					>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			Log.d(TAG, "ACTION_POINTER_DOWN index = "+mDrawPointerIndex);
			//if(pointerIndex == 0)
				//break;
			
			mDrawGesturePointerID = ev.getPointerId(mDrawPointerIndex);
			
			final float x = ev.getX(mDrawPointerIndex);
			final float y = ev.getY(mDrawPointerIndex);
			
			Log.d(TAG,"Secondary Pointer Down: x="+x+" y="+y);
			
			break;
		}
		
		//case MotionEvent.AC
		
		}
		return true;
	}
	
}
