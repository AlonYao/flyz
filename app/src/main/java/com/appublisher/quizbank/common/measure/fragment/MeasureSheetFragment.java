package com.appublisher.quizbank.common.measure.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.customui.ExpandableHeightGridView;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.adapter.MeasureSheetAdapter;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureSubmitBean;
import com.appublisher.quizbank.common.measure.model.MeasureModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：答题纸
 */

public class MeasureSheetFragment extends DialogFragment implements
        View.OnClickListener, MeasureConstants{

    private static final String ARGS_POSITION = "position";

    private List<MeasureQuestionBean> mQuestions;
    private List<MeasureSubmitBean> mSubmits;
    private LinearLayout mContainer;
    private Button mBtnSubmit;
    private int mPosition; // Viewpager中的position

    public static MeasureSheetFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARGS_POSITION, position);
        MeasureSheetFragment fragment = new MeasureSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏处理
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Measure_Dialog_FullScreen);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.measure_sheet_fragment, container);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.themecolor));
        toolbar.setNavigationIcon(R.drawable.scratch_paper_exit);
        toolbar.setTitle(R.string.measure_sheet_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mContainer = (LinearLayout) view.findViewById(R.id.measure_sheet_container);
        mBtnSubmit = (Button) view.findViewById(R.id.measure_sheet_submit);
        mBtnSubmit.setOnClickListener(this);

        showContent();
        return view;
    }

    @Override
    public void onResume() {
        // 全屏处理
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        super.onResume();
    }

    private void initData() {
        mPosition = getArguments().getInt(ARGS_POSITION);
        mSubmits = MeasureModel.getUserAnswerCache(getActivity());
        if (getActivity() instanceof MeasureActivity) {
            mQuestions = ((MeasureActivity) getActivity()).mModel.getAdapterQuestions();
        }
    }

    private void showContent() {
        if (isEntirePaper()) {
            showEntire();
        } else {
            showCommon();
        }
    }

    private void showEntire() {
        if (mQuestions == null) return;
        List<MeasureQuestionBean> questionsForAdapter = new ArrayList<>();
        int size = mQuestions.size();

        // 遍历
        for (int i = 0; i < size; i++) {
            MeasureQuestionBean questionBean = mQuestions.get(i);
            if (questionBean == null) continue;
            if (questionBean.is_desc()) {
                if (questionsForAdapter.size() != 0) {
                    // 添加GridView
                    String name = questionsForAdapter.get(0).getCategory_name();
                    addGridViewItem(name, questionsForAdapter);
                    questionsForAdapter = new ArrayList<>();
                }
            } else {
                questionsForAdapter.add(questionBean);
                if (i == size - 1) {
                    String name = questionsForAdapter.get(0).getCategory_name();
                    addGridViewItem(name, questionsForAdapter);
                }
            }
        }
    }

    private void showCommon() {
        addGridViewItem("", mQuestions);
    }

    private void addGridViewItem(String category, List<MeasureQuestionBean> questions) {
        if (questions == null) return;

        // init view
        View child = LayoutInflater.from(getActivity()).inflate(
                R.layout.measure_sheet_item, mContainer, false);
        TextView tvCategory = (TextView) child.findViewById(R.id.measure_sheet_item_tv);
        ExpandableHeightGridView gridView =
                (ExpandableHeightGridView)
                        child.findViewById(R.id.measure_sheet_item_gv);

        tvCategory.setText(category == null ? "" : category);

        MeasureSheetAdapter adapter = new MeasureSheetAdapter(this, questions, mSubmits);
        gridView.setAdapter(adapter);

        mContainer.addView(child);
    }

    private boolean isEntirePaper() {
        // 判断依据：是否有说明页
        if (mQuestions == null || mQuestions.size() == 0) return false;
        MeasureQuestionBean questionBean = mQuestions.get(0);
        return questionBean != null && questionBean.is_desc();
    }

    public void skip(int index) {
        if (!(getActivity() instanceof MeasureActivity)) return;
        ((MeasureActivity) getActivity()).mViewPager.setCurrentItem(index);
        dismiss();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.measure_sheet_submit) {
            // 提交
            if (!(getActivity() instanceof MeasureActivity)) return;
            final MeasureModel model = ((MeasureActivity) getActivity()).mModel;
            if (model == null) return;

            // 提交
            if (MOCK.equals(model.mPaperType)) {
                // 模考
                ((MeasureActivity) getActivity()).showLoading();
                model.getServerTimeStamp(new MeasureModel.ServerTimeListener() {
                    @Override
                    public void onTimeOut() {
                        model.submitPaperDone();
                    }

                    @Override
                    public void canSubmit() {
                        checkIsAllDone();
                    }
                });
            } else if (VIP.equals(model.mPaperType)) {
                // 小班
                model.submitVipXCPaper();
            } else {
                // 其他
                checkIsAllDone();
            }
        }
    }

    private void checkIsAllDone() {
        if (!(getActivity() instanceof MeasureActivity)) return;
        final MeasureModel model = ((MeasureActivity) getActivity()).mModel;
        if (model == null) return;

        if (model.isAllDone()) {
            model.submitPaperDone();
        } else {
            ((MeasureActivity) getActivity()).showMeasureSheetUndoneAlert();
        }
    }
}
