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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.offline.adapter.PurchasedClassesAdapter;
import com.appublisher.quizbank.model.offline.model.business.OfflineModel;
import com.appublisher.quizbank.model.offline.model.db.OfflineDAO;
import com.appublisher.quizbank.model.offline.netdata.PurchasedClassM;
import com.appublisher.quizbank.utils.Logger;
import com.appublisher.quizbank.utils.Utils;
import com.duobeiyun.DuobeiYunClient;
import com.duobeiyun.listener.DownloadTaskListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 离线模块课程列表
 */
public class OfflineClassActivity extends AppCompatActivity implements View.OnClickListener{

    public int mMenuStatus; // 1：下载 2：删除
    public static HashMap<Integer, Boolean> mSelectedMap; // 用来控制CheckBox的选中状况
    public static ArrayList<Integer> mDownloadList; // 下载列表（保存position）
    public Button mBtnBottom;
    public PurchasedClassesAdapter mAdapter;
    public String mFrom;
    public static int mPercent;
    public static int mCurDownloadPosition;
    public static String mCurDownloadRoomId;
    public final static int DOWNLOAD_BEGIN = 1;
    public final static int DOWNLOAD_PROGRESS = 2;
    public final static int DOWNLOAD_FINISH = 3;
    public static Handler mHandler;
    public static boolean mHasUnFinishTask;

    public static ArrayList<PurchasedClassM> mClasses;
    public static ListView mLv;

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
                    case DOWNLOAD_BEGIN:
                        if (mDownloadList == null || mDownloadList.size() == 0) break;
                        mCurDownloadPosition = mDownloadList.get(0);

                        mCurDownloadRoomId = OfflineModel.getRoomIdByPosition(mCurDownloadPosition);
                        if (mCurDownloadRoomId == null || mCurDownloadRoomId.length() == 0) return;

                        DuobeiYunClient.download(
                                activity,
                                mCurDownloadRoomId,
                                new DownloadTaskListener() {
                                    @Override
                                    public void onProgress(int progress, int fileLength) {
                                        super.onProgress(progress, fileLength);
                                        mPercent = progress;
                                        mHandler.sendEmptyMessage(DOWNLOAD_PROGRESS);
                                    }

                                    @Override
                                    public void onError(String error) {
                                        super.onError(error);
                                        Logger.e(error);
                                    }

                                    @Override
                                    public void onFinish(File file) {
                                        super.onFinish(file);
                                        mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                                    }
                                });

                        break;

                    case DOWNLOAD_PROGRESS:
                        View view = Utils.getViewByPosition(mCurDownloadPosition, mLv);
                        TextView tvStatus =
                                (TextView) view.findViewById(R.id.item_purchased_classes_status);
                        tvStatus.setVisibility(View.VISIBLE);
                        String text = String.valueOf(mPercent) + "%";
                        tvStatus.setText(text);
                        break;

                    case DOWNLOAD_FINISH:
                        // 更新数据库
                        OfflineDAO.saveRoomId(mCurDownloadRoomId);

                        // 更新UI
                        view = Utils.getViewByPosition(mCurDownloadPosition, mLv);
                        tvStatus = (TextView) view.findViewById(R.id.item_purchased_classes_status);
                        tvStatus.setVisibility(View.GONE);
                        ImageView ivPlay =
                                (ImageView) view.findViewById(R.id.item_purchased_classes_play);
                        ivPlay.setVisibility(View.VISIBLE);

                        // 更新下载列表，继续下载其他视频
                        mDownloadList.remove(0);

                        if (mDownloadList.size() == 0) {
                            mHasUnFinishTask = false;
                        } else {
                            mHasUnFinishTask = true;
                            mHandler.sendEmptyMessage(DOWNLOAD_BEGIN);
                        }

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
        mCurDownloadPosition = -1;

        mAdapter = new PurchasedClassesAdapter(this, mClasses);
        mLv.setAdapter(mAdapter);

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String roomId = OfflineModel.getRoomIdByPosition(position);
                if (OfflineModel.isRoomIdDownload(roomId) && mMenuStatus != 2) {
                    // 如果视频已经成功下载，且不是删除状态
                    String url = DuobeiYunClient.playUrl(roomId);
                    Intent intent = new Intent(OfflineClassActivity.this, WebViewActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("bar_title", OfflineModel.getClassNameByPosition(position));
                    startActivity(intent);

                } else if (mMenuStatus == 1 || mMenuStatus == 2) {
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
                CheckBox cb = OfflineModel.getCheckBoxByPosition(this, i);
                if (cb == null) continue;
                cb.setChecked(true);
                mSelectedMap.put(i, true);
            }

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
            if ("local".equals(mFrom)) {
                MenuItemCompat.setShowAsAction(menu.add("删除").setIcon(
                        R.drawable.offline_delete), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            } else if ("all".equals(mFrom)) {
                MenuItemCompat.setShowAsAction(menu.add("下载").setIcon(
                        R.drawable.offline_download), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
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
                    if (mDownloadList == null) mDownloadList = new ArrayList<>();
                    OfflineModel.createSelectedPositionList(this); // 生成position列表

                    mAdapter.notifyDataSetChanged();

                    if (!mHasUnFinishTask) {
                        mHandler.sendEmptyMessage(DOWNLOAD_BEGIN);
                    }

                    OfflineModel.setCancel(this);

                } else if (mMenuStatus == 2) {
                    // 删除
                    mDownloadList = new ArrayList<>();
                    OfflineModel.createSelectedPositionList(this); // 生成position列表

                    for (Integer position : mDownloadList) {
                        String roomId = OfflineModel.getRoomIdByPosition(position);
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
