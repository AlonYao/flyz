package com.appublisher.quizbank.common.measure.fragment;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.model.MeasureModel;

/**
 * 做题模块
 */

public class MeasureBaseFragment extends Fragment implements MeasureConstants{

    public View mRoot;
    public MeasureQuestionBean mQuestion;
    public int mLastY;

    public void showMaterial(String material) {
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
        } else if (getActivity() instanceof MeasureAnalysisActivity) {
            return ((MeasureAnalysisActivity) getActivity())
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
        } else if (getActivity() instanceof MeasureAnalysisActivity) {
            ((MeasureAnalysisActivity) getActivity())
                    .mModel.saveFinalHeight(mQuestion.getMaterial_id(), height);
            ((MeasureAnalysisActivity) getActivity()).mAdapter.notifyDataSetChanged();
        }
    }

}
