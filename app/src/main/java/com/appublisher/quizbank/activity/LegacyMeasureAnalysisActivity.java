package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
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
import com.appublisher.lib_basic.HomeButtonManager;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.LegacyMeasureAnalysisModel;
import com.appublisher.quizbank.model.business.LegacyMeasureModel;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.MeasureAnalysisResp;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.AlertManager;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * 做题解析模块
 */
public class LegacyMeasureAnalysisActivity extends BaseActivity implements RequestCallback {

    public int mScreenHeight;
    public int mCurQuestionId;
    public int mHierarchyId;
    public int mHierarchyLevel;
    public boolean mIsFromError;
    public ViewPager mViewPager;
    public String mAnalysisType;
    public String mCollect;
    public String mPaperName;
    public int mPaperId;
    public String mFrom;
    public AnswerM mCurAnswerModel;
    public ArrayList<Integer> mDeleteErrorQuestions;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public ArrayList<HashMap<String, Integer>> mEntirePaperCategory;

    private long mPopupDismissTime;
    private PopupWindow mPopupWindow;
    public QRequest mQRequest;
    private HomeButtonManager mHomeWatcher;

    /**
     * Umeng
     */
    public boolean mUmengIsPressHome;
    public long mUmengTimestamp;
    public String mUmengEntry;
    public String mUmengEntryReview;
    public String mUmengFavorite;
    public String mUmengDelete;
    public String mUmengAnswerSheet;
    public String mUmengFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legacy_measure_analysis);

        // ToolBar
       setToolBar(this);

        // View 初始化
        mViewPager = (ViewPager) findViewById(R.id.measure_analysis_viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 初始化成员变量
        mQRequest = new QRequest(this, this);
        mUmengIsPressHome = false;
        mHomeWatcher = new HomeButtonManager(this);
        mUmengFavorite = "0";
        mUmengAnswerSheet = "0";
        mUmengFeedback = "0";

        // 获取ToolBar高度
        int toolBarHeight = LegacyMeasureModel.getViewHeight(toolbar);

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        // 获取数据
        mAnalysisType = getIntent().getStringExtra("analysis_type");
        mPaperName = getIntent().getStringExtra("paper_name");
        mPaperId = getIntent().getIntExtra("paper_id", 0);
        mHierarchyId = getIntent().getIntExtra("hierarchy_id", 0);
        mHierarchyLevel = getIntent().getIntExtra("hierarchy_level", 0);
        mFrom = getIntent().getStringExtra("from");
        mIsFromError = getIntent().getBooleanExtra("is_from_error", false);
        mUmengEntry = getIntent().getStringExtra("umeng_entry");
        mUmengEntryReview = getIntent().getStringExtra("umeng_entry_review");
        mUmengTimestamp = getIntent().getLongExtra("umeng_timestamp", System.currentTimeMillis());

        if (mIsFromError) mDeleteErrorQuestions = new ArrayList<>();

        //noinspection IfCanBeSwitch
        if (("collect".equals(mAnalysisType) || "error".equals(mAnalysisType))
                && !"study_record".equals(mFrom)) {
            ProgressDialogManager.showProgressDialog(this, true);
            mQRequest.collectErrorQuestions(String.valueOf(mHierarchyId), mAnalysisType);

        } else {
            //noinspection unchecked
            ArrayList<QuestionM> questions =
                    (ArrayList<QuestionM>) getIntent().getSerializableExtra("questions");
            //noinspection unchecked
            ArrayList<AnswerM> answers =
                    (ArrayList<AnswerM>) getIntent().getSerializableExtra("answers");
            LegacyMeasureAnalysisModel.setViewPager(this, questions, answers);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng 统计时长处理
        if (mUmengIsPressHome) {
            // 如果已经按过Home键后，再次回到App，更新参数状态
            mUmengEntry = "Continue";
            mUmengTimestamp = System.currentTimeMillis();
            mUmengIsPressHome = false;
        }

        // Home键监听
        mHomeWatcher.setOnHomePressedListener(new HomeButtonManager.OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                // 友盟统计
                mUmengIsPressHome = true;
                UmengManager.onEvent(LegacyMeasureAnalysisActivity.this, "Back");
            }

            @Override
            public void onHomeLongPressed() {
                // Do Nothing
            }
        });
        mHomeWatcher.startWatch();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Home键监听
        mHomeWatcher.stopWatch();

        if (mPopupWindow != null) mPopupWindow.dismiss();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        HashMap<String, String> map = new HashMap<>();
        map.put("Entry", mUmengEntryReview);
        map.put("AnswerSheet", mUmengAnswerSheet);
        map.put("Feedback", mUmengFeedback);
        MobclickAgent.onEvent(this, "Review", map);
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

        MenuItemCompat.setShowAsAction(menu.add("答题卡").setIcon(
                R.drawable.measure_icon_answersheet), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("分享").setIcon(R.drawable.quiz_share), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

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
            // Umeng
            UmengManager.onEvent(LegacyMeasureAnalysisActivity.this, "Back");
            finish();

        } else if ("收藏".equals(item.getTitle())) {
            if (mCurAnswerModel != null && mCurAnswerModel.is_collected()) {
                // 如果是已收藏状态，取消收藏
                LegacyMeasureAnalysisModel.setUnCollect(this, item);

                ToastManager.showToast(this, "取消收藏");

                // Umeng
                mUmengFavorite = "-1";

            } else {
                // 如果是未收藏状态，收藏
                LegacyMeasureAnalysisModel.setCollect(this, item);

                ToastManager.showToast(this, "收藏成功");

                // Umeng
                mUmengFavorite = "1";
            }

            mQRequest.collectQuestion(ParamBuilder.collectQuestion(
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

            // Umeng
            mUmengDelete = "1";
        } else if (item.getTitle().equals("答题卡")) {
            skipToAnswerSheet();
        } else if ("分享".equals(item.getTitle())) {
            /** 构造友盟分享实体 **/
            String[] content = {"检验学霸的唯一标准就是做对题目，我出一道考考你？接招吗？",
                    "发现一道比较难的题目，我可是做对了哦，你呢？",
                    "长得美的这道题都做对了，比如我~~",
                    "这道题我用时不到一分钟哦，看看你是不是比我快？"};
            int random = new Random().nextInt(content.length);
            Resources resources = getResources();
            // 获取单题分享链接
            SharedPreferences sp = getSharedPreferences("global_setting", MODE_PRIVATE);
            String spString = sp.getString("global_setting", "");
            GlobalSettingsResp globalSettingsResp =
                    GsonManager.getModel(spString, GlobalSettingsResp.class);
            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                String questionShareUrl = globalSettingsResp.getQuestion_share_url();
                String targetUrl = questionShareUrl + "question_id=" + mCurQuestionId;
                UmengManager.UMShareEntity umShareEntity = new UmengManager.UMShareEntity()
                        .setTitle(resources.getString(R.string.share_title))
                        .setText(content[random])
                        .setUmImage(new UMImage(this, Utils.getBitmapByView(mViewPager)))
                        .setTargetUrl(targetUrl);
                UmengManager.shareAction(LegacyMeasureAnalysisActivity.this, umShareEntity, "quizbank", new UmengManager.PlatformInter() {
                    @Override
                    public void platform(SHARE_MEDIA platformType) {
                        // Empty
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Umeng
        UmengManager.onEvent(LegacyMeasureAnalysisActivity.this, "Back");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**使用SSO授权必须添加如下代码 */
        UmengManager.onActivityResult(this, requestCode, resultCode, data);
        // 答题卡回调
        if (resultCode == ActivitySkipConstants.ANSWER_SHEET_SKIP && data != null) {
            int position = data.getIntExtra("position", 0);

            if (mViewPager == null) return;
            mViewPager.setCurrentItem(position);
        }
    }

    /**
     * 跳转至答题卡
     */
    public void skipToAnswerSheet() {
        Intent intent = new Intent(this, AnswerSheetActivity.class);
        intent.putExtra("user_answer", mUserAnswerList);
        intent.putExtra("paper_type", mAnalysisType);
        intent.putExtra("category", mEntirePaperCategory);
        intent.putExtra("from", "analysis");
        startActivityForResult(intent, ActivitySkipConstants.ANSWER_SHEET_SKIP);
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
        //noinspection deprecation
        mPopupWindow.setBackgroundDrawable(
                getResources().getDrawable(R.color.transparency));

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPopupDismissTime = System.currentTimeMillis();
            }
        });

        // 菜单
        final TextView tvImageText = (TextView) view.findViewById(R.id.fb_menu_imagetext);
        final TextView tvAnswerWrong = (TextView) view.findViewById(R.id.fb_menu_answerwrong);
        final TextView tvAnalysisWrong = (TextView) view.findViewById(R.id.fb_menu_analysiswrong);
        final TextView tvBetterAnalysis = (TextView) view.findViewById(R.id.fb_menu_betteranalysis);

        // 图文问题
        tvImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LegacyMeasureAnalysisActivity.this, MyAnalysisActivity.class);
                intent.putExtra("question_id", String.valueOf(mCurQuestionId));
                intent.putExtra("type", "1");
                intent.putExtra("bar_title", tvImageText.getText().toString());
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });

        // 答案问题
        tvAnswerWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LegacyMeasureAnalysisActivity.this, MyAnalysisActivity.class);
                intent.putExtra("question_id", String.valueOf(mCurQuestionId));
                intent.putExtra("type", "2");
                intent.putExtra("bar_title", tvAnswerWrong.getText().toString());
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });

        // 解析问题
        tvAnalysisWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LegacyMeasureAnalysisActivity.this, MyAnalysisActivity.class);
                intent.putExtra("question_id", String.valueOf(mCurQuestionId));
                intent.putExtra("type", "3");
                intent.putExtra("bar_title", tvAnalysisWrong.getText().toString());
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });

        // 其他解析
        tvBetterAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LegacyMeasureAnalysisActivity.this, MyAnalysisActivity.class);
                intent.putExtra("question_id", String.valueOf(mCurQuestionId));
                intent.putExtra("type", "4");
                intent.putExtra("bar_title", tvBetterAnalysis.getText().toString());
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });
    }

    /**
     * 处理解析回调
     *
     * @param response 解析数据回调
     */
    private void dealMeasureAnalysisResp(JSONObject response) {
        if (response == null) return;

        Gson gson = new Gson();
        MeasureAnalysisResp measureAnalysisResp =
                gson.fromJson(response.toString(), MeasureAnalysisResp.class);

        if (measureAnalysisResp == null || measureAnalysisResp.getResponse_code() != 1) return;
        ArrayList<QuestionM> questions = measureAnalysisResp.getQuestions();

        LegacyMeasureAnalysisModel.setViewPager(this, questions, measureAnalysisResp.getAnswers());
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("collect_error_questions".equals(apiName)) dealMeasureAnalysisResp(response);

        if ("history_exercise_detail".equals(apiName)) dealMeasureAnalysisResp(response);
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
