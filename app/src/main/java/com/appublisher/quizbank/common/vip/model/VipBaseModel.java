package com.appublisher.quizbank.common.vip.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 小班模块管理类
 */
public class VipBaseModel implements RequestCallback{

    public Context mContext;
    VipRequest mVipRequest;

    public static final int CAMERA_REQUEST_CODE = 10;
    public static final int GALLERY_REQUEST_CODE = 11;
    public static final String PIC_CACHE_DIR =
            Environment.getExternalStorageDirectory() + "/yaoguo/vip/";

    VipBaseModel(Context context) {
        mContext = context;
        mVipRequest = new VipRequest(mContext, this);
    }

    /**
     * 获取缩略图
     * @param data 图片地址
     * @param index 图片序号
     * @param targetWidth 缩放后宽
     * @param targetHeight 缩放后高
     * @return Bitmap
     */
    public Bitmap getThumbnail(Intent data, int index, int targetWidth, int targetHeight) {
        if (data == null) return null;

        Bitmap bitmap;
        ArrayList<String> paths =
                data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
        if (paths == null || index >= paths.size()) return null;

        String path = paths.get(index);
        if (path == null || path.length() == 0) return null;

        Bitmap preBitmap = BitmapFactory.decodeFile(path);
        if (preBitmap == null) return null;

        int scale = getScale(
                preBitmap.getWidth(), preBitmap.getHeight(), targetWidth, targetHeight);
        bitmap = picZoom(
                preBitmap,
                preBitmap.getWidth() / scale,
                preBitmap.getHeight() / scale);
        if (preBitmap != bitmap && !preBitmap.isRecycled()) {
            preBitmap.recycle();
        }

        return bitmap;
    }

    /**
     * 获取缩略图
     * @param targetWidth 缩放后宽
     * @param targetHeight 缩放后高
     * @return Bitmap
     */
    public Bitmap getThumbnail(String path, int targetWidth, int targetHeight) {
        if (path == null || path.length() == 0) return null;

        Bitmap bitmap;
        Bitmap preBitmap = BitmapFactory.decodeFile(path);
        if (preBitmap == null) return null;

        int scale = getScale(
                preBitmap.getWidth(), preBitmap.getHeight(), targetWidth, targetHeight);
        bitmap = picZoom(
                preBitmap,
                preBitmap.getWidth() / scale,
                preBitmap.getHeight() / scale);
        if (preBitmap != bitmap && !preBitmap.isRecycled()) {
            preBitmap.recycle();
        }

        return bitmap;
    }

    /**
     * 通过序号获取图片路径
     * @param data 数据
     * @param index 序号
     * @return String
     */
    public String getPathByIndex(Intent data, int index) {
        if (data == null) return null;
        ArrayList<String> paths =
                data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
        if (paths == null || index >= paths.size()) return null;
        return paths.get(index);
    }

    /**
     * 获取比例
     * @param oldWidth 旧宽
     * @param oldHeight 旧高
     * @param newWidth 新宽
     * @param newHeight 新高
     * @return int
     */
    private static int getScale(int oldWidth, int oldHeight, int newWidth, int newHeight) {
        if ((oldHeight > newHeight && oldWidth > newWidth)
                || (oldHeight <= newHeight && oldWidth > newWidth)) {
            int be = (int) (oldWidth / (float) newWidth);
            if (be <= 1)
                be = 1;
            return be;
        } else if (oldHeight > newHeight && oldWidth <= newWidth) {
            int be = (int) (oldHeight / (float) newHeight);
            if (be <= 1)
                be = 1;
            return be;
        }
        return 1;
    }

    /**
     * 缩放
     * @param bmp 原始图
     * @param width 缩放后宽
     * @param height 缩放后高
     * @return Bitmap
     */
    private Bitmap picZoom(Bitmap bmp, int width, int height) {
        int bmpWidth = bmp.getWidth();
        int bmpHeght = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
    }

    /**
     * 跳转至拍照
     * @param max_length 最大能获取的图片数量
     */
    public void toCamera(int max_length) {
        MultiImageSelector.create()
                .count(max_length)
                .start((Activity) mContext, CAMERA_REQUEST_CODE);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
