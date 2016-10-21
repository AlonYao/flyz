package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.lib_basic.ImageManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.model.VipBaseModel;
import com.appublisher.quizbank.common.vip.model.VipZJZDModel;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 小班：字迹诊断
 */
public class VipZJZDActivity extends VipBaseActivity implements View.OnClickListener{

    private ImageView mIvExample;
    private TextView mTvMaterial;
    private TextView mTvStatus;
    private Button mBtnSubmit;
    private FlowLayout mMyjobContainer;
    private VipZJZDModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_zjzd);
        setToolBar(this);
        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == VipBaseModel.CAMERA_REQUEST_CODE) {
            // 拍照回调
            ArrayList<String> paths =
                    data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (mModel.mPaths != null) {
                mModel.mPaths.addAll(paths);
            } else {
                mModel.mPaths = paths;
            }
            showMyJob(mModel.mPaths, FILE, VipZJZDModel.MAX_LENGTH);
            updateSubmitButton();

        } else if (requestCode == VipBaseModel.GALLERY_REQUEST_CODE) {
            // 图片浏览回调
            ArrayList<String> paths =
                    data.getStringArrayListExtra(VipGalleryActivity.INTENT_PATHS);
            if (mModel.mCanSubmit) {
                showMyJob(paths, FILE, VipZJZDModel.MAX_LENGTH);
            } else {
                showMyJob(paths, URL, VipZJZDModel.MAX_LENGTH);
            }
            mModel.mPaths = paths;
            updateSubmitButton();
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
        } else if (id == R.id.vip_zjzd_submit) {
            // 提交
            if (mModel.isCanSubmit()) mModel.submit();
        }
    }

    private void initData() {
        mModel = new VipZJZDModel(this);
        mModel.mExerciseId = getIntent().getIntExtra("exerciseId", 0);
        // 获取练习详情
        showLoading();
        mModel.getExerciseDetail();
    }

    private void initView() {
        mIvExample = (ImageView) findViewById(R.id.vip_zjzd_example);
        mTvMaterial = (TextView) findViewById(R.id.vip_zjzd_material);
        mTvStatus = (TextView) findViewById(R.id.vip_zjzd_status);
        mBtnSubmit = (Button) findViewById(R.id.vip_zjzd_submit);
        mMyjobContainer = (FlowLayout) findViewById(R.id.vip_zjzd_myjob_container);

        mIvExample.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
    }

    public void showTvMaterial(String text) {
        mTvMaterial.setText(text);
    }

    public void showIvExample(String url) {
        ImageManager.displayImage(url, mIvExample);
    }

    public void showMyJob(ArrayList<String> paths, String type, int max_length) {
        showMyJob(paths, type, max_length, mMyjobContainer, this, new MyJobActionListener() {
            @Override
            public void toCamera(int maxLength) {
                mModel.toCamera(maxLength);
            }
        });
    }

    private void updateSubmitButton() {
        int curLength = mModel.mPaths == null ? 0 : mModel.mPaths.size();
        updateSubmitButton(curLength, mBtnSubmit);
    }

    /**
     * 显示状态文字
     * @param status 状态
     * @param text 文字
     */
    @SuppressWarnings("deprecation")
    public void showStatus(int status, String text) {
        if (status == 0) {
            mTvStatus.setVisibility(View.GONE);
        } else {
            mTvStatus.setVisibility(View.VISIBLE);
            // 文字
            if (status == 3) {
                mTvStatus.setText("等待审核");
            } else {
                mTvStatus.setText(text);
            }
            // 颜色
            if (status == 1) {
                mTvStatus.setTextColor(getResources().getColor(R.color.vip_green));
            } else {
                mTvStatus.setTextColor(getResources().getColor(R.color.vip_red));
            }
        }
    }

    public void showSubmitBtn(boolean is_show) {
        if (is_show) {
            mBtnSubmit.setVisibility(View.VISIBLE);
        } else {
            mBtnSubmit.setVisibility(View.GONE);
        }
    }

}
