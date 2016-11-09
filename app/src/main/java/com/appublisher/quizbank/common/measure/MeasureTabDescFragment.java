package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appublisher.quizbank.R;

/**
 * 做题模块：Tab说明页
 */

public class MeasureTabDescFragment extends Fragment{

    private static final String ARGS_TAB_NAME = "tab_name";
    private static final String ARGS_TAB_POSITION = "tab_position";

    private String mTabName;
    private int mTabPosition;

    public static MeasureTabDescFragment newInstance(String tabName, int tabPosition) {
        Bundle args = new Bundle();
        args.putString(ARGS_TAB_NAME, tabName);
        args.putInt(ARGS_TAB_POSITION, tabPosition);
        MeasureTabDescFragment fragment = new MeasureTabDescFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabName = getArguments().getString(ARGS_TAB_NAME);
        mTabPosition = getArguments().getInt(ARGS_TAB_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.measure_tab_desc, container, false);
        TextView tvPosition = (TextView) root.findViewById(R.id.measure_tab_desc_position);
        TextView tvName = (TextView) root.findViewById(R.id.measure_tab_desc_name);
        TextView tvDesc = (TextView) root.findViewById(R.id.measure_tab_desc);

        showPosition(tvPosition);
        tvName.setText(mTabName);
        showDesc(tvDesc);

        return root;
    }

    private void showDesc(TextView textView) {
        if (textView == null) return;
        if ("常识判断".equals(mTabName)) {
            textView.setText(getResources().getString(R.string.measure_desc_changshi));
        } else if ("言语理解与表达".equals(mTabName)) {
            textView.setText(getResources().getString(R.string.measure_desc_yanyu));
        } else if ("数量关系".equals(mTabName)) {
            textView.setText(getResources().getString(R.string.measure_desc_shuliang));
        } else if ("判断推理".equals(mTabName)) {
            textView.setText(getResources().getString(R.string.measure_desc_panduan));
        } else if ("资料分析".equals(mTabName)) {
            textView.setText(getResources().getString(R.string.measure_desc_ziliao));
        }
    }

    private void showPosition(TextView textView) {
        if (textView == null) return;
        String text = "";
        switch (mTabPosition) {
            case 0:
                text = "第一部分";
                break;

            case 1:
                text = "第二部分";
                break;

            case 2:
                text = "第三部分";
                break;

            case 3:
                text = "第四部分";
                break;

            case 4:
                text = "第五部分";
                break;
        }
        textView.setText(text);
    }

}
