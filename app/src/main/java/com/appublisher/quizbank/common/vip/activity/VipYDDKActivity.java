package com.appublisher.quizbank.common.vip.activity;


import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.model.VipSubmitEntity;
import com.appublisher.quizbank.common.vip.netdata.VipYDDKResp;
import com.appublisher.quizbank.common.vip.network.VipParamBuilder;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 阅读打卡
 */
public class VipYDDKActivity extends BaseActivity implements RequestCallback {
    private VipRequest mRequest;
    private int exerciseId;
    private int questionId = -1;
    private TextView questionContent;
    private TextView answerContent;
    private View submitView;
    private EditText textInput;
    private ImageButton submit;
    private ImageView userAnswerArrow;
    private ScrollView questionView;
    private ScrollView answerView;
    private int mScreenHeight;
    private int mLastY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_yddk);
        setToolBar(this);

        exerciseId = getIntent().getIntExtra("exerciseId", -1);
        mRequest = new VipRequest(this, this);

        // 获取ToolBar高度
        int toolBarHeight = getSupportActionBar().getHeight();

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        initView();
        initData();
    }

    public void initView() {
        questionContent = (TextView) findViewById(R.id.content);
        submitView = findViewById(R.id.submit_view);
        textInput = (EditText) findViewById(R.id.textinput);
        answerContent = (TextView) findViewById(R.id.answer_content);
        submit = (ImageButton) findViewById(R.id.submit);
        questionView = (ScrollView) findViewById(R.id.question_view);
        answerView = (ScrollView) findViewById(R.id.answer_view);
        userAnswerArrow = (ImageView) findViewById(R.id.user_answer_iv);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = textInput.getText().toString();
                if (userAnswer == null || userAnswer.equals("")) {
                    ToastManager.showToast(VipYDDKActivity.this, "写点东西吧");
                } else {
                    if (questionId != -1) {
                        mRequest.submit(VipParamBuilder.submit(new VipSubmitEntity().setExercise_id(exerciseId).setAnswer_content(userAnswer).setQuestion_id(questionId)));
                        hideInputKeyboard(v);
                        showLoading();
                    }
                }
            }
        });

        questionContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputKeyboard(v);
            }
        });
    }

    public void initData() {
        if (exerciseId != -1) {
            mRequest.getExerciseDetail(exerciseId);
            showLoading();
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null) return;

        if ("exercise_detail".equals(apiName)) {
            VipYDDKResp vipYDDKResp = GsonManager.getModel(response, VipYDDKResp.class);
            if (vipYDDKResp.getResponse_code() == 1) {
                if (vipYDDKResp.getQuestion() != null && vipYDDKResp.getQuestion().size() > 0) {
                    questionContent.setText(Html.fromHtml(vipYDDKResp.getQuestion().get(0).getQuestion()));
                    questionId = vipYDDKResp.getQuestion().get(0).getQuestion_id();

                    //已做
                    if (vipYDDKResp.getStatus() != 6 && vipYDDKResp.getStatus() != 0) {
                        submitView.setVisibility(View.GONE);
                        answerView.setVisibility(View.VISIBLE);
                        userAnswerArrow.setVisibility(View.VISIBLE);
                        answerContent.setText(vipYDDKResp.getQuestion().get(0).getUser_answer().getContent());
                        questionDone();
                    } else {
                        answerView.setVisibility(View.GONE);
                        userAnswerArrow.setVisibility(View.GONE);
                    }
                }
            }
        } else if ("submit".equals(apiName)) {
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                int responseCode = jsonObject.optInt("response_code");
                if (responseCode == 1) {
                    ToastManager.showToast(this, "提交成功");
                    initData();
                } else {
                    ToastManager.showToast(this, "提交失败，请重新提交");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    public void questionDone() {
        ViewGroup.LayoutParams layoutParams = questionView.getLayoutParams();
        layoutParams.height = 800;
        questionView.setLayoutParams(layoutParams);

        userAnswerArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        mLastY = (int) event.getRawY();
                        break;

                    /**
                     * layout(l,t,r,b)
                     * l  Left position, relative to parent
                     t  Top position, relative to parent
                     r  Right position, relative to parent
                     b  Bottom position, relative to parent
                     * */
                    case MotionEvent.ACTION_MOVE:

                        int dy = (int) event.getRawY() - mLastY;

                        int top = v.getTop() + dy;
                        int bottom = v.getBottom() + dy;

                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > mScreenHeight) {
                            bottom = mScreenHeight;
                            top = bottom - v.getHeight();
                        }
                        v.layout(v.getLeft(), top, v.getRight(), bottom);

                        ViewGroup.LayoutParams layoutParams = questionView.getLayoutParams();
                        layoutParams.height = questionView.getHeight() + dy;
                        questionView.setLayoutParams(layoutParams);

                        mLastY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }
        });
    }

    public void hideInputKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
