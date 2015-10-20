package com.appublisher.quizbank.model.offline.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.offline.adapter.PurchasedClassesAdapter;
import com.appublisher.quizbank.model.offline.model.business.OfflineModel;
import com.appublisher.quizbank.model.offline.netdata.PurchasedClassM;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 离线模块课程列表
 */
public class OfflineClassActivity extends AppCompatActivity implements View.OnClickListener{

    private int mMenuStatus; // 1：下载 2：删除
    private HashMap<Integer, Boolean> mSelectedMap; // 用来控制CheckBox的选中状况
    private Button mBtnBottom;
    private static int mPercent;
    private static int mCurPosition;
    private final static int DOWNLOAD_BEGIN = 1;
    private final static int DOWNLOAD_PROGRESS = 2;
    private final static int DOWNLOAD_FINISH = 3;

    public ArrayList<PurchasedClassM> mClasses;
    public ListView mLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_course);

        // Toolbar
        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, "离线管理");

        // Init view
        mLv = (ListView) findViewById(R.id.offline_class_lv);
        mBtnBottom = (Button) findViewById(R.id.offline_bottom_btn);

        mBtnBottom.setOnClickListener(this);

        // Init data
        mSelectedMap = new HashMap<>();
        // noinspection unchecked
        mClasses = (ArrayList<PurchasedClassM>) getIntent().getSerializableExtra("class_list");

        for (int i = 0; i < 30; i++) {
            PurchasedClassM m = new PurchasedClassM();
            m.setName("data " + String.valueOf(i));
            if (i == 3 || i == 10 || i == 11) m.setStatus(1);
            mClasses.add(m);
        }

        PurchasedClassesAdapter adapter = new PurchasedClassesAdapter(this, mClasses);
        mLv.setAdapter(adapter);

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMenuStatus == 1 || mMenuStatus == 2) {
                    CheckBox cb = (CheckBox) view.findViewById(R.id.item_purchased_classes_cb);
                    cb.toggle();
                    mSelectedMap.put(position, cb.isChecked());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();

        } else if ("删除".equals(item.getTitle())) {
            mMenuStatus = 2;
            invalidateOptionsMenu();

        } else if ("下载".equals(item.getTitle())) {
            mMenuStatus = 1;
            invalidateOptionsMenu();

            if (mClasses == null) return super.onOptionsItemSelected(item);

            int size = mClasses.size();
            for (int i = 0; i < size; i++) {
                CheckBox cb = OfflineModel.getCheckBoxByPosition(this, i);
                if (cb == null) continue;
                cb.setVisibility(View.VISIBLE);
                cb.setChecked(false);
            }

            mBtnBottom.setVisibility(View.VISIBLE);

        } else if ("全选".equals(item.getTitle())) {
            if (mClasses == null) return super.onOptionsItemSelected(item);

            int size = mClasses.size();
            for (int i = 0; i < size; i++) {
                CheckBox cb = OfflineModel.getCheckBoxByPosition(this, i);
                if (cb == null) continue;
                cb.setChecked(true);
                mSelectedMap.put(i, true);
            }

        } else if ("取消".equals(item.getTitle())) {
            mMenuStatus = 0;
            invalidateOptionsMenu();

            if (mClasses == null) return super.onOptionsItemSelected(item);

            int size = mClasses.size();
            for (int i = 0; i < size; i++) {
                CheckBox cb = OfflineModel.getCheckBoxByPosition(this, i);
                if (cb == null) continue;
                cb.setVisibility(View.GONE);
            }

            mBtnBottom.setVisibility(View.GONE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        if (mMenuStatus == 1 || mMenuStatus == 2) {
            MenuItemCompat.setShowAsAction(menu.add("取消"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setShowAsAction(menu.add("全选"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        } else {
            MenuItemCompat.setShowAsAction(menu.add("删除").setIcon(
                    R.drawable.offline_delete), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setShowAsAction(menu.add("下载").setIcon(
                    R.drawable.offline_download), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.offline_bottom_btn:
                if (mClasses == null) break;

                if (mMenuStatus == 1) {
                    // 下载
//                    for (int i = mCurPosition; i < mClasses.size(); i++) {
//                        if (!mSelectedMap.containsKey(i) || !mSelectedMap.get(i)) continue;
//                        DuobeiYunClient.download(this, "jzb5a197820df24060bb8a607354dfce75",
//                                new DownloadTaskListener() {
//                                    @Override
//                                    public void onProgress(int progress) {
//                                        super.onProgress(progress);
//                                        Logger.i("progress:::::::" + String.valueOf(progress));
//                                        mProgress = progress;
//                                        mHandler.sendEmptyMessage(PROGRESS);
//                                        ProgressDialogManager.closeProgressDialog();
//                                    }
//
//                                    @Override
//                                    public void onError(String error) {
//                                        super.onError(error);
//                                        Logger.i("error:::::::" + String.valueOf(error));
//                                        ProgressDialogManager.closeProgressDialog();
//                                    }
//
//                                    @Override
//                                    public boolean onConnect(int type, String msg) {
//                                        Logger.i("type:::::::" + String.valueOf(type));
//                                        Logger.i("msg:::::::" + String.valueOf(msg));
//                                        ProgressDialogManager.closeProgressDialog();
//                                        return super.onConnect(type, msg);
//                                    }
//
//                                    @Override
//                                    public void onFinish(File file) {
//                                        super.onFinish(file);
//                                        Logger.i("file:::::::" + file.getAbsolutePath());
//                                        ProgressDialogManager.closeProgressDialog();
//                                    }
//                                });
//                    }

                } else if (mMenuStatus == 2) {
                    // 删除
                }

                break;
        }
    }
}
