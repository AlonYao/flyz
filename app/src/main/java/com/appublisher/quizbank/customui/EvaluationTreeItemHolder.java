package com.appublisher.quizbank.customui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created on 15/12/21.
 */
public class EvaluationTreeItemHolder extends TreeNode.BaseNodeViewHolder<EvaluationTreeItemHolder.TreeItem> {
    private ImageView mIvToggle;

    public EvaluationTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, final EvaluationTreeItemHolder.TreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (value.level) {
            case 1:
                view = inflater.inflate(R.layout.treeview_level_item_1, null, false);
                break;

            case 2:
                view = inflater.inflate(R.layout.treeview_level_item_2, null, false);
                break;

            case 3:
                view = inflater.inflate(R.layout.treeview_level_item_3, null, false);
                break;

            default:
                view = inflater.inflate(R.layout.treeview_level_item_1, null, false);
                break;
        }

        // View 初始化
        mIvToggle = (ImageView) view.findViewById(R.id.toggle);
        TextView tvName = (TextView) view.findViewById(R.id.treeview_name);
        ImageView level_1 = (ImageView) view.findViewById(R.id.level_1);
        ImageView level_2 = (ImageView) view.findViewById(R.id.level_2);
        ImageView level_3 = (ImageView) view.findViewById(R.id.level_3);
        ImageView level_4 = (ImageView) view.findViewById(R.id.level_4);
        ImageView level_5 = (ImageView) view.findViewById(R.id.level_5);
        // 知识点层级名字
        tvName.setText(value.name);
        switch (value.ev_level) {
            case 1:
                level_1.setImageDrawable(context.getResources().getDrawable(R.drawable.level_5));
                break;
            case 2:
                level_1.setImageDrawable(context.getResources().getDrawable(R.drawable.level_5));
                level_2.setImageDrawable(context.getResources().getDrawable(R.drawable.level_6));
                break;
            case 3:
                level_1.setImageDrawable(context.getResources().getDrawable(R.drawable.level_5));
                level_2.setImageDrawable(context.getResources().getDrawable(R.drawable.level_6));
                level_3.setImageDrawable(context.getResources().getDrawable(R.drawable.level_7));
                break;
            case 4:
                level_1.setImageDrawable(context.getResources().getDrawable(R.drawable.level_5));
                level_2.setImageDrawable(context.getResources().getDrawable(R.drawable.level_6));
                level_3.setImageDrawable(context.getResources().getDrawable(R.drawable.level_7));
                level_4.setImageDrawable(context.getResources().getDrawable(R.drawable.level_8));
                break;
            case 5:
                level_1.setImageDrawable(context.getResources().getDrawable(R.drawable.level_5));
                level_2.setImageDrawable(context.getResources().getDrawable(R.drawable.level_6));
                level_3.setImageDrawable(context.getResources().getDrawable(R.drawable.level_7));
                level_4.setImageDrawable(context.getResources().getDrawable(R.drawable.level_8));
                level_5.setImageDrawable(context.getResources().getDrawable(R.drawable.level_9));
                break;
            default:
                break;
        }
        // 最后一层不显示开关
        if (value.level == 3) {
            mIvToggle.setVisibility(View.GONE);
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
        public int ev_level;

        public TreeItem(int level, int id, String name, int done, int total, String note_type, int ev_level) {
            this.level = level;
            this.id = id;
            this.name = name;
            this.note_type = note_type;
            this.total = total;
            this.done = done;
            this.ev_level = ev_level;
        }
    }
}
