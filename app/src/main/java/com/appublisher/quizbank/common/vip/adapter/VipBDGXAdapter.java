package com.appublisher.quizbank.common.vip.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.model.VipSubmitEntity;
import com.appublisher.quizbank.common.vip.netdata.VipBDGXResp;
import com.appublisher.quizbank.common.vip.network.VipParamBuilder;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import java.util.List;

/**
 * Created by jinbao on 2016/9/19.
 */
public class VipBDGXAdapter extends PagerAdapter {

    private VipBDGXResp vipBDGXResp;
    private Context context;
    private int exerciseType;

    public VipBDGXAdapter(Context context, VipBDGXResp vipBDGXResp) {
        this.context = context;
        this.vipBDGXResp = vipBDGXResp;
    }

    public void setVipBDGXResp(VipBDGXResp vipBDGXResp) {
        this.vipBDGXResp = vipBDGXResp;
    }

    public void setExerciseType(int exerciseType) {
        this.exerciseType = exerciseType;
    }

    @Override
    public int getCount() {
        return vipBDGXResp == null ? 0 : vipBDGXResp.getQuestion().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.vip_bdgx_item, null);
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        final TextView explainText = (TextView) view.findViewById(R.id.explain_text);
        final TextView content = (TextView) view.findViewById(R.id.content);
        final EditText inputAnswer = (EditText) view.findViewById(R.id.textinput);
        final TextView submit = (TextView) view.findViewById(R.id.submit);
        final View explainView = view.findViewById(R.id.explain_view);
        final View answerView = view.findViewById(R.id.answer_view);
        final TextView myAnswer = (TextView) view.findViewById(R.id.my_answer);
        final TextView standard = (TextView) view.findViewById(R.id.standard_answer);
        final VipBDGXResp.QuestionBean questionBean = vipBDGXResp.getQuestion().get(position);
        final String index = String.valueOf(position + 1) + "/" + vipBDGXResp.getQuestion().size();

        if (position != 0) {
            explainView.setVisibility(View.GONE);
        } else if (5 == exerciseType) {
            explainText.setText(context.getResources().getString(R.string.vip_explain_bdgx));
        } else if (6 == exerciseType) {
            explainText.setText(context.getResources().getString(R.string.vip_explain_yitl));
        }

        String contentText = index + "   " + vipBDGXResp.getQuestion().get(position).getQuestion();
        final SpannableStringBuilder sp = new SpannableStringBuilder(contentText);
        sp.setSpan(new AbsoluteSizeSpan(20, true), 0, index.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText + contentText);

        if (questionBean.getUser_answer() == null) {
            inputAnswer.setVisibility(View.VISIBLE);
            answerView.setVisibility(View.GONE);
        } else {
            inputAnswer.setVisibility(View.GONE);
            myAnswer.setText(questionBean.getUser_answer().getAnswer());
            standard.setText(questionBean.getAnswer());
            answerView.setVisibility(View.VISIBLE);
            if (position == getCount() - 1) {
                submit.setText("返回列表");
                submit.setVisibility(View.VISIBLE);
            } else {
                submit.setVisibility(View.GONE);
            }
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = submit.getText().toString();
                if ("提交并查看答案".equals(text)) {
                    int done = 0;
                    if (position == getCount() - 1) {
                        done = 1;
                    }

                    String inputText = inputAnswer.getText().toString();

                    if ("".equals(inputText)) {
                        ToastManager.showToast(context, "请输入答案");
                    } else {
                        new VipRequest(context, (RequestCallback) context).submit(VipParamBuilder.submit(new VipSubmitEntity().setExercise_id(vipBDGXResp.getExercise_id()).setQuestion_id(questionBean.getQuestion_id()).setAnswer_content(inputText).setDone(done)));

                        questionBean.setUser_answer(new VipBDGXResp.QuestionBean.UserAnswerBean().setAnswer(inputText));
                        vipBDGXResp.getQuestion().set(position, questionBean);

                        ProgressDialogManager.showProgressDialog(context);
                    }

                } else if ("返回列表".equals(text)) {
                    ((Activity) context).finish();
                }
            }
        });

        //软键盘消失
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        submit.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (submit.getVisibility() == View.VISIBLE && inputAnswer.hasFocus() && submit.getRootView().getHeight() > 500) {
                    scrollView.postDelayed(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }, 200);
                }
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //用于数据刷新
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
