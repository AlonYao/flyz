package com.appublisher.quizbank.activity;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.customui.ScaleImageView;
import com.appublisher.quizbank.network.Request;

public class ScaleImageActivity extends Activity{
	private ScaleImageView imageView;
	private boolean isReturn = true;
	
	private float downX, downY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.full_screen_image);

		String url = getIntent().getExtras().getString("imgUrl");
		String filePath = getIntent().getExtras().getString("filePath");
		String bitmap = getIntent().getExtras().getString("bitmap");
		
		imageView = (ScaleImageView)findViewById(R.id.full_screen_imageView);
		
		if(url != null && !url.equals("")){
			new Request(this).loadImage(url, imageView);
		} else if(filePath != null && !filePath.equals("")) {
			imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
		}

//        else if(bitmap.equals("toscale") && DailyLearnApp.toScale != null) {
//			imageView.setImageBitmap(BitmapFactory.decodeFile(DailyLearnApp.toScale));
//		}

//		DailyLearnApp.resumeFromScaleImage = true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

	}
	
	@Override
	protected void onPause() {
		super.onPause();

	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			isReturn = true;
			downX = ev.getX();
			downY = ev.getY();
			
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = ev.getX() - downX;
			float moveY = ev.getY() - downY;
			
			if((moveY*moveY + moveX*moveX) > 10){
				isReturn = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(isReturn){
				ScaleImageActivity.this.finish();
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
}
