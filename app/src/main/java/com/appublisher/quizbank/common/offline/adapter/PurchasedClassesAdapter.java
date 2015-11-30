package com.appublisher.quizbank.common.offline.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.offline.activity.OfflineClassActivity;
import com.appublisher.quizbank.common.offline.model.business.OfflineConstants;
import com.appublisher.quizbank.common.offline.model.business.OfflineModel;
import com.appublisher.quizbank.common.offline.netdata.PurchasedClassM;

import java.util.ArrayList;

/**
 * 已购课程列表Adapter
 */
public class PurchasedClassesAdapter extends BaseAdapter {

    private OfflineClassActivity mActivity;
    private ArrayList<PurchasedClassM> mClasses;
    public boolean isDelete = false;
    public boolean isDownload = true;//下载按钮是否可点

    public PurchasedClassesAdapter(OfflineClassActivity activity,
                                   ArrayList<PurchasedClassM> classes) {
        mActivity = activity;
        mClasses = classes;
    }

    @Override
    public int getCount() {
        return mClasses == null ? 0 : mClasses.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // view初始化
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(
                    R.layout.purchased_classes_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvTitle =
                    (TextView) convertView.findViewById(R.id.item_purchased_classes_title);
            viewHolder.tvStatus =
                    (TextView) convertView.findViewById(R.id.item_purchased_classes_status);
            viewHolder.ivPlay =
                    (ImageView) convertView.findViewById(R.id.item_purchased_classes_play);
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.item_purchased_classes_cb);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Init View Status
        viewHolder.tvTitle.setTextColor(mActivity.getResources().getColor(R.color.common_text));

        // 设置内容
        setContent(viewHolder, position);

        return convertView;
    }

    /**
     * 设置内容
     *
     * @param viewHolder ViewHolder
     * @param position   位置
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mClasses == null || position >= mClasses.size()) return;

        PurchasedClassM classM = mClasses.get(position);
        if (classM == null) return;

        String date = "";
        try {
            String startTime = classM.getStart_time();
            date = startTime.substring(0, 10).replaceAll("-", ".").substring(5, 10);

            String firstNum = date.substring(0, 1);

            if ("0".equals(firstNum)) {
                date = date.substring(1, 5);
            }

        } catch (Exception e) {
            // Empty
        }

        // 课堂名称
        String title = date + " " + classM.getLector() + "：" + classM.getName();
        viewHolder.tvTitle.setText(title);

        switch (classM.getStatus()) {
            case 0:
                // 课堂未结束
                viewHolder.tvTitle.setTextColor(
                        mActivity.getResources().getColor(R.color.common_line));
                viewHolder.cb.setVisibility(View.GONE);
                viewHolder.tvStatus.setVisibility(View.GONE);
                viewHolder.ivPlay.setVisibility(View.GONE);
                break;

            case 1:
                // 转录中
                viewHolder.tvStatus.setVisibility(View.VISIBLE);
                viewHolder.tvStatus.setText(R.string.offline_transcribe);

                viewHolder.cb.setVisibility(View.GONE);
                viewHolder.ivPlay.setVisibility(View.GONE);
                break;

            case 2:
                // 可下载
                boolean isRoomIdDownload = OfflineModel.isRoomIdDownload(classM.getRoom_id(), mActivity.mCourseId);
                if (isRoomIdDownload && mActivity.mMenuStatus == 2) {
                    // 已下载 且 在删除状态
                    viewHolder.ivPlay.setVisibility(View.GONE);
                    viewHolder.tvStatus.setVisibility(View.GONE);
                    viewHolder.cb.setVisibility(View.VISIBLE);
                    viewHolder.cb.setChecked(OfflineModel.isPositionSelected(mActivity, position));
                    isDelete = true;
                } else if (isRoomIdDownload) {
                    // 已下载 且 不在删除状态
                    viewHolder.ivPlay.setVisibility(View.VISIBLE);
                    viewHolder.tvStatus.setVisibility(View.GONE);
                    viewHolder.cb.setVisibility(View.GONE);
                } else {
                    // 未下载
                    viewHolder.ivPlay.setVisibility(View.GONE);
                    if (OfflineModel.isRoomIdInDownloadList(classM.getRoom_id(), mActivity.mCourseId)) {
                        // 该任务在下载队列中
                        viewHolder.tvStatus.setVisibility(View.VISIBLE);
                        viewHolder.cb.setVisibility(View.GONE);
                        if (OfflineConstants.mCurDownloadRoomId.equals(classM.getRoom_id())
                                && OfflineConstants.mStatus == OfflineConstants.PROGRESS) {
                            String text = String.valueOf(OfflineConstants.mPercent) + "%";
                            viewHolder.tvStatus.setText(text);
                        } else if (mActivity.mMenuStatus == 2) {
                            viewHolder.ivPlay.setVisibility(View.GONE);
                            viewHolder.tvStatus.setVisibility(View.GONE);
                            viewHolder.cb.setVisibility(View.VISIBLE);
                            viewHolder.cb.setChecked(OfflineModel.isPositionSelected(mActivity, position));
                            isDelete = true;
                        } else {
                            viewHolder.tvStatus.setText(R.string.offline_waiting);
                        }
                    } else {
                        // 该任务没有被下载
                        viewHolder.tvStatus.setVisibility(View.GONE);
                        isDownload = false;
                        if (mActivity.mMenuStatus == 1) {
                            viewHolder.cb.setVisibility(View.VISIBLE);
                            viewHolder.cb.setChecked(
                                    OfflineModel.isPositionSelected(mActivity, position));
                        } else {
                            viewHolder.cb.setVisibility(View.GONE);
                        }
                    }
                }

                break;
        }
    }

    public class ViewHolder {
        TextView tvTitle;
        TextView tvStatus;
        ImageView ivPlay;
        CheckBox cb;
    }

}
