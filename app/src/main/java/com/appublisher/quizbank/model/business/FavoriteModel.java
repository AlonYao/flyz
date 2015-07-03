package com.appublisher.quizbank.model.business;

import android.view.View;
import android.widget.LinearLayout;

import com.appublisher.quizbank.customui.TreeItemHolder;
import com.appublisher.quizbank.fragment.FavoriteFragment;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyResp;
import com.appublisher.quizbank.model.netdata.hierarchy.NoteGroupM;
import com.appublisher.quizbank.model.netdata.hierarchy.NoteItemM;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * FavoriteFragment Model
 */
public class FavoriteModel {

    private static FavoriteFragment mFragment;

    public static void dealNoteHierarchyResp(FavoriteFragment fragment, JSONObject response) {
        if (response == null) {
            fragment.mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        Gson gson = GsonManager.initGson();
        HierarchyResp hierarchyResp = gson.fromJson(response.toString(), HierarchyResp.class);

        if (hierarchyResp == null || hierarchyResp.getResponse_code() != 1) {
            fragment.mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        ArrayList<HierarchyM> hierarchys = hierarchyResp.getHierarchy();

        if (hierarchys == null || hierarchys.size() == 0) {
            fragment.mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        int hierarchysSize = hierarchys.size();
        if (hierarchysSize == 0) {
            fragment.mIvNull.setVisibility(View.VISIBLE);
        } else {
            fragment.mIvNull.setVisibility(View.GONE);
            for (int i = 0; i < hierarchysSize; i++) {
                HierarchyM hierarchy = hierarchys.get(i);

                if (hierarchy == null) continue;

                mFragment = fragment;
                addHierarchy(hierarchy);
            }
        }
    }

    /**
     * 添加知识点层级第一层
     * @param hierarchy 第一层数据
     */
    private static void addHierarchy(HierarchyM hierarchy) {
        if (mFragment.mContainer == null) return;

        TreeNode root = TreeNode.root();

        TreeNode firstRoot = new TreeNode(
                new TreeItemHolder.TreeItem(
                        1,
                        hierarchy.getCategory_id(),
                        hierarchy.getName(),
                        "collect"));

        root.addChild(firstRoot);

        // 添加第二层
        ArrayList<NoteGroupM> noteGroups = hierarchy.getNote_group();
        addNoteGroup(firstRoot, noteGroups);

        // rootContainer
        LinearLayout rootContainer = new LinearLayout(mFragment.mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, 30);
        rootContainer.setLayoutParams(lp);
        rootContainer.setOrientation(LinearLayout.VERTICAL);

        AndroidTreeView tView = new AndroidTreeView(mFragment.mActivity, root);
        tView.setDefaultViewHolder(TreeItemHolder.class);

        rootContainer.addView(tView.getView());

        mFragment.mContainer.addView(rootContainer);
    }

    /**
     * 添加第二层级
     * @param firstRoot 第一层级节点
     * @param noteGroups 第二层级数据
     */
    private static void addNoteGroup(TreeNode firstRoot, ArrayList<NoteGroupM> noteGroups) {
        if (noteGroups == null || noteGroups.size() == 0) return;

        int size = noteGroups.size();
        for (int i = 0; i < size; i++) {
            NoteGroupM noteGroup = noteGroups.get(i);

            if (noteGroup == null) continue;
            TreeNode secondRoot = new TreeNode(
                    new TreeItemHolder.TreeItem(
                            2,
                            noteGroup.getGroup_id(),
                            noteGroup.getName(),
                            "collect"));
            firstRoot.addChild(secondRoot);

            addNotes(secondRoot, noteGroup.getNotes());
        }
    }

    /**
     * 添加第三层
     * @param secondRoot 第二层级节点
     * @param notes 第三层级数据
     */
    private static void addNotes(TreeNode secondRoot, ArrayList<NoteItemM> notes) {
        if (notes == null || notes.size() == 0) return;

        int size = notes.size();
        for (int i = 0; i < size; i++) {
            NoteItemM note = notes.get(i);

            if (note == null) continue;
            TreeNode thirdRoot = new TreeNode(
                    new TreeItemHolder.TreeItem(
                            3,
                            note.getNote_id(),
                            note.getName(),
                            "collect"));
            secondRoot.addChild(thirdRoot);
        }
    }
}
