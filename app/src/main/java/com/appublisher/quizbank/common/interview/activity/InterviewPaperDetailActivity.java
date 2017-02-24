package com.appublisher.quizbank.common.interview.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.MediaRecorderManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.InterviewDetailAdapter;
import com.appublisher.quizbank.common.interview.fragment.InterviewDetailBaseFragment;
import com.appublisher.quizbank.common.interview.model.InterviewDetailModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.common.interview.view.InterviewDetailBaseFragmentCallBak;
import com.appublisher.quizbank.common.interview.viewgroup.ScrollExtendViewPager;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class InterviewPaperDetailActivity extends BaseActivity implements RequestCallback, InterviewDetailBaseFragmentCallBak {

    private static final int UN_RECORD = 0;
    private static final int RECORDING = 1;
    private static final int RECORDED_UN_SUBMIT = 2;
    private static final int RECORDED_HAD_SUBMIT = 3;
    private static final int HAD_REMARKED = 4;     // 已经点评
    private static final String SUBMIT = "submit";              //可提交
    private static final String HAD_SUBMIT = "hadSubmit";      // 已提交
    private static final String TEACHER_REMARK = "teacherRemark";      // 名师点评
    private static final String QUESTION_ITEM = "questionItem";
    private static final String ANALYSIS_ITEM = "analysisItem";
    public static final String NOT_EXIST_PLAYING_MEDIA = "notExistPlayingMedia";
    public InterviewRequest mRequest;
    public ScrollExtendViewPager mViewPager;
    public InterviewDetailAdapter mAdapter;
    private int mPaperId;
    private int mWhatView;
    private int mNoteId;
    private int mCurrentPagerId;   // 当前的viewPager的索引
//<<<<<<< HEAD
//=======
//    private String mFrom;
//    public List<InterviewPaperDetailResp.QuestionsBean> mList;
//    private boolean mIsShowBuyAllMenu = false;
//    private InterviewPaperDetailResp.AllAudioBean mAllAudioBean;
//    private InterviewPaperDetailResp.SingleAudioBean mSingleAudioBean;
//    public InterviewDetailModel mModel;
    public MediaRecorderManager mMediaRecorderManager;        // 新的播放器类
//    public String playingViewState;
//>>>>>>> 87bbd428172ec5a4180edaab566f5c46cc43042a
    public int mPlayingChildViewId;
    private int mUnSubmitRecordAudioNum;
    private String mPaperType;
    private String mQuestionFrom;
    public String mPlayingViewState;
    private String mDataFrom;
    private String mItemType;
    private String mQuestionTime;
    public List<InterviewPaperDetailResp.QuestionsBean> mQuestionsBeanList;
    private InterviewPaperDetailResp.AllAudioBean mAllAudioBean;
    private InterviewPaperDetailResp.SingleAudioBean mSingleAudioBean;
    public InterviewDetailModel mModel;
//    public MediaRecordManagerUtil mMediaRecorderManager;        // 新的播放器类
    private boolean mIsShowBuyAllMenu = false;
    private boolean mExitsPlayingMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_paper_detail);
        setToolBar(this);
        setTitle("");
        mQuestionFrom = getIntent().getStringExtra("from");          // 来源:问题的类型
        mPaperId = getIntent().getIntExtra("paper_id", 0);
        mPaperType = getIntent().getStringExtra("paper_type");
        mNoteId = getIntent().getIntExtra("note_id", 0);

        mPlayingViewState = NOT_EXIST_PLAYING_MEDIA;
        mExitsPlayingMedia = false;
        mUnSubmitRecordAudioNum = 0;

        // 所有fragment中用同一个录音器
        mMediaRecorderManager = new MediaRecorderManager(this);
        mViewPager = (ScrollExtendViewPager) findViewById(R.id.viewpager);   //自定义的viewpager

        if (mViewPager == null) return;
        mViewPager.setScroll(true);
//        initListener(mViewPager);

        mModel = new InterviewDetailModel(this, this);
        mRequest = new InterviewRequest(this, this);

        mDataFrom = getIntent().getStringExtra("dataFrom");
        mItemType = getIntent().getStringExtra("itemType"); // item类型
        mQuestionTime = getIntent().getStringExtra("time"); // 时间


        if("studyRecordInterview".equals(mDataFrom)){       // 数据来源自记录页面的面试页面
            mRequest.getStudyRecordInterviewPaperDetail(mItemType, mQuestionTime);
        }else if("recordCollect".equals(mDataFrom)){        // 来源: 记录页面的收藏页面
            int note_id = getIntent().getIntExtra("note_id", 0);
            mRequest.getRecordInterviewCollectPaperDetail(note_id);
        } else if ("record_comment".equals(mDataFrom)) {             // 来自名师点评页
            int record_id = getIntent().getIntExtra("record_id", 0);
            mRequest.getRecordInterviewTeacherRemark(record_id);
        } else {
            mRequest.getPaperDetail(mPaperId, mPaperType, mNoteId); // 请求数据
        }
        showLoading();
    }

    public void setCanBack(int view) {
        mWhatView = view;
    }

    public void getData() {
        mUnSubmitRecordAudioNum = 0;
        mWhatView = RECORDED_HAD_SUBMIT;


        if ("studyRecordInterview".equals(mDataFrom)){
            mRequest.getStudyRecordInterviewPaperDetail(mItemType, mQuestionTime);
        } else if ("recordCollect".equals(mDataFrom)){
            int note_id = getIntent().getIntExtra("note_id", 0);
            mRequest.getRecordInterviewCollectPaperDetail(note_id);
        } else {
            mRequest.getPaperDetail(mPaperId, mPaperType, mNoteId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if (mQuestionsBeanList == null || mCurrentPagerId < 0 ) {
            mCurrentPagerId = 0;
        }
        if (mModel.getIsCollected( mCurrentPagerId)){
            MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_collected),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        } else {
            MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_uncollect),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }
        // 购买状态
        if (mIsShowBuyAllMenu && !"record_comment".equals(mDataFrom)) {
            MenuItemCompat.setShowAsAction(
                    menu.add("开启完整版"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 通过fragment中穿件来的id判断具体是录音页面哪个状态:进行分别处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mWhatView == RECORDING) {
                ToastManager.showToast(this, "请专心录音哦");
                return true;
            } else if (mWhatView == RECORDED_UN_SUBMIT || mUnSubmitRecordAudioNum > 0 ) {
                InterviewDetailModel.showBackPressedAlert(this);   // 显示退出dailog
                return true;
            }
            if (mExitsPlayingMedia){
//=======
//            } else if (mWhatView == RECORDEDUNSBMIT || mUnSubmitRecordAudioNum > 0) {
//                InterviewDetailModel.showBackPressedDailog(this);   // 显示退出dailog
//                return true;
//            }
//            if (isExitsPlayingMedia) {
//>>>>>>> 87bbd428172ec5a4180edaab566f5c46cc43042a
                // 将播放状态的播放器变成停止状态
                changePlayingMediaToStop();
                return true;
            }
        } else if ("开启完整版".equals(item.getTitle())) {
            if (mWhatView == RECORDING) {
                ToastManager.showToast(this, "请专心录音哦");
                return true;
            } else if ( mWhatView == RECORDED_UN_SUBMIT || mWhatView == UN_RECORD) {
                mModel.showOpenFullDialog();
            }
        } else if ("收藏".equals(item.getTitle())) {
            if (mModel.getIsCollected(mCurrentPagerId)) {   // 判断当前viewpager的小题是否收藏
                mModel.setCollected(mCurrentPagerId, false);
                ToastManager.showToast(this, "取消收藏");

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "CancelCollect");
                if (isDone()) {
                    UmengManager.onEvent(this, "InterviewAnalysis", map);
                } else {
                    UmengManager.onEvent(this, "InterviewQuestion", map);
                }
                // 录音状态:
                if (mWhatView == RECORDING || mWhatView == RECORDED_UN_SUBMIT){
                    UmengManager.onEvent(this, "InterviewRecord", map);
                }
            } else {
                mModel.setCollected(mCurrentPagerId, true);
                ToastManager.showToast(this, "收藏成功");
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Collect");
                if (isDone()) {
                    UmengManager.onEvent(this, "InterviewAnalysis", map);
                } else {
                    UmengManager.onEvent(this, "InterviewQuestion", map);
                }
                // 录音状态:
                if (mWhatView == RECORDING || mWhatView == RECORDED_UN_SUBMIT){
                    UmengManager.onEvent(this, "InterviewRecord", map);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    *   返回键
    * */
    @Override
    public void onBackPressed() {
        if (mWhatView == RECORDING) {
            ToastManager.showToast(this, "请专心录音哦");
            return;
        } else if (mWhatView == RECORDED_UN_SUBMIT || mUnSubmitRecordAudioNum >0) {
            InterviewDetailModel.showBackPressedAlert(this);   // 显示退出dailog
            return;
        }
        if (mExitsPlayingMedia){
            // 将播放状态的播放器变成停止状态
            changePlayingMediaToStop();
            return;
        }
        super.onBackPressed();
    }

    /*
    *   设置页面中是否存在播放的播放器
    * */
    public void setExitsPlayingMedia(boolean isExitsPlayingMedia){
        this.mExitsPlayingMedia = isExitsPlayingMedia;
    }

    /*
    *
    * */
    public void changePlayingMediaToStop() {
        // 弹窗提示
        SharedPreferences sp = InterviewDetailModel.getInterviewSharedPreferences(this);
        boolean isFirstCheckBox = sp.getBoolean("isFirstCheckBox", true);
        if (isFirstCheckBox){
            InterviewDetailModel.showStopMediaPlayingAlert(this);
        } else {
            mMediaRecorderManager.stopPlay();
            finish();
        }
    }

    public InterviewPaperDetailResp.AllAudioBean getAllAudioBean() {
        return mAllAudioBean;
    }

    public InterviewPaperDetailResp.SingleAudioBean getSingleAudioBean() {
        return mSingleAudioBean;
    }

    public int getCurQuestionId() {
        if (mQuestionsBeanList == null || mCurrentPagerId < 0 || mCurrentPagerId >= mQuestionsBeanList.size())  return 0;
        InterviewPaperDetailResp.QuestionsBean bean = mQuestionsBeanList.get(mCurrentPagerId);
        if (bean == null) return 0;
        return bean.getId();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null || apiName == null) return;

        if ("paper_detail".equals(apiName) || "history_interview_detail".equals(apiName)
                || "get_note_collect".equals(apiName) || "teacher_comment_detail".equals(apiName)) {
            InterviewPaperDetailResp resp =
                    GsonManager.getModel(response, InterviewPaperDetailResp.class); // 将数据封装成bean对象
            if (resp != null && resp.getResponse_code() == 1) {
                // 获取问题的数据集合
                mQuestionsBeanList = resp.getQuestions();
                if (mQuestionsBeanList == null || mQuestionsBeanList.size() == 0) {
                    ToastManager.showToast(this, "没有面试题目");
                } else {
                    mAdapter = new InterviewDetailAdapter(               // 将数据传给adapter
                            getSupportFragmentManager(),
                            mQuestionsBeanList,
                            this,
                            mQuestionFrom);
                    // 给model数据
                    mViewPager.setAdapter(mAdapter);
//                  设置viewPager缓存也个数
                    int childCount = mViewPager.getAdapter().getCount();         // viewPager的总共的页数
                    mViewPager.setOffscreenPageLimit(childCount - 1);
                    // 选中当前viewpager
                    setViewPagerItem();
                }

                // 购买信息
                mAllAudioBean = resp.getAll_audio();
                if (mAllAudioBean != null) {
                    boolean isBuyAll = mAllAudioBean.is_purchased();
                    if (!isBuyAll) mIsShowBuyAllMenu = true;
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
                // 需要将暂停的状态的播放器恢复默认状态
                changeFragmentPauseToDefault();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /*
    *   需要将暂停的状态的播放器恢复默认状态
    * */
    private void changeFragmentPauseToDefault() {
        if (mAdapter.mFragmentList.size() <= 0)  return;
        InterviewDetailBaseFragment fragment = (InterviewDetailBaseFragment) mAdapter.mFragmentList.get(mCurrentPagerId);
        if (fragment.mUserAnswerFilePath != null && fragment.checkIsRecordFileExist() && !fragment.mIsUserNotSubmitAudioPause ){
            fragment.mUserNotSubmitAudioOffset = 0;
            fragment.mUserNotSubmitAudioProgressBar.setProgress(100);
            String duration = FileManager.getVideoDuration(fragment.mUserAnswerFilePath);
            if (Integer.parseInt(duration)>= 360){
                fragment.mTimeNotSubmitPlayTv.setText(mModel.formatDateTime(360));
            } else {
                fragment.mTimeNotSubmitPlayTv.setText(mModel.formatDateTime(Integer.parseInt(duration)+1));
            }
            fragment.mNotSubmitStateTv.setText("听语音");
        }
        if ( !fragment.mIsUserHadSubmitAudioPause){
            fragment.mUserHadSubmitAudioOffset = 0;
            fragment.mUserHadSubmitAudioProgressBar.setProgress(100);
            if (fragment.mQuestionBean.getUser_audio_duration() >= 360){
                fragment.mTimeHadSubmitPlayTv.setText(mModel.formatDateTime(360));
            } else {
                fragment.mTimeHadSubmitPlayTv.setText(mModel.formatDateTime(fragment.mQuestionBean.getUser_audio_duration() + 1));
            }
            fragment.mUserAnswerPlayStateTv.setText("听语音");
        }
        if (fragment.mIsUnPurchasedOrPurchasedView.equals("PurchasedView")){
            if ( !fragment.mIsQuestionAudioPause){
                fragment.mQuestionAudioOffset = 0;
                fragment.mQuestionAudioProgressBar.setProgress(100);
                fragment.mQuestionAudioIv.setImageResource(R.drawable.interview_listen_audio);
                fragment.mQuestionAudioTv.setText("听语音");
            }
            if ( !fragment.mIsAnalysisAudioPause){
                fragment.mAnalysisAudioOffset = 0;
                fragment.mAnalysisAudioProgressBar.setProgress(100);
                fragment.mAnalysisAudioIv.setImageResource(R.drawable.interview_listen_audio);
                fragment.mAnalysisAudioTv.setText("听语音");
            }
        }
        if (fragment.mRemarkState < 0) return;
        if ( !fragment.mIsTeacherAudioPause && fragment.mRemarkState == HAD_REMARKED ){
            fragment.mTeacherRemarkAudioOffset = 0;
            fragment.mTeacherRemarkProgressBar.setProgress(100);
            fragment.mTeacherRemarkPlayTimeTv.setText(mModel.formatDateTime(fragment.mQuestionBean.getTeacher_audio_duration()));
            fragment.mTeacherRemarkPlayStateTv.setText("收听点评");
        }
    }

    /*
    *   提交录音后,选中当前viewPager,并刷新menu
    * */
    public void setViewPagerItem() {
        invalidateOptionsMenu();
        if (mViewPager == null) return;
        mViewPager.setScroll(true);
        mViewPager.setCurrentItem(mCurrentPagerId);
    }

    /*
    *   已经录过音但没有提交
    * */
    public void setIsHadUnSubmitRecordedAudio(boolean isHadUnSubmitRecordedAudio){
        if (isHadUnSubmitRecordedAudio){
            mUnSubmitRecordAudioNum = mUnSubmitRecordAudioNum + 1;
        } else {
            mUnSubmitRecordAudioNum = mUnSubmitRecordAudioNum - 1;
        }
    }

    /*
    *   由fragment传入正在播放的播放器
    * */
    public void setPlayingViewState(String playingViewState){
        this.mPlayingViewState = playingViewState;
    }

    /*
    *  让activity将正在播放的播放器恢复默认状态
    * */
    public void changePlayingViewToDefault(){
        if (mAdapter.mFragmentList.size() <= 0)  return;
        InterviewDetailBaseFragment fragment = (InterviewDetailBaseFragment) mAdapter.mFragmentList.get(mPlayingChildViewId);  // mPlayingChildViewId为存在播放状态的播放器的页面的id
        switch(mPlayingViewState){
            case QUESTION_ITEM:
                fragment.mQuestionAudioProgressBar.setProgress(100);
                fragment.mQuestionAudioOffset = 0;
                fragment.mediaPlayingAnimation(false);
                fragment.mQuestionAudioIv.setImageResource(R.drawable.interview_listen_audio);
                break;
            case ANALYSIS_ITEM:
                fragment.mAnalysisAudioProgressBar.setProgress(100);
                fragment.mAnalysisAudioOffset = 0;
                fragment.mediaPlayingAnimation(false);
                fragment.mAnalysisAudioIv.setImageResource(R.drawable.interview_listen_audio);
                break;
            case SUBMIT:
                fragment.mUserNotSubmitAudioProgressBar.setProgress(100);
                fragment.mUserNotSubmitAudioOffset = 0;
                String duration = FileManager.getVideoDuration(fragment.mUserAnswerFilePath);
                if (Integer.parseInt(duration)>= 360){
                    fragment.mTimeNotSubmitPlayTv.setText(mModel.formatDateTime(360));
                } else {
                    fragment.mTimeNotSubmitPlayTv.setText(mModel.formatDateTime(Integer.parseInt(duration)+1));
                }
                break;
            case HAD_SUBMIT:
                fragment.mUserHadSubmitAudioProgressBar.setProgress(100);
                fragment.mUserHadSubmitAudioOffset = 0;
                if (fragment.mQuestionBean.getUser_audio_duration() >= 360){
                    fragment.mTimeHadSubmitPlayTv.setText(mModel.formatDateTime(360));
                }else{
                    fragment.mTimeHadSubmitPlayTv.setText(mModel.formatDateTime(fragment.mQuestionBean.getUser_audio_duration() + 1));
//=======
//                if (Integer.parseInt(duration) >= 360) {
//                    fragment.mTvtimeNotSubmPlay.setText(mModel.formatDateTime(360));
//                } else {
//                    fragment.mTvtimeNotSubmPlay.setText(mModel.formatDateTime(Integer.parseInt(duration) + 1));
//                }
//                break;
//            case HADSUBMIT:
//                fragment.mUserAnswerProgressBar.setProgress(100);
//                fragment.mOffset = 0;
//                if (fragment.mQuestionBean.getUser_audio_duration() >= 360) {
//                    fragment.mTvtimeHadSumbPlay.setText(mModel.formatDateTime(360));
//                } else {
//                    fragment.mTvtimeHadSumbPlay.setText(mModel.formatDateTime(fragment.mQuestionBean.getUser_audio_duration() + 1));
//>>>>>>> 87bbd428172ec5a4180edaab566f5c46cc43042a
                }
                break;
            case TEACHER_REMARK:
                fragment.mTeacherRemarkProgressBar.setProgress(100);
                fragment.mTeacherRemarkAudioOffset = 0;
                fragment.mTeacherRemarkPlayTimeTv.setText(mModel.formatDateTime(fragment.mQuestionBean.getTeacher_audio_duration()));
                break;
        }
        fragment.mPlayingMedia = NOT_EXIST_PLAYING_MEDIA;
    }

    /*
    *   获取存在播放状态的播放器的view的id
    * */
    public void setPlayingChildViewId(int playingChildViewId) {
        mPlayingChildViewId = playingChildViewId;
    }

    private boolean isDone(){
        if (mAdapter.mFragmentList.size() <= 0)  return false;
        InterviewDetailBaseFragment fragment = (InterviewDetailBaseFragment) mAdapter.mFragmentList.get(mCurrentPagerId);
        return fragment.mQuestionBean != null && fragment.mQuestionBean.getUser_audio() != null
                && fragment.mQuestionBean.getUser_audio().length() > 0;
    }

    @Override
    public void refreshTeacherRemarkRemainder(String num) {
    }

    @Override
    public void popupAppliedForRemarkReminderAlert() {
    }

    @Override
    public void checkIsFirstSubmit() {
    }

}
