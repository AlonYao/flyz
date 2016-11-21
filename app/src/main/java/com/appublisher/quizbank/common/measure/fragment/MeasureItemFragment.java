package com.appublisher.quizbank.common.measure.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureModel;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureExcludeBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

/**
 * 做题模块
 */

public class MeasureItemFragment extends Fragment implements
        View.OnClickListener, View.OnLongClickListener{

    private static final String ARGS_QUESTION = "question";
    private static final String ARGS_POSITION = "position";
    private static final String OPTION_A = "A";
    private static final String OPTION_B = "B";
    private static final String OPTION_C = "C";
    private static final String OPTION_D = "D";

    private MeasureQuestionBean mQuestion;
    private LinearLayout mStemContainer;
    private LinearLayout mOptionA;
    private LinearLayout mOptionB;
    private LinearLayout mOptionC;
    private LinearLayout mOptionD;
    private LinearLayout mOptionAContainer;
    private LinearLayout mOptionBContainer;
    private LinearLayout mOptionCContainer;
    private LinearLayout mOptionDContainer;
    private TextView mTvOptionA;
    private TextView mTvOptionB;
    private TextView mTvOptionC;
    private TextView mTvOptionD;
    private View mRoot;
    private int mPosition;
    private int mLastY;
    private boolean mOptionClick;

    public static MeasureItemFragment newInstance(String question, int position) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTION, question);
        args.putInt(ARGS_POSITION, position);
        MeasureItemFragment fragment = new MeasureItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuestion = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTION), MeasureQuestionBean.class);
        mPosition = getArguments().getInt(ARGS_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.measure_item, container, false);
        initView();
        setOnClick();
        showContent();
        return mRoot;
    }

    private void initView() {
        mStemContainer = (LinearLayout) mRoot.findViewById(R.id.measure_stem_container);
        mOptionAContainer = (LinearLayout) mRoot.findViewById(
                R.id.measure_option_a_container);
        mOptionBContainer = (LinearLayout) mRoot.findViewById(
                R.id.measure_option_b_container);
        mOptionCContainer = (LinearLayout) mRoot.findViewById(
                R.id.measure_option_c_container);
        mOptionDContainer = (LinearLayout) mRoot.findViewById(
                R.id.measure_option_d_container);
        mTvOptionA = (TextView) mRoot.findViewById(R.id.measure_option_a_tv);
        mTvOptionB = (TextView) mRoot.findViewById(R.id.measure_option_b_tv);
        mTvOptionC = (TextView) mRoot.findViewById(R.id.measure_option_c_tv);
        mTvOptionD = (TextView) mRoot.findViewById(R.id.measure_option_d_tv);
        mOptionA = (LinearLayout) mRoot.findViewById(R.id.measure_option_a);
        mOptionB = (LinearLayout) mRoot.findViewById(R.id.measure_option_b);
        mOptionC = (LinearLayout) mRoot.findViewById(R.id.measure_option_c);
        mOptionD = (LinearLayout) mRoot.findViewById(R.id.measure_option_d);
    }

    private void setOnClick() {
        mOptionA.setOnClickListener(this);
        mOptionB.setOnClickListener(this);
        mOptionC.setOnClickListener(this);
        mOptionD.setOnClickListener(this);

        mOptionA.setOnLongClickListener(this);
        mOptionB.setOnLongClickListener(this);
        mOptionC.setOnLongClickListener(this);
        mOptionD.setOnLongClickListener(this);
    }

    private void showContent() {
        if (mQuestion == null) return;
        // 处理题号
        String stem =
                String.valueOf(mQuestion.getQuestion_order())
                + "/" + String.valueOf(mQuestion.getQuestion_amount()) + "  ";
        stem = stem + mQuestion.getQuestion();
        MeasureModel.addRichTextToContainer(getContext(), mStemContainer, stem, true);
        // 选项
        MeasureModel.addRichTextToContainer(
                getContext(), mOptionAContainer, mQuestion.getOption_a(), false);
        MeasureModel.addRichTextToContainer(
                getContext(), mOptionBContainer, mQuestion.getOption_b(), false);
        MeasureModel.addRichTextToContainer(
                getContext(), mOptionCContainer, mQuestion.getOption_c(), false);
        MeasureModel.addRichTextToContainer(
                getContext(), mOptionDContainer, mQuestion.getOption_d(), false);
        // 材料
        showMaterial(mQuestion.getMaterial());
    }

    private void showMaterial(String material) {
        if (material == null || material.length() == 0) return;
        ViewStub vsMaterial = (ViewStub) mRoot.findViewById(R.id.measure_material_viewstub);
        vsMaterial.inflate();
        ViewStub vsDivider = (ViewStub) mRoot.findViewById(R.id.measure_divider_viewstub);
        vsDivider.inflate();
        // 显示材料
        final ScrollView svTop = (ScrollView) mRoot.findViewById(R.id.measure_top);
        ImageView ivPull = (ImageView) mRoot.findViewById(R.id.measure_iv);
        LinearLayout mMaterialContainer = (LinearLayout) mRoot.findViewById(R.id.measure_material);
        MeasureModel.addRichTextToContainer(getContext(), mMaterialContainer, material, true);

        // 更新上部分ScrollView的高度
        int finalHeight = getFinalHeight();
        if (finalHeight != 0) {
            changeSvHeight(svTop, finalHeight);
        }

        ivPull.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mLastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int dy = (int) event.getRawY() - mLastY;

                        // 边界溢出处理
                        int finalY = svTop.getHeight() + dy;
                        if (finalY <= v.getHeight()) break;

                        // 改变上部分ScrollView的高度
                        changeSvHeight(svTop, finalY);
                        mLastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        // 保存最终位置
                        saveFinalHeight(svTop.getHeight());
                        break;
                }

                return false;
            }
        });
    }

    private void changeSvHeight(ScrollView sv, int height) {
        ViewGroup.LayoutParams layoutParams = sv.getLayoutParams();
        layoutParams.height = height;
        sv.setLayoutParams(layoutParams);
    }

    private int getFinalHeight() {
        if (mQuestion == null) return 0;
        if (getActivity() instanceof MeasureActivity) {
            return ((MeasureActivity) getActivity())
                    .mModel.getFinalHeightById(mQuestion.getMaterial_id());
        }
        return 0;
    }

    private void saveFinalHeight(int height) {
        if (mQuestion == null) return;
        if (getActivity() instanceof MeasureActivity) {
            ((MeasureActivity) getActivity())
                    .mModel.saveFinalHeight(mQuestion.getMaterial_id(), height);
            ((MeasureActivity) getActivity()).mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.measure_option_a:
                if (isOptionExclude(OPTION_A)) {
                    changeExcludeStatus(OPTION_A, false);
                } else {
                    optionOnClickAction(OPTION_A);
                }
                break;

            case R.id.measure_option_b:
                if (isOptionExclude(OPTION_B)) {
                    changeExcludeStatus(OPTION_B, false);
                } else {
                    optionOnClickAction(OPTION_B);
                }
                break;

            case R.id.measure_option_c:
                if (isOptionExclude(OPTION_C)) {
                    changeExcludeStatus(OPTION_C, false);
                } else {
                    optionOnClickAction(OPTION_C);
                }
                break;

            case R.id.measure_option_d:
                if (isOptionExclude(OPTION_D)) {
                    changeExcludeStatus(OPTION_D, false);
                } else {
                    optionOnClickAction(OPTION_D);
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.measure_option_a:
                if (isOptionExclude(OPTION_A)) {
                    changeExcludeStatus(OPTION_A, false);
                } else {
                    changeExcludeStatus(OPTION_A, true);
                }
                break;

            case R.id.measure_option_b:
                if (isOptionExclude(OPTION_B)) {
                    changeExcludeStatus(OPTION_B, false);
                } else {
                    changeExcludeStatus(OPTION_B, true);
                }
                break;

            case R.id.measure_option_c:
                if (isOptionExclude(OPTION_C)) {
                    changeExcludeStatus(OPTION_C, false);
                } else {
                    changeExcludeStatus(OPTION_C, true);
                }
                break;

            case R.id.measure_option_d:
                if (isOptionExclude(OPTION_D)) {
                    changeExcludeStatus(OPTION_D, false);
                } else {
                    changeExcludeStatus(OPTION_D, true);
                }
                break;
        }
        return true;
    }

    private void changeExcludeStatus(String option, boolean isExclude) {
        TextView tvParent = null;
        LinearLayout llParent = null;
        if (OPTION_A.equals(option)) {
            tvParent = mTvOptionA;
            llParent = mOptionAContainer;
        } else if (OPTION_B.equals(option)) {
            tvParent = mTvOptionB;
            llParent = mOptionBContainer;
        } else if (OPTION_C.equals(option)) {
            tvParent = mTvOptionC;
            llParent = mOptionCContainer;
        } else if (OPTION_D.equals(option)) {
            tvParent = mTvOptionD;
            llParent = mOptionDContainer;
        }

        if (tvParent != null) {
            if (isExclude) {
                tvParent.setTextColor(ContextCompat.getColor(
                        getContext(), R.color.measure_exclude));
                tvParent.setBackgroundResource(R.drawable.measure_exclude_bg);
            } else {
                tvParent.setBackgroundResource(R.drawable.measure_option_bg_selector);
                tvParent.setTextColor(ContextCompat.getColorStateList(
                        getContext(), R.drawable.measure_option_text_selector));
                tvParent.setSelected(false);
            }
        }

        if (llParent != null) {
            int sizeFlowLayout = llParent.getChildCount();
            for (int i = 0; i < sizeFlowLayout; i++) {
                View flowLayout = llParent.getChildAt(i);
                if (flowLayout == null || (!(flowLayout instanceof FlowLayout))) continue;

                int sizeTextView = ((FlowLayout) flowLayout).getChildCount();
                for (int j = 0; j < sizeTextView; j++) {
                    View textView = ((FlowLayout) flowLayout).getChildAt(j);
                    if (textView == null || (!(textView instanceof TextView))) continue;
                    if (isExclude) {
                        ((TextView) textView).setTextColor(
                                ContextCompat.getColor(getContext(), R.color.measure_exclude));
                    } else {
                        ((TextView) textView).setTextColor(
                                ContextCompat.getColor(getContext(), R.color.measure_text));
                    }
                }
            }
        }

        saveExclude(option, isExclude);
    }

    private boolean isOptionExclude(String option) {
        // 异常处理部分
        if (!(getActivity() instanceof MeasureActivity)) return false;
        List<MeasureExcludeBean> list = ((MeasureActivity) getActivity()).mModel.mExcludes;
        if (list == null || mQuestion == null) return false;
        int order = mQuestion.getQuestion_order();
        if (order >= list.size()) return false;
        MeasureExcludeBean bean = list.get(order);
        if (bean == null) return false;

        if (OPTION_A.equals(option)) {
            return bean.isExclude_a();
        } else if (OPTION_B.equals(option)) {
            return bean.isExclude_b();
        } else if (OPTION_C.equals(option)) {
            return bean.isExclude_c();
        } else if (OPTION_D.equals(option)) {
            return bean.isExclude_d();
        }

        return false;
    }

    private void saveExclude(String option, boolean isExclude) {
        // 异常处理部分
        if (!(getActivity() instanceof MeasureActivity)) return;
        List<MeasureExcludeBean> list = ((MeasureActivity) getActivity()).mModel.mExcludes;
        if (list == null || mQuestion == null) return;
        int order = mQuestion.getQuestion_order();
        if (order >= list.size()) return;
        MeasureExcludeBean bean = list.get(order);
        if (bean == null) return;

        if (OPTION_A.equals(option)) {
            bean.setExclude_a(isExclude);
        } else if (OPTION_B.equals(option)) {
            bean.setExclude_b(isExclude);
        } else if (OPTION_C.equals(option)) {
            bean.setExclude_c(isExclude);
        } else if (OPTION_D.equals(option)) {
            bean.setExclude_d(isExclude);
        }

        list.set(order, bean);
        ((MeasureActivity) getActivity()).mModel.mExcludes = list;
    }

    /**
     * 重置按钮状态
     */
    private void resetOption() {
        mTvOptionA.setSelected(false);
        mTvOptionB.setSelected(false);
        mTvOptionC.setSelected(false);
        mTvOptionD.setSelected(false);
    }

    /**
     * 选项点击动作
     * @param option 选项
     */
    public void optionOnClickAction(String option) {
        if (mOptionClick) return;

        mOptionClick = true;
        resetOption();

        TextView textView = null;
        if (OPTION_A.equals(option)) {
            textView = mTvOptionA;
        } else if (OPTION_B.equals(option)) {
            textView = mTvOptionB;
        } else if (OPTION_C.equals(option)) {
            textView = mTvOptionC;
        } else if (OPTION_D.equals(option)) {
            textView = mTvOptionD;
        }
        if (textView == null) return;
        textView.setSelected(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageSkip();
                mOptionClick = false;
            }
        }, 100);

        // 保存用户做题记录
        saveUserAnswer(option);
    }

    private void saveUserAnswer(String option) {
        if (mQuestion == null || option == null) return;
        int isRight = 0;
        if (option.equals(mQuestion.getAnswer())) {
            isRight = 1;
        }
        MeasureModel.saveSubmitAnswer(
                getContext(), mQuestion.getQuestion_order() - 1, option, isRight);
    }

    /**
     * 页面跳转
     */
    private void pageSkip() {
        if (mQuestion.getQuestion_order() == mQuestion.getQuestion_amount()) {
            Toast.makeText(getContext(), "答题卡", Toast.LENGTH_SHORT).show();
        } else {
            if (getActivity() instanceof MeasureActivity) {
                ((MeasureActivity) getActivity()).mViewPager.setCurrentItem(mPosition + 1);
            }
        }
    }

}
