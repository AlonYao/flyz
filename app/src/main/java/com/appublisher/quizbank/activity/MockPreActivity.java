package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_course.CourseWebViewActivity;
import com.appublisher.lib_login.activity.BindingMobileActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureMockReportActivity;
import com.appublisher.quizbank.common.measure.model.MeasureModel;
import com.appublisher.quizbank.common.measure.netdata.ServerCurrentTimeResp;
import com.appublisher.quizbank.dao.MockDAO;
import com.appublisher.quizbank.model.business.LegacyMeasureModel;
import com.appublisher.quizbank.model.netdata.mock.MockGufenResp;
import com.appublisher.quizbank.model.netdata.mock.MockPreResp;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MockPreActivity extends BaseActivity implements RequestCallback, View.OnClickListener {
    private LinearLayout examdeailContainer;
    private LinearLayout rankingContainer;
    private QRequest mQRequest;
    private int mock_id;//模考id
    private String paper_name;
    private String mock_time;
    private String courseDetailLink;
    private TextView bottom_right;
    public TextView bottom_left;
    private Handler mHandler;
    private int exercise_id = -1;
    private MockPreResp mMockPreResp;
    //预约后倒计时＋考试时间倒计时
    public Timer mTimer;
    public static long mDuration;
    public static long mHours; //小时
    public static long mMins; //分钟
    public static long mSec; //秒数
    public static final int BEGINMOCK_N = 1;
    public static final int BEGINMOCK_Y = 0;
    //非预约时默认倒计时
    public Timer mBeginMockTimer;

    // 系统时间
    private String mServerCurrentTime;

    private static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final MockPreActivity activity = (MockPreActivity) mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case BEGINMOCK_N:
                        String mins = String.valueOf(mMins);
                        String sec = String.valueOf(mSec);
                        String hour = String.valueOf(mHours);
                        if (hour.length() == 1) hour = "0" + hour;
                        if (mins.length() == 1) mins = "0" + mins;
                        if (sec.length() == 1) sec = "0" + sec;
                        String time = hour + ":" + mins + ":" + sec;
                        String text = time + " 开考";
                        activity.bottom_left.setText(text);
                        break;

                    case BEGINMOCK_Y:
                        //考试时间到
                        activity.bottom_left.setText("点击进入");
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
        setContentView(R.layout.activity_mock_pre);

        setToolBar(this);

        paper_name = getIntent().getStringExtra("paper_name");
        mock_id = getIntent().getIntExtra("mock_id", -1);

        initViews();

        mHandler = new MsgHandler(this);
        mQRequest = new QRequest(this, this);
    }

    public void initViews() {
        examdeailContainer = (LinearLayout) findViewById(R.id.examdetailcontainer);
        rankingContainer = (LinearLayout) findViewById(R.id.rankingcontainer);
        bottom_right = (TextView) findViewById(R.id.mockpre_bottom_right);
        bottom_left = (TextView) findViewById(R.id.mockpre_bottom_left);

        bottom_right.setOnClickListener(this);
        bottom_left.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取数据(模考列表)
        showLoading();
        mQRequest.getServerCurrentTime();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mBeginMockTimer != null) {
                mBeginMockTimer.cancel();
            }
            if (mTimer != null) {
                mTimer.cancel();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mBeginMockTimer != null) {
            mBeginMockTimer.cancel();
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onBackPressed();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) return;
        hideLoading();
        switch (apiName) {
            case "mockpre_exam_info":
                dealMockPreInfo(response);
                break;

            case "book_mock":
                dealBookMockSuccess();
                break;

            case "server_current_time":
                if (mock_id == -1) {
                    mQRequest.getMockGufen();
                } else {
                    mQRequest.getMockPreExamInfo(String.valueOf(mock_id));
                    MeasureModel.saveCacheMockId(this, mock_id);
                }

                ServerCurrentTimeResp resp = GsonManager.getModel(
                        response.toString(), ServerCurrentTimeResp.class);
                if (resp != null && resp.getResponse_code() == 1) {
                    mServerCurrentTime = resp.getCurrent_time();
                }
                break;

            case "mock_gufen":
                MockGufenResp mockGufenResp = GsonManager.getModel(response, MockGufenResp.class);
                if (mockGufenResp == null || mockGufenResp.getResponse_code() != 1) return;
                MockGufenResp.MockBean mockBean = mockGufenResp.getMock();
                if (mockBean == null) return;
                mock_id = mockBean.getId();
                mQRequest.getMockPreExamInfo(String.valueOf(mock_id));
                MeasureModel.saveCacheMockId(this, mock_id);
                break;
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

    /**
     * 动态添加考试说明
     * @param detail 详情文字
     * @param isLast 是否是最后一段
     */
    public void addExamChildViews(String detail, boolean isLast) {
        float destity = getResources().getDisplayMetrics().density;
        LinearLayout exam = new LinearLayout(this);
        LinearLayout.LayoutParams lpex = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpex.setMargins(0, 0, 0, (int) destity * 15);
        exam.setLayoutParams(lpex);
        exam.setOrientation(LinearLayout.HORIZONTAL);
        exam.setGravity(Gravity.TOP);
        // 详情文字处理
        TextView tvDetail = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins((int) destity * 15, 0, 0, 0);
        tvDetail.setLayoutParams(lp);
        tvDetail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        tvDetail.setTextSize(17);
        tvDetail.setTextColor(getResources().getColor(R.color.common_text));
        if (isLast) {
            int start = detail.length() + 2;
            int end = start + 4;
            SpannableStringBuilder style = new SpannableStringBuilder(detail + "  查看详情");
            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.apptheme)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDetail.setText(style);
            exam.addView(tvDetail);
            tvDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipCourseDetailPage();
                }
            });
            examdeailContainer.addView(exam);
        } else {
            tvDetail.setText(Html.fromHtml(detail));
            exam.addView(tvDetail);
            examdeailContainer.addView(exam);
        }
    }

    /**
     * 动态添加排名信息
     * @param detail 详情文字
     */
    public void addRankChildViews(String detail) {
        float destity = getResources().getDisplayMetrics().density;
        LinearLayout exam = new LinearLayout(this);
        LinearLayout.LayoutParams lpex = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpex.setMargins(0, (int) destity * 15, 0, 0);
        exam.setLayoutParams(lpex);
        exam.setOrientation(LinearLayout.HORIZONTAL);
        exam.setGravity(Gravity.TOP);
        // 详情文字处理
        TextView tvDetail = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) destity * 15, 0, 0, 0);
        tvDetail.setLayoutParams(lp);
        tvDetail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        tvDetail.setTextSize(17);
        tvDetail.setTextColor(getResources().getColor(R.color.common_text));
        tvDetail.setText(detail);
        exam.addView(tvDetail);
        rankingContainer.addView(exam);
    }

    /**
     * 处理模考信息
     *
     * @param response 回调数据
     */
    public void dealMockPreInfo(JSONObject response) {
        MockPreResp mockPreResp = GsonManager.getModel(response.toString(), MockPreResp.class);
        mMockPreResp = mockPreResp;
        if (mockPreResp == null || mockPreResp.getResponse_code() != 1) {
            return;
        }

        // 初始化Container
        examdeailContainer.removeAllViews();
        rankingContainer.removeAllViews();

        //模
        mock_time = mockPreResp.getMock_time();

        // 缓存模考时间
        LegacyMeasureModel legacyMeasureModel = new LegacyMeasureModel(this);
        legacyMeasureModel.updateMockTime(mock_time);
        legacyMeasureModel.updatePaperName(paper_name);

        mDuration = getSecondsByDateMinusServerTime(mock_time);
        int date = MockDAO.getIsDateById(mock_id);
        String mock_status = mockPreResp.getMock_status();
        if (mockPreResp.getExercise_id() > 0) {
            bottom_left.setText("练习报告");
            exercise_id = mockPreResp.getExercise_id();
        } else {
            switch (mock_status) {
                case "unstart": //未开始
                    if (date == 0) {//未预约过
                        bottom_left.setText("预约考试");
                        startTimeBackground();
                    } else {//倒计时
                        startTimer();
                    }
                    break;

                case "on_going": //开考30分钟内
                    bottom_left.setText("点击进入");
                    break;

                case "end": // 模考彻底结束
                case "finish": //开考30分钟后
                    bottom_left.setText("来晚啦");
                    bottom_left.setBackgroundColor(getResources().getColor(R.color.evaluation_text_gray));
                    break;
            }
        }
        //是否已报名
        if (mockPreResp.getIs_purchased()) {
            bottom_right.setText("查看详情");
        }
        //排名
        List<String> award_info = mockPreResp.getAward_info();
        int size = award_info == null ? 0 : award_info.size();
        for (int i = 0; i < size; i++) {
            addRankChildViews(award_info.get(i));
        }

        //模考信息
        List<MockPreResp.DateInfoEntity> dataInfoEntity = mockPreResp.getDate_info();
        //查看详情链接
        size = dataInfoEntity == null ? 0 : dataInfoEntity.size();
        for (int i = 0; i < size; i++) {
            MockPreResp.DateInfoEntity entity = dataInfoEntity.get(i);
            if (entity == null) continue;

            String link = entity.getLink();
            if (link == null || link.length() == 0) {
                addExamChildViews(entity.getText(), false);
            } else {
                courseDetailLink = link;
                addExamChildViews(entity.getText(), true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mockpre_bottom_right://课程报名
                skipCourseDetailPage();
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "OrderCourse");
                UmengManager.onEvent(this, "Mock", map);
                break;

            case R.id.mockpre_bottom_left://进入考试
                if ("点击进入".equals(bottom_left.getText().toString().trim())) {
                    final Intent intent = new Intent(this, MockListActivity.class);
                    intent.putExtra("mock_list", GsonManager.modelToString(mMockPreResp));
                    intent.putExtra("from", "mockpre");
                    intent.putExtra("paper_id", mock_id);
                    intent.putExtra("paper_type", "mock");
                    intent.putExtra("mock_time", mock_time);
                    intent.putExtra("paper_name", paper_name);
                    intent.putExtra("redo", false);
                    startActivity(intent);
                    finish();

                    // Umeng
                    map = new HashMap<>();
                    map.put("Action", "Exam");
                    UmengManager.onEvent(this, "Mock", map);

                } else if ("预约考试".equals(bottom_left.getText().toString().trim())) {
                    // 判断用户是否有手机号
                    String mobileNum = LoginModel.getUserMobile();
                    if (mobileNum == null || mobileNum.length() == 0) {
                        Intent intent = new Intent(this, BindingMobileActivity.class);
                        intent.putExtra("from", "mock_openopencourse");
                        intent.putExtra("mock_id", mock_id);
                        startActivityForResult(intent, ActivitySkipConstants.ANSWER_SHEET_SKIP);
                    } else {
                        mQRequest.bookMock(ParamBuilder.getBookMock(mock_id + ""));
                    }

                    // Umeng
                    map = new HashMap<>();
                    map.put("Action", "OrderExam");
                    UmengManager.onEvent(this, "Mock", map);

                } else if (exercise_id != -1) {//进入练习报告页
                    Intent intent = new Intent(this, MeasureMockReportActivity.class);
                    intent.putExtra(MeasureConstants.INTENT_PAPER_ID, exercise_id);
                    startActivity(intent);

                    // Umeng
                    map = new HashMap<>();
                    map.put("Action", "Report");
                    UmengManager.onEvent(this, "Mock", map);
                }

                if (mServerCurrentTime == null || mServerCurrentTime.length() == 0) {
                    mQRequest.getServerCurrentTime();
                }

                break;
            default:
                break;
        }
    }

    /**
     * 跳转课程详情页
     */
    public void skipCourseDetailPage() {
        String url = LoginParamBuilder.finalUrl(courseDetailLink);

        Logger.i("url===" + url);
        Intent intent = new Intent(this, CourseWebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("bar_title", "");
        intent.putExtra("from", "course");
        startActivity(intent);
    }

    /**
     * 倒计时启动
     */
    public void startTimer() {
        if (mServerCurrentTime == null || mServerCurrentTime.length() == 0) return;

        mHours = mDuration / (60 * 60);
        mMins = (mDuration / 60) % 60;
        mSec = mDuration % 60;

        if (mTimer != null) {
            mTimer.cancel();
        }

        if (mBeginMockTimer != null) {
            mBeginMockTimer.cancel();
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mSec--;
                if (mSec < 0) {
                    mMins--;
                    mSec = 59;

                    if (mMins < 0) {
                        mHours--;
                        mMins = 59;
                    }

                    mHandler.sendEmptyMessage(BEGINMOCK_N);
                } else if (mHours == 0 && mMins == 0 && mSec == 0) {
                    mTimer.cancel();
                    mHandler.sendEmptyMessage(BEGINMOCK_Y);
                } else {
                    mHandler.sendEmptyMessage(BEGINMOCK_N);
                }
            }
        }, 0, 1000);
    }

    /**
     * 用户不预约，到时间后也可进入考试
     */
    public void startTimeBackground() {
        if (mServerCurrentTime == null || mServerCurrentTime.length() == 0) return;

        mHours = mDuration / (60 * 60);
        mMins = mDuration / 60;
        mSec = mDuration % 60;

        if (mTimer != null) {
            mTimer.cancel();
        }

        if (mBeginMockTimer != null) {
            mBeginMockTimer.cancel();
        }

        mBeginMockTimer = new Timer();
        mBeginMockTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mSec--;
                if (mSec < 0) {
                    mMins--;
                    mSec = 59;

                    if (mMins < 0) {
                        mHours--;
                        mMins = 59;
                    }

                } else if (mHours == 0 && mMins == 0 && mSec == 0) {
                    mBeginMockTimer.cancel();
                    mHandler.sendEmptyMessage(BEGINMOCK_Y);
                }
            }
        }, 0, 1000);
    }

    /**
     * 绑定手机号后
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivitySkipConstants.BOOK_MOCK_RESULT) {//绑定成功
            mQRequest.bookMock(ParamBuilder.getBookMock(mock_id + ""));
        } else if (resultCode == ActivitySkipConstants.MOBILE_BOOK_MOCK_RESULT) {//已经预约成功
            dealBookMockSuccess();
        }
    }

    public void dealBookMockSuccess() {
        //绑定成功后操作
        MockDAO.save(mock_id, 1);
        ToastManager.showToast(this, "考试前会收到短信提示哦");
        mDuration = getSecondsByDateMinusServerTime(mock_time);
        startTimer();
    }

    /**
     * 计算指定日期与服务器日期的秒数差
     *
     * @param date 指定日期
     * @return 秒数差
     */
    public long getSecondsByDateMinusServerTime(String date) {
        long seconds = 0;

        if (date == null || date.length() == 0) return 0;

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition parsePosition = new ParsePosition(0);
            Date time = formatter.parse(date, parsePosition);
            seconds = time.getTime() - Long.parseLong(mServerCurrentTime) * 1000;
        } catch (Exception e) {
            // Empty
        }

        return seconds / 1000;
    }
}
