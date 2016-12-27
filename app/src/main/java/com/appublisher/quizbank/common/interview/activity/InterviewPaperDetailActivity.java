package com.appublisher.quizbank.common.interview.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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

import java.util.ArrayList;
import java.util.List;

public class InterviewPaperDetailActivity extends BaseActivity implements RequestCallback {

    private int paper_id;
    public InterviewRequest mRequest;
    public MyViewPager viewPager;
    public InterviewDetailAdapter mAdaper;
    private List<InterviewPaperDetailResp.QuestionsBean> list;
 //   public boolean isCanBack;
    private int whatView ;
    private int unrecord = 0;
    private int recording = 1;
    private int recorded_unsbmit = 2;
    private InterviewUnPurchasedModel mUnPurchasedModel;
    private boolean isAnswer;
    private String paper_type;
    private int note_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_paper_detail);

        setToolBar(this);
        setTitle("");

        paper_id = getIntent().getIntExtra("paper_id", 0);
        paper_type = getIntent().getStringExtra("paper_type");
        note_id = getIntent().getIntExtra("note_id", 0);

        viewPager = (MyViewPager) findViewById(R.id.viewpager);   //自定义的viewpager

        list = new ArrayList<>();
        mAdaper = new InterviewDetailAdapter(getSupportFragmentManager(), list, this);
        viewPager.setAdapter(mAdaper);
        viewPager.setScroll(true);

        mRequest = new InterviewRequest(this, this);

        mRequest.getPaperDetail(paper_id, paper_type, note_id);
        showLoading();
    }
    public int setCanBack(int whatView){
        this.whatView = whatView;
        return whatView;
    }
    public boolean setIsAnswer(boolean isAnswer){
        this.isAnswer = isAnswer;
        return isAnswer;
    }

    public void getData() {
        mRequest.getPaperDetail(paper_id, paper_type, note_id);

    }

    /*
    *   设置toolbar
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

      //  isAnswer = false;
        if(isAnswer ==false) {
            MenuItemCompat.setShowAsAction(menu.add("开启完整版"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }else{
            MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_uncollect),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *   通过fragment中穿件来的id判断具体是录音页面哪个状态:进行分别处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 返回键
            if( whatView ==recording ){
                return true;
            }else if (whatView ==recorded_unsbmit){
                if(mUnPurchasedModel==null){
                    mUnPurchasedModel = new InterviewUnPurchasedModel(this);
                }
                mUnPurchasedModel.showBackPressedDailog(this);   // 显示退出dailog

                return true;
            }
        }else if("开启完整版".equals(item.getTitle())){
            if( whatView ==recording || whatView == recorded_unsbmit){
                return true;
            }else if(whatView == unrecord){
                if(mUnPurchasedModel==null) {
                    mUnPurchasedModel = new InterviewUnPurchasedModel(this);
                }
                String openNineUrl = "打开全部的链接";
                mUnPurchasedModel.showOpenFullDialog(this,openNineUrl);
            }
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    *   返回键
    * */
    @Override
    public void onBackPressed() {
        if(whatView ==recording){
            return;
        }else if(whatView == recorded_unsbmit){
            if(mUnPurchasedModel==null) {
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
            InterviewPaperDetailResp interviewPaperDetailResp = GsonManager.getModel(response, InterviewPaperDetailResp.class);
            if (interviewPaperDetailResp != null && interviewPaperDetailResp.getResponse_code() == 1) {
                list.clear();
                list.addAll(interviewPaperDetailResp.getQuestions());
                mAdaper.notifyDataSetChanged();
                if (list.size() == 0) {
                    ToastManager.showToast(this, "没有面试题目");
                }
            } else if (interviewPaperDetailResp != null && interviewPaperDetailResp.getResponse_code() == 1001) {
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
}
