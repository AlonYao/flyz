package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

/**
 * 做题模块
 */

public class MeasureItemFragment extends Fragment implements View.OnClickListener{

    private static final String ARGS_QUESTION = "question";
    private static final String ARGS_POSITION = "position";
    private static final String ARGS_SIZE = "size";

    private MeasureQuestion mQuestion;
    private LinearLayout mStemContainer;
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
    private int mSize;
    private int mLastY;
    private boolean mOptionClick;

    public static MeasureItemFragment newInstance(String question, int position, int size) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTION, question);
        args.putInt(ARGS_POSITION, position);
        args.putInt(ARGS_SIZE, size);
        MeasureItemFragment fragment = new MeasureItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuestion = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTION), MeasureQuestion.class);
        mPosition = getArguments().getInt(ARGS_POSITION);
        mSize = getArguments().getInt(ARGS_SIZE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    }

    private void setOnClick() {
        mOptionAContainer.setOnClickListener(this);
        mOptionBContainer.setOnClickListener(this);
        mOptionCContainer.setOnClickListener(this);
        mOptionDContainer.setOnClickListener(this);
        mTvOptionA.setOnClickListener(this);
        mTvOptionB.setOnClickListener(this);
        mTvOptionC.setOnClickListener(this);
        mTvOptionD.setOnClickListener(this);
    }

    private void showContent() {
        if (mQuestion == null) return;
        // 处理题号
        String stem = String.valueOf(mPosition + 1) + "/" + String.valueOf(mSize) + "  ";
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

                        // 保存最终位置
                        saveFinalHeight(finalY);
                        break;

                    case MotionEvent.ACTION_UP:
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.measure_option_a_tv:
            case R.id.measure_option_b_tv:
            case R.id.measure_option_c_tv:
            case R.id.measure_option_d_tv:
                optionOnClickAction((TextView) v);
                break;

            case R.id.measure_option_a_container:
                optionOnClickAction(mTvOptionA);
                break;

            case R.id.measure_option_b_container:
                optionOnClickAction(mTvOptionB);
                break;

            case R.id.measure_option_c_container:
                optionOnClickAction(mTvOptionC);
                break;

            case R.id.measure_option_d_container:
                optionOnClickAction(mTvOptionD);
                break;
        }
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
     * @param textView 选项
     */
    public void optionOnClickAction(final TextView textView) {
        if (mOptionClick) return;

        mOptionClick = true;
        resetOption();
        textView.setSelected(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageSkip();
                mOptionClick = false;
            }
        }, 100);
    }

    /**
     * 页面跳转
     */
    private void pageSkip() {
        if (mPosition + 1 == mSize) {
            Toast.makeText(getContext(), "答题卡", Toast.LENGTH_SHORT).show();
        } else {
            if (getActivity() instanceof MeasureActivity) {
                ((MeasureActivity) getActivity()).mViewPager.setCurrentItem(mPosition + 1);
            }
        }
    }
}
