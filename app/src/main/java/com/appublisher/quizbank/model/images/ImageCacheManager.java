package com.appublisher.quizbank.model.images;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.appublisher.lib_basic.volley.Request;

/**
 * Implementation of volley's ImageCache interface. This manager tracks the application image loader and cache. 
 * 
 * Volley recommends an L1 non-blocking cache which is the default MEMORY CacheType. 
 * @author Trey Robinson
 *
 */
public class ImageCacheManager{

	/**
	 * Volley recommends in-memory L1 cache but both a disk and memory cache are provided.
	 * Volley includes a L2 disk cache out of the box but you can technically use a disk cache as an L1 cache provided
	 * you can live with potential i/o blocking. 
	 *
	 */
	public enum CacheType {
		DISK, 
		MEMORY
	}
	
	private static ImageCacheManager mInstance;
	
	/**
	 * Volley image loader 
	 */
	private ImageLoader mImageLoader;

	
	/**
	 * Image cache implementation
	 */
//	private ImageCache mImageCache;
	
	public DiskLruImageCache mDistCache;
	public BitmapLruImageCache mBitmapCache;
	
	/**
	 * 设置图片的最小宽度
	 * 
	 */
	public int minWidth=0;
	
	
	/**
	 * @return
	 * 		instance of the cache manager
	 */
	public static ImageCacheManager getInstance(){
		if(mInstance == null)
			mInstance = new ImageCacheManager();
		
		return mInstance;
	}
	
	/**
	 * Initializer for the manager. Must be called prior to use. 
	 * 
	 * @param context
	 * 			application context
	 * @param uniqueName
	 * 			name for the cache location
	 * @param cacheSize
	 * 			max size for the cache
	 * @param compressFormat
	 * 			file type compression format.
	 * @param quality
	 */
	public void init(Context context, String uniqueName, int cacheSize, CompressFormat compressFormat, int quality, CacheType type){
		switch (type) {
			case DISK:
				mDistCache= new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
				if (minWidth>0) {
					mDistCache.setMinWidth(minWidth);
				}
				mImageLoader = new ImageLoader(Request.getRequestQueue(), mDistCache);
				break;
				
			case MEMORY:
				mBitmapCache = new BitmapLruImageCache(cacheSize);
				if (minWidth>0) {
					mBitmapCache.setMinWidth(minWidth);
				}
				mImageLoader = new ImageLoader(Request.getRequestQueue(), mBitmapCache);
				break;
				
			default:
				mBitmapCache = new BitmapLruImageCache(cacheSize);
				if (minWidth>0) {
					mBitmapCache.setMinWidth(minWidth);
				}
				mImageLoader = new ImageLoader(Request.getRequestQueue(), mBitmapCache);
				break;
		}
	}
	
	
	/**
	 * 	Executes and image load
	 * @param url
	 * 		location of image
	 * @param listener
	 * 		Listener for completion
	 */
	public void getImage(String url, ImageListener listener){
		mImageLoader.get(url, listener);
	}

	/**
	 * @return
	 * 		instance of the image loader
	 */
	public ImageLoader getImageLoader() {
		return mImageLoader;
	}
}

