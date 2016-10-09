package com.appublisher.quizbank.common.vip.activity;

import android.annotation.SuppressLint;
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
import com.appublisher.quizbank.common.vip.model.VipZJZDModel;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class VipBaseActivity extends BaseActivity {

    public static final String FILE = "file";
    public static final String URL = "url";

    private FlowLayout mMyJobContainer;
    private VipBaseModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VipBaseModel.CAMERA_REQUEST_CODE) {
            // 拍照回调
            ArrayList<String> paths =
                    data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            ArrayList<String> prePaths = mModel.getPaths();
            if (prePaths != null) {
                prePaths.addAll(paths);
            }
            mModel.setPaths(prePaths);
            showMyJob(mModel.getPaths(), FILE, VipZJZDModel.MAX_LENGTH);
        } else if (requestCode == VipBaseModel.GALLERY_REQUEST_CODE) {
            // 图片浏览回调
            ArrayList<String> paths =
                    data.getStringArrayListExtra(VipGalleryActivity.INTENT_PATHS);
            showMyJob(paths, FILE, VipZJZDModel.MAX_LENGTH);
            mModel.setPaths(paths);
        }
    }

    public void showMyJob(final ArrayList<String> paths,
                          String type,
                          int max_length,
                          final VipBaseModel model) {
        if (FILE.equals(type)) {
            // 文件
            mMyJobContainer.removeAllViews();
            if (paths == null || paths.size() == 0) {
                // default
                ImageView imageView = getMyJobItem();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.toCamera(VipZJZDModel.MAX_LENGTH);
                    }
                });
                mMyJobContainer.addView(imageView);
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
                                    new Intent(model.mContext, VipGalleryActivity.class);
                            intent.putExtra(VipGalleryActivity.INTENT_INDEX, index);
                            intent.putExtra(VipGalleryActivity.INTENT_PATHS, paths);
                            intent.putExtra(VipGalleryActivity.INTENT_CAN_DELETE, true);
                            startActivityForResult(intent, VipBaseModel.GALLERY_REQUEST_CODE);
                        }
                    });
                    mMyJobContainer.addView(imageView);
                }
                // 是否显示添加按钮
                if (paths.size() < max_length) {
                    ImageView imageView = getMyJobItem();
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            model.toCamera(VipZJZDModel.MAX_LENGTH - paths.size());
                        }
                    });
                    mMyJobContainer.addView(imageView);
                }
            }
        } else if (URL.equals(type)) {
            // Url
            mMyJobContainer.removeAllViews();
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
                                new Intent(model.mContext, VipGalleryActivity.class);
                        intent.putExtra(VipGalleryActivity.INTENT_INDEX, index);
                        intent.putExtra(VipGalleryActivity.INTENT_PATHS, paths);
                        startActivity(intent);
                    }
                });
                mMyJobContainer.addView(imageView);
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

    /**
     * get & set
     */
    public void setMyJobContainer(FlowLayout myJobContainer) {
        this.mMyJobContainer = myJobContainer;
    }
}
