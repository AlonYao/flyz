package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.customui.TreeItemHolder;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyResp;
import com.appublisher.quizbank.model.netdata.hierarchy.NoteGroupM;
import com.appublisher.quizbank.model.netdata.hierarchy.NoteItemM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 错题本
 */
public class WrongQuestionsFragment extends Fragment implements RequestCallback{

    private Activity mActivity;
    private LinearLayout mContainer;
    private ImageView mIvNull;
    private View mView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wrongquestions, container, false);

        // View 初始化
        mContainer = (LinearLayout) mView.findViewById(R.id.wrongq_container);
        mIvNull = (ImageView) mView.findViewById(R.id.quizbank_null);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        ProgressBarManager.showProgressBar(mView);
        new Request(mActivity, this).getNoteHierarchy("error");

        // Umeng
        MobclickAgent.onPageStart("WrongQuestionsFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("WrongQuestionsFragment");
    }

    /**
     * 处理知识点层级（错题）回调
     * @param response 回调对象
     */
    private void dealNoteHierarchyResp(JSONObject response) {
        if (response == null) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        Gson gson = new Gson();
        HierarchyResp hierarchyResp = gson.fromJson(response.toString(), HierarchyResp.class);

        if (hierarchyResp == null || hierarchyResp.getResponse_code() != 1) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        }
        ArrayList<HierarchyM> hierarchys = hierarchyResp.getHierarchy();

        if (hierarchys == null || hierarchys.size() == 0) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        int hierarchysSize = hierarchys.size();
        if (hierarchysSize == 0) {
            mIvNull.setVisibility(View.VISIBLE);
        } else {
            mIvNull.setVisibility(View.GONE);
            for (int i = 0; i < hierarchysSize; i++) {
                HierarchyM hierarchy = hierarchys.get(i);

                if (hierarchy == null) continue;
                addHierarchy(hierarchy);
            }
        }
    }

    /**
     * 添加知识点层级第一层
     * @param hierarchy 第一层数据
     */
    private void addHierarchy(HierarchyM hierarchy) {
        if (mContainer == null) return;

        TreeNode root = TreeNode.root();

        TreeNode firstRoot = new TreeNode(
                new TreeItemHolder.TreeItem(
                        1,
                        hierarchy.getCategory_id(),
                        hierarchy.getName(),
                        "error"));

        root.addChild(firstRoot);

        // 添加第二层
        ArrayList<NoteGroupM> noteGroups = hierarchy.getNote_group();
        addNoteGroup(firstRoot, noteGroups);

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

        mContainer.addView(rootContainer);
    }

    /**
     * 添加第二层级
     * @param firstRoot 第一层级节点
     * @param noteGroups 第二层级数据
     */
    private void addNoteGroup(TreeNode firstRoot, ArrayList<NoteGroupM> noteGroups) {
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
                            "error"));
            firstRoot.addChild(secondRoot);

            addNotes(secondRoot, noteGroup.getNotes());
        }
    }

    /**
     * 添加第三层
     * @param secondRoot 第二层级节点
     * @param notes 第三层级数据
     */
    private void addNotes(TreeNode secondRoot, ArrayList<NoteItemM> notes) {
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
                            "error"));
            secondRoot.addChild(thirdRoot);
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("note_hierarchy".equals(apiName)) dealNoteHierarchyResp(response);

        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressBarManager.hideProgressBar();
    }
}
