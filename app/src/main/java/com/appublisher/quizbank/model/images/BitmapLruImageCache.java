package com.appublisher.quizbank.model.images;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

@SuppressLint("NewApi")
public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageCache {
	// 限制图片的最小宽度
	public int minWidth=0;
	
	public boolean success = true;
	
	public BitmapLruImageCache(int maxSize) {
		super(maxSize);
	}
	
	public void setMinWidth(int width) {
		this.minWidth = width;
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		if (get(url)==null) {
			success = false;
		}
		
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// 对小于指定尺寸的图片进行放大
		if (minWidth>0 && bitmap!=null){
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			if(width < minWidth){
				float scale = ((float)minWidth)/width;
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0 ,width, height, matrix, true);
			}
		}
		
		put(url, bitmap);
	}
}