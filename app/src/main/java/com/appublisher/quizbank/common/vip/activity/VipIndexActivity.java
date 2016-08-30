package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_login.activity.UserInfoActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.model.netdata.UserExamInfoModel;
import com.appublisher.lib_login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.fragment.SettingFragment;
import com.appublisher.quizbank.model.netdata.exam.ExamDetailModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.umeng.fb.model.UserInfo;

public class VipIndexActivity extends BaseActivity {

    private TextView nickname;
    private RoundedImageView avatarImage;
    private TextView evaluationText;
    private TextView examText;
    private ImageView settingImage;
    private ScrollView containerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_index);
        setToolBar(this);

        //initViews
        initViews();
        setValues();

    }

    public void initViews() {
        containerView = (ScrollView) findViewById(R.id.container_view);
        nickname = (TextView) findViewById(R.id.nickname);
        evaluationText = (TextView) findViewById(R.id.ev_txt);
        avatarImage = (RoundedImageView) findViewById(R.id.user_avatar);
        examText = (TextView) findViewById(R.id.exam_txt);
        settingImage = (ImageView) findViewById(R.id.setting_image);
    }

    public void setValues() {

        final UserInfoModel userInfoModel = LoginModel.getUserInfoM();
        nickname.setText(userInfoModel.getNickname());
        LoginModel.setAvatar(this, avatarImage);
        final UserExamInfoModel userExamInfoModel = LoginModel.getExamInfo();
        String name = userExamInfoModel.getName();
        String date = userExamInfoModel.getDate();

        long day = Utils.dateMinusNow(date);
        if (day < 0) {
            day = 0;
        }
        String text = "距离" + name + "还有" + String.valueOf(day) + "天";
        examText.setText(text);

        evaluationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(VipIndexActivity.this, EvaluationActivity.class);
                startActivity(intent);
            }
        });

        settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container_view,new SettingFragment());
            }
        });
    }

}
