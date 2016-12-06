package com.appublisher.quizbank.common.measure.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_course.opencourse.activity.OpenCourseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MyAnalysisActivity;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.adapter.MeasureAnalysisAdapter;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.fragment.MeasureAnalysisSheetFragment;
import com.appublisher.quizbank.common.measure.model.MeasureAnalysisModel;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MeasureAnalysisActivity extends MeasureBaseActivity implements
        MeasureConstants, View.OnClickListener{

    private PopupWindow mPopupWindow;
    private long mPopupDismissTime;
    private int mCurPosition;
    private int mEnterLastPageCount;
    private AlertDialog mLastPageAlert;

    public int mCurQuestionId;
    public MeasureAnalysisModel mModel;
    public MeasureAnalysisAdapter mAdapter;
    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_analysis);
        setToolBar(this);
        setTitle(R.string.measure_analysis);
        initView();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        if (mModel.isCollected(mCurPosition)) {
            MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(
                    R.drawable.measure_analysis_collected), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        } else {
            MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(
                    R.drawable.measure_analysis_uncollect), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        MenuItemCompat.setShowAsAction(menu.add("反馈").setIcon(
                R.drawable.measure_analysis_feedback), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        // 判断是否显示错题
//        if (mIsFromError && mDeleteErrorQuestions != null) {
//            int size = mDeleteErrorQuestions.size();
//
//            if (size == 0) {
//                MenuItemCompat.setShowAsAction(menu.add("错题").setIcon(
//                        R.drawable.scratch_paper_clear), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
//            } else {
//                // 遍历
//                for (int i = 0; i < size; i++) {
//                    int questionId = mDeleteErrorQuestions.get(i);
//                    if (questionId != mCurQuestionId) {
//                        MenuItemCompat.setShowAsAction(menu.add("错题").setIcon(
//                                R.drawable.scratch_paper_clear),
//                                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
//                    }
//                }
//            }
//        }

        // 初始化反馈菜单
        initPopupWindowView();

        MenuItemCompat.setShowAsAction(menu.add("答题卡").setIcon(
                R.drawable.measure_icon_answersheet), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(
                menu.add("分享").setIcon(R.drawable.share),
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (mCurAnswerModel != null && mCurAnswerModel.is_collected()) {
//            menu.getItem(0).setIcon(R.drawable.measure_analysis_collected);
//        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();

        } else if ("收藏".equals(item.getTitle())) {
            if (mModel.isCollected(mCurPosition)) {
                // 如果是已收藏状态，取消收藏
                mModel.setCollected(mCurPosition, false);
                ToastManager.showToast(this, "取消收藏");

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Cancel");
                UmengManager.onEvent(this, "ReviewDetail", map);

            } else {
                // 如果是未收藏状态，收藏
                mModel.setCollected(mCurPosition, true);
                ToastManager.showToast(this, "收藏成功");

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Collect");
                UmengManager.onEvent(this, "ReviewDetail", map);
            }

        } else if ("反馈".equals(item.getTitle())) {
            View feedbackMenu = findViewById(item.getItemId());

            // 显示反馈菜单
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else if (System.currentTimeMillis() - mPopupDismissTime > 500) {
                mPopupWindow.showAsDropDown(feedbackMenu, 0, 12);
            }
        } else if ("错题".equals(item.getTitle())) {
//            AlertManager.deleteErrorQuestionAlert(this);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Delete");
            UmengManager.onEvent(this, "ReviewDetail", map);

        } else if (item.getTitle().equals("答题卡")) {
            skipToSheet();

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("AnswerSheet", "1");
            UmengManager.onEvent(this, "Review", map);

        } else if ("分享".equals(item.getTitle())) {
            /** 构造友盟分享实体 **/
            String[] content = {"检验学霸的唯一标准就是做对题目，我出一道考考你？接招吗？",
                    "发现一道比较难的题目，我可是做对了哦，你呢？",
                    "长得美的这道题都做对了，比如我~~",
                    "这道题我用时不到一分钟哦，看看你是不是比我快？"};
            int random = new Random().nextInt(content.length);
            Resources resources = getResources();
            // 获取单题分享链接
            SharedPreferences sp = getSharedPreferences("global_setting", MODE_PRIVATE);
            String spString = sp.getString("global_setting", "");
            GlobalSettingsResp globalSettingsResp =
                    GsonManager.getModel(spString, GlobalSettingsResp.class);
            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                String questionShareUrl = globalSettingsResp.getQuestion_share_url();
                String targetUrl = questionShareUrl + "question_id=" + mCurQuestionId;
                UmengManager.UMShareEntity umShareEntity = new UmengManager.UMShareEntity()
                        .setTitle(resources.getString(R.string.share_title))
                        .setText(content[random])
                        .setUmImage(UmengManager.getUMImage(this, mViewPager))
                        .setTargetUrl(targetUrl);
                UmengManager.shareAction(this, umShareEntity, "quizbank", new UmengManager.PlatformInter() {
                    @Override
                    public void platform(SHARE_MEDIA platformType) {
                        // Empty
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.measure_analysis_viewpager);
        setViewPager(mViewPager);
    }

    private void initData() {
        mModel = new MeasureAnalysisModel(this);
        mModel.mAnalysisBean = GsonManager.getModel(
                getIntent().getStringExtra(INTENT_ANALYSIS_BEAN), MeasureAnalysisBean.class);
        mModel.mIsErrorOnly = getIntent().getBooleanExtra(INTENT_ANALYSIS_IS_ERROR_ONLY, false);
        mModel.mIsFromFolder = getIntent().getBooleanExtra(INTENT_IS_FROM_FOLDER, false);
        mModel.mHierarchyId = getIntent().getIntExtra(INTENT_HIERARCHY_ID, 0);
        mModel.mPaperType = getIntent().getStringExtra(INTENT_PAPER_TYPE);
        mModel.getData();
        setModel(mModel);
    }

    public void showViewPager(List<MeasureQuestionBean> questions,
                              List<MeasureAnswerBean> answers) {
        mAdapter = new MeasureAnalysisAdapter(getSupportFragmentManager(), questions, answers);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
                // 最后一页再往后滑，弹出末题引导
                if (mCurPosition == mModel.getSize() - 1 && positionOffsetPixels == 0) {
                    if (mEnterLastPageCount >= 5) {
                        showLastPageAlert();
                        mEnterLastPageCount = 0;
                    } else {
                        mEnterLastPageCount++;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                scrollTabLayout(position);
                mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Empty
            }
        });
    }

    /**
     * 初始化popup菜单
     */
    private void initPopupWindowView() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(
                R.layout.measure_analysis_popup_feedback,
                null, false);
        mPopupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(view);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(false);
        //noinspection deprecation
        mPopupWindow.setBackgroundDrawable(
                getResources().getDrawable(R.color.transparency));

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPopupDismissTime = System.currentTimeMillis();
            }
        });

        // 菜单
        final TextView tvImageText = (TextView) view.findViewById(R.id.fb_menu_imagetext);
        final TextView tvAnswerWrong = (TextView) view.findViewById(R.id.fb_menu_answerwrong);
        final TextView tvAnalysisWrong = (TextView) view.findViewById(R.id.fb_menu_analysiswrong);
        final TextView tvBetterAnalysis = (TextView) view.findViewById(R.id.fb_menu_betteranalysis);

        tvImageText.setOnClickListener(this);
        tvAnswerWrong.setOnClickListener(this);
        tvAnalysisWrong.setOnClickListener(this);
        tvBetterAnalysis.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fb_menu_imagetext) {
            // 图文问题
            Intent intent = new Intent(this, MyAnalysisActivity.class);
            intent.putExtra("question_id", String.valueOf(mCurQuestionId));
            intent.putExtra("type", "1");
            intent.putExtra("bar_title", ((TextView) v).getText().toString());
            startActivity(intent);
            mPopupWindow.dismiss();

        } else if (v.getId() == R.id.fb_menu_answerwrong) {
            // 答案问题
            Intent intent = new Intent(this, MyAnalysisActivity.class);
            intent.putExtra("question_id", String.valueOf(mCurQuestionId));
            intent.putExtra("type", "2");
            intent.putExtra("bar_title", ((TextView) v).getText().toString());
            startActivity(intent);
            mPopupWindow.dismiss();

        } else if (v.getId() == R.id.fb_menu_analysiswrong) {
            // 解析问题
            Intent intent = new Intent(this, MyAnalysisActivity.class);
            intent.putExtra("question_id", String.valueOf(mCurQuestionId));
            intent.putExtra("type", "3");
            intent.putExtra("bar_title", ((TextView) v).getText().toString());
            startActivity(intent);
            mPopupWindow.dismiss();

        } else if (v.getId() == R.id.fb_menu_betteranalysis) {
            // 其他解析
            Intent intent = new Intent(this, MyAnalysisActivity.class);
            intent.putExtra("question_id", String.valueOf(mCurQuestionId));
            intent.putExtra("type", "4");
            intent.putExtra("bar_title", ((TextView) v).getText().toString());
            startActivity(intent);
            mPopupWindow.dismiss();
        }
    }

    /**
     * 跳转至答题卡
     */
    private void skipToSheet() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("SheetFragment");
        if (fragment != null) {
            transaction.remove(fragment);
        }
        MeasureAnalysisSheetFragment sheetFragment = new MeasureAnalysisSheetFragment();
        sheetFragment.show(transaction, "SheetFragment");
    }

    public void showLastPageAlert() {
        if (mLastPageAlert != null && mLastPageAlert.isShowing()) return;

        mLastPageAlert = new AlertDialog.Builder(this).create();
        mLastPageAlert.setCancelable(true);
        mLastPageAlert.show();

        Window window = mLastPageAlert.getWindow();
        if (window == null) return;
        window.setContentView(R.layout.alert_item_lastpage);
        window.setBackgroundDrawableResource(R.color.transparency);

        TextView tvAnother = (TextView) window.findViewById(R.id.alert_lastpage_another);
        TextView tvBack = (TextView) window.findViewById(R.id.alert_lastpage_back);
        TextView tvZhibo = (TextView) window.findViewById(R.id.alert_lastpage_zhibo);

        // 再来一发
        if (mModel.isShowAnother()) {
            tvAnother.setVisibility(View.VISIBLE);
        } else {
            tvAnother.setVisibility(View.GONE);
        }

        // 再来一发点击事件
        tvAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AUTO.equals(mModel.mPaperType)) {
                    Intent intent = new Intent(
                            MeasureAnalysisActivity.this, MeasureActivity.class);
                    intent.putExtra(INTENT_PAPER_TYPE, AUTO);
                    startActivity(intent);
                    finish();
                } else if (NOTE.equals(mModel.mPaperType)) {
                    Intent intent = new Intent(
                            MeasureAnalysisActivity.this, MeasureActivity.class);
                    intent.putExtra(INTENT_PAPER_TYPE, NOTE);
                    intent.putExtra(INTENT_HIERARCHY_ID, mModel.mHierarchyId);
                    startActivity(intent);
                    finish();
                } else {
                    mLastPageAlert.dismiss();
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Guide", "Again");
                UmengManager.onEvent(MeasureAnalysisActivity.this, "Review", map);
            }
        });

        // 返回
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Guide", "Back");
                UmengManager.onEvent(MeasureAnalysisActivity.this, "Review", map);
            }
        });

        // 看个直播
        tvZhibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        MeasureAnalysisActivity.this, OpenCourseActivity.class);
                startActivity(intent);
            }
        });
    }
}
