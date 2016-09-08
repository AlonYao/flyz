package com.appublisher.quizbank.common.vip.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.network.VipParamBuilder;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 小班模块管理类
 */
public class VipManager implements RequestCallback{

    public Context mContext;
    public VipRequest mVipRequest;

    public static final int CAMERA_REQUEST_CODE = 10;
    public static final String PIC_CACHE_DIR =
            Environment.getExternalStorageDirectory() + "/yaoguo/vip/";
    private IntelligentPaperListener mIntelligentPaperListener;

    public VipManager(Context context) {
        mContext = context;
        mVipRequest = new VipRequest(mContext, this);
    }

    /**
     * 智能组卷接口
     */
    public interface IntelligentPaperListener {
        void complete(JSONObject resp);
    }

    /**
     * 获取智能组卷
     * @param listener IntelligentPaperListener
     * @param exercise_id 练习id
     */
    public void obtainIntelligentPaper(int exercise_id, IntelligentPaperListener listener) {
        mIntelligentPaperListener = listener;
        ProgressDialogManager.showProgressDialog(mContext);
        mVipRequest.getIntelligentPaper(exercise_id);
    }

    /**
     * 处理智能组卷回调
     * @param response JSONObject
     */
    private void dealIntelligentPaperResp(JSONObject response) {
        mIntelligentPaperListener.complete(response);
    }

    /**
     * 小班系统真题作业提交
     */
    public void submitPaper(ArrayList<HashMap<String, Object>> list,
                            int exercise_id) {
        if (list == null) return;

        // 初始化数据
        int duration_total = 0;

        JSONArray answers = new JSONArray();

        // 标记有没有未做的题
        boolean hasNoAnswer = false;

        HashMap<String, Object> userAnswerMap;
        for (int i = 0; i < list.size(); i++) {
            try {
                userAnswerMap = list.get(i);

                int id = (int) userAnswerMap.get("id");
                String answer = (String) userAnswerMap.get("answer");
                int category = (int) userAnswerMap.get("category_id");
                int duration = (int) userAnswerMap.get("duration");
                String right_answer = (String) userAnswerMap.get("right_answer");
                //noinspection unchecked
                ArrayList<Integer> note_ids = (ArrayList<Integer>) userAnswerMap.get("note_ids");

                // 判断对错
                int is_right = 0;
                if (answer != null && right_answer != null
                        && !"".equals(answer) && answer.equals(right_answer)) {
                    is_right = 1;
                }

                // 标记有没有未做的题
                if (answer == null || answer.length() == 0) hasNoAnswer = true;

                // 统计总时长
                duration_total = duration_total + duration;

                JSONObject joQuestion = new JSONObject();
                joQuestion.put("id", id);
                joQuestion.put("answer", answer);
                joQuestion.put("is_right", is_right);
                joQuestion.put("category", category);
                joQuestion.put("note_ids", note_ids);
                joQuestion.put("duration", duration);
                answers.put(joQuestion);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (hasNoAnswer) {
            // 提示用户存在未完成课程
            showUnFinishAlert(mContext, exercise_id, answers.toString(), duration_total);
        } else {
            postPaperAnswer(mContext, exercise_id, answers.toString(), duration_total);
        }
    }

    /**
     * 提交真题答案
     * @param context Context
     * @param exercise_id 练习id
     * @param answers 用户答案
     * @param duration 总时长
     */
    private void postPaperAnswer(Context context,
                                 int exercise_id,
                                 String answers,
                                 int duration) {
        ProgressDialogManager.showProgressDialog(context, false);
        VipSubmitEntity entity = new VipSubmitEntity();
        entity.exercise_id = exercise_id;
        entity.answer_content = answers;
        entity.duration = duration;
        new VipRequest(context, this).submit(VipParamBuilder.submit(entity));
    }

    /**
     * 如果有未完成题目时的提示
     */
    public void showUnFinishAlert(final Context context,
                                 final int exercise_id,
                                 final String answers,
                                 final int duration) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.alert_answersheet_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_answersheet_p,
                        new DialogInterface.OnClickListener() {// 确定

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postPaperAnswer(
                                        context,
                                        exercise_id,
                                        answers,
                                        duration);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.alert_answersheet_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    /**
     * 获取缩略图
     * @param preBitmap 原始图
     * @param targetWidth 缩放后宽
     * @param targetHeight 缩放后高
     * @return Bitmap
     */
    public static Bitmap getThumbnail(Bitmap preBitmap, int targetWidth, int targetHeight) {
        Bitmap bitmap;
        if (preBitmap == null) return null;
        // 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
        int scale = getScale(
                preBitmap.getWidth(), preBitmap.getHeight(), targetWidth, targetHeight);
        bitmap = picZoom(
                preBitmap,
                preBitmap.getWidth() / scale,
                preBitmap.getHeight() / scale);
        // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
        preBitmap.recycle();
        return bitmap;
    }

    /**
     * 获取缩略图
     * @param file_name 文件名
     * @param targetWidth 缩放后宽
     * @param targetHeight 缩放后高
     * @return Bitmap
     */
    public static Bitmap getThumbnail(String file_name, int targetWidth, int targetHeight) {

        Logger.e("1111111111111111111");

        Bitmap bitmap;
        Bitmap preBitmap = BitmapFactory.decodeFile(PIC_CACHE_DIR + file_name);
        if (preBitmap == null) return null;
        // 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
        int scale = getScale(
                preBitmap.getWidth(), preBitmap.getHeight(), targetWidth, targetHeight);
        bitmap = picZoom(
                preBitmap,
                preBitmap.getWidth() / scale,
                preBitmap.getHeight() / scale);
        // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
        preBitmap.recycle();
        return bitmap;
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
    private static Bitmap picZoom(Bitmap bmp, int width, int height) {
        int bmpWidth = bmp.getWidth();
        int bmpHeght = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
    }

    /**
     * 跳转至拍照
     * @param file_name 文件名
     */
    public void toCamera(String file_name) {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        FileManager.mkDir(PIC_CACHE_DIR);
        File file = new File(PIC_CACHE_DIR, file_name);
        intentFromCapture.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(file));
        ((Activity) mContext).startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (VipRequest.GET_INTELLIGENT_PAPER.equals(apiName)) {
            dealIntelligentPaperResp(response);
        }
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
