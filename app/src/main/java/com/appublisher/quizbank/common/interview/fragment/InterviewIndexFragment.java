package com.appublisher.quizbank.common.interview.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.CarouselAdapter;
import com.appublisher.quizbank.common.interview.activity.InterviewCategoryActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewGuoKaoActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperListActivity;
import com.appublisher.quizbank.model.business.StudyIndexModel;
import com.appublisher.quizbank.model.netdata.CarouselM;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.ProgressBarManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jinbao on 2016/11/14.
 */

public class InterviewIndexFragment extends Fragment implements RequestCallback {

    private View guokaoView;
    private View starAnalysisView;
    private View categoryView;
    private View historyView;
    private LinearLayout mLlDots;

    public View carouselView;
    public ViewPager viewPager;
    public CarouselAdapter carouselAdapter;
    public List<CarouselM> carouselInterviewList;
    public QRequest mQRequest;

    //设置当前 第几个图片 被选中
    private int autoCurrIndex = 0;
    private Timer timer = new Timer();
    public static final int CAROUSEL_SLIDE = 1;
    private MsgHandler mHandler;

    public static class MsgHandler extends Handler {
        private WeakReference<Fragment> mFragemt;

        public MsgHandler(Fragment fragment) {
            mFragemt = new WeakReference<>(fragment);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final InterviewIndexFragment interviewIndexFragment = (InterviewIndexFragment) mFragemt.get();
            if (interviewIndexFragment != null) {
                switch (msg.what) {
                    case CAROUSEL_SLIDE:
                        if (interviewIndexFragment.carouselInterviewList.size() != 0) {
                            interviewIndexFragment.viewPager.setCurrentItem(msg.arg1);
                        }
                        break;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_interview_index, null);

        guokaoView = view.findViewById(R.id.guokao_view);
        starAnalysisView = view.findViewById(R.id.star_analysis_view);
        categoryView = view.findViewById(R.id.category_view);
        historyView = view.findViewById(R.id.history_view);

        carouselView = view.findViewById(R.id.carousel_view_rl);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mLlDots = (LinearLayout) view.findViewById(R.id.carousel_dot_ll);

        carouselInterviewList = new ArrayList<>();
        carouselAdapter = new CarouselAdapter(getActivity(), carouselInterviewList);

        mHandler = new MsgHandler(this);
        mQRequest = new QRequest(getActivity(), this);
        mQRequest.getCarousel();

        setValue();

        return view;
    }

    public void setValue() {
        guokaoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewGuoKaoActivity.class);
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Jingxuan");
                UmengManager.onEvent(getContext(), "InterviewPage", map);
            }
        });

        starAnalysisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewPaperListActivity.class);
                intent.putExtra("from", "teacher");
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Jiexi");
                UmengManager.onEvent(getContext(), "InterviewPage", map);
            }
        });

        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewCategoryActivity.class);
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Tupo");
                UmengManager.onEvent(getContext(), "InterviewPage", map);
            }
        });

        historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewPaperListActivity.class);
                intent.putExtra("from", "history");
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Zhenti");
                UmengManager.onEvent(getContext(), "InterviewPage", map);
            }
        });

        viewPager.setAdapter(carouselAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                autoCurrIndex = position;

                for (int i = 0; i < mLlDots.getChildCount(); i++) {
                    if (i == position) {
                        mLlDots.getChildAt(i).setSelected(true);
                    } else {
                        mLlDots.getChildAt(i).setSelected(false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) {
            ProgressBarManager.hideProgressBar();
            return;
        }

        if ("get_carousel".equals(apiName))
            StudyIndexModel.dealCarouselResp(response, this);

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mHandler.removeMessages(CAROUSEL_SLIDE);
        }
    }

    public void initDots(int length) {
        mLlDots.removeAllViews();
        for (int j = 0; j < length; j++) {
            mLlDots.addView(initDot());
        }
        mLlDots.getChildAt(0).setSelected(true);

        startCarousel();
    }

    private View initDot() {
        return LayoutInflater.from(getActivity()).inflate(R.layout.carousel_dot, null);
    }

    private void startCarousel() {
        // 设置自动轮播图片，5s后执行，周期是5s
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.cancel();
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = CAROUSEL_SLIDE;
                if (autoCurrIndex == carouselInterviewList.size() - 1) {
                    autoCurrIndex = -1;
                }
                message.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(message);
            }
        }, 2000, 2000);
    }
}
