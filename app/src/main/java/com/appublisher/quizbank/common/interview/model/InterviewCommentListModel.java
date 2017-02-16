package com.appublisher.quizbank.common.interview.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewCommentListActivity;
import com.appublisher.quizbank.common.interview.adapter.InterviewCommentFilterNoteAdapter;
import com.appublisher.quizbank.common.interview.adapter.InterviewCommentFilterStatusAdapter;
import com.appublisher.quizbank.common.interview.netdata.CommentFilterResp;
import com.appublisher.quizbank.common.interview.netdata.InterviewCommentListResp;

import org.json.JSONObject;

/**
 * Created by jinbao on 2017/2/9.
 */

public class InterviewCommentListModel {

    private PopupWindow mStatusPop;
    private PopupWindow mNotePop;
    private CommentFilterResp mCommentFilterResp;
    private TextView statusSelectedText;
    private TextView noteSelectedText;


    public void showCommentStatusPop(InterviewCommentListActivity activity) {
        if (mCommentFilterResp == null) return;
        if (mStatusPop == null)
            initStatusPop(activity);

        mStatusPop.showAsDropDown(activity.mCommentStatusRl, 0, 2);
    }

    public void showCommentNotePop(InterviewCommentListActivity activity) {
        if (mCommentFilterResp == null) return;
        if (mNotePop == null)
            initNotePop(activity);
        mNotePop.showAsDropDown(activity.mCommentNoteRl, 0, 2);
    }

    public void initStatusPop(final InterviewCommentListActivity activity) {
        View statusView = LayoutInflater.from(activity).inflate(R.layout.pop_filter, null);
        GridView gridView = (GridView) statusView.findViewById(R.id.gridview);
        InterviewCommentFilterStatusAdapter adapter = new InterviewCommentFilterStatusAdapter(activity, mCommentFilterResp.getStatus());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                activity.mCommentStatusTv.setText(textView.getText());
                activity.mCommentStatusTv.setTextColor(activity.getResources().getColor(R.color.apptheme));
                activity.status_id = mCommentFilterResp.getStatus().get(position).getStatus_id();
                itemSelected(textView, activity);
                if (statusSelectedText != null && statusSelectedText != textView) {
                    itemCancel(statusSelectedText, activity);
                    activity.page = 1;
                }
                statusSelectedText = textView;
            }
        });
        mStatusPop = new PopupWindow(statusView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mStatusPop.setOutsideTouchable(true);
        mStatusPop.setBackgroundDrawable(
                activity.getResources().getDrawable(com.appublisher.quizbank.R.color.transparency));

        mStatusPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.mCommentStatusIv.setImageResource(R.drawable.wholepage_arrowdown);
            }
        });
        TextView statusCancle = (TextView) statusView.findViewById(R.id.vip_filter_cancel);
        TextView statusConfirm = (TextView) statusView.findViewById(R.id.vip_filter_confirm);
        statusCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStatusPop.isShowing())
                    mStatusPop.dismiss();
            }
        });
        statusConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStatusPop.isShowing()) {
                    activity.refreshData();
                    mStatusPop.dismiss();
                }

            }
        });
    }

    public void initNotePop(final InterviewCommentListActivity activity) {
        View statusView = LayoutInflater.from(activity).inflate(R.layout.pop_filter, null);
        GridView gridView = (GridView) statusView.findViewById(R.id.gridview);
        InterviewCommentFilterNoteAdapter adapter = new InterviewCommentFilterNoteAdapter(activity, mCommentFilterResp.getNotes());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                activity.mCommentNoteTv.setText(textView.getText());
                activity.mCommentNoteTv.setTextColor(activity.getResources().getColor(R.color.apptheme));
                activity.note_id = mCommentFilterResp.getNotes().get(position).getNote_id();
                itemSelected(textView, activity);
                if (noteSelectedText != null && noteSelectedText != textView) {
                    itemCancel(noteSelectedText, activity);
                    activity.page = 1;
                }
                noteSelectedText = textView;
            }
        });
        mNotePop = new PopupWindow(statusView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mNotePop.setOutsideTouchable(true);
        mNotePop.setBackgroundDrawable(
                activity.getResources().getDrawable(com.appublisher.quizbank.R.color.transparency));

        mNotePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.mCommentNoteIv.setImageResource(R.drawable.wholepage_arrowdown);
            }
        });
        TextView statusCancle = (TextView) statusView.findViewById(R.id.vip_filter_cancel);
        TextView statusConfirm = (TextView) statusView.findViewById(R.id.vip_filter_confirm);
        statusCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotePop.isShowing())
                    mNotePop.dismiss();
            }
        });
        statusConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotePop.isShowing()) {
                    activity.refreshData();
                    mNotePop.dismiss();
                }

            }
        });
    }

    /**
     * 处理接口回调数据
     *
     * @param jsonObject
     */
    public void dealCommentFilterResp(JSONObject jsonObject) {
        CommentFilterResp commentFilterResp = GsonManager.getModel(jsonObject, CommentFilterResp.class);
        if (commentFilterResp.getResponse_code() == 1) {
            mCommentFilterResp = commentFilterResp;
            CommentFilterResp.NotesBean notesBean = new CommentFilterResp.NotesBean();
            notesBean.setNote_id(-1);
            notesBean.setNote_name("全部");
            mCommentFilterResp.getNotes().add(0, notesBean);
            CommentFilterResp.StatusBean statusBean = new CommentFilterResp.StatusBean();
            statusBean.setStatus_id(-1);
            statusBean.setComment_status("全部");
            mCommentFilterResp.getStatus().add(0, statusBean);
        }
    }

    public void dealCommentListResp(JSONObject jsonObject, InterviewCommentListActivity activity) {
        InterviewCommentListResp interviewCommentListResp = GsonManager.getModel(jsonObject, InterviewCommentListResp.class);
        if (interviewCommentListResp.getResponse_code() == 1) {
            if (activity.page == 1) {
                activity.mList.clear();
            }

            activity.mList.addAll(interviewCommentListResp.getList());
            activity.mAdapter.notifyDataSetChanged();

            if (activity.mList.size() == 0) {
                activity.mNullView.setVisibility(View.VISIBLE);
                activity.mListView.setVisibility(View.GONE);
                if (activity.status_id == -1 && activity.note_id == -1) {
                    activity.mNullStatus.setText("您还没有名师点评哦！");
                } else {
                    activity.mNullStatus.setText("没有相关条件的点评");
                }
            } else {
                activity.mNullView.setVisibility(View.GONE);
                activity.mListView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 取消选中item
     *
     * @param textView
     * @param context
     */
    public void itemCancel(TextView textView, Context context) {
        textView.setTextColor(context.getResources().getColor(R.color.common_text));
        Drawable drawable = textView.getBackground();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(
                    context.getResources().getColor(R.color.vip_filter_item_unselect));
        }
    }

    /**
     * 选中item
     *
     * @param textView
     * @param context
     */
    public void itemSelected(TextView textView, Context context) {
        textView.setTextColor(Color.WHITE);
        Drawable drawable = textView.getBackground();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(context.getResources().getColor(R.color.apptheme));
        }
    }
}
