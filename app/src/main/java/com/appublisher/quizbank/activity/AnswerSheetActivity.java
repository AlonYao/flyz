package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.GridView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.AnswerSheetAdapter;
import com.appublisher.quizbank.model.CommonModel;

import java.util.ArrayList;
import java.util.HashMap;

public class AnswerSheetActivity extends ActionBarActivity {

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_sheet);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        GridView gridView = (GridView) findViewById(R.id.answer_sheet_gv);

        // 获取数据
        ArrayList<HashMap<String, Object>> userAnswerList = (ArrayList<HashMap<String, Object>>)
                getIntent().getSerializableExtra("user_answer");

        if (userAnswerList != null) {
            AnswerSheetAdapter answerSheetAdapter = new AnswerSheetAdapter(this, userAnswerList);
            gridView.setAdapter(answerSheetAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
