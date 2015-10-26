package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
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
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.MockDAO;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.login.activity.BindingMobileActivity;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.netdata.mock.MockPre;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MockPreActivity extends ActionBarActivity implements RequestCallback, View.OnClickListener {
    private LinearLayout examdeailContainer;
    private LinearLayout rankingContainer;
    private Request mRequest;
    private int mock_id;//模考id
    private String paper_name;
    private String mock_time;
    private String courseDetailLink;
    private boolean is_purchased;
    private TextView bottom_right;
    public static TextView bottom_left;
    private Handler mHandler;
    private int course_id;
    public static boolean beginMock;//是否可以进入考试
    public static boolean isDate;//是否是预约时间
    public static boolean isExercise;//进入练习报告页
    private int exercise_id;
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
                        bottom_left.setText(text);
                        break;

                    case BEGINMOCK_Y:
                        //考试时间到
                        bottom_left.setText("点击进入");
                        beginMock = true;
                        isDate = false;//不再预约
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
        // Toolbar
        CommonModel.setToolBar(this);
        //获取传参
        mock_id = getIntent().getIntExtra("mock_id", -1);
        paper_name = getIntent().getStringExtra("paper_name");
        //布局
        examdeailContainer = (LinearLayout) findViewById(R.id.examdetailcontainer);
        rankingContainer = (LinearLayout) findViewById(R.id.rankingcontainer);
        bottom_right = (TextView) findViewById(R.id.mockpre_bottom_right);
        bottom_left = (TextView) findViewById(R.id.mockpre_bottom_left);
        //设置监听
        bottom_right.setOnClickListener(this);
        bottom_left.setOnClickListener(this);
        //初始化时不可以进入考试,不可预约
        beginMock = false;
        isDate = false;
        isExercise = false;
        //成员变量初始化
        // 成员变量初始化
        mHandler = new MsgHandler(this);
        mRequest = new Request(this, this);
        //获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        mRequest.getMockPreExamInfo(mock_id + "");
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
    public void requestCompleted(JSONObject response, String apiName) {
        if (apiName == null) return;

        switch (apiName) {
            case "mockpre_exam_info":
                ProgressDialogManager.closeProgressDialog();
                dealMockPreInfo(response);
                break;

            case "mock_signup": //报名结果
                if (response == null || response.toString().equals("")) {
                    ToastManager.showToast(this, "报名失败");
                    return;
                }

                try {
                    int response_code = response.getInt("response_code");
                    if (response_code == 1) {
                        ToastManager.showToast(this, "课程已开通，详情见侧边栏课程中心");
                        bottom_right.setText("查看详情");
                        courseDetailLink = courseDetailLink.replace("unpurchased", "purchased");
                        is_purchased = true;
                    } else {
                        ToastManager.showToast(this, "报名失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case "book_mock":
                dealBookMockSuccess();
                break;
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }

    //动态添加模考说明
    public void addExamChildViews(String tipString, String detailString, boolean isLast) {
        float destity = getResources().getDisplayMetrics().density;
        LinearLayout exam = new LinearLayout(this);
        LinearLayout.LayoutParams lpex = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpex.setMargins(0, (int) destity * 15, 0, 0);
        exam.setLayoutParams(lpex);
        exam.setOrientation(LinearLayout.HORIZONTAL);
        exam.setGravity(Gravity.TOP);
        TextView textView = new TextView(this);
        textView.setHeight((int) destity * 20);
        textView.setWidth((int) destity * 20);
        textView.setBackgroundResource(R.drawable.mockpre_tips);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setText(tipString);
        exam.addView(textView);
        TextView detail = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) destity * 15, 0, 0, 0);
        detail.setLayoutParams(lp);
        detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        detail.setTextSize(17);
        detail.setTextColor(getResources().getColor(R.color.common_text));
        if (isLast) {
            int start = detailString.length() + 2;
            int end = start + 4;
            SpannableStringBuilder style = new SpannableStringBuilder(detailString + "  查看详情");
            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.apptheme)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            detail.setText(style);
            exam.addView(detail);
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipCourseDetailPage();
                }
            });
            examdeailContainer.addView(exam);
        } else {
            detail.setText(Html.fromHtml(detailString));
            exam.addView(detail);
            examdeailContainer.addView(exam);
        }

    }

    //动态添加排名信息
    public void addRankChildViews(String tipString, String detailString) {
        float destity = getResources().getDisplayMetrics().density;
        LinearLayout exam = new LinearLayout(this);
        LinearLayout.LayoutParams lpex = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpex.setMargins(0, (int) destity * 15, 0, 0);
        exam.setLayoutParams(lpex);
        exam.setOrientation(LinearLayout.HORIZONTAL);
        exam.setGravity(Gravity.TOP);
        TextView textView = new TextView(this);
        textView.setHeight((int) destity * 20);
        textView.setWidth((int) destity * 20);
        textView.setBackgroundResource(R.drawable.mockpre_tips);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setText(tipString);
        exam.addView(textView);
        TextView detail = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) destity * 15, 0, 0, 0);
        detail.setLayoutParams(lp);
        detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        detail.setTextSize(17);
        detail.setTextColor(getResources().getColor(R.color.common_text));
        detail.setText(detailString);
        exam.addView(detail);
        rankingContainer.addView(exam);
    }

    public void dealMockPreInfo(JSONObject response) {
        MockPre mockPre = GsonManager.getObejctFromJSON(response.toString(), MockPre.class);
        if (mockPre.getResponse_code() != 1) {
            return;
        }
        //模
        mock_time = mockPre.getMock_time();
        long time = Utils.getSecondsByDateMinusNow(mock_time);
        int date = MockDAO.getIsDateById(mock_id);
        String mock_status = mockPre.getMock_status();
        if (mockPre.getExercise_id() > 0) {
            bottom_left.setText("练习报告");
            isExercise = true;
            exercise_id = mockPre.getExercise_id();
        } else {
            if (mock_status.equals("unstart")) {//未开始
                mDuration = time;
                if (date == 0) {//未预约过
                    bottom_left.setText("预约考试");
                    isDate = true;
                    startTimeBackground();
                } else {//倒计时
                    startTimer();
                }
            } else if (mock_status.equals("on_going")) {//开考30分钟内
                bottom_left.setText("点击进入");
                beginMock = true;
            } else if (mock_status.equals("end") || mock_status.equals("finish")) {//开考30分钟后
                bottom_left.setText("来晚啦");
                bottom_left.setBackgroundColor(getResources().getColor(R.color.evaluation_text_gray));
            }
        }
        //是否已报名
        is_purchased = mockPre.getIs_purchased();
        if (is_purchased) {
            bottom_right.setText("查看详情");
        }
        //课程id
        course_id = mockPre.getCourse_id();
        //排名
        List<String> award_info = mockPre.getAward_info();
        for (int i = 0; i < award_info.size(); i++) {
            addRankChildViews((i + 1) + "", award_info.get(i));
        }

        //模考信息
        List<MockPre.DateInfoEntity> dataInfoEntity = mockPre.getDate_info();
        //查看详情链接
        int size = dataInfoEntity == null ? 0 : dataInfoEntity.size();
        for (int i = 0; i < size; i++) {
            MockPre.DateInfoEntity entity = dataInfoEntity.get(i);
            if (entity == null) continue;

            String link = entity.getLink();
            if (link == null || link.length() == 0) {
                addExamChildViews((i + 1) + "", entity.getText(), false);
            } else {
                courseDetailLink = link;
                addExamChildViews((i + 1) + "", entity.getText(), true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mockpre_bottom_right://课程报名
                if (is_purchased) {//已购
                    skipCourseDetailPage();
                } else {//报名
                    mRequest.mockSignUp(ParamBuilder.getSignUpMockCourse(course_id + ""));
                }
                break;
            case R.id.mockpre_bottom_left://进入考试
                if (beginMock) {
                    Intent intent = new Intent(this, MeasureActivity.class);
                    intent.putExtra("from", "mockpre");
                    intent.putExtra("paper_id", mock_id);
                    intent.putExtra("paper_type", "mock");
                    intent.putExtra("mock_time", mock_time);
                    intent.putExtra("paper_name", paper_name);
                    intent.putExtra("redo", false);
                    startActivity(intent);
                    finish();
                }
                if (isDate) {
                    // 判断用户是否有手机号
                    String mobileNum = LoginModel.getUserMobile();
                    if (mobileNum == null || mobileNum.length() == 0) {
                        Intent intent = new Intent(this, BindingMobileActivity.class);
                        intent.putExtra("from", "mock_openopencourse");
                        intent.putExtra("mock_id", mock_id);
                        startActivityForResult(intent, ActivitySkipConstants.ANSWER_SHEET_SKIP);
                    } else {
                        mRequest.bookMock(ParamBuilder.getBookMock(mock_id + ""));
                    }
                }
                if (isExercise) {//进入练习报告页
                    Intent intent = new Intent(this, PracticeReportActivity.class);
                    intent.putExtra("exercise_id", exercise_id);
                    intent.putExtra("paper_type", "mock");
                    intent.putExtra("paper_name", paper_name);
                    intent.putExtra("from", "mock");
                    startActivity(intent);
                    finish();
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
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", courseDetailLink);
        intent.putExtra("bar_title", "");
        intent.putExtra("from", "opencourse_pre");
        startActivity(intent);
    }

    /**
     * 倒计时启动
     */
    public void startTimer() {
        mHours = mDuration / (60*60);
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
        mMins = mDuration / 60;
        mSec = mDuration % 60;
        if (mBeginMockTimer != null) {
            mTimer.cancel();
        }
        mBeginMockTimer = new Timer();
        mBeginMockTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mSec--;
                if (mSec < 0) {
                    mMins--;
                    mSec = 59;
                } else if (mMins == 0 && mSec == 0) {
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
            mRequest.bookMock(ParamBuilder.getBookMock(mock_id + ""));
        } else if (resultCode == ActivitySkipConstants.MOBILE_BOOK_MOCK_RESULT) {//已经预约成功
            dealBookMockSuccess();
        }
    }

    public void dealBookMockSuccess() {
        //绑定成功后操作
        MockDAO.save(mock_id, 1);
        ToastManager.showToast(this, "考试前会收到短信提示哦");
        mDuration = Utils.getSecondsByDateMinusNow(mock_time);
        startTimer();
        isDate = false;
    }
}
