package com.appublisher.quizbank.common.vip.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipMSJPAdapter;
import com.appublisher.quizbank.common.vip.fragment.VipMSJPQuestionFragment;
import com.appublisher.quizbank.common.vip.model.VipMSJPModel;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;

/**
 * 小班：名师精批
 */
public class VipMSJPActivity extends VipBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipMSJPAdapter mAdapter;
    private VipMSJPModel mModel;

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
        mModel.sendToUmeng();
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
    }

    private void initData() {
        mModel = new VipMSJPModel(this);
        mModel.mExerciseId = getIntent().getIntExtra("exerciseId", 0);
        showLoading();
        mModel.getExerciseDetail();
        // Umeng
        mModel.mUMBegin = System.currentTimeMillis();
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
