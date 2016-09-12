package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.model.VipManager;
import com.appublisher.quizbank.common.vip.model.VipZJZDModel;

/**
 * 小班：字迹诊断
 */
public class VipZJZDActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvMyjobAdd;
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
            // 将保存在本地的图片取出并缩小后显示在界面上
            Bitmap bitmap = mModel.getThumbnail(data);
            if (bitmap != null) mIvMyjobAdd.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_zjzd_myjob_iv) {
            // 添加图片
            mModel.toCamera();
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
        mIvMyjobAdd = (ImageView) findViewById(R.id.vip_zjzd_myjob_iv);
        mIvMyjobAdd.setOnClickListener(this);
    }

    private void showLoading() {
        ProgressDialogManager.showProgressDialog(this);
    }
}
