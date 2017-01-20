package com.appublisher.quizbank.common.interview.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.InterviewDetailAdapter;
import com.appublisher.quizbank.common.interview.model.InterviewDetailModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.common.interview.viewgroup.ScrollExtendViewPager;
import com.appublisher.quizbank.common.utils.MediaRecorderManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class InterviewPaperDetailActivity extends BaseActivity implements RequestCallback {

    private static final int UNRECORD = 0;
    private static final int RECORDING = 1;
    private static final int RECORDEDUNSBMIT = 2;
    private static final int TEACHERREMARK = 3;
    private int mPaperId;
    public InterviewRequest mRequest;
    public ScrollExtendViewPager mViewPager;
    public InterviewDetailAdapter mAdaper;
    private int mWhatView;
    private String mPaperType;
    private int mNoteId;
    private int mCurrentPagerId;   // 当前的viewPager的索引
    private String mFrom;
    public List<InterviewPaperDetailResp.QuestionsBean> mList;
    private boolean mIsBuyAll = false;
    private InterviewPaperDetailResp.AllAudioBean mAllAudioBean;
    private InterviewPaperDetailResp.SingleAudioBean mSingleAudioBean;
    public InterviewDetailModel mModel;
    public MediaRecorderManager mMediaRecorderManager;
    private boolean isTeacherRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_paper_detail);

        setToolBar(this);
        setTitle("");
        mFrom = getIntent().getStringExtra("from");          // 来源:问题的类型
        mPaperId = getIntent().getIntExtra("paper_id", 0);
        mPaperType = getIntent().getStringExtra("paper_type");
        mNoteId = getIntent().getIntExtra("note_id", 0);

        String type = getIntent().getStringExtra("itemType");      // item类型
        String time = getIntent().getStringExtra("time");          // 时间

        // 所有fragment中用同一个录音器
        mMediaRecorderManager = new MediaRecorderManager(getApplicationContext());

        mViewPager = (ScrollExtendViewPager) findViewById(R.id.viewpager);   //自定义的viewpager

        if(mViewPager == null ) return;
        mViewPager.setScroll(true);
        initListener(mViewPager);

        mModel = new InterviewDetailModel(this);
        mRequest = new InterviewRequest(this, this);

        String dataFrom = getIntent().getStringExtra("dataFrom");

        if("studyRecordInterview".equals(dataFrom)){       // 数据来源自记录页面的面试页面
            mRequest.getStudyRecordInterviewPaperDetail(type, time);
        }else if("recordCollect".equals(dataFrom)){        // 来源: 记录页面的收藏页面
            int note_id = getIntent().getIntExtra("note_id", 0);
            mRequest.getRecordInterviewCollectPaperDetail(note_id);
        } else{
            mRequest.getPaperDetail(mPaperId, mPaperType, mNoteId); // 请求数据
        }
        showLoading();
    }

    public void setCanBack(int view) {
        mWhatView = view;
    }
    public void getData() {
        mRequest.getPaperDetail(mPaperId, mPaperType, mNoteId);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mList == null || mCurrentPagerId < 0 ) {
            mCurrentPagerId = 0;
        }
        if(isTeacherRemark){            // 是否为名师引导页
            setDisplayHomeAsUpEnabled(this, false);
            // 名师点评引导页
            MenuItemCompat.setShowAsAction(
                    menu.add("关闭"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }else{
            if(mModel.getIsAnswer( mCurrentPagerId)){  // 判断是否回答
                if(mModel.getIsCollected( mCurrentPagerId)){
                    MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_collected),
                            MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
                }else{
                    MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_uncollect),
                            MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
                }
            }
        }

        // 购买状态
        if (!mIsBuyAll) {
            MenuItemCompat.setShowAsAction(
                    menu.add("开启完整版"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 通过fragment中穿件来的id判断具体是录音页面哪个状态:进行分别处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mWhatView == RECORDING) {
                return true;
            } else if (mWhatView == RECORDEDUNSBMIT) {
                InterviewDetailModel.showBackPressedDailog(this);   // 显示退出dailog
                return true;
            }
        } else if ("开启完整版".equals(item.getTitle())) {
            if (mWhatView == RECORDING || mWhatView == RECORDEDUNSBMIT) {
                return true;
            } else if (mWhatView == UNRECORD) {
                mModel.showOpenFullDialog();
            }
        } else if("收藏".equals(item.getTitle())){
            if (mModel.getIsCollected(mCurrentPagerId)) {   // 判断当前viewpager的小题是否收藏

                mModel.setCollected(mCurrentPagerId, false);
                ToastManager.showToast(this, "取消收藏");

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Cancel");
                UmengManager.onEvent(this, "InterviewAnalysis", map);

            } else {

                mModel.setCollected(mCurrentPagerId, true);
                ToastManager.showToast(this, "收藏成功");

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Collect");
                UmengManager.onEvent(this, "InterviewAnalysis", map);
            }
      } else if("关闭".equals(item.getTitle())){
            getSupportFragmentManager().popBackStack(); // 将引导页fragment推出heap
            isTeacherRemark = false;
            setDisplayHomeAsUpEnabled(this, true);
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    *   返回键
    * */
    @Override
    public void onBackPressed() {
        if (mWhatView == RECORDING) {
            return;
        } else if (mWhatView == RECORDEDUNSBMIT) {
            InterviewDetailModel.showBackPressedDailog(this);   // 显示退出dailog
            return;
        }
        super.onBackPressed();
    }

    public InterviewPaperDetailResp.AllAudioBean getAllAudioBean() {
        return mAllAudioBean;
    }

    public InterviewPaperDetailResp.SingleAudioBean getSingleAudioBean() {
        return mSingleAudioBean;
    }

    public int getCurQuestionId() {
        if (mList == null || mCurrentPagerId < 0 || mCurrentPagerId >= mList.size()) return 0;
        InterviewPaperDetailResp.QuestionsBean bean = mList.get(mCurrentPagerId);
        if (bean == null) return 0;
        return bean.getId();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null || apiName == null) return;

        if ("paper_detail".equals(apiName) || "history_interview_detail".equals(apiName)
                || "get_note_collect".equals(apiName)) {
            InterviewPaperDetailResp resp =
                    GsonManager.getModel(response, InterviewPaperDetailResp.class); // 将数据封装成bean对象
            if (resp != null && resp.getResponse_code() == 1) {
                // 获取问题的数据集合
                mList = resp.getQuestions();
                if (mList == null || mList.size() == 0) {
                    ToastManager.showToast(this, "没有面试题目");
                } else {
                    mAdaper = new InterviewDetailAdapter(               // 将数据传给adapter
                            getSupportFragmentManager(),
                            mList,
                            this,
                            mFrom);
                    // 给model数据
                    mViewPager.setAdapter(mAdaper);
                    // 设置viewPager缓存也个数
                    int childCount = mViewPager.getAdapter().getCount();         // viewPager的总共的页数
                    mViewPager.setOffscreenPageLimit(childCount -1);
                    // 选中当前viewpager
                    setViewPagerItem();
                }

                // 购买信息
                mAllAudioBean = resp.getAll_audio();
                if (mAllAudioBean != null) {
                    mIsBuyAll = mAllAudioBean.is_purchased();
                }

                mSingleAudioBean = resp.getSingle_audio();
                invalidateOptionsMenu(); // 刷新menu
            } else if (resp != null && resp.getResponse_code() == 1001) {
                ToastManager.showToast(this, "没有面试题目");
            }
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

    private void initListener(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {       //  当前viewpager
                mCurrentPagerId = position;
                invalidateOptionsMenu();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    /*
    *   提交录音后,选中当前viewPager,并刷新menu
    * */
    public void setViewPagerItem(){
        invalidateOptionsMenu();
        mViewPager.setScroll(true);
        mViewPager.setCurrentItem(mCurrentPagerId);
    }
    /*
    *   是否为名师引导页
    * */
    public void setIsTeacherRemarkView(boolean isTeacherRemark){
        this.isTeacherRemark = isTeacherRemark;
    }
}
