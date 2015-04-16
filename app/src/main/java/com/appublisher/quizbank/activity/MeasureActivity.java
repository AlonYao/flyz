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
import com.appublisher.quizbank.model.netdata.measure.NoteM;
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
        if ("auto".equals(mPaperType)) {
            ProgressDialogManager.showProgressDialog(this, true);
            request.getAutoTraining();
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
     * 设置内容
     * @param autoTrainingResp 快速智能练习回调
     */
    private void setContent(AutoTrainingResp autoTrainingResp) {
        mPaperId = autoTrainingResp.getPaper_id();
        ArrayList<QuestionM> questions = autoTrainingResp.getQuestions();

        if (questions == null) return;

        // 初始化用户答案
        int size = questions.size();
        mUserAnswerList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<>();

            QuestionM question = questions.get(i);
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

        // 设置ViewPager
        MeasureAdapter measureAdapter = new MeasureAdapter(this, questions);
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

        // 倒计时
        mDuration = autoTrainingResp.getDuration();
        startTimer();
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

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if ("auto_training".equals(apiName)) {
            AutoTrainingResp autoTrainingResp = mGson.fromJson(
                    response.toString(), AutoTrainingResp.class);

            if (autoTrainingResp != null && autoTrainingResp.getResponse_code() == 1) {
                setContent(autoTrainingResp);
            } else {
                ToastManager.showToast(this, getString(R.string.netdata_error));
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
