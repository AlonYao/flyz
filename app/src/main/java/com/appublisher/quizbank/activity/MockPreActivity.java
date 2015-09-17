package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ToastManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class MockPreActivity extends ActionBarActivity implements RequestCallback {
    private LinearLayout examdeailContainer;
    private LinearLayout rankingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_pre);
        // Toolbar
        CommonModel.setToolBar(this);

        //布局
        examdeailContainer = (LinearLayout) findViewById(R.id.examdetailcontainer);
        rankingContainer = (LinearLayout) findViewById(R.id.rankingcontainer);
        addExamChildViews("1", "我是内容我是内容我是内容我是内容我是内容我是内容我是内容", false);
        addExamChildViews("2", "我是内容我是内容我是内容我是内容我是内容我是内容我是内容", false);
        addExamChildViews("3", "我是内容我是内容我是内容我是内容我是内容我是内容我是内容", false);
        addExamChildViews("4", "我是内容我是内容我是内容我是内容我是内容我是内容我是内容", true);


        addRankChildViews("1", "kindle");
        addRankChildViews("2", "iPad");
        addRankChildViews("3", "你猜");
        addRankChildViews("4", "考不上了");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }

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
        detail.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        detail.setTextColor(getResources().getColor(R.color.setting_text));

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
                    ToastManager.showToast(MockPreActivity.this, "呵呵哒");
                }
            });
            examdeailContainer.addView(exam);
        } else {
            detail.setText(detailString);
            exam.addView(detail);
            examdeailContainer.addView(exam);
        }

    }

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
        detail.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        detail.setTextColor(getResources().getColor(R.color.setting_text));
        detail.setText(detailString);
        exam.addView(detail);
        rankingContainer.addView(exam);
    }
}
