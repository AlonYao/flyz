package com.appublisher.quizbank.common.vip.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.activity.UserInfoActivity;
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
import com.appublisher.quizbank.utils.FastBlur;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jinbao on 2016/9/8.
 */
public class VipIndexFragment extends Fragment implements RequestCallback {

    private View mView;
    private TextView nickname;
    private RoundedImageView avatarImage;
    private ImageView avatarBgImage;
    private TextView evaluationText;
    private TextView examText;
    public TextView messageTips;
    public TextView classTime;
    public TextView homeworkTimeText;
    public TextView homeworkTipsText;
    private View notificationView;
    private View exerciseView;
    private View courseView;
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
        avatarBgImage = (ImageView) mView.findViewById(R.id.avatar);
        examText = (TextView) mView.findViewById(R.id.exam_txt);
        classTime = (TextView) mView.findViewById(R.id.course_time);
        homeworkTimeText = (TextView) mView.findViewById(R.id.homework_time);
        homeworkTipsText = (TextView) mView.findViewById(R.id.homework_tips);
        messageTips = (TextView) mView.findViewById(R.id.message_tips);
        notificationView = mView.findViewById(R.id.message);
        exerciseView = mView.findViewById(R.id.homework);
        courseView = mView.findViewById(R.id.course);
    }

    public void setValues() {

        evaluationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), EvaluationActivity.class);
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Mine");
                UmengManager.onEvent(getContext(), "VIP", map);
            }
        });


        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), VipNotificationActivity.class);
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Message");
                UmengManager.onEvent(getContext(), "VIP", map);
            }
        });

        exerciseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), VipExerciseIndexActivity.class);
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "HomeWork");
                UmengManager.onEvent(getContext(), "VIP", map);
            }
        });
        courseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
                intent.putExtra("from", "course_purchased");
                getActivity().startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "MyCourse");
                UmengManager.onEvent(getContext(), "VIP", map);
            }
        });

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Account");
                UmengManager.onEvent(getContext(), "VIP", map);
            }
        });
    }

    private void blur(Bitmap bkg, ImageView view) {
        float scaleFactor = 1;
        float radius = 80;
//        Bitmap overlay = Bitmap.createBitmap(
//                (int) (view.getMeasuredWidth() / scaleFactor),
//                (int) (view.getMeasuredHeight() / scaleFactor),
//                Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
//                / scaleFactor);
//        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
//        Paint paint = new Paint();
//        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//        canvas.drawBitmap(bkg, 0, 0, paint);
        if (bkg == null) return;
        bkg = FastBlur.doBlur(bkg, (int) radius, true);
        view.setImageBitmap(bkg);
    }

    @Override
    public void onResume() {
        super.onResume();

        final UserInfoModel userInfoModel = LoginModel.getUserInfoM();
        nickname.setText(userInfoModel == null ? "" : userInfoModel.getNickname());

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

        avatarImage.setBackgroundResource(R.drawable.avatar_bg_shadow);

        // 头像处理
        LoginModel.setAvatar(avatarImage);
        ImageManager.displayImage(
                LoginModel.getUserAvatar(), avatarBgImage, new ImageManager.LoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                avatarBgImage.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        avatarBgImage.getViewTreeObserver().removeOnPreDrawListener(this);
                        avatarBgImage.buildDrawingCache();
                        Bitmap bmp = avatarBgImage.getDrawingCache();
                        blur(bmp, avatarBgImage);
                        return true;
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
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
