package com.appublisher.quizbank.common.interview.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
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
import com.appublisher.quizbank.common.interview.model.InterviewDetailModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewControlsStateBean;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.common.interview.view.InterviewConstants;
import com.appublisher.quizbank.common.interview.view.InterviewDetailBaseFragmentCallBak;
import com.appublisher.quizbank.common.interview.viewgroup.ScrollExtendViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterviewPaperDetailActivity extends BaseActivity implements RequestCallback, InterviewDetailBaseFragmentCallBak {

    public InterviewRequest mRequest;
    public ScrollExtendViewPager mViewPager;
    public InterviewDetailAdapter mAdapter;
    private int mPaperId;
    private int mWhatView;
    private int mNoteId;
    public int mCurrentPagerId;   // 当前的viewPager的索引
    public int mPlayingChildViewId;

    public MediaRecorderManager mMediaRecorderManager;        // 新的播放器类
    private String mPaperType;
    private String mQuestionFrom;
    public String mPlayingViewState;
    private String mDataFrom;
    private String mItemType;
    private String mQuestionTime;

    private InterviewPaperDetailResp.AllAudioBean mAllAudioBean;
    private InterviewPaperDetailResp.SingleAudioBean mSingleAudioBean;
    public InterviewDetailModel mModel;
    private boolean mIsShowBuyAllMenu = false;
    private boolean mExitsPlayingMedia;
    public boolean mHadDoneQuestion;

    public List<InterviewPaperDetailResp.QuestionsBean> mQuestionsBeanList;
    public HashMap<String, InterviewControlsStateBean> mHoldFragmentControlsMap;
    public SparseArray<String> mRecordPathArray;

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

        mPlayingViewState = InterviewConstants.NOT_EXIST_PLAYING_MEDIA;
        mExitsPlayingMedia = false;
        mHadDoneQuestion = false;
        // 所有fragment中用同一个录音器
        mMediaRecorderManager = new MediaRecorderManager(this);
        mViewPager = (ScrollExtendViewPager) findViewById(R.id.viewpager);   //自定义的viewpager

        if (mViewPager == null) return;
        mViewPager.setScroll(true);

        if (mRecordPathArray == null || mRecordPathArray.size() <= 0)
            mRecordPathArray = new SparseArray<>();         // 保存录音缓存文件路径的集合

        // 新建map存储控件
        if (mHoldFragmentControlsMap == null || mHoldFragmentControlsMap.size() <= 0)
            mHoldFragmentControlsMap = new HashMap<>();

        initListener(mViewPager);
        mModel = new InterviewDetailModel(this, this);
        mRequest = new InterviewRequest(this, this);

        mDataFrom = getIntent().getStringExtra("dataFrom");
        mItemType = getIntent().getStringExtra("itemType"); // item类型
        mQuestionTime = getIntent().getStringExtra("time"); // 时间

        if ("studyRecordInterview".equals(mDataFrom)){       // 数据来源自记录页面的面试页面
            mRequest.getStudyRecordInterviewPaperDetail(mItemType, mQuestionTime);
        } else if ("recordCollect".equals(mDataFrom)){        // 来源: 记录页面的收藏页面
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
        mWhatView = InterviewConstants.RECORDED_HAD_SUBMIT;
        // 将缓存路径集合清空
        if (mRecordPathArray != null && mRecordPathArray.size() > 0 ) mRecordPathArray.clear();

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
            if (mWhatView == InterviewConstants.RECORDING) {
                ToastManager.showToast(this, "请专心录音哦");
                return true;
            } else if (mWhatView == InterviewConstants.RECORDED_UN_SUBMIT || checkRecordPathMap() ) {
                InterviewDetailModel.showBackPressedAlert(this);   // 显示退出dialog
                return true;
            }
            if (mExitsPlayingMedia){
                // 将播放状态的播放器变成停止状态
                changePlayingMediaToStop();
                return true;
            }
        } else if ("开启完整版".equals(item.getTitle())) {
            if (mWhatView == InterviewConstants.RECORDING) {
                ToastManager.showToast(this, "请专心录音哦");
                return true;
            } else if ( mWhatView == InterviewConstants.RECORDED_UN_SUBMIT || mWhatView == InterviewConstants.UN_RECORD) {
                mModel.showOpenFullDialog();
            }
        } else if ("收藏".equals(item.getTitle())) {
            if (mModel.getIsCollected(mCurrentPagerId)) {   // 判断当前viewpager的小题是否收藏
                mModel.setCollected(mCurrentPagerId, false);
                ToastManager.showToast(this, "取消收藏");

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "CancelCollect");
                if (mHadDoneQuestion) {
                    UmengManager.onEvent(this, "InterviewAnalysis", map);
                } else {
                    UmengManager.onEvent(this, "InterviewQuestion", map);
                }
                // 录音状态:
                if (mWhatView == InterviewConstants.RECORDING || mWhatView == InterviewConstants.RECORDED_UN_SUBMIT){
                    UmengManager.onEvent(this, "InterviewRecord", map);
                }
            } else {
                mModel.setCollected(mCurrentPagerId, true);
                ToastManager.showToast(this, "收藏成功");
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Collect");
                if (mHadDoneQuestion) {
                    UmengManager.onEvent(this, "InterviewAnalysis", map);
                } else {
                    UmengManager.onEvent(this, "InterviewQuestion", map);
                }
                // 录音状态:
                if (mWhatView == InterviewConstants.RECORDING || mWhatView == InterviewConstants.RECORDED_UN_SUBMIT){
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
        if (mWhatView == InterviewConstants.RECORDING) {
            ToastManager.showToast(this, "请专心录音哦");
            return;
        } else if (mWhatView == InterviewConstants.RECORDED_UN_SUBMIT || checkRecordPathMap()) {
            InterviewDetailModel.showBackPressedAlert(this);   // 显示退出dialog
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
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {       //  当前viewpager
                mCurrentPagerId = position;
                invalidateOptionsMenu();
//              控件的播放状态
                updateFragmentPlayState();
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
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

    private boolean checkRecordPathMap(){
        return mRecordPathArray != null && mRecordPathArray.size() > 0 ;
    }

    /*
    *   清空未提交语音缓存文件
    * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < mRecordPathArray.size(); i++) {
            String recordPath = mRecordPathArray.valueAt(i);
            if (("").equals(recordPath) || recordPath == null) continue;
            File file = new File(recordPath);
            if (file.exists() && file.isFile()){
                FileManager.deleteFiles(recordPath);
            }
        }

        if (mHoldFragmentControlsMap == null || mHoldFragmentControlsMap.size() <= 0) return;
            mHoldFragmentControlsMap.clear();
    }

    /*
    *   刷新控件的播放状态:处理相邻页面暂停状态恢复默认状态
    * */
    private void updateFragmentPlayState() {
        // 将暂停状态恢复成默认状态
        if (mHoldFragmentControlsMap == null || mHoldFragmentControlsMap.size() <= 0) return;

        // 遍历集合将暂停的bean修改成默认
        for (Map.Entry<String, InterviewControlsStateBean> controlsStateBeanEntry:
                mHoldFragmentControlsMap.entrySet()) {
            InterviewControlsStateBean interviewControlsStateBean = controlsStateBeanEntry.getValue();
            if (interviewControlsStateBean == null || interviewControlsStateBean.getControlsViewBean() == null) continue;     // 翻到第三页内部bean为null

            String key = controlsStateBeanEntry.getKey();
            String state = interviewControlsStateBean.getState();
            String itemType = interviewControlsStateBean.getItemType();
            int totalDuration = interviewControlsStateBean.getTotalDuration();
            if (("").equals(key) || key == null
                    || ("").equals(state) || state == null
                        || ("").equals(itemType) || itemType == null) continue;

            if (state.equals(InterviewConstants.PAUSE) || state.equals(InterviewConstants.OVER)) {
                interviewControlsStateBean.getControlsViewBean().getProgressBar().setProgress(100);
                interviewControlsStateBean.setOffset(0);
                interviewControlsStateBean.setState(InterviewConstants.OVER);
                switch (itemType) {
                    case InterviewConstants.SUBMIT:
                        interviewControlsStateBean.getControlsViewBean().getProgressBarStateTv().setText("听语音");
                        interviewControlsStateBean.getControlsViewBean().getProgressBarTimeTv().setText(mModel.formatDateTime(totalDuration));
                        break;
                    case InterviewConstants.HAD_SUBMIT:
                        interviewControlsStateBean.getControlsViewBean().getProgressBarStateTv().setText("听语音");
                        interviewControlsStateBean.getControlsViewBean().getProgressBarTimeTv().setText(mModel.formatDateTime(totalDuration));
                        break;
                    case InterviewConstants.QUESTION_ITEM:
                        interviewControlsStateBean.getControlsViewBean().getProgressBarStateTv().setText("听语音");
                        interviewControlsStateBean.getControlsViewBean().getProgressBarTimeIv().setImageResource(R.drawable.interview_listen_audio);
                        break;
                    case InterviewConstants.ANALYSIS_ITEM:
                        interviewControlsStateBean.getControlsViewBean().getProgressBarStateTv().setText("听语音");
                        interviewControlsStateBean.getControlsViewBean().getProgressBarTimeIv().setImageResource(R.drawable.interview_listen_audio);
                        break;
                    case InterviewConstants.TEACHER_REMARK:
                        interviewControlsStateBean.getControlsViewBean().getProgressBarStateTv().setText("收听点评");
                        interviewControlsStateBean.getControlsViewBean().getProgressBarTimeTv().setText(mModel.formatDateTime(totalDuration));
                        break;
                }
                mHoldFragmentControlsMap.put(key, interviewControlsStateBean);
            }
        }
    }

    /*
    *   获取存在播放状态的播放器的view的id
    * */
    public void setPlayingChildViewId(int playingChildViewId) {
        mPlayingChildViewId = playingChildViewId;
    }

    public void setIsDone(boolean isDone){
        mHadDoneQuestion = isDone;
    }

    @Override
    public void refreshTeacherRemarkRemainder(String num) {}

    @Override
    public void popupAppliedForRemarkReminderAlert() {}

    @Override
    public void checkIsFirstSubmit() {}

}
