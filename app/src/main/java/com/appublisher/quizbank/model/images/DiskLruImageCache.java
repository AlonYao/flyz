package com.appublisher.quizbank.model.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.appublisher.quizbank.BuildConfig;
import com.appublisher.quizbank.utils.Logger;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementation of DiskLruCache
 * 
 */
public class DiskLruImageCache implements ImageCache {
    private DiskLruCache mDiskCache;
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private static int IO_BUFFER_SIZE = 8*1024;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    
    // 限制图片的最小宽度
 	public int minWidth=0;
 	public boolean success = true;
 	
 	
    public DiskLruImageCache(Context context,String uniqueName, int diskCacheSize,
        CompressFormat compressFormat, int quality ) {
        try {
                final File diskCacheDir = getDiskCacheDir(context, uniqueName );
                mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
                mCompressFormat = compressFormat;
                mCompressQuality = quality;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    
    public void setMinWidth(int width) {
		this.minWidth = width;
	}

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor )
        throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ), IO_BUFFER_SIZE );
            return bitmap.compress( mCompressFormat, mCompressQuality, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {

        final String cachePath = context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

    @Override
    public void putBitmap( String key, Bitmap data ) {
        DiskLruCache.Editor editor = null;
        try {
        	// 对小于指定尺寸的图片进行放大
    		if (minWidth>0 && data!=null){
    			int width = data.getWidth();
    			int height = data.getHeight();
    			if(width < minWidth){
    				float scale = ((float)minWidth)/width;
    				Matrix matrix = new Matrix();
    				matrix.postScale(scale, scale);
    				data = Bitmap.createBitmap(data, 0, 0 ,width, height, matrix, true);
    			}
    		}
    		
            editor = mDiskCache.edit( createKey(key) );
            if ( editor == null ) {
                return;
            }

            if( writeBitmapToFile( data, editor ) ) {               
                mDiskCache.flush();
                editor.commit();
                if ( BuildConfig.DEBUG ) {
//                	Logger.d( "cache_test_DISK_: image put on disk cache " + key );
                }
            } else {
                editor.abort();
                if ( BuildConfig.DEBUG ) {
//                	Logger.d( "cache_test_DISK_: ERROR on: image put on disk cache " + key );
                }
            }   
        } catch (IOException e) {
            if ( BuildConfig.DEBUG ) {
//            	Logger.d( "cache_test_DISK_: ERROR on: image put on disk cache " + key );
            }
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }           
        }
//        Logger.v("Added item to Disk Cache: "+key);
    }

    @Override
    public Bitmap getBitmap( String key ) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskCache.get( createKey(key) );
            if ( snapshot == null ) {
        		success = false;
                return null;
            }
            final InputStream in = snapshot.getInputStream( 0 );
            if ( in != null ) {
                final BufferedInputStream buffIn = 
                new BufferedInputStream( in, IO_BUFFER_SIZE );
                bitmap = BitmapFactory.decodeStream( buffIn );              
            }   
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        if ( BuildConfig.DEBUG ) {
//        	Logger.d( "cache_test_DISK_: "+bitmap == null ? "" : "image read from disk " + key);
        }
//        Logger.v("Retrieved item from Disk Cache: "+key);
        return bitmap;
    }

    public boolean containsKey( String key ) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get( key );
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        if ( BuildConfig.DEBUG ) {
        	Logger.d("cache_test_DISK_: disk cache CLEARED");
        }
        try {
            mDiskCache.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }


    /**
	 * Creates a unique cache key based on a url value
	 * @param url
	 * 		url to be used in key creation
	 * @return
	 * 		cache key value
	 */
	private String createKey(String url){
		return String.valueOf(url.hashCode());
	}
}
