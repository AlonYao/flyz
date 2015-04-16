package com.appublisher.quizbank.customui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appublisher.quizbank.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * 树形结构容器
 */
public class TreeItemHolder extends TreeNode.BaseNodeViewHolder<TreeItemHolder.TreeItem>{
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
                view = inflater.inflate(R.layout.treeview_item_1, null, false);
                break;
        }

        // View 初始化
        mIvToggle = (ImageView) view.findViewById(R.id.toggle);
        TextView tvName = (TextView) view.findViewById(R.id.treeview_name);
        ImageView ivDo = (ImageView) view.findViewById(R.id.treeview_do);

        // 知识点层级名字
        tvName.setText(value.name);

        // 做题按钮
        ivDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, value.name, Toast.LENGTH_SHORT).show();
            }
        });

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

        public TreeItem(int level, int id, String name) {
            this.level = level;
            this.id = id;
            this.name = name;
        }
    }
}
