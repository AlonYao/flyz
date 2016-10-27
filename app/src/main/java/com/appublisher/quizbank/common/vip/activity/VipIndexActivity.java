package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.model.netdata.UserExamInfoModel;
import com.appublisher.lib_login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.CommonFragmentActivity;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.common.vip.model.VipIndexModel;
import com.appublisher.quizbank.common.vip.network.VipRequest;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VipIndexActivity extends BaseActivity implements RequestCallback {

    private TextView nickname;
    private RoundedImageView avatarImage;
    private TextView evaluationText;
    private TextView examText;
    public TextView messageTips;
    public TextView classTime;
    public TextView homeworkTimeText;
    public TextView homeworkTipsText;
    private ImageView settingImage;
    private View notificationView;
    private View exerciseView;
    private View courseView;
    private VipRequest mRequest;
    //um
    private boolean isEntryClick = false;
    private Map<String, String> actionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_index);
        setToolBar(this);
        mRequest = new VipRequest(this, this);
        //initViews
        initViews();
        setValues();

    }

    public void initViews() {
        nickname = (TextView) findViewById(R.id.nickname);
        evaluationText = (TextView) findViewById(R.id.ev_txt);
        avatarImage = (RoundedImageView) findViewById(R.id.user_avatar);
        examText = (TextView) findViewById(R.id.exam_txt);
        classTime = (TextView) findViewById(R.id.course_time);
        homeworkTimeText = (TextView) findViewById(R.id.homework_time);
        homeworkTipsText = (TextView) findViewById(R.id.homework_tips);
        messageTips = (TextView) findViewById(R.id.message_tips);
        notificationView = findViewById(R.id.message);
        exerciseView = findViewById(R.id.homework);
        courseView = findViewById(R.id.course);
        settingImage = (ImageView) findViewById(R.id.setting_image);
    }

    public void setValues() {

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
                final Intent intent = new Intent(VipIndexActivity.this, CommonFragmentActivity.class);
                intent.putExtra("from", "setting");
                startActivity(intent);
            }
        });

        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //um
                actionMap.clear();
                actionMap.put("Action", "Message");
                UmengManager.onEvent(VipIndexActivity.this, "VipHome", actionMap);
                isEntryClick = true;


                final Intent intent = new Intent(VipIndexActivity.this, VipNotificationActivity.class);
                startActivity(intent);
            }
        });

        exerciseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //um
                actionMap.clear();
                actionMap.put("Action", "Homework");
                UmengManager.onEvent(VipIndexActivity.this, "VipHome", actionMap);
                isEntryClick = true;

                final Intent intent = new Intent(VipIndexActivity.this, VipExerciseIndexActivity.class);
                startActivity(intent);
            }
        });
        courseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(VipIndexActivity.this, CommonFragmentActivity.class);
                intent.putExtra("from", "course");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        final UserInfoModel userInfoModel = LoginModel.getUserInfoM();
        nickname.setText(userInfoModel == null ? "" : userInfoModel.getNickname());
        LoginModel.setAvatar(this, avatarImage);
        final UserExamInfoModel userExamInfoModel = LoginModel.getExamInfo();
        String name = userExamInfoModel == null ? "" : userExamInfoModel.getName();
        String date = userExamInfoModel == null ? "" : userExamInfoModel.getDate();

        long day = Utils.dateMinusNow(date);
        if (day < 0) {
            day = 0;
        }
        String text = "距离" + name + "还有" + String.valueOf(day) + "天";
        examText.setText(text);
        mRequest.getVipIndexEntryData();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) return;
        if ("vip_index_entry_data".equals(apiName)) {
            VipIndexModel.dealEntryData(response, this);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //um
        if (!isEntryClick) {
            actionMap.clear();
            actionMap.put("Action", "0");
            UmengManager.onEvent(VipIndexActivity.this, "VipHome", actionMap);
            isEntryClick = true;
        }

    }
}
