package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ToastManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyAnalysisActivity extends ActionBarActivity implements RequestCallback{

    private EditText mEditText;
    private String mQuestionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_analysis);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        mEditText = (EditText) findViewById(R.id.myanalysis_text);

        // 获取数据
        mQuestionId = getIntent().getStringExtra("question_id");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("提交"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();

        } else if ("提交".equals(item.getTitle())) {
            if (mEditText.getText().toString().length() < 10) {
                ToastManager.showToast(this, "字数不够……");
            } else {
                new Request(this).reportErrorQuestion(ParamBuilder.reportErrorQuestion(
                        mQuestionId, "4", mEditText.getText().toString()));

                ToastManager.showToast(this, "提交成功");

                finish();
            }
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
}