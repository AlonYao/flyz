package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureAnalysisModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.MeasureAnalysisResp;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeasureAnalysisActivity extends ActionBarActivity implements RequestCallback{

    public int mScreenHeight;
    public ViewPager mViewPager;
    public String mAnalysisType;
    public int mCurQuestionId;

    private PopupWindow mPopupWindow;
    private long mPopupDismissTime;
    private Request mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_analysis);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        mViewPager = (ViewPager) findViewById(R.id.measure_analysis_viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 初始化成员变量
        mRequest = new Request(this, this);

        // 获取ToolBar高度
        int toolBarHeight = MeasureModel.getViewHeight(toolbar);

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        // 获取数据
        mAnalysisType = getIntent().getStringExtra("analysis_type");
        if ("collect".equals(mAnalysisType) || "error".equals(mAnalysisType)) {
            int hierarchy_id = getIntent().getIntExtra("hierarchy_id", 0);
            int hierarchy_level = getIntent().getIntExtra("hierarchy_level", 0);

            switch (hierarchy_level) {
                case 1:
                    ProgressDialogManager.showProgressDialog(this, true);
                    mRequest.collectErrorQuestions(
                            String.valueOf(hierarchy_id), "", "", mAnalysisType);
                    break;

                case 2:
                    ProgressDialogManager.showProgressDialog(this, true);
                    mRequest.collectErrorQuestions(
                            "", String.valueOf(hierarchy_id), "", mAnalysisType);
                    break;

                case 3:
                    ProgressDialogManager.showProgressDialog(this, true);
                    mRequest.collectErrorQuestions(
                            "", "", String.valueOf(hierarchy_id), mAnalysisType);
                    break;
            }
        } else if ("mokao".equals(mAnalysisType)) {
            int exerciseId = getIntent().getIntExtra("exercise_id", 0);

            ProgressDialogManager.showProgressDialog(this, true);
            mRequest.getHistoryExerciseDetail(exerciseId, mAnalysisType);
        } else {
            //noinspection unchecked
            ArrayList<QuestionM> questions =
                    (ArrayList<QuestionM>) getIntent().getSerializableExtra("questions");

            @SuppressWarnings("unchecked") ArrayList<AnswerM> answers =
                    (ArrayList<AnswerM>) getIntent().getSerializableExtra("answers");

            MeasureAnalysisModel.setViewPager(this, questions, answers);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(
                R.drawable.measure_analysis_uncollect), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("反馈").setIcon(
                R.drawable.measure_analysis_feedback), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        // 初始化反馈菜单
        initPopupWindowView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if ("收藏".equals(item.getTitle())) {
            mRequest.collectQuestion(ParamBuilder.collectQuestion(
                    String.valueOf(mCurQuestionId), "collect"));
        } else if ("反馈".equals(item.getTitle())) {
            View feedbackMenu = findViewById(item.getItemId());

            // 显示反馈菜单
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else if (System.currentTimeMillis() - mPopupDismissTime > 500) {
                mPopupWindow.showAsDropDown(feedbackMenu, 0, 12);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPopupWindow != null) mPopupWindow.dismiss();
    }

    /**
     * 初始化popup菜单
     */
    private void initPopupWindowView() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(
                R.layout.measure_analysis_popup_feedback,
                null, false);
        mPopupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(view);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setBackgroundDrawable(
                getResources().getDrawable(R.color.transparency));

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPopupDismissTime = System.currentTimeMillis();
            }
        });

        // 菜单
        TextView tvImageText = (TextView) view.findViewById(R.id.fb_menu_imagetext);
        TextView tvAnswerWrong = (TextView) view.findViewById(R.id.fb_menu_answerwrong);
        TextView tvAnalysisWrong = (TextView) view.findViewById(R.id.fb_menu_analysiswrong);
        TextView tvBetterAnalysis = (TextView) view.findViewById(R.id.fb_menu_betteranalysis);

        // 图文问题
        tvImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastManager.showToast(MeasureAnalysisActivity.this, "图文问题");
            }
        });

        // 答案问题
        tvAnswerWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastManager.showToast(MeasureAnalysisActivity.this, "答案问题");
            }
        });

        // 解析问题
        tvAnalysisWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastManager.showToast(MeasureAnalysisActivity.this, "解析问题");
            }
        });

        // 其他解析
        tvBetterAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastManager.showToast(MeasureAnalysisActivity.this, "更好的解析");
            }
        });
    }

    /**
     * 处理解析回调
     * @param response 解析数据回调
     */
    private void dealMeasureAnalysisResp(JSONObject response) {
        if (response == null) return;

        Gson gson = new Gson();
        MeasureAnalysisResp measureAnalysisResp =
                gson.fromJson(response.toString(), MeasureAnalysisResp.class);

        if (measureAnalysisResp == null || measureAnalysisResp.getResponse_code() != 1) return;
        ArrayList<QuestionM> questions = measureAnalysisResp.getQuestions();

        MeasureAnalysisModel.setViewPager(this, questions, measureAnalysisResp.getAnswers());
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("collect_error_questions".equals(apiName)) dealMeasureAnalysisResp(response);
        if ("history_exercise_detail".equals(apiName)) dealMeasureAnalysisResp(response);
        if ("collect_question".equals(apiName)) ToastManager.showToast(this, "收藏成功");

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
