package com.appublisher.quizbank.model.offline.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.offline.adapter.PurchasedClassesAdapter;
import com.appublisher.quizbank.model.offline.model.business.OfflineConstants;
import com.appublisher.quizbank.model.offline.model.business.OfflineModel;
import com.appublisher.quizbank.model.offline.model.db.OfflineDAO;
import com.appublisher.quizbank.model.offline.netdata.PurchasedClassM;
import com.appublisher.quizbank.utils.ToastManager;
import com.duobeiyun.DuobeiYunClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 离线模块课程列表
 */
public class OfflineClassActivity extends AppCompatActivity implements View.OnClickListener{

    public int mMenuStatus; // 1：下载 2：删除
    public Button mBtnBottom;
    public int mAllSelectFlag; // 控制全选、取消全选
    public String mFrom;
    public int mCourseId;

    /** static **/
    public HashMap<Integer, Boolean> mSelectedMap; // 用来控制CheckBox的选中状况
    public static PurchasedClassesAdapter mAdapter;
    public static Handler mHandler;
    public ArrayList<PurchasedClassM> mClasses;
    public static ListView mLv;

    /** final **/
    public final static int DOWNLOAD_PROGRESS = 1;
    public final static int DOWNLOAD_FINISH = 2;

    private static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case DOWNLOAD_PROGRESS:
                        mAdapter.notifyDataSetChanged();
                        break;

                    case DOWNLOAD_FINISH:
                        mAdapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_class);

        // Toolbar
        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, getIntent().getStringExtra("bar_title"));

        // Init view
        mLv = (ListView) findViewById(R.id.offline_class_lv);
        mBtnBottom = (Button) findViewById(R.id.offline_bottom_btn);

        mBtnBottom.setOnClickListener(this);

        // Init data
        mSelectedMap = new HashMap<>();
        mHandler = new MsgHandler(this);
        mClasses = (ArrayList<PurchasedClassM>) getIntent().getSerializableExtra("class_list");
        mFrom = getIntent().getStringExtra("from");
        mCourseId = getIntent().getIntExtra("course_id", 0);

        mAdapter = new PurchasedClassesAdapter(this, mClasses);
        mLv.setAdapter(mAdapter);

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String roomId = OfflineModel.getRoomIdByPosition(
                        OfflineClassActivity.this, position);

                if (OfflineModel.isRoomIdDownload(roomId) && mMenuStatus != 2) {
                    // 如果视频已经成功下载，且不是删除状态
                    String url = DuobeiYunClient.playUrl(roomId);
                    Intent intent = new Intent(OfflineClassActivity.this, WebViewActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("bar_title", OfflineModel.getClassNameByPosition(
                            OfflineClassActivity.this, position));
                    startActivity(intent);

                } else if (mMenuStatus == 1 || mMenuStatus == 2) {
                    CheckBox cb = (CheckBox) view.findViewById(R.id.item_purchased_classes_cb);

                    if (cb.getVisibility() == View.GONE) return;

                    cb.toggle();
                    mSelectedMap.put(position, cb.isChecked());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OfflineConstants.mStatus != OfflineConstants.DONE) {
            new OfflineModel(new OfflineModel.downloadProgressListener() {
                @Override
                public void onProgress(int progress) {
                    mHandler.sendEmptyMessage(DOWNLOAD_PROGRESS);
                }

                @Override
                public void onFinish() {
                    mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 判断是否超时
        if (OfflineConstants.mDownloadList != null && OfflineConstants.mDownloadList.size() != 0
                && (System.currentTimeMillis() - OfflineConstants.mLastTimestamp) > 60000) {
            OfflineModel.removeTopRoomId();
            OfflineModel.startDownload(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();

        } else if ("删除".equals(item.getTitle())) {
            mMenuStatus = 2;
            invalidateOptionsMenu();

            OfflineModel.initSelectedMap(this);

            mAdapter.notifyDataSetChanged();

            mBtnBottom.setVisibility(View.VISIBLE);
            mBtnBottom.setText(R.string.offline_delete_btn);

        } else if ("下载".equals(item.getTitle())) {
            mMenuStatus = 1;
            invalidateOptionsMenu();

            mAdapter.notifyDataSetChanged();

            mBtnBottom.setVisibility(View.VISIBLE);
            mBtnBottom.setText(R.string.offline_download_btn);

        } else if ("全选".equals(item.getTitle())) {
            if (mClasses == null) return super.onOptionsItemSelected(item);

            int size = mClasses.size();
            for (int i = 0; i < size; i++) {
                if (mAllSelectFlag == 0) {
                    mSelectedMap.put(i, true);
                } else {
                    mSelectedMap.put(i, false);
                }
            }

            if (mAllSelectFlag == 0) {
                mAllSelectFlag = 1;
            } else {
                mAllSelectFlag = 0;
            }

            mAdapter.notifyDataSetChanged();

        } else if ("取消".equals(item.getTitle())) {
            OfflineModel.setCancel(this);
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
            if ("all".equals(mFrom)) {
                MenuItemCompat.setShowAsAction(menu.add("删除").setIcon(
                        R.drawable.offline_delete), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
                MenuItemCompat.setShowAsAction(menu.add("下载").setIcon(
                        R.drawable.offline_download), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            } else {
                MenuItemCompat.setShowAsAction(menu.add("删除").setIcon(
                        R.drawable.offline_delete), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            }
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
                    if (OfflineConstants.mDownloadList == null)
                        OfflineConstants.mDownloadList = new ArrayList<>();

                    // 数据异常处理
                    if (mClasses == null) return;

                    int size = mClasses.size();
                    for (int i = 0; i < size; i++) {
                        if (mSelectedMap.containsKey(i) && mSelectedMap.get(i)) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("position", i);
                            map.put("room_id", OfflineModel.getRoomIdByPosition(this, i));
                            map.put("course_id", mCourseId);
                            OfflineConstants.mDownloadList.add(map);
                        }
                    }

                    if (OfflineConstants.mStatus == OfflineConstants.DONE) {
                        OfflineModel.startDownload(this);
                        mAdapter.notifyDataSetChanged();
                    }

                    new OfflineModel(new OfflineModel.downloadProgressListener() {
                        @Override
                        public void onProgress(int progress) {
                            mHandler.sendEmptyMessage(DOWNLOAD_PROGRESS);
                        }

                        @Override
                        public void onFinish() {
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                        }
                    });

                    OfflineModel.setCancel(this);

                    ToastManager.showToast(this, "下载任务正在进行，请不要将应用关闭或切换至后台");

                } else if (mMenuStatus == 2) {
                    // 删除
                    ArrayList<Integer> list = new ArrayList<>();

                    // 数据异常处理
                    if (mClasses == null) return;

                    int size = mClasses.size();
                    for (int i = 0; i < size; i++) {
                        if (mSelectedMap.containsKey(i) && mSelectedMap.get(i)) {
                            list.add(i);
                        }
                    }

                    for (Integer position : list) {
                        String roomId = OfflineModel.getRoomIdByPosition(this, position);
                        try {
                            DuobeiYunClient.delete(roomId);
                            OfflineDAO.deleteRoomId(roomId);
                            mClasses.remove((int) position);
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            // Empty
                        }
                    }

                    OfflineModel.setCancel(this);
                }

                break;
        }
    }
}
