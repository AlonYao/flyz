package com.appublisher.quizbank.common.vip.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

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
    private ImageView getMyJobItem() {
        @SuppressLint("InflateParams")
        ImageView imageView = (ImageView)
                LayoutInflater.from(this).inflate(R.layout.vip_myjob_item, null);
        int px = Utils.dip2px(this, 75);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(px, px);
        params.setMargins(0, 0, Utils.dip2px(this, 10), Utils.dip2px(this, 10));
        imageView.setLayoutParams(params);
        return imageView;
    }

}
