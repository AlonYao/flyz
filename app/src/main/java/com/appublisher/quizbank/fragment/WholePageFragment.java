package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.appublisher.quizbank.R;

/**
 * 真题演练
 */
public class WholePageFragment extends Fragment{

    private Activity mActivity;
    private PopupWindow mPwProvince;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // view 初始化
        View view = inflater.inflate(R.layout.fragment_wholepage, container, false);
        RelativeLayout rlProvince = (RelativeLayout) view.findViewById(R.id.wholepage_province_rl);

        // 省份
        rlProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    /**
     * 初始化省份菜单
     */
    private void initPwProvince() {
        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(mActivity).inflate(R.layout.wholepage_popup_province, null);

        mPwProvince = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mPwProvince.setOutsideTouchable(true);
        mPwProvince.setBackgroundDrawable(
                mActivity.getResources().getDrawable(R.color.transparency));
        mPwProvince.update();
    }
}
