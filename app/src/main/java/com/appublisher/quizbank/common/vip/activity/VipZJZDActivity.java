package com.appublisher.quizbank.common.vip.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.model.VipManager;
import com.appublisher.quizbank.common.vip.model.VipZJZDModel;

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

    private void initData() {
        mModel = new VipZJZDModel(this);
    }

    private void initView() {
        mIvMyjobAdd = (ImageView) findViewById(R.id.vip_zjzd_myjob_iv);
        mIvMyjobAdd.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VipManager.CAMERA_REQUEST_CODE) {
            // 将保存在本地的图片取出并缩小后显示在界面上
            Bitmap bitmap = mModel.getThumbnail();
            mIvMyjobAdd.setImageBitmap(mModel.getThumbnail());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_zjzd_myjob_iv) {
            // 添加图片
            String[] avatarItems = new String[] { "选择本地图片", "拍照" };
            new AlertDialog.Builder(VipZJZDActivity.this)
                    .setItems(avatarItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
//                                    Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
//                                    intentFromGallery.setDataAndType(
//                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                            "image/*");
//                                    startActivityForResult(intentFromGallery,
//                                            IMAGE_REQUEST_CODE);
                            } else {
                                mModel.toCamera();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }
}
