package com.appublisher.quizbank.common.interview.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.InterviewDetailAdapter;
import com.appublisher.quizbank.common.interview.model.InterviewUnPurchasedModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.common.interview.viewgroup.MyViewPager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class InterviewPaperDetailActivity extends BaseActivity implements RequestCallback {

    private int paper_id;
    public InterviewRequest mRequest;
    public MyViewPager viewPager;
    public InterviewDetailAdapter mAdaper;
    //   public boolean isCanBack;
    private int whatView;
    private int unrecord = 0;
    private int recording = 1;
    private int recorded_unsbmit = 2;
    private InterviewUnPurchasedModel mUnPurchasedModel;
    private String paper_type;
    private int note_id;
    private boolean isCollect;   // 是否收藏
    private boolean isAnswer;    // 是否答题
    private int mCurrentPagerId;   // 当前的viewPager的索引
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


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
        viewPager.setScroll(true);

        mRequest = new InterviewRequest(this, this);

        mRequest.getPaperDetail(paper_id, paper_type, note_id);
        showLoading();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }



    public int setCanBack(int whatView) {
        this.whatView = whatView;
        return whatView;
    }

    public boolean setIsAnswer(boolean isanswer) {
        Logger.e("aaaaaaaa");
        isAnswer = isanswer;
        return isAnswer;
    }

    public void getData() {
        mRequest.getPaperDetail(paper_id, paper_type, note_id);

    }

    // item 是否为收藏状态
    public boolean setIsCollect(boolean iscollect) {
        isCollect = iscollect;
        return isCollect;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {      // 动态修改menu
        if (isAnswer) {
            if (isCollect) {
                MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_collected),
                        MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            } else {
                Logger.e("ccccccccc");
                MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(R.drawable.measure_analysis_uncollect),
                        MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            }
        } else {
            Logger.e("bbbbbbbb");
            MenuItemCompat.setShowAsAction(menu.add("开启完整版"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onPrepareOptionsMenu(menu);
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
            if (mUnPurchasedModel.isCollected(mCurrentPagerId)) {
                // 如果是已收藏状态，取消收藏
                mUnPurchasedModel.setCollected(mCurrentPagerId, false);
                ToastManager.showToast(this, "取消收藏");

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Cancel");
                UmengManager.onEvent(this, "ReviewDetail", map);

            } else {
                // 如果是未收藏状态，收藏
                mUnPurchasedModel.setCollected(mCurrentPagerId, true);
                ToastManager.showToast(this, "收藏成功");

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Collect");
                UmengManager.onEvent(this, "ReviewDetail", map);
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
            InterviewPaperDetailResp interviewPaperDetailResp = GsonManager.getModel(response, InterviewPaperDetailResp.class);
            if (interviewPaperDetailResp != null && interviewPaperDetailResp.getResponse_code() == 1) {
                List<InterviewPaperDetailResp.QuestionsBean> list = interviewPaperDetailResp.getQuestions();
                if (list == null || list.size() == 0) {
                    ToastManager.showToast(this, "没有面试题目");
                } else {
                    mAdaper = new InterviewDetailAdapter(
                            getSupportFragmentManager(),
                            list,
                            this);

                    invalidateOptionsMenu(); // 刷新menu
                    initListener(viewPager);
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("InterviewPaperDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
