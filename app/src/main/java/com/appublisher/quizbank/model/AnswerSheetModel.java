package com.appublisher.quizbank.model;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.adapter.EntireAnswerSheetAdapter;
import com.appublisher.quizbank.customui.ExpandableHeightGridView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * AnswerSheet Activity Model
 */
public class AnswerSheetModel {

    /**
     * 设置整卷答题卡
     * @param activity AnswerSheetActivity
     */
    @SuppressWarnings({"unchecked", "Annotator"})
    public static void setEntireContent(final AnswerSheetActivity activity) {
        if (activity.mUserAnswerList == null || activity.mUserAnswerList.size() == 0) return;

        HashMap<String, Integer> entirePaperCategory =
                (HashMap<String, Integer>) activity.getIntent().getSerializableExtra("category");

        if (entirePaperCategory == null) return;

        int offset = 0;
        int categoryNum = 1;
        for (Object o : entirePaperCategory.entrySet()) {
            HashMap.Entry entry = (HashMap.Entry) o;
            String categoryName = (String) entry.getKey();
            int categorySize = (int) entry.getValue();

            View categoryChildView =
                    LayoutInflater.from(activity).inflate(
                            R.layout.answer_sheet_entire_item,
                            activity.mLlEntireContainer,
                            false);

            TextView tvName =
                    (TextView) categoryChildView.findViewById(R.id.answer_sheet_entire_item_tv);
            ExpandableHeightGridView ehGridView =
                    (ExpandableHeightGridView)
                            categoryChildView.findViewById(R.id.answer_sheet_entire_item_gv);

            tvName.setText("第" + String.valueOf(categoryNum) + "部分  " + categoryName);

            // 构造分类的题目
            ArrayList<HashMap<String, Object>> categoryUserAnswer = new ArrayList<>();

            for (int i = offset; i < offset + categorySize; i++) {
                categoryUserAnswer.add(activity.mUserAnswerList.get(i));
            }

            EntireAnswerSheetAdapter entireAnswerSheetAdapter =
                    new EntireAnswerSheetAdapter(activity, categoryUserAnswer, offset);
            ehGridView.setAdapter(entireAnswerSheetAdapter);

            activity.mLlEntireContainer.addView(categoryChildView);

            offset = offset + categorySize;
            categoryNum++;
        }
    }
}
