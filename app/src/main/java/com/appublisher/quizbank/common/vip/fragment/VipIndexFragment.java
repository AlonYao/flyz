package com.appublisher.quizbank.common.vip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.model.netdata.UserExamInfoModel;
import com.appublisher.lib_login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.CommonFragmentActivity;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.common.vip.activity.VipExerciseIndexActivity;
import com.appublisher.quizbank.common.vip.activity.VipNotificationActivity;
import com.appublisher.quizbank.common.vip.model.VipIndexModel;
import com.appublisher.quizbank.common.vip.network.VipRequest;
import com.appublisher.quizbank.fragment.SettingFragment;
import com.appublisher.quizbank.network.QRequest;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jinbao on 2016/9/8.
 */
public class VipIndexFragment extends Fragment implements RequestCallback {

    private View mView;
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
    private VipRequest mRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        mView = inflater.inflate(R.layout.fragment_vip_index, container, false);
        mRequest = new VipRequest(getActivity(), this);
        //initViews
        initViews();
        setValues();
        return mView;
    }

    public void initViews() {
        nickname = (TextView) mView.findViewById(R.id.nickname);
        evaluationText = (TextView) mView.findViewById(R.id.ev_txt);
        avatarImage = (RoundedImageView) mView.findViewById(R.id.user_avatar);
        examText = (TextView) mView.findViewById(R.id.exam_txt);
        classTime = (TextView) mView.findViewById(R.id.course_time);
        homeworkTimeText = (TextView) mView.findViewById(R.id.homework_time);
        homeworkTipsText = (TextView) mView.findViewById(R.id.homework_tips);
        messageTips = (TextView) mView.findViewById(R.id.message_tips);
        notificationView = mView.findViewById(R.id.message);
        exerciseView = mView.findViewById(R.id.homework);
        settingImage = (ImageView) mView.findViewById(R.id.setting_image);
    }

    public void setValues() {

        final UserInfoModel userInfoModel = LoginModel.getUserInfoM();
        nickname.setText(userInfoModel.getNickname());
        LoginModel.setAvatar(getActivity(), avatarImage);
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
                final Intent intent = new Intent(getActivity(), EvaluationActivity.class);
                startActivity(intent);
            }
        });

        settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
                intent.putExtra("from", "setting");
                startActivity(intent);
            }
        });

        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), VipNotificationActivity.class);
                startActivity(intent);
            }
        });

        exerciseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), VipExerciseIndexActivity.class);
                startActivity(intent);
            }
        });

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

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
