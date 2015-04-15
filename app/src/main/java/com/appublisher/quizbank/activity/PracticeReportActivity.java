package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.netdata.measure.NoteM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习报告Activity
 */
public class PracticeReportActivity extends ActionBarActivity {

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_report);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        TextView tvPaperName = (TextView) findViewById(R.id.practice_report_name);
        TextView tvRightNum = (TextView) findViewById(R.id.practice_report_rightnum);
        TextView tvTotalNum = (TextView) findViewById(R.id.practice_report_totalnum);
        LinearLayout llCategoryContainer =
                (LinearLayout) findViewById(R.id.practice_report_category_container);
        LinearLayout llNoteNoChange =
                (LinearLayout) findViewById(R.id.practice_report_notenochange);
        LinearLayout llNoteContainer =
                (LinearLayout) findViewById(R.id.practice_report_note_container);

        // 获取数据
        String paperName = getIntent().getStringExtra("paper_name");
        int rightNum = getIntent().getIntExtra("right_num", 0);
        int totalNum = getIntent().getIntExtra("total_num", 0);
        HashMap<String, HashMap<String, Object>> categoryMap =
                (HashMap<String, HashMap<String, Object>>)
                        getIntent().getSerializableExtra("category");
        ArrayList<NoteM> notes = (ArrayList<NoteM>) getIntent().getSerializableExtra("notes");

        // 显示内容
        tvPaperName.setText(paperName);
        tvRightNum.setText(String.valueOf(rightNum));
        tvTotalNum.setText(String.valueOf(totalNum));

        // 添加科目变化
        if (categoryMap != null) {
            for (Object o : categoryMap.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String categoryName = (String) entry.getKey();
                HashMap<String, Object> map = (HashMap<String, Object>) entry.getValue();

                if (categoryName != null && map != null) {
                    View child = LayoutInflater.from(this).inflate(
                            R.layout.practice_report_category, llCategoryContainer, false);

                    TextView tvCategoryName =
                            (TextView) child.findViewById(R.id.practice_report_category);
                    TextView tvCategoryRightNum =
                            (TextView) child.findViewById(R.id.practice_report_category_rightnum);
                    TextView tvCategoryTotalNum =
                            (TextView) child.findViewById(R.id.practice_report_category_totalnum);
                    TextView tvCategoryTime =
                            (TextView) child.findViewById(R.id.practice_report_category_time);
                    ImageView ivCategoryPb =
                            (ImageView) child.findViewById(R.id.practice_report_category_pb);

                    tvCategoryName.setText(categoryName);

                    int categoryRightNum = (int) map.get("right_num");
                    int categoryTotalNum = (int) map.get("total_num");
                    int categoryDuration = (int) map.get("duration_total");

                    tvCategoryRightNum.setText(String.valueOf(categoryRightNum));
                    tvCategoryTotalNum.setText(String.valueOf(categoryTotalNum));

                    if (categoryTotalNum != 0) {
                        int pbLength = (categoryRightNum * 146) / categoryTotalNum;
                        if (pbLength == 0) pbLength++;
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.width = pbLength * 2;
                        lp.height = 10;
                        ivCategoryPb.setLayoutParams(lp);

                        int ratio = categoryDuration / categoryTotalNum;
                        tvCategoryTime.setText(String.valueOf(ratio));
                    }

                    llCategoryContainer.addView(child);
                }
            }
        }

        // 添加知识点变化
        if (notes == null) {
            llNoteNoChange.setVisibility(View.VISIBLE);
            llNoteContainer.setVisibility(View.GONE);
        } else {
            llNoteNoChange.setVisibility(View.GONE);
            llNoteContainer.setVisibility(View.VISIBLE);
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
