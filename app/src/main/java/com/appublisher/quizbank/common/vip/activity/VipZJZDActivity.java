package com.appublisher.quizbank.common.vip.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.model.VipManager;
import com.appublisher.quizbank.common.vip.model.VipZJZDModel;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 小班：字迹诊断
 */
public class VipZJZDActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String FILE = "file";
    public static final String URL = "url";

    private ImageView mIvExample;
    private TextView mTvMaterial;
    private TextView mTvStatus;
    private FlowLayout mMyjobContainer;
    private VipZJZDModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_zjzd);
        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VipManager.CAMERA_REQUEST_CODE) {
            ArrayList<String> paths =
                    data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (mModel.getPaths() == null) {
                mModel.setPaths(paths);
            } else {
                ArrayList<String> temp = mModel.getPaths();
                temp.addAll(paths);
                mModel.setPaths(temp);
            }
            showMyJob(mModel.getPaths(), FILE, VipZJZDModel.MAX_LENGTH);
        } else if (requestCode == VipManager.GALLERY_REQUEST_CODE) {
            ArrayList<String> paths =
                    data.getStringArrayListExtra(VipGalleryActivity.INTENT_PATHS);
            showMyJob(paths, FILE, VipZJZDModel.MAX_LENGTH);
            mModel.setPaths(paths);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.vip_zjzd_example) {
            // 作业示例
            ArrayList<String> list = new ArrayList<>();
            list.add(mModel.getExampleUrl());
            Intent intent =
                    new Intent(VipZJZDActivity.this, VipGalleryActivity.class);
            intent.putExtra(VipGalleryActivity.INTENT_PATHS, list);
            startActivity(intent);
        }
    }

    private void initData() {
        mModel = new VipZJZDModel(this);
        mModel.setExerciseId(getIntent().getIntExtra(VipZJZDModel.INTENT_EXERCISEID, 0));
        // 获取练习详情
        showLoading();
        mModel.getExerciseDetail();
    }

    private void initView() {
        mIvExample = (ImageView) findViewById(R.id.vip_zjzd_example);
        mTvMaterial = (TextView) findViewById(R.id.vip_zjzd_material);
        mTvStatus = (TextView) findViewById(R.id.vip_zjzd_status);
        mMyjobContainer = (FlowLayout) findViewById(R.id.vip_zjzd_myjob_container);

        mIvExample.setOnClickListener(this);
    }

    private void showLoading() {
        ProgressDialogManager.showProgressDialog(this);
    }

    public void showTvMaterial(String text) {
        mTvMaterial.setText(text);
    }

    public void showIvExample(String url) {
        ImageManager.displayImage(url, mIvExample);
    }

    public void showMyJob(final ArrayList<String> paths, String type, int max_length) {
        if (FILE.equals(type)) {
            // 文件
            mMyjobContainer.removeAllViews();
            if (paths == null || paths.size() == 0) {
                // default
                ImageView imageView = getMyJobItem(this);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mModel.toCamera(VipZJZDModel.MAX_LENGTH);
                    }
                });
                mMyjobContainer.addView(imageView);
            } else {
                int size = paths.size() >= max_length ? max_length : paths.size();
                for (int i = 0; i < size; i++) {
                    final int index = i;
                    ImageView imageView = getMyJobItem(this);
                    ImageManager.displayImageFromFile(paths.get(i), imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =
                                    new Intent(VipZJZDActivity.this, VipGalleryActivity.class);
                            intent.putExtra(VipGalleryActivity.INTENT_INDEX, index);
                            intent.putExtra(VipGalleryActivity.INTENT_PATHS, paths);
                            intent.putExtra(VipGalleryActivity.INTENT_CAN_DELETE, true);
                            startActivityForResult(intent, VipManager.GALLERY_REQUEST_CODE);
                        }
                    });
                    mMyjobContainer.addView(imageView);
                }
                // 是否显示添加按钮
                if (paths.size() < max_length) {
                    ImageView imageView = getMyJobItem(this);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mModel.toCamera(VipZJZDModel.MAX_LENGTH - paths.size());
                        }
                    });
                    mMyjobContainer.addView(imageView);
                }
            }
        } else if (URL.equals(type)) {
            // Url
            mMyjobContainer.removeAllViews();
            if (paths == null) return;
            int size = paths.size();
            for (int i = 0; i < size; i++) {
                final int index = i;
                ImageView imageView = getMyJobItem(this);
                ImageManager.displayImage(paths.get(i), imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =
                                new Intent(VipZJZDActivity.this, VipGalleryActivity.class);
                        intent.putExtra(VipGalleryActivity.INTENT_INDEX, index);
                        intent.putExtra(VipGalleryActivity.INTENT_PATHS, paths);
                        startActivity(intent);
                    }
                });
                mMyjobContainer.addView(imageView);
            }
        }
    }

    /**
     * 获取我的作业item
     * @param context Context
     * @return ImageView
     */
    private ImageView getMyJobItem(Context context) {
        @SuppressLint("InflateParams")
        ImageView imageView = (ImageView)
                LayoutInflater.from(this).inflate(R.layout.vip_myjob_item, null);
        int px = Utils.dip2px(context, 75);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(px, px);
        params.setMargins(0, 0, Utils.dip2px(context, 10), Utils.dip2px(context, 10));
        imageView.setLayoutParams(params);
        return imageView;
    }

    public void showStatus(int status, String text) {
        mTvStatus.setText(text);
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
        window.setContentView(R.layout.vip_zjzd_reject_alert);
        window.setBackgroundDrawableResource(R.color.transparency);

        TextView tvMsg = (TextView) window.findViewById(R.id.vip_zjzd_reject_msg);
        Button btn = (Button) window.findViewById(R.id.vip_zjzd_reject_btn);

        String text;
        text = "驳回原因：" + msg + "\n";
        text = text + "下次提交时间：" + date;
        tvMsg.setText(text);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

}
