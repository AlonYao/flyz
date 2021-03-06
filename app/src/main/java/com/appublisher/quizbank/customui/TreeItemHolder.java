package com.appublisher.quizbank.customui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.common.measure.activity.MeasureDescriptionActivity;
import com.appublisher.quizbank.model.business.KnowledgeTreeModel;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 树形结构容器
 */
public class TreeItemHolder extends TreeNode.BaseNodeViewHolder<TreeItemHolder.TreeItem> {
    private ImageView mIvToggle;
    private String mType;

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
        LinearLayout evLevel = (LinearLayout) view.findViewById(R.id.ev_level);
        ImageView level_1 = (ImageView) view.findViewById(R.id.level_1);
        ImageView level_2 = (ImageView) view.findViewById(R.id.level_2);
        ImageView level_3 = (ImageView) view.findViewById(R.id.level_3);
        ImageView level_4 = (ImageView) view.findViewById(R.id.level_4);
        ImageView level_5 = (ImageView) view.findViewById(R.id.level_5);
        RelativeLayout vipLayout = (RelativeLayout) view.findViewById(R.id.treeview_vip);

        mType = value.type;

        // 知识点层级名字
        tvName.setText(value.name);

        // 如果没有子节点，则不显示开关
        if (value.childs == null || value.childs.size() == 0) {
            mIvToggle.setVisibility(View.GONE);
        }

        if (KnowledgeTreeModel.TYPE_EVALUATION.equals(value.type)) {
            // 能力评估页面特殊处理
            evLevel.setVisibility(View.VISIBLE);

            switch (value.ev_level) {
                case 1:
                    level_1.setImageResource(R.drawable.level_5);
                    break;
                case 2:
                    level_1.setImageResource(R.drawable.level_5);
                    level_2.setImageResource(R.drawable.level_6);
                    break;
                case 3:
                    level_1.setImageResource(R.drawable.level_5);
                    level_2.setImageResource(R.drawable.level_6);
                    level_3.setImageResource(R.drawable.level_7);
                    break;
                case 4:
                    level_1.setImageResource(R.drawable.level_5);
                    level_2.setImageResource(R.drawable.level_6);
                    level_3.setImageResource(R.drawable.level_7);
                    level_4.setImageResource(R.drawable.level_8);
                    break;
                case 5:
                    level_1.setImageResource(R.drawable.level_5);
                    level_2.setImageResource(R.drawable.level_6);
                    level_3.setImageResource(R.drawable.level_7);
                    level_4.setImageResource(R.drawable.level_8);
                    level_5.setImageResource(R.drawable.level_9);
                    break;
                default:
                    break;
            }
        } else if (KnowledgeTreeModel.TYPE_NOTE.equals(value.type)) {
            // 专项训练特殊处理
            doneText.setVisibility(View.VISIBLE);
            totalText.setVisibility(View.VISIBLE);

            String done = value.done + "/";
            doneText.setText(done);
            totalText.setText(String.valueOf(value.total));

            ivDo.setVisibility(View.VISIBLE);

        } else if (KnowledgeTreeModel.TYPE_VIP_XC_REPORT.equals(value.type)) {
            ivWatch.setVisibility(View.GONE);
            ivDo.setVisibility(View.GONE);
            vipLayout.setVisibility(View.VISIBLE);
            TextView tvRight = (TextView) view.findViewById(R.id.treeview_rightnum);
            TextView tvTotal = (TextView) view.findViewById(R.id.treeview_totalnum);
            TextView tvSpeed = (TextView) view.findViewById(R.id.treeview_speed);
            tvRight.setText(String.valueOf(value.right));
            tvTotal.setText(String.valueOf(value.total));
            tvSpeed.setText(String.valueOf(Utils.getSpeedByRound(value.duration, value.total)));

        } else {
            ivWatch.setVisibility(View.VISIBLE);
            ivDo.setVisibility(View.VISIBLE);
        }

        // 做题按钮
        ivDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MeasureDescriptionActivity.class);
                intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, value.type);
                intent.putExtra(MeasureConstants.INTENT_HIERARCHY_ID, value.id);
                context.startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Quiz");
                if (KnowledgeTreeModel.TYPE_COLLECT.equals(mType)) {
                    UmengManager.onEvent(context, "Collect", map);
                } else if (KnowledgeTreeModel.TYPE_ERROR.equals(mType)) {
                    UmengManager.onEvent(context, "Error", map);
                } else if (KnowledgeTreeModel.TYPE_NOTE.equals(mType)) {
                    UmengManager.onEvent(context, "Notelist", map);
                }
            }
        });

        // 看题按钮
        ivWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MeasureAnalysisActivity.class);
                intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, value.type);
                intent.putExtra(MeasureConstants.INTENT_HIERARCHY_ID, value.id);
                intent.putExtra(MeasureConstants.INTENT_IS_FROM_FOLDER, true);
                context.startActivity(intent);

                // Umeng
                if (KnowledgeTreeModel.TYPE_COLLECT.equals(mType)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "Review");
                    UmengManager.onEvent(context, "Collect", map);
                } else if (KnowledgeTreeModel.TYPE_ERROR.equals(mType)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "Review");
                    UmengManager.onEvent(context, "Error", map);
                }
            }
        });

        // Umeng
        node.setClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                if (!KnowledgeTreeModel.TYPE_NOTE.equals(mType)) return;

                String action = "Expand";
                if (node.isExpanded()) {
                    action = "Fold";
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", action);
                if (KnowledgeTreeModel.TYPE_COLLECT.equals(mType)) {
                    UmengManager.onEvent(context, "Collect", map);
                } else if (KnowledgeTreeModel.TYPE_ERROR.equals(mType)) {
                    UmengManager.onEvent(context, "Error", map);
                } else if (KnowledgeTreeModel.TYPE_NOTE.equals(mType)) {
                    UmengManager.onEvent(context, "Notelist", map);
                }
            }
        });

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
        public String type;
        public int done;
        public int total;
        public ArrayList<HierarchyM> childs;
        public int ev_level;
        public int right;
        public int duration;

        public TreeItem(int level,
                        int id,
                        String name,
                        int done,
                        int total,
                        String type,
                        ArrayList<HierarchyM> childs,
                        int ev_level) {
            this.level = level;
            this.id = id;
            this.name = name;
            this.type = type;
            this.total = total;
            this.done = done;
            this.childs = childs;
            this.ev_level = ev_level;
        }

        public TreeItem(int level,
                        int id,
                        String name,
                        int done,
                        int total,
                        String type,
                        ArrayList<HierarchyM> childs,
                        int ev_level,
                        int right,
                        int duration) {
            this.level = level;
            this.id = id;
            this.name = name;
            this.type = type;
            this.total = total;
            this.done = done;
            this.childs = childs;
            this.ev_level = ev_level;
            this.right = right;
            this.duration = duration;
        }
    }
}
