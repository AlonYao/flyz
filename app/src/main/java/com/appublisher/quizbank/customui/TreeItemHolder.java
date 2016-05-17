package com.appublisher.quizbank.customui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;

/**
 * 树形结构容器
 */
public class TreeItemHolder extends TreeNode.BaseNodeViewHolder<TreeItemHolder.TreeItem> {
    private ImageView mIvToggle;

    public TreeItemHolder(Context context) {
        super(context);
    }

    @SuppressLint("InflateParams")
    @Override
    public View createNodeView(TreeNode node, final TreeItemHolder.TreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        View view;
        switch (value.level) {
            case 1:
                view = inflater.inflate(R.layout.treeview_item_1, null, false);
                break;

            case 2:
                view = inflater.inflate(R.layout.treeview_item_2, null, false);
                break;

            case 3:
                view = inflater.inflate(R.layout.treeview_item_3, null, false);
                break;

            default:
                view = inflater.inflate(R.layout.treeview_item_3, null, false);
                break;
        }

        // View 初始化
        mIvToggle = (ImageView) view.findViewById(R.id.toggle);
        TextView tvName = (TextView) view.findViewById(R.id.treeview_name);
        ImageView ivDo = (ImageView) view.findViewById(R.id.treeview_do);
        ImageView ivWatch = (ImageView) view.findViewById(R.id.treeview_watch);
        TextView doneText = (TextView) view.findViewById(R.id.done);
        TextView totalText = (TextView) view.findViewById(R.id.total);

        // 知识点层级名字
        tvName.setText(value.name);

        //专项训练加统计信息
        if ("note".equals(value.note_type)) {
            doneText.setText(value.done + "/");
            totalText.setText(value.total + "");
        }

        // 做题按钮
        ivDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PracticeDescriptionActivity.class);
                intent.putExtra("paper_type", value.note_type);
                intent.putExtra("paper_name", value.name);
                intent.putExtra("hierarchy_id", value.id);
                intent.putExtra("hierarchy_level", value.level);
                intent.putExtra("umeng_entry", "List");
                context.startActivity(intent);
            }
        });

        // 看题按钮
        ivWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MeasureAnalysisActivity.class);
                intent.putExtra("analysis_type", value.note_type);
                intent.putExtra("hierarchy_id", value.id);
                intent.putExtra("hierarchy_level", value.level);
                intent.putExtra("umeng_entry", "List");
                if ("error".equals(value.note_type)) {
                    intent.putExtra("is_from_error", true);
                    intent.putExtra("umeng_entry_review", "Error");
                } else if ("collect".equals(value.note_type)) {
                    intent.putExtra("umeng_entry_review", "Collect");
                }
                intent.putExtra("from", "collect_or_error");

                context.startActivity(intent);
            }
        });

//        // 最后一层不显示开关
//        if (value.level == 3) {
//            mIvToggle.setVisibility(View.GONE);
//        }

        // 如果没有子节点，则不显示开关
        if (value.childs == null || value.childs.size() == 0) {
            mIvToggle.setVisibility(View.GONE);
        }

        // 全部专项不显示看题按钮
        if ("note".equals(value.note_type)) {
            ivWatch.setVisibility(View.GONE);
        } else {
            ivWatch.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        mIvToggle.setImageResource(active ? R.drawable.treeview_minus : R.drawable.treeview_add);
    }

    /**
     * 树形结构数据模型
     */
    public static class TreeItem {
        public int level;
        public int id;
        public String name;
        public String note_type;
        public int done;
        public int total;
        public ArrayList<HierarchyM> childs;

        public TreeItem(int level, int id, String name, int done, int total, String note_type) {
            this.level = level;
            this.id = id;
            this.name = name;
            this.note_type = note_type;
            this.total = total;
            this.done = done;
        }

        public TreeItem(int level,
                        int id,
                        String name,
                        int done,
                        int total,
                        String note_type,
                        ArrayList<HierarchyM> childs) {
            this.level = level;
            this.id = id;
            this.name = name;
            this.note_type = note_type;
            this.total = total;
            this.done = done;
            this.childs = childs;
        }
    }
}
