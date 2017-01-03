package com.appublisher.quizbank.common.interview.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.InterviewDetailAdapter;
import com.appublisher.quizbank.common.interview.model.InterviewUnPurchasedModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.common.interview.viewgroup.MyViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class InterviewPaperDetailActivity extends BaseActivity implements RequestCallback {

    private int paper_id;
    public InterviewRequest mRequest;
    public MyViewPager viewPager;
    public InterviewDetailAdapter mAdaper;
    private int whatView;
    private int unrecord = 0;
    private int recording = 1;
    private int recorded_unsbmit = 2;
    private InterviewUnPurchasedModel mUnPurchasedModel;
    private String paper_type;
    private int note_id;
    private int mCurrentPagerId;   // 当前的viewPager的索引

    private String mFrom;
    public List<InterviewPaperDetailResp.QuestionsBean> list;
    private int mQuestionbeanId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_paper_detail);

        setToolBar(this);
        setTitle("");
        mFrom = getIntent().getStringExtra("from");          // 来源:问题的类型
        paper_id = getIntent().getIntExtra("paper_id", 0);
        paper_type = getIntent().getStringExtra("paper_type");
        note_id = getIntent().getIntExtra("note_id", 0);

        viewPager = (MyViewPager) findViewById(R.id.viewpager);   //自定义的viewpager
        viewPager.setScroll(true);
        initListener(viewPager);
        mUnPurchasedModel = new InterviewUnPurchasedModel(this);

        mRequest = new InterviewRequest(this, this);
        mRequest.getPaperDetail(paper_id, paper_type, note_id); // 请求数据

        showLoading();
    }

    public int setCanBack(int whatView) {
        this.whatView = whatView;
        return whatView;
    }

    public void getData() {
        mRequest.getPaperDetail(paper_id, paper_type, note_id);
    }

    @Override
   public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if (list == null) return super.onCreateOptionsMenu(menu);

        if(mUnPurchasedModel.getIsAnswer( mCurrentPagerId, this)){ // 判断是否回答 -->需要放到model中,因为涉及到修改   在此处应该讲bean 传给model
            if(mUnPurchasedModel.getIsCollected( mCurrentPagerId, this)){
                MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_collected),
                        MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            }else{
                MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_uncollect),
                        MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            }
        }else{
            MenuItemCompat.setShowAsAction(menu.add("开启完整版"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
   }


    /**
     * 通过fragment中穿件来的id判断具体是录音页面哪个状态:进行分别处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 返回键
            if (whatView == recording) {
                return true;
            } else if (whatView == recorded_unsbmit) {
                if (mUnPurchasedModel == null) {
                    mUnPurchasedModel = new InterviewUnPurchasedModel(this);
                }
                mUnPurchasedModel.showBackPressedDailog(this);   // 显示退出dailog

                return true;
            }
        } else if ("开启完整版".equals(item.getTitle())) {
            if (whatView == recording || whatView == recorded_unsbmit) {
                return true;
            } else if (whatView == unrecord) {
                if (mUnPurchasedModel == null) {
                    mUnPurchasedModel = new InterviewUnPurchasedModel(this);
                }
                String openNineUrl = "打开全部的链接";
                mUnPurchasedModel.showOpenFullDialog(this, openNineUrl);
            }
        }else if("收藏".equals(item.getTitle())){
            // 检验是否收藏
            if (mUnPurchasedModel.getIsCollected(mCurrentPagerId, this)) {   // 判断当前viewpager的小题是否收藏
                // 如果是已收藏状态，取消收藏
                mUnPurchasedModel.setCollected(mCurrentPagerId, false,this);
                ToastManager.showToast(this, "取消收藏");

            } else {
                // 如果是未收藏状态，收藏
                mUnPurchasedModel.setCollected(mCurrentPagerId, true, this);
                ToastManager.showToast(this, "收藏成功");
            }
      }
        return super.onOptionsItemSelected(item);
    }

    /*
    *   返回键
    * */
    @Override
    public void onBackPressed() {
        if (whatView == recording) {
            return;
        } else if (whatView == recorded_unsbmit) {
            if (mUnPurchasedModel == null) {
                mUnPurchasedModel = new InterviewUnPurchasedModel(this);
            }
            mUnPurchasedModel.showBackPressedDailog(this);
        }
        super.onBackPressed();
    }


    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null || apiName == null) return;
        if ("paper_detail".equals(apiName)) {
            InterviewPaperDetailResp interviewPaperDetailResp = GsonManager.getModel(response, InterviewPaperDetailResp.class); // 将数据封装成bean对象

            if (interviewPaperDetailResp != null && interviewPaperDetailResp.getResponse_code() == 1) {

                // 获取问题的数据集合
                list = interviewPaperDetailResp.getQuestions();

                if (list == null || list.size() == 0) {
                    ToastManager.showToast(this, "没有面试题目");
                } else {
                    mAdaper = new InterviewDetailAdapter(               // 将数据传给adapter
                            getSupportFragmentManager(),
                            list,
                            this,
                            mFrom);

                   invalidateOptionsMenu(); // 刷新menu
                    // 给model数据
                    viewPager.setAdapter(mAdaper);
                }
            } else if (interviewPaperDetailResp != null && interviewPaperDetailResp.getResponse_code() == 1001) {
                ToastManager.showToast(this, "没有面试题目");
            }
        }
    }
    private void initListener(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {       //  当前viewpager
                mCurrentPagerId = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
