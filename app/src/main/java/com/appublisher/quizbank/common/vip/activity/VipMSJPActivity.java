package com.appublisher.quizbank.common.vip.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipMSJPAdapter;
import com.appublisher.quizbank.common.vip.fragment.VipMSJPQuestionFragment;
import com.appublisher.quizbank.common.vip.model.VipMSJPModel;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;

import java.util.HashMap;

/**
 * 小班：名师精批
 */
public class VipMSJPActivity extends VipBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipMSJPAdapter mAdapter;
    private VipMSJPModel mModel;

    // Umeng
    private long mUMTimeStamp;
    private int mUMSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_msjp);
        setToolBar(this);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        int dur = (int) ((System.currentTimeMillis() - mUMTimeStamp) / 1000);
        HashMap<String, String> map = new HashMap<>();
        map.put("Switch", String.valueOf(mUMSwitch));
        UmengManager.onEventValue(this, "Mingshi", map, dur);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VipMSJPQuestionFragment fragment =
                (VipMSJPQuestionFragment) mAdapter.getItem(0);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.vip_msjp_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vip_msjp_viewpager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mUMSwitch++;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        mModel = new VipMSJPModel(this);
        mModel.mExerciseId = getIntent().getIntExtra("exerciseId", 0);
        showLoading();
        mModel.getExerciseDetail();
        // Umeng
        mUMTimeStamp = System.currentTimeMillis();
    }

    public void showContent(VipMSJPResp resp) {
        mAdapter = new VipMSJPAdapter(getSupportFragmentManager(), resp);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 显示约束作业列表
     * @param nameList 作业列表
     */
    public void showPreExercisesAlert(String nameList) {
        String msg = "完成对其他同学的评论可以解锁本作业\n\n" + nameList;
        String p = "确认";
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(p,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).show();
    }
}
