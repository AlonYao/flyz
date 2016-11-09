package com.appublisher.quizbank.common.measure;

import android.content.Context;
import android.content.Intent;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.netdata.MeasureAutoResp;
import com.appublisher.quizbank.common.measure.netdata.MeasureEntireResp;
import com.appublisher.quizbank.common.measure.netdata.MeasureNotesResp;
import com.appublisher.quizbank.common.measure.network.MeasureRequest;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：管理类
 */

public class MeasureModel implements RequestCallback, MeasureConstants{

    private Context mContext;
    private MeasureRequest mRequest;
    private SparseIntArray mFinalHeightMap;
    private List<MeasureTabBean> mTabs;
    public String mPaperType;
    public int mPaperId;
    public int mHierarchyId;

    MeasureModel(Context context) {
        mContext = context;
        mRequest = new MeasureRequest(context, this);
    }

    public int getFinalHeightById(int id) {
        if (mFinalHeightMap == null) return 0;
        return mFinalHeightMap.get(id);
    }

    public void saveFinalHeight(int id, int finalHeight) {
        if (mFinalHeightMap == null) mFinalHeightMap = new SparseIntArray();
        mFinalHeightMap.put(id, finalHeight);
    }

    public void getData() {
        if (AUTO.equals(mPaperType)) {
            mRequest.getAutoTraining();
        } else if (NOTE.equals(mPaperType)) {
            mRequest.getNoteQuestions(mHierarchyId, "note");
        } else if (ENTIRE.equals(mPaperType)) {
            mRequest.getPaperExercise(mPaperId, mPaperType);
        }
    }

    private void hideLoading() {
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }

    /**
     * 处理快速智能练习
     * @param response JSONObject
     */
    private void dealAutoTrainingResp(JSONObject response) {
        MeasureAutoResp resp = GsonManager.getModel(response, MeasureAutoResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        if (!(mContext instanceof MeasureActivity)) return;
        ((MeasureActivity) mContext).showViewPager(resp.getQuestions());
    }

    /**
     * 处理知识点专项训练
     * @param response JSONObject
     */
    private void dealNoteQuestionsResp(JSONObject response) {
        MeasureNotesResp resp = GsonManager.getModel(response, MeasureNotesResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        if (!(mContext instanceof MeasureActivity)) return;
        ((MeasureActivity) mContext).showViewPager(resp.getQuestions());
    }

    /**
     * 处理每日模考&整卷
     * @param response JSONObject
     */
    private void dealPaperExerciseResp(JSONObject response) {
        MeasureEntireResp resp = GsonManager.getModel(response, MeasureEntireResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        List<MeasureEntireResp.CategoryBean> categorys = resp.getCategory();
        if (categorys == null) return;

        // 构造数据结构
        mTabs = new ArrayList<>();
        List<MeasureQuestion> questions = new ArrayList<>();

        // 遍历
        int size = categorys.size();
        for (int i = 0; i < size; i++) {
            MeasureEntireResp.CategoryBean category = categorys.get(i);
            if (category == null) continue;
            List<MeasureQuestion> categoryQuestions = category.getQuestions();
            if (categoryQuestions == null) continue;
            // 添加Tab数据
            MeasureTabBean tabBean = new MeasureTabBean();
            tabBean.setName(category.getName());
            tabBean.setPosition(questions.size());
            mTabs.add(tabBean);
            // 添加题目数据，构造说明页
            MeasureQuestion question = new MeasureQuestion();
            question.setIs_desc(true);
            question.setCategory_name(category.getName());
            question.setDesc_position(i);
            // 添加至题目list
            questions.add(question);
            questions.addAll(categoryQuestions);
        }

        if (!(mContext instanceof MeasureActivity)) return;
        ((MeasureActivity) mContext).showTabLayout(mTabs);
        ((MeasureActivity) mContext).showViewPager(questions);
    }

    /**
     * 动态添加富文本
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    @SuppressWarnings("deprecation")
    public static void addRichTextToContainer(final Context context,
                                              LinearLayout container,
                                              String rich,
                                              boolean textClick) {
        if (rich == null || rich.length() <= 0) return;

//        Request request = new Request(activity);

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(context);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(context);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        flowLayout.setLayoutParams(params);
        flowLayout.setGravity(Gravity.CENTER_VERTICAL);

        for (final ParseManager.ParsedSegment segment : segments) {
            if (segment.text == null || segment.text.length() == 0) {
                continue;
            }

            if (MatchInfo.MatchType.None == segment.type) {
                TextView textView = new TextView(context);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(context.getResources().getColor(R.color.common_text));
                textView.setText(segment.text);
                flowLayout.addView(textView);

                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }

            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(context);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);

                imgView.setImageResource(R.drawable.measure_loading_img);

                flowLayout.addView(imgView);

                // 异步加载图片
//                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
//                final float minHeight = (float) ((dm.heightPixels - 50) * 0.05); // 50是状态栏高度
//
//                ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
//                    @Override
//                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                        Bitmap data = imageContainer.getBitmap();
//
//                        if (data == null) return;
//
//                        // 对小于指定尺寸的图片进行放大(2倍)
//                        int width = data.getWidth();
//                        int height = data.getHeight();
//                        if (height < minHeight) {
//                            Matrix matrix = new Matrix();
//                            matrix.postScale(2.0f, 2.0f);
//                            data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
//                        }
//
//                        imgView.setImageBitmap(data);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                };
//
//                request.loadImage(segment.text.toString(), imageListener);

                ImageManager.displayImage(segment.text.toString(), imgView);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, ScaleImageActivity.class);
                        intent.putExtra(ScaleImageActivity.INTENT_IMGURL, segment.text.toString());
                        context.startActivity(intent);
                    }
                });
            }
        }

        container.addView(flowLayout);
    }

    public int getTabPositionScrollTo(int curPosition) {
        if (mTabs == null) return 0;
        int size = mTabs.size();
        int tab = 0;
        for (int i = 0; i < size; i++) {
            MeasureTabBean tabBean = mTabs.get(i);
            if (tabBean == null) continue;
            if (curPosition >= tabBean.getPosition()) tab = i;
        }
        return tab;
    }

    public int getPositionByTab(int tabPosition) {
        if (mTabs == null || tabPosition >= mTabs.size()) return 0;
        MeasureTabBean tabBean = mTabs.get(tabPosition);
        if (tabBean == null) return 0;
        return tabBean.getPosition();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (AUTO_TRAINING.equals(apiName)) {
            dealAutoTrainingResp(response);
        } else if (NOTE_QUESTIONS.equals(apiName)) {
            dealNoteQuestionsResp(response);
        } else if (PAPER_EXERCISE.equals(apiName)) {
            dealPaperExerciseResp(response);
        }
        hideLoading();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

}
