package com.appublisher.quizbank.model.business;

import android.widget.LinearLayout;

import com.appublisher.quizbank.activity.SpecialProjectActivity;
import com.appublisher.quizbank.customui.TreeItemHolder;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 专项练习Activity Model
 */
public class SpecialProjectModel {

    private static SpecialProjectActivity mActivity;

    /**
     * 处理专项练习回调
     * @param activity SpecialProjectActivity
     * @param response 回调数据
     */
    public static void dealNoteHierarchyResp(SpecialProjectActivity activity, JSONObject response) {
        mActivity = activity;

        if (response == null) return;

        HierarchyResp hierarchyResp = GsonManager.getModel(response, HierarchyResp.class);

        if (hierarchyResp == null || hierarchyResp.getResponse_code() != 1) return;
        ArrayList<HierarchyM> hierarchys = hierarchyResp.getHierarchy();

        if (hierarchys == null || hierarchys.size() == 0) return;

        int hierarchysSize = hierarchys.size();
        for (int i = 0; i < hierarchysSize; i++) {
            HierarchyM hierarchy = hierarchys.get(i);

            if (hierarchy == null) continue;
            addHierarchy(hierarchy);
        }
    }

    /**
     * 添加知识点层级第一层
     * @param hierarchy 第一层数据
     */
    private static void addHierarchy(HierarchyM hierarchy) {
        if (mActivity.mContainer == null) return;

        TreeNode root = TreeNode.root();

        TreeNode firstRoot = new TreeNode(
                new TreeItemHolder.TreeItem(
                        1,
                        hierarchy.getCategory_id(),
                        hierarchy.getName(),
                        hierarchy.getDone(),
                        hierarchy.getTotal(),
                        "note",
                        hierarchy.getChilds()));

        root.addChild(firstRoot);

        // 添加第二层
        ArrayList<HierarchyM> hierarchys = hierarchy.getChilds();
        addNoteGroup(firstRoot, hierarchys);

        // rootContainer
        LinearLayout rootContainer = new LinearLayout(mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, 30);
        rootContainer.setLayoutParams(lp);
        rootContainer.setOrientation(LinearLayout.VERTICAL);

        AndroidTreeView tView = new AndroidTreeView(mActivity, root);
        tView.setDefaultViewHolder(TreeItemHolder.class);

        rootContainer.addView(tView.getView());

        mActivity.mContainer.addView(rootContainer);
    }

    /**
     * 添加第二层级
     * @param firstRoot 第一层级节点
     * @param noteGroups 第二层级数据
     */
    private static void addNoteGroup(TreeNode firstRoot, ArrayList<HierarchyM> noteGroups) {
        if (noteGroups == null || noteGroups.size() == 0) return;

        int size = noteGroups.size();
        for (int i = 0; i < size; i++) {
            HierarchyM hierarchy = noteGroups.get(i);

            if (hierarchy == null) continue;
            TreeNode secondRoot = new TreeNode(
                    new TreeItemHolder.TreeItem(
                            2,
                            0,
                            hierarchy.getName(),
                            hierarchy.getDone(),
                            hierarchy.getTotal(),
                            "note",
                            hierarchy.getChilds()));
            firstRoot.addChild(secondRoot);

            addNotes(secondRoot, hierarchy.getChilds());
        }
    }

    /**
     * 添加第三层
     * @param secondRoot 第二层级节点
     * @param notes 第三层级数据
     */
    private static void addNotes(TreeNode secondRoot, ArrayList<HierarchyM> notes) {
        if (notes == null || notes.size() == 0) return;

        int size = notes.size();
        for (int i = 0; i < size; i++) {
            HierarchyM hierarchy = notes.get(i);

            if (hierarchy == null) continue;
            TreeNode thirdRoot = new TreeNode(
                    new TreeItemHolder.TreeItem(
                            3,
                            0,
                            hierarchy.getName(),
                            hierarchy.getDone(),
                            hierarchy.getTotal(),
                            "note",
                            hierarchy.getChilds()));
            secondRoot.addChild(thirdRoot);
        }
    }
}
