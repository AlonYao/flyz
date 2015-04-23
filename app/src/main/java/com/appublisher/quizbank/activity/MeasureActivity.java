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
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.model.netdata.measure.AutoTrainingResp;
import com.appublisher.quizbank.model.netdata.measure.CategoryM;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.PaperExerciseEntireResp;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

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
public class MeasureActivity extends ActionBarActivity implements RequestCallback{

    public int mScreenHeight;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public ViewPager mViewPager;
    public long mCurTimestamp;
    public int mCurPosition;
    public int mDuration;

    private Gson mGson;
    private Handler mHandler;
    private Timer mTimer;
    private String mPaperType;
    private String mPaperName;
    private int mPaperId;
    private boolean mRedo;
    private static Toolbar mToolbar;
    private static int mMins;
    private static int mSec;
    private ArrayList<QuestionM> mQuestions;
    private HashMap<String, Integer> mEntirePaperCategory;

    private static final int TIME_ON = 0;
    private static final int TIME_OUT = 1;

    private static class MsgHandler extends Handler {
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
        Request request = new Request(this, this);
        mGson = new Gson();
        mHandler = new MsgHandler(this);

        // 获取ToolBar高度
        int toolBarHeight = MeasureModel.getViewHeight(mToolbar);

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        // 获取数据
        mPaperType = getIntent().getStringExtra("paper_type");
        mPaperName = getIntent().getStringExtra("paper_name");

        switch (mPaperType) {
            case "auto":
                ProgressDialogManager.showProgressDialog(this, true);
                request.getAutoTraining();

                break;

            case "note":
                int hierarchy_id = getIntent().getIntExtra("hierarchy_id", 0);
                int hierarchy_level = getIntent().getIntExtra("hierarchy_level", 0);
                String note_type = getIntent().getStringExtra("note_type");

                switch (hierarchy_level) {
                    case 1:
                        ProgressDialogManager.showProgressDialog(this, true);
                        request.getNoteQuestions(String.valueOf(hierarchy_id), "", "", note_type);

                        break;

                    case 2:
                        ProgressDialogManager.showProgressDialog(this, true);
                        request.getNoteQuestions("", String.valueOf(hierarchy_id), "", note_type);

                        break;

                    case 3:
                        ProgressDialogManager.showProgressDialog(this, true);
                        request.getNoteQuestions("", "", String.valueOf(hierarchy_id), note_type);

                        break;
                }

                break;

            case "entire":
                mPaperId = getIntent().getIntExtra("paper_id", 0);

                ProgressDialogManager.showProgressDialog(this, true);
                request.getPaperExercise(mPaperId, mPaperType);

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("草稿纸").setIcon(
                R.drawable.measure_icon_scratch_paper), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("答题卡").setIcon(
                R.drawable.measure_icon_answersheet), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("暂停").setIcon(
                R.drawable.measure_icon_pause), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveTest();

        } else if (item.getTitle().equals("暂停")) {
            if (mTimer != null) mTimer.cancel();
            saveQuestionTime();
            AlertManager.pauseAlert(this);

        } else if (item.getTitle().equals("答题卡")) {
            skipToAnswerSheet();

        } else if (item.getTitle().equals("草稿纸")) {
            Intent intent = new Intent(this, ScratchPaperActivity.class);
            startActivity(intent);
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

            Intent intent = new Intent(this, PracticeReportActivity.class);
            intent.putExtra("notes", notes);
            intent.putExtra("paper_name", paper_name);
            intent.putExtra("right_num", right_num);
            intent.putExtra("total_num", total_num);
            intent.putExtra("category", category);
            intent.putExtra("questions", mQuestions);
            intent.putExtra("user_answer", mUserAnswerList);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        saveTest();
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
        MeasureAdapter measureAdapter = new MeasureAdapter(this, mQuestions);
        mViewPager.setAdapter(measureAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                saveQuestionTime();
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
    private void saveTest() {
        if (mUserAnswerList == null || mUserAnswerList.size() == 0) {
            finish();
            return;
        }

        boolean isSave = false;
        int size = mUserAnswerList.size();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = mUserAnswerList.get(i);
            if (map != null && map.containsKey("answer")
                    && !"".equals(map.get("answer"))) {
                isSave = true;
                break;
            }
        }

        if (isSave) {
            AlertManager.saveTestAlert(this);
        } else {
            finish();
        }
    }

    /**
     * 处理试卷练习回调
     * @param response 回调数据
     */
    private void dealPaperExercise(JSONObject response) {
        if ("entire".equals(mPaperType)) {
            PaperExerciseEntireResp paperExerciseEntireResp =
                    mGson.fromJson(response.toString(), PaperExerciseEntireResp.class);

            if (paperExerciseEntireResp == null || paperExerciseEntireResp.getResponse_code() != 1)
                return;

            ArrayList<CategoryM> categorys = paperExerciseEntireResp.getCategory();

            if (categorys == null || categorys.size() == 0) return;

            // 拼接问题
            mQuestions = new ArrayList<>();
            mEntirePaperCategory = new HashMap<>();
            int size = categorys.size();
            for (int i = 0; i < size; i++) {
                CategoryM category = categorys.get(i);

                if (category == null) continue;

                ArrayList<QuestionM> questions = category.getQuestions();
                String categoryName = category.getName();

                if (questions == null || questions.size() == 0) continue;

                mQuestions.addAll(questions);

                // 保存各个分类的数量
                mEntirePaperCategory.put(categoryName, questions.size());
            }

            // 倒计时时间
            mDuration = paperExerciseEntireResp.getDuration();

            setContent();
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if ("auto_training".equals(apiName) || "note_questions".equals(apiName)) {
            AutoTrainingResp autoTrainingResp = mGson.fromJson(
                    response.toString(), AutoTrainingResp.class);

            if (autoTrainingResp != null && autoTrainingResp.getResponse_code() == 1) {
                dealAutoTrainingResp(autoTrainingResp);
            } else {
                ToastManager.showToast(this, getString(R.string.netdata_error));
            }
        }

        if ("paper_exercise".equals(apiName)) dealPaperExercise(response);

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
