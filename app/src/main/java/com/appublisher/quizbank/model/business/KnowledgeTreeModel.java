package com.appublisher.quizbank.model.business;

import android.content.Context;
import android.widget.LinearLayout;

import com.appublisher.quizbank.customui.TreeItemHolder;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 知识点树形结构
 */
public class KnowledgeTreeModel {

    public static final String TYPE_NOTE = "note";
    public static final String TYPE_COLLECT = "collect";
    public static final String TYPE_ERROR = "error";

    private Context mContext;
    private LinearLayout mContainer;
    private String mType;
    private ICheckHierarchyResp mICheckHierarchyResp;

    public KnowledgeTreeModel(Context context, LinearLayout view, String type) {
        this.mContext = context;
        this.mContainer = view;
        this.mType = type;
    }

    public KnowledgeTreeModel(Context context,
                              LinearLayout view,
                              String type,
                              ICheckHierarchyResp iCheckHierarchyResp) {
        this.mContext = context;
        this.mContainer = view;
        this.mType = type;
        this.mICheckHierarchyResp = iCheckHierarchyResp;
    }

    public interface ICheckHierarchyResp {
        void isCorrectData(boolean isCorrect);
    }

    /**
     * 数据错误回调
     */
    private void dataCorrectResp(boolean isCorrect) {
        if (mICheckHierarchyResp == null) return;
        mICheckHierarchyResp.isCorrectData(isCorrect);
    }

    /**
     * 处理专项练习回调
     * @param response 回调数据
     */
    public void dealHierarchyResp(JSONObject response) {
        if (response == null) {
            dataCorrectResp(false);
            return;
        }

        HierarchyResp hierarchyResp = GsonManager.getModel(response, HierarchyResp.class);

        if (hierarchyResp == null || hierarchyResp.getResponse_code() != 1) {
            dataCorrectResp(false);
            return;
        }

        ArrayList<HierarchyM> hierarchys = hierarchyResp.getHierarchy();
        if (hierarchys == null) {
            dataCorrectResp(false);
            return;
        }

        int hierarchysSize = hierarchys.size();
        if (hierarchysSize == 0) {
            dataCorrectResp(false);
        } else {
            dataCorrectResp(true);
            mContainer.removeAllViews();

            for (int i = 0; i < hierarchysSize; i++) {
                HierarchyM hierarchy = hierarchys.get(i);

                if (hierarchy == null) continue;
                addFirstRoot(hierarchy);
            }
        }
    }

    /**
     * 添加第一层
     * @param hierarchy HierarchyM
     */
    public void addFirstRoot(HierarchyM hierarchy) {
        if (mContainer == null) return;

        TreeNode root = TreeNode.root();
        TreeNode firstRoot = getTreeNode(hierarchy, 1);
        root.addChild(firstRoot);

        // 添加第二层
        ArrayList<HierarchyM> hierarchys = hierarchy.getChilds();
        addSecondRoot(firstRoot, hierarchys);

        // rootContainer
        LinearLayout rootContainer = new LinearLayout(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, 30);
        rootContainer.setLayoutParams(lp);
        rootContainer.setOrientation(LinearLayout.VERTICAL);

        AndroidTreeView tView = new AndroidTreeView(mContext, root);
        tView.setDefaultViewHolder(TreeItemHolder.class);

        rootContainer.addView(tView.getView());

        mContainer.addView(rootContainer);
    }

    /**
     * 添加第二层
     * @param node TreeNode
     * @param hierarchys ArrayList<HierarchyM>
     */
    private void addSecondRoot(TreeNode node, ArrayList<HierarchyM> hierarchys) {
        if (hierarchys == null) return;

        int size = hierarchys.size();
        for (int i = 0; i < size; i++) {
            HierarchyM hierarchy = hierarchys.get(i);
            if (hierarchy == null) continue;

            TreeNode childNode = getTreeNode(hierarchy, 2);
            node.addChild(childNode);

            addRoot(childNode, hierarchy.getChilds());
        }
    }

    /**
     * 获取TreeNode
     * @param hierarchy HierarchyM
     * @param level 默认是0
     * @return TreeNode
     */
    private TreeNode getTreeNode(HierarchyM hierarchy, int level) {
        if (hierarchy == null) hierarchy = new HierarchyM();
        return new TreeNode(
                new TreeItemHolder.TreeItem(
                        level,
                        hierarchy.getCategory_id(),
                        hierarchy.getName(),
                        hierarchy.getDone(),
                        hierarchy.getTotal(),
                        mType,
                        hierarchy.getChilds()));
    }

    /**
     * 增加节点
     * @param node TreeNode
     * @param hierarchys ArrayList<HierarchyM>
     */
    private void addRoot(TreeNode node, ArrayList<HierarchyM> hierarchys) {
        if (hierarchys == null) return;

        int size = hierarchys.size();
        for (int i = 0; i < size; i++) {
            HierarchyM hierarchy = hierarchys.get(i);

            if (hierarchy == null) continue;
            TreeNode childNode = getTreeNode(hierarchy, 0);
            node.addChild(childNode);

            addRoot(childNode, hierarchy.getChilds());
        }
    }

}
