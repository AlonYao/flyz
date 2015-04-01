package com.appublisher.quizbank.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastManager {

	private static Toast mToast;
	
	public static void showToastCenter(Context context, String text) {
		if (mToast != null) {
			mToast.setText(text);
		} else {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}
	
	public static void showToast(Context context, String text) {
		if (mToast != null) {
			mToast.setText(text);
		} else {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		
		mToast.show();
	}
}
