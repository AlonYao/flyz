package com.appublisher.quizbank.common.interview.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewMaterialDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huaxiao on 2016/12/16.
 * 用来展示未付费页面的Fragment,本类中只处理控件view,数据展示和控件点击交给InterviewUnPurchasdModel
 */

public class InterviewUnPurchasedFragment extends InterviewDetailBaseFragment {

    private View mUnPurchasedView;
    public Activity mActivity;
    public ViewPager mViewPager;
    private View merterialView;
    private View analysisSwitchView;
    private LinearLayout questionContent;
    private View analysisView;
    private TextView analysisSwitchTv;
    private ImageView analysisIm;
    private TextView reminderTv;
    private TextView analysisTv;
    private TextView noteTv;
    private TextView sourceTv;
    private TextView keywordsTv;
    private static final String ARGS_QUESTIONBEAN = "questionbean";
    private static final String ARGS_POSITION = "position";
    private static final String ARGS_LISTLENGTH = "listLength";
    private InterviewPaperDetailResp.QuestionsBean mQuestionbean;
    private int mPosition;
    private int mListLength;

    public static InterviewUnPurchasedFragment newInstance(String questionbean, int position,int listLength) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTIONBEAN, questionbean);
        args.putInt(ARGS_POSITION, position);
        args.putInt(ARGS_LISTLENGTH, listLength);
        InterviewUnPurchasedFragment fragment = new InterviewUnPurchasedFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // InterviewPaperDetailResp.QuestionsBean 对象
        mQuestionbean = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTIONBEAN), InterviewPaperDetailResp.QuestionsBean.class);
        mPosition = getArguments().getInt(ARGS_POSITION);
        mListLength = getArguments().getInt(ARGS_LISTLENGTH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mUnPurchasedView = inflater.inflate(R.layout.interview_question_item_recordsound_notpayfor,container,false);

        initView();
        initListener();
        return mUnPurchasedView;
    }

    private void initView() {

        // 材料行:逻辑显示与否根据数据集合判断
        merterialView = mUnPurchasedView.findViewById(R.id.meterial_rl);
        // 解析行:逻辑:点击事件:展开与折叠 & 是否答题的逻辑
        analysisSwitchView = mUnPurchasedView.findViewById(R.id.analysis_switch_rl);

        // 展示问题的容器
        questionContent = (LinearLayout) mUnPurchasedView.findViewById(R.id.question_content);
        //解析答案的容器
        analysisView = mUnPurchasedView.findViewById(R.id.analysis_ll);

        //解析行的左面的文字
        analysisSwitchTv = (TextView) mUnPurchasedView.findViewById(R.id.analysis_switch_tv);
        // 解析行右面的ImageView:逻辑:展开:换图片 & 折叠换图片
        analysisIm = (ImageView) mUnPurchasedView.findViewById(R.id.analysis_im);
        // 解析行右面ImageView下面的文字
        reminderTv = (TextView) mUnPurchasedView.findViewById(R.id.open_analysis);

        // 答案中的标签:解析
        analysisTv = (TextView) mUnPurchasedView.findViewById(R.id.analysis_tv);
        // 答案中的标签:知识点
        noteTv = (TextView) mUnPurchasedView.findViewById(R.id.note_tv);
        // 答案中的标签:来源
        sourceTv = (TextView) mUnPurchasedView.findViewById(R.id.source_tv);
        //答案中的标签:关键词
        keywordsTv = (TextView) mUnPurchasedView.findViewById(R.id.keywords_tv);
    }

    private void initListener() {

       if (mQuestionbean != null && mPosition < mListLength && mListLength >0 ) {
     //       final InterviewPaperDetailResp.QuestionsBean questionsBean = list.get(mPosition);  // 获取集合中的对应的索引的具体的第几道题

            //材料
            if (mQuestionbean.getMaterial() != null && !"".equals(mQuestionbean.getMaterial())) {
                merterialView.setVisibility(View.VISIBLE);
                merterialView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent(mActivity, InterviewMaterialDetailActivity.class);
                        intent.putExtra("material", mQuestionbean.getMaterial());
                        mActivity.startActivity(intent);

                        // Umeng
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Action", "Material");
                        UmengManager.onEvent(mActivity, "InterviewProblem", map);
                    }
                });
            } else {
                merterialView.setVisibility(View.GONE);
            }

            analysisView.setVisibility(View.GONE);  // 解析答案的容器默认不显示

            if ("notice".equals(mQuestionbean.getStatus())) {   //判断解析行左面的文字是"提示"还是"解析"
                analysisSwitchTv.setText("展开提示");
            } else {
                analysisSwitchTv.setText("展开解析");
            }
            /**
             *  展开解析时需要监听是否已经答题: 用一个常量字符记录(在基类中处理录音页面的逻辑时)
             * **/
            analysisSwitchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {     // 解析行的逻辑处理: 逻辑:点击事件:展开与折叠 & 是否答题的逻辑
                    if (analysisView.getVisibility() == View.VISIBLE) {    // 折叠状态
                        analysisView.setVisibility(View.GONE);
                        analysisIm.setImageResource(R.drawable.interview_answer_lookover);
                        if ("notice".equals(mQuestionbean.getStatus())) {
                            analysisSwitchTv.setText("展开提示");
                            reminderTv.setText("查看");
                            analysisTv.setVisibility(View.GONE);
                        } else {
                            analysisSwitchTv.setText("展开解析");
                            reminderTv.setText("查看");
                            analysisTv.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //   if(/**字段判断是否答题:自己处理:在基类InterviewDetailAdapter处理后用一个常量记录**/){
                        // 如果答完题状态
                        analysisView.setVisibility(View.VISIBLE);           //展开状态
                        analysisIm.setImageResource(R.drawable.interview_answer_packup);
                        if ("notice".equals(mQuestionbean.getStatus())) {
                            analysisSwitchTv.setText("收起提示");
                            reminderTv.setText("收起");
                            analysisTv.setVisibility(View.GONE);
                        } else {
                            analysisSwitchTv.setText("收起解析");
                            reminderTv.setText("收起");
                            analysisTv.setVisibility(View.VISIBLE);
                        }
//                        }else{
                        /**弹窗逻辑处理
                         *     并且需要获取当前的fragment,并再获取当前的activity,获取toolbar上的title的点击事件,
                         *       不需要:可以获取当前的fragment,然后获取当前的model,在model中预先处理好弹窗方法
                         *               主要就是刷新adapter,然后获取当前题目id,然后成功后,带上索引,进入当前索引
                         *                   在基类model中处理弹窗的item的接口回调,然后交给子类,再由子类的model中刷新adapter(带上索引)
                         * **/
//                       }
                    }
                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "Answer");
                    UmengManager.onEvent(mActivity, "InterviewProblem", map);
                }
            });

            //下面的是展示问题的文字的处理
           String rich = (mPosition + 1) + "/" + mListLength + "  " + mQuestionbean.getQuestion();
      //      String rich = (mPosition + 1) + "/" + "4" + "  " + mQuestionbean.getQuestion();
            addRichTextToContainer((Activity) mActivity, questionContent, rich, true);

            //下面是展示答案的文字的处理
            SpannableString analysis = new SpannableString("【解析】" + mQuestionbean.getAnalysis());
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.themecolor));
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Utils.sp2px(mActivity, 15));
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

            //解析
            analysis.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysis.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysis.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysisTv.setText(analysis);

            //知识点
            SpannableString note = new SpannableString("【知识点】" + mQuestionbean.getNotes());
            note.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            note.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            note.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noteTv.setText(note);

            //来源
            SpannableString source = new SpannableString("【来源】" + mQuestionbean.getFrom());
            source.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sourceTv.setText(source);

            //关键词
            SpannableString keywords = new SpannableString("【关键词】" + mQuestionbean.getKeywords());
            keywords.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywords.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywords.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywordsTv.setText(keywords);

        }
    }
    /**
     * 动态添加富文本
     *
     * @param activity  Activity
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    public static void addRichTextToContainer(final Activity activity,
                                              LinearLayout container,
                                              String rich,
                                              boolean textClick) {
        if (rich == null || rich.length() <= 0) return;

        Request request = new Request(activity);

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(activity);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(activity);
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
                TextView textView = new TextView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(activity.getResources().getColor(R.color.common_text));
                textView.setText(segment.text);
                flowLayout.addView(textView);

                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }

            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);

                imgView.setImageResource(R.drawable.measure_loading_img);

                flowLayout.addView(imgView);

                // 异步加载图片
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                final float minHeight = (float) ((dm.heightPixels - 50) * 0.05); // 50是状态栏高度

                ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap data = imageContainer.getBitmap();

                        if (data == null) return;

                        // 对小于指定尺寸的图片进行放大(2倍)
                        int width = data.getWidth();
                        int height = data.getHeight();
                        if (height < minHeight) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(2.0f, 2.0f);
                            data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
                        }

                        imgView.setImageBitmap(data);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                };

                request.loadImage(segment.text.toString(), imageListener);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(activity, ScaleImageActivity.class);
                        intent.putExtra("imgUrl", segment.text.toString());
                        activity.startActivity(intent);
                    }
                });
            }
        }

        container.addView(flowLayout);
    }

}
