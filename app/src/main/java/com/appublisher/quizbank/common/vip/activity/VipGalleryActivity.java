package com.appublisher.quizbank.common.vip.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

import java.util.ArrayList;

/**
 * 小班：本地图片查看
 */
public class VipGalleryActivity extends BaseActivity {

    private static final String DELETE = "delete";

    public static final String INTENT_PATHS = "paths";
    public static final String INTENT_INDEX = "index";
    public static final String INTENT_CAN_DELETE = "can_delete";

    private ViewPager mViewpager;
    private VipGalleryAdapter mAdapter;
    private ArrayList<String> mPaths;
    private int mCurIndex;
    private boolean mIsCanDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_gallery);
        setToolBar(this);
        initData();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsCanDelete) {
            MenuItemCompat.setShowAsAction(menu.add(DELETE).setIcon(R.drawable.vip_delete),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DELETE.equals(item.getTitle())) {
            showDeleteAlert();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        setTitle(getTitle(mCurIndex, mPaths));
        mViewpager = (ViewPager) findViewById(R.id.vip_gallery_vp);
        mViewpager.setAdapter(mAdapter);
        mViewpager.setCurrentItem(mCurIndex, true);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
                // Empty
            }

            @Override
            public void onPageSelected(int position) {
                mCurIndex = position;
                setTitle(getTitle(position, mPaths));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Empty
            }
        });
    }

    private void initData() {
        mPaths = getIntent().getStringArrayListExtra(INTENT_PATHS);
        mCurIndex = getIntent().getIntExtra(INTENT_INDEX, 0);
        mIsCanDelete = getIntent().getBooleanExtra(INTENT_CAN_DELETE, false);
//        mPaths = new ArrayList<>();
//        mPaths.add("/storage/emulated/0/DCIM/Camera/IMG_20160913_163015.jpg");
//        mPaths.add("/storage/emulated/0/DCIM/Camera/IMG_20160913_163015.jpg");
//        mPaths.add("/storage/emulated/0/DCIM/Camera/IMG_20160913_163015.jpg");
//        mPaths.add("/storage/emulated/0/DCIM/Camera/IMG_20160913_163015.jpg");
//        mPaths.add("/storage/emulated/0/DCIM/Camera/IMG_20160913_163015.jpg");
//        mPaths.add("/storage/emulated/0/DCIM/Camera/IMG_20160913_163015.jpg");
        mAdapter = new VipGalleryAdapter(this, mPaths);
        setResult();
    }

    private String getTitle(int index, ArrayList<String> paths) {
        return (index + 1) + "/" + paths.size();
    }

    private void showDeleteAlert() {
        new AlertDialog.Builder(this)
                .setMessage("要删除这张照片吗？")
                .setTitle("提示")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {// 确定

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    private void delete() {
        mAdapter.deleteView(mViewpager, mCurIndex);
        if (mPaths.size() == 0) finish();
        if (mCurIndex == mPaths.size()) mCurIndex--;
        mViewpager.setCurrentItem(mCurIndex);
        setTitle(getTitle(mCurIndex, mPaths));
        setResult();
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(INTENT_PATHS, mPaths);
        setResult(0, intent);
    }

}
