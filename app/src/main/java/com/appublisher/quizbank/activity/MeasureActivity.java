package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.MeasureAdapter;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.MeasureModel;
import com.appublisher.quizbank.model.entity.measure.MeasureEntity;
import com.appublisher.quizbank.model.netdata.ServerCurrentTimeResp;
import com.appublisher.quizbank.model.netdata.measure.AutoTrainingResp;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.model.netdata.measure.SubmitPaperResp;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.HomeWatcher;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 做题
 */
public class MeasureActivity extends ActionBarActivity implements RequestCallback {

    public int mScreenHeight;
    public int mCurPosition;
    public int mPaperId;
    public int mExerciseId;
    public int mHierarchyId;
    public int mHierarchyLevel;
    public long mCurTimestamp;
    public boolean mRedo;
    public boolean mPressBack;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public ArrayList<QuestionM> mQuestions;
    public ArrayList<HashMap<String, Integer>> mEntirePaperCategory;
    public ViewPager mViewPager;
    public String mPaperType;
    public String mPaperName;
    public String mFrom;
    public Gson mGson;
    public Timer mTimer;

    public static Toolbar mToolbar;
    public static Handler mHandler;
    public static Request mRequest;
    public static String mMockTime;
    public static int mMins;//分钟
    public static int mSec;//秒数
    public static int mDuration;
    public static int mock_duration;
    public static final int TIME_ON = 0;
    public static final int TIME_OUT = 1;
    public static final int TIME_ON_MOCK = 2;

    /**
     * Umeng
     */
    public long mUmengTimestamp;
    public boolean mUmengIsPressHome;
    public String mUmengEntry;
    private String mUmengDraft; // 草稿纸
    private String mUmengPause; // 暂停
    private String mUmengAnswerSheet; // 答题卡

    private HomeWatcher mHomeWatcher;
    public static boolean mockpre = false;

    /**
     * 直接交卷获取参数
     */
    public int mTotalNum;
    public int mRightNum;
    public HashMap<String, HashMap<String, Object>> mCategoryMap;

    public static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final MeasureActivity activity = (MeasureActivity) mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case TIME_ON:
                        String mins = String.valueOf(mMins);
                        String sec = String.valueOf(mSec);

                        if (mins.length() == 1) mins = "0" + mins;
                        if (sec.length() == 1) sec = "0" + sec;

                        String time = mins + ":" + sec;

                        activity.getSupportActionBar().setTitle(time);

                        if (mMins < 1) {
                            mToolbar.setTitleTextColor(Color.parseColor("#FFCD02"));
                        }
                        break;

                    case TIME_OUT:
                        activity.getSupportActionBar().setTitle("00:00");
                        break;

                    case TIME_ON_MOCK:
                        mock_duration = (int) Utils.getSecondsByDateMinusNow(mMockTime) + mDuration;
                        MeasureActivity.mMins = mock_duration / 60;
                        MeasureActivity.mSec = mock_duration % 60;
                        mins = String.valueOf(mMins);
                        sec = String.valueOf(mSec);
                        if (mins.length() == 1) mins = "0" + mins;
                        if (sec.length() == 1) sec = "0" + sec;
                        time = mins + ":" + sec;
                        activity.getSupportActionBar().setTitle(time);
                        if (mMins == 15 && mSec == 0) {
                            ToastManager.showToast(activity, "距离考试结束还有15分钟");
                        }
                        if (mock_duration == 0) {
                            // 停止发消息
                            mRequest.getServerCurrentTime();
                        } else {
                            Message message = mHandler.obtainMessage(TIME_ON_MOCK);
                            mHandler.sendMessageDelayed(message, 1000);
                        }
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        mViewPager = (ViewPager) findViewById(R.id.measure_viewpager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // 初始化成员变量
        mCurTimestamp = System.currentTimeMillis();
        mCurPosition = 0;
        mGson = new Gson();
        mHandler = new MsgHandler(this);
        mHomeWatcher = new HomeWatcher(this);
        mUmengIsPressHome = false;
        mUmengDraft = "0";
        mUmengPause = "0";
        mUmengAnswerSheet = "0";
        mRequest = new Request(this, this);

        // 获取ToolBar高度
        int toolBarHeight = MeasureModel.getViewHeight(mToolbar);

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        // 获取数据
        mPaperType = getIntent().getStringExtra("paper_type");
        mPaperName = getIntent().getStringExtra("paper_name");
        mPaperId = getIntent().getIntExtra("paper_id", 0);
        mExerciseId = getIntent().getIntExtra("exercise_id", 0);
        mRedo = getIntent().getBooleanExtra("redo", false);
        mHierarchyId = getIntent().getIntExtra("hierarchy_id", 0);
        mHierarchyLevel = getIntent().getIntExtra("hierarchy_level", 0);
        mUmengEntry = getIntent().getStringExtra("umeng_entry");
        mFrom = getIntent().getStringExtra("from");

        // 判断是否是模考介绍页
        if ("mockpre".equals(mFrom)) {
            mockpre = true;
            mMockTime = getIntent().getStringExtra("mock_time");
        }

        MeasureModel.getData(this);
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
        } else {
            mUmengTimestamp = mCurTimestamp;
        }

        // Home键监听
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                // 友盟统计
                mUmengIsPressHome = true;
                UmengManager.sendToUmeng(MeasureActivity.this, "0");
            }

            @Override
            public void onHomeLongPressed() {
                // Do Nothing
            }
        });
        mHomeWatcher.startWatch();

        // Umeng
        MobclickAgent.onPageStart("MeasureActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Home键监听
        mHomeWatcher.stopWatch();

        // Umeng
        MobclickAgent.onPageEnd("MeasureActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清空计时器
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        // Umeng
        HashMap<String, String> map = new HashMap<>();
        map.put("Draft", mUmengDraft);
        map.put("Pause", mUmengPause);
        map.put("AnswerSheet", mUmengAnswerSheet);
        MobclickAgent.onEvent(this, "Test", map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("草稿纸").setIcon(
                R.drawable.measure_icon_scratch_paper), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("答题卡").setIcon(
                R.drawable.measure_icon_answersheet), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        if (!mockpre) {
            MenuItemCompat.setShowAsAction(menu.add("暂停").setIcon(
                    R.drawable.measure_icon_pause), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            checkSave();

        } else if (item.getTitle().equals("暂停")) {
            if (mTimer != null) mTimer.cancel();
            saveQuestionTime();
            AlertManager.pauseAlert(this);

            // Umeng
            mUmengPause = "1";

        } else if (item.getTitle().equals("答题卡")) {
            skipToAnswerSheet();

            // Umeng
            mUmengAnswerSheet = "1";

        } else if (item.getTitle().equals("草稿纸")) {
            Intent intent = new Intent(this, ScratchPaperActivity.class);
            startActivity(intent);

            // Umeng
            mUmengDraft = "1";
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 答题卡回调
        if (resultCode == ActivitySkipConstants.ANSWER_SHEET_SKIP && data != null) {
            int position = data.getIntExtra("position", 0);

            if (mViewPager == null) return;
            mViewPager.setCurrentItem(position);
        } else if (resultCode == ActivitySkipConstants.ANSWER_SHEET_SUBMIT && data != null) {
            ArrayList<NoteM> notes = (ArrayList<NoteM>) data.getSerializableExtra("notes");
            String paper_name = data.getStringExtra("paper_name");
            int right_num = data.getIntExtra("right_num", 0);
            int total_num = data.getIntExtra("total_num", 0);
            HashMap<String, HashMap<String, Object>> category =
                    (HashMap<String, HashMap<String, Object>>)
                            data.getSerializableExtra("category");

            // 跳转到练习报告页面
            Intent intent = new Intent(this, PracticeReportActivity.class);
            intent.putExtra("notes", notes);
            intent.putExtra("paper_name", paper_name);
            intent.putExtra("paper_type", mPaperType);
            intent.putExtra("right_num", right_num);
            intent.putExtra("total_num", total_num);
            intent.putExtra("category", category);
            intent.putExtra("questions", mQuestions);
            intent.putExtra("user_answer", mUserAnswerList);
            intent.putExtra("hierarchy_id", mHierarchyId);
            intent.putExtra("hierarchy_level", mHierarchyLevel);
            intent.putExtra("umeng_entry", mUmengEntry);
            intent.putExtra("umeng_timestamp", mUmengTimestamp);
            intent.putExtra("measure_entity", data.getSerializableExtra("measure_entity"));
            intent.putExtra("exercise_id", mExerciseId);
            intent.putExtra("paper_id", mPaperId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        checkSave();
    }

    /**
     * 跳转至答题卡
     */
    public void skipToAnswerSheet() {
        Intent intent = new Intent(this, AnswerSheetActivity.class);
        intent.putExtra("user_answer", mUserAnswerList);
        intent.putExtra("paper_type", mPaperType);
        intent.putExtra("paper_id", mPaperId);
        intent.putExtra("redo", mRedo);
        intent.putExtra("paper_name", mPaperName);
        intent.putExtra("category", mEntirePaperCategory);
        intent.putExtra("umeng_entry", mUmengEntry);
        intent.putExtra("umeng_timestamp", mUmengTimestamp);
        intent.putExtra("from", mFrom);
        intent.putExtra("mock_time", mMockTime);
        startActivityForResult(intent, ActivitySkipConstants.ANSWER_SHEET_SKIP);
    }

    /**
     * 保存做题时间
     */
    private void saveQuestionTime() {
        int duration = (int) ((System.currentTimeMillis() - mCurTimestamp) / 1000);
        mCurTimestamp = System.currentTimeMillis();
        HashMap<String, Object> userAnswerMap = mUserAnswerList.get(mCurPosition);

        if (userAnswerMap.containsKey("duration")) {
            duration = duration + (int) userAnswerMap.get("duration");
        }

        userAnswerMap.put("duration", duration);
        mUserAnswerList.set(mCurPosition, userAnswerMap);
    }

    /**
     * 处理快速智能练习回调
     *
     * @param autoTrainingResp 快速智能练习回调
     */
    private void dealAutoTrainingResp(AutoTrainingResp autoTrainingResp) {
        mPaperId = autoTrainingResp.getPaper_id();
        mQuestions = autoTrainingResp.getQuestions();

        if (mQuestions == null) return;

        // 倒计时时间
        mDuration = autoTrainingResp.getDuration();

        setContent();
    }

    /**
     * 设置内容
     */
    private void setContent() {
        // 初始化用户答案
        initUserAnswer();

        // 设置ViewPager
        setViewPager();

        // 倒计时
        startTimer();
    }

    /**
     * 初始化用户答案
     */
    private void initUserAnswer() {
        int size = mQuestions.size();
        mUserAnswerList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<>();

            QuestionM question = mQuestions.get(i);
            if (question != null) {
                map.put("id", question.getId());
                map.put("right_answer", question.getAnswer());
                map.put("note_id", question.getNote_id());
                map.put("category_id", question.getCategory_id());
                map.put("category_name", question.getCategory_name());
            } else {
                map.put("id", 0);
                map.put("right_answer", "right_answer");
                map.put("note_id", 0);
                map.put("category_id", 0);
                map.put("category_name", "科目");
            }

            map.put("duration", 0);
            map.put("answer", "");

            mUserAnswerList.add(map);
        }
    }

    /**
     * 设置ViewPager
     */
    private void setViewPager() {
        MeasureAdapter measureAdapter = new MeasureAdapter(this);
        mViewPager.setAdapter(measureAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                saveQuestionTime();

                // Umeng 判断单个题是否完成
                if (mUserAnswerList != null && position < mUserAnswerList.size()) {
                    HashMap<String, Object> map = mUserAnswerList.get(mCurPosition);
                    if (map != null && map.containsKey("answer")
                            && map.get("answer") != null && !map.get("answer").equals("")) {
                        UmengManager.sendCountEvent(MeasureActivity.this, "Problem", "Answer", "1");
                    } else {
                        UmengManager.sendCountEvent(MeasureActivity.this, "Problem", "Answer", "0");
                    }
                }

                mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 倒计时启动
     */
    public void startTimer() {
        mMins = mDuration / 60;
        mSec = mDuration % 60;

        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mSec--;
                mDuration--;
                if (mSec < 0) {
                    mMins--;
                    mSec = 59;
                    mHandler.sendEmptyMessage(TIME_ON);
                    if (mMins < 0) {
                        mTimer.cancel();
                        mHandler.sendEmptyMessage(TIME_OUT);
                    }
                } else {
                    mHandler.sendEmptyMessage(TIME_ON);
                }
            }
        }, 0, 1000);
    }

    /**
     * 是否记录本次练习
     */
    private void checkSave() {
        if (mockpre) {
            mRequest.getServerCurrentTime();
            mPressBack = true;
        } else {
            saveTest();
        }
    }

    /**
     * 保存
     */
    public void saveTest() {
        boolean isSave = false;
        if (mUserAnswerList != null) {
            int size = mUserAnswerList.size();
            for (int i = 0; i < size; i++) {
                HashMap<String, Object> map = mUserAnswerList.get(i);
                if (map != null && map.containsKey("answer")
                        && !"".equals(map.get("answer"))) {
                    isSave = true;
                    break;
                }
            }
        }

        if (isSave) {
            AlertManager.saveTestAlert(this);
        } else {
            UmengManager.sendToUmeng(MeasureActivity.this, "0");
            finish();
        }
    }

    /**
     * 处理提交试卷的回调
     *
     * @param response 回调
     */
    private void dealSubmitPaperResp(JSONObject response) {
        if (response == null) return;

        SubmitPaperResp submitPaperResp =
                GsonManager.getObejctFromJSON(response.toString(), SubmitPaperResp.class);

        if (submitPaperResp == null || submitPaperResp.getResponse_code() != 1) return;

        ArrayList<NoteM> notes = submitPaperResp.getNotes();

        MeasureEntity measureEntity = new MeasureEntity();
        measureEntity.setDefeat(submitPaperResp.getDefeat());
        measureEntity.setScore(submitPaperResp.getScore());
        measureEntity.setScores(submitPaperResp.getScores());
        measureEntity.setExercise_id(submitPaperResp.getExercise_id());

        // 跳转到练习报告页面
        Intent intent = new Intent(this, PracticeReportActivity.class);
        intent.putExtra("notes", notes);
        intent.putExtra("paper_name", mPaperName);
        intent.putExtra("paper_type", mPaperType);
        intent.putExtra("right_num", mRightNum);
        intent.putExtra("total_num", mTotalNum);
        intent.putExtra("category", mCategoryMap);
        intent.putExtra("questions", mQuestions);
        intent.putExtra("user_answer", mUserAnswerList);
        intent.putExtra("hierarchy_id", mHierarchyId);
        intent.putExtra("hierarchy_level", mHierarchyLevel);
        intent.putExtra("umeng_entry", mUmengEntry);
        intent.putExtra("umeng_timestamp", mUmengTimestamp);
        intent.putExtra("measure_entity", measureEntity);
        intent.putExtra("from", "mock");
        intent.putExtra("exercise_id", measureEntity.getExercise_id());
        intent.putExtra("paper_id", mPaperId);
        startActivity(intent);
        finish();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        switch (apiName) {
            case "submit_paper":
                dealSubmitPaperResp(response);
                break;
            case "auto_training":
            case "note_questions":
                // 快速智能练习&专项练习
                AutoTrainingResp autoTrainingResp = mGson.fromJson(
                        response.toString(), AutoTrainingResp.class);

                if (autoTrainingResp != null && autoTrainingResp.getResponse_code() == 1) {
                    dealAutoTrainingResp(autoTrainingResp);
                } else {
                    ToastManager.showToast(this, getString(R.string.netdata_error));
                }
                break;

            case "paper_exercise":
            case "history_exercise_detail":
                // mini模考、整卷、模考、估分、历史练习
                MeasureModel.dealExerciseDetailResp(this, response);
                break;

            case "server_current_time":
                ServerCurrentTimeResp resp = GsonManager.getGson().fromJson(
                        response.toString(), ServerCurrentTimeResp.class);
                MeasureModel.dealServerCurrentTimeResp(this, resp);
                break;
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