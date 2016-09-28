package com.appublisher.quizbank.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * 图片处理
 */
public class ImageManager {

    public interface LoadingProgressListener {
        void onProgressUpdate(String imageUri, View view, int current, int total);
    }

    public interface LoadingListener {
        void onLoadingStarted(String imageUri, View view);
        void onLoadingFailed(String imageUri, View view, FailReason failReason);
        void onLoadingComplete(String imageUri, View view, Bitmap loadedImage);
        void onLoadingCancelled(String imageUri, View view);
    }

    /**
     * 初始化
     * @param context Context
     */
    public static void init(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .imageDownloader(new BaseImageDownloader(context, 5000, 5000))
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    /**
     * 展示图片
     * @param uri Url
     * @param imageview ImageView
     */
    public static void displayImage(String uri, ImageView imageview) {
        ImageLoader.getInstance().displayImage(uri, imageview);
    }

    /**
     * 展示图片
     * @param uri Url
     * @param imageview ImageView
     */
    public static void displayImage(String uri,
                                    ImageView imageview,
                                    final LoadingProgressListener progressListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(uri, imageview, options, null,
                new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri,
                                                 View view,
                                                 int current,
                                                 int total) {
                        progressListener.onProgressUpdate(imageUri, view, current, total);
                    }
                });
    }

    /**
     * 展示本地文件中的图片
     * @param path 路径
     * @param imageView ImageView
     */
    public static void displayImageFromFile(String path, ImageView imageView) {
        String uri = "file://" + path;
        ImageManager.displayImage(uri, imageView);
    }

    /**
     * 展示图片
     * @param uri Url
     * @param imageview ImageView
     */
    public static void displayImage(String uri,
                                    ImageView imageview,
                                    final LoadingListener listener,
                                    final LoadingProgressListener progressListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(uri, imageview, options,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        listener.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        listener.onLoadingFailed(imageUri, view, failReason);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        listener.onLoadingComplete(imageUri, view, loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        listener.onLoadingCancelled(imageUri, view);
                    }
                },
                new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri,
                                                 View view,
                                                 int current,
                                                 int total) {
                        progressListener.onProgressUpdate(imageUri, view, current, total);
                    }
                });
    }

    /**
     * 展示图片
     * @param uri Url
     * @param imageview ImageView
     */
    public static void displayImage(String uri,
                                    ImageView imageview,
                                    final LoadingListener listener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(uri, imageview, options,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        listener.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        listener.onLoadingFailed(imageUri, view, failReason);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        listener.onLoadingComplete(imageUri, view, loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        listener.onLoadingCancelled(imageUri, view);
                    }
                });
    }

    /**
     * 清除缓存
     */
    public static void clear() {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

}
