package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.appublisher.quizbank.ActivitySkipConstants;
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
        TextView tvSubmit = (TextView) findViewById(R.id.answer_sheet_submit);

        // 获取数据
        ArrayList<HashMap<String, Object>> userAnswerList = (ArrayList<HashMap<String, Object>>)
                getIntent().getSerializableExtra("user_answer");

        if (userAnswerList != null) {
            AnswerSheetAdapter answerSheetAdapter = new AnswerSheetAdapter(this, userAnswerList);
            gridView.setAdapter(answerSheetAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(AnswerSheetActivity.this, MeasureActivity.class);
                    intent.putExtra("position", position);
                    setResult(ActivitySkipConstants.ANSWER_SHEET_SKIP, intent);
                    finish();
                }
            });
        }

        // 交卷
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerSheetActivity.this, PracticeReportActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
