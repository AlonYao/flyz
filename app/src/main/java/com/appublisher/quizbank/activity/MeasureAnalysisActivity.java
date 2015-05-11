package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 做题解析模块
 */
public class MeasureAnalysisActivity extends ActionBarActivity implements RequestCallback{

    public int mScreenHeight;
    public int mCurQuestionId;
    public int mHierarchyId;
    public int mHierarchyLevel;
    public boolean mIsFromError;
    public ViewPager mViewPager;
    public String mAnalysisType;
    public String mCollect;
    public String mPaperName;
    public String mFrom;
    public AnswerM mCurAnswerModel;
    public ArrayList<Integer> mDeleteErrorQuestions;

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
        mPaperName = getIntent().getStringExtra("paper_name");
        mHierarchyId = getIntent().getIntExtra("hierarchy_id", 0);
        mHierarchyLevel = getIntent().getIntExtra("hierarchy_level", 0);
        mFrom = getIntent().getStringExtra("from");
        mIsFromError = getIntent().getBooleanExtra("is_from_error", false);

        if (mIsFromError) mDeleteErrorQuestions = new ArrayList<>();

        if ("collect".equals(mAnalysisType) || "error".equals(mAnalysisType)) {

            switch (mHierarchyLevel) {
                case 1:
                    ProgressDialogManager.showProgressDialog(this, true);
                    mRequest.collectErrorQuestions(
                            String.valueOf(mHierarchyId), "", "", mAnalysisType);
                    break;

                case 2:
                    ProgressDialogManager.showProgressDialog(this, true);
                    mRequest.collectErrorQuestions(
                            "", String.valueOf(mHierarchyId), "", mAnalysisType);
                    break;

                case 3:
                    ProgressDialogManager.showProgressDialog(this, true);
                    mRequest.collectErrorQuestions(
                            "", "", String.valueOf(mHierarchyId), mAnalysisType);
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

            //noinspection unchecked
            ArrayList<AnswerM> answers =
                    (ArrayList<AnswerM>) getIntent().getSerializableExtra("answers");

            MeasureAnalysisModel.setViewPager(this, questions, answers);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("MeasureAnalysisActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPopupWindow != null) mPopupWindow.dismiss();

        // Umeng
        MobclickAgent.onPageEnd("MeasureAnalysisActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(
                R.drawable.measure_analysis_uncollect), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("反馈").setIcon(
                R.drawable.measure_analysis_feedback), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        // 判断是否显示错题
        if (mIsFromError && mDeleteErrorQuestions != null) {
            int size = mDeleteErrorQuestions.size();

            if (size == 0) {
                MenuItemCompat.setShowAsAction(menu.add("错题").setIcon(
                        R.drawable.scratch_paper_clear), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            } else {
                // 遍历
                for (int i = 0; i < size; i++) {
                    int questionId = mDeleteErrorQuestions.get(i);
                    if (questionId != mCurQuestionId) {
                        MenuItemCompat.setShowAsAction(menu.add("错题").setIcon(
                                R.drawable.scratch_paper_clear),
                                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
                    }
                }
            }
        }

        // 初始化反馈菜单
        initPopupWindowView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurAnswerModel != null && mCurAnswerModel.is_collected()) {
            menu.getItem(0).setIcon(R.drawable.measure_analysis_collected);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();

        } else if ("收藏".equals(item.getTitle())) {
            //noinspection ConstantConditions
            if (item.getIcon().getConstantState().equals(
                    getResources().getDrawable(
                            R.drawable.measure_analysis_uncollect).getConstantState())) {
                // 未收藏
                MeasureAnalysisModel.setCollect(this, item);

            } else {
                // 已收藏
                MeasureAnalysisModel.setUnCollect(this, item);
            }

            mRequest.collectQuestion(ParamBuilder.collectQuestion(
                    String.valueOf(mCurQuestionId), mCollect));

        } else if ("反馈".equals(item.getTitle())) {
            View feedbackMenu = findViewById(item.getItemId());

            // 显示反馈菜单
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else if (System.currentTimeMillis() - mPopupDismissTime > 500) {
                mPopupWindow.showAsDropDown(feedbackMenu, 0, 12);
            }
        } else if ("错题".equals(item.getTitle())) {
            AlertManager.deleteErrorQuestionAlert(this);
        }

        return super.onOptionsItemSelected(item);
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
                mRequest.reportErrorQuestion(ParamBuilder.reportErrorQuestion(
                        String.valueOf(mCurQuestionId), "1", ""));

                mPopupWindow.dismiss();

                ToastManager.showToast(MeasureAnalysisActivity.this, "提交成功");
            }
        });

        // 答案问题
        tvAnswerWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequest.reportErrorQuestion(ParamBuilder.reportErrorQuestion(
                        String.valueOf(mCurQuestionId), "2", ""));

                mPopupWindow.dismiss();

                ToastManager.showToast(MeasureAnalysisActivity.this, "提交成功");
            }
        });

        // 解析问题
        tvAnalysisWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequest.reportErrorQuestion(ParamBuilder.reportErrorQuestion(
                        String.valueOf(mCurQuestionId), "3", ""));

                mPopupWindow.dismiss();

                ToastManager.showToast(MeasureAnalysisActivity.this, "提交成功");
            }
        });

        // 其他解析
        tvBetterAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeasureAnalysisActivity.this, MyAnalysisActivity.class);
                intent.putExtra("question_id", String.valueOf(mCurQuestionId));
                startActivity(intent);

                mPopupWindow.dismiss();
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

        if ("collect_question".equals(apiName)) {
            if ("collect".equals(mCollect)) {
                ToastManager.showToast(this, "收藏成功");
            } else if ("cancel".equals(mCollect)) {
                ToastManager.showToast(this, "取消收藏");
            }
        }

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
