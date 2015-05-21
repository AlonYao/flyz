package com.appublisher.quizbank.activity;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.network.Request;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ScaleImageActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.full_screen_image);

		String url = getIntent().getExtras().getString("imgUrl");
		String filePath = getIntent().getExtras().getString("filePath");

		PhotoView imageView = (PhotoView) findViewById(R.id.full_screen_imageView);
		
		if(url != null && !url.equals("")){
			new Request(this).loadImage(url, imageView);
		} else if(filePath != null && !filePath.equals("")) {
			imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
		}

		imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
			@Override
			public void onViewTap(View view, float v, float v1) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Umeng
		MobclickAgent.onPageStart("ScaleImageActivity");
		MobclickAgent.onResume(this);

		// TalkingData
		TCAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Umeng
		MobclickAgent.onPageEnd("ScaleImageActivity");
		MobclickAgent.onPause(this);

		// TalkingData
		TCAgent.onPause(this);
	}
}
