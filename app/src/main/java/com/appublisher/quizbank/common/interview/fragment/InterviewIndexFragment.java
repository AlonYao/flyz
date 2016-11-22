package com.appublisher.quizbank.common.interview.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.CarouselAdapter;
import com.appublisher.quizbank.common.interview.activity.InterviewGuoKaoActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewCategoryActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperListActivity;
import com.appublisher.quizbank.model.business.StudyIndexModel;
import com.appublisher.quizbank.model.netdata.CarouselM;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.ProgressBarManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinbao on 2016/11/14.
 */

public class InterviewIndexFragment extends Fragment implements RequestCallback {

    private View guokaoView;
    private View starAnalysisView;
    private View categoryView;
    private View historyView;

    public View carouselView;
    public ViewPager viewPager;
    public CarouselAdapter carouselAdapter;
    public List<CarouselM> carouselInterviewList;
    public QRequest mQRequest;

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

        carouselInterviewList = new ArrayList<>();
        carouselAdapter = new CarouselAdapter(getActivity(), carouselInterviewList);

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
            }
        });

        starAnalysisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewPaperListActivity.class);
                intent.putExtra("from", "teacher");
                startActivity(intent);
            }
        });

        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewCategoryActivity.class);
                startActivity(intent);
            }
        });

        historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewPaperListActivity.class);
                intent.putExtra("from", "history");
                startActivity(intent);
            }
        });

        viewPager.setAdapter(carouselAdapter);
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
}
