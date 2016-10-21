package com.appublisher.quizbank.common.vip.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.model.VipBaseModel;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

public class VipBaseActivity extends BaseActivity {

    public static final String FILE = "file";
    public static final String URL = "url";

    public interface MyJobActionListener {
        void toCamera(int maxLength);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 显示我的作业
     * @param paths file path or Url
     * @param type file or Url
     * @param max_length 最大长度
     * @param myJobContainer 父容器
     * @param context 上下文
     * @param listener 动作监听
     */
    public void showMyJob(final ArrayList<String> paths,
                          String type,
                          final int max_length,
                          FlowLayout myJobContainer,
                          final Context context,
                          final MyJobActionListener listener) {
        if (FILE.equals(type)) {
            // 文件
            myJobContainer.removeAllViews();
            if (paths == null || paths.size() == 0) {
                // default
                ImageView imageView = getMyJobItem();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.toCamera(max_length);
                    }
                });
                myJobContainer.addView(imageView);
            } else {
                int size = paths.size() >= max_length ? max_length : paths.size();
                for (int i = 0; i < size; i++) {
                    final int index = i;
                    ImageView imageView = getMyJobItem();
                    ImageManager.displayImageFromFile(paths.get(i), imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =
                                    new Intent(context, VipGalleryActivity.class);
                            intent.putExtra(VipGalleryActivity.INTENT_INDEX, index);
                            intent.putExtra(VipGalleryActivity.INTENT_PATHS, paths);
                            intent.putExtra(VipGalleryActivity.INTENT_CAN_DELETE, true);
                            startActivityForResult(intent, VipBaseModel.GALLERY_REQUEST_CODE);
                        }
                    });
                    myJobContainer.addView(imageView);
                }
                // 是否显示添加按钮
                if (paths.size() < max_length) {
                    ImageView imageView = getMyJobItem();
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.toCamera(max_length - paths.size());
                        }
                    });
                    myJobContainer.addView(imageView);
                }
            }
        } else if (URL.equals(type)) {
            // Url
            myJobContainer.removeAllViews();
            if (paths == null) return;
            int size = paths.size();
            for (int i = 0; i < size; i++) {
                final int index = i;
                ImageView imageView = getMyJobItem();
                imageView.setImageResource(R.drawable.vip_loading);
                ImageManager.displayImage(paths.get(i), imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =
                                new Intent(context, VipGalleryActivity.class);
                        intent.putExtra(VipGalleryActivity.INTENT_INDEX, index);
                        intent.putExtra(VipGalleryActivity.INTENT_PATHS, paths);
                        startActivity(intent);
                    }
                });
                myJobContainer.addView(imageView);
            }
        }
    }

    /**
     * 获取我的作业item
     * @return ImageView
     */
    public ImageView getMyJobItem() {
        @SuppressLint("InflateParams")
        ImageView imageView = (ImageView)
                LayoutInflater.from(this).inflate(R.layout.vip_myjob_item, null);
        int px = Utils.dip2px(this, 75);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(px, px);
        params.setMargins(0, 0, Utils.dip2px(this, 10), Utils.dip2px(this, 10));
        imageView.setLayoutParams(params);
        return imageView;
    }

    /**
     * 更新提交按钮
     * @param curLength 当前长度
     * @param button Button
     */
    @SuppressWarnings("deprecation")
    public void updateSubmitButton(int curLength, Button button) {
        if (curLength > 0) {
            button.setBackgroundColor(getResources().getColor(R.color.themecolor));
        } else {
            button.setBackgroundColor(getResources().getColor(R.color.vip_gray));
        }
    }

    /**
     * 驳回Alert
     * @param msg 驳回原因
     * @param date 下次提交时间
     */
    public void showRejectAlert(String msg, String date) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();

        Window window = alertDialog.getWindow();
        if (window == null) return;
        window.setContentView(R.layout.vip_zjzd_reject_alert);
        window.setBackgroundDrawableResource(R.color.transparency);

        TextView tvMsg = (TextView) window.findViewById(R.id.vip_zjzd_reject_msg);
        Button btn = (Button) window.findViewById(R.id.vip_zjzd_reject_btn);

        String text;
        text = "驳回原因：" + msg + "\n\n";
        text = text + "下次提交时间：" + date;
        tvMsg.setText(text);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void showSubmitErrorToast() {
        Toast.makeText(this, "提交失败，请重试……", Toast.LENGTH_SHORT).show();
    }

}
