package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;
import com.appublisher.quizbank.common.measure.network.MeasureRequest;
import com.appublisher.quizbank.common.measure.view.IMeasureSearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：搜题
 */

public class MeasureSearchModel implements RequestCallback, MeasureConstants{

    private static final int COUNT = 10;

    private Context mContext;
    private MeasureRequest mRequest;
    private String mCurKeywords;
    private List<String> mKeywordsList;
    private int mOffset;
    private List<MeasureSearchResp.SearchItemBean> mList;
    private IMeasureSearchView mView;

    public MeasureSearchModel(Context context, IMeasureSearchView view) {
        mContext = context;
        mRequest = new MeasureRequest(context, this);
        mView = view;
    }

    public void search(String keywords) {
        mCurKeywords = keywords;
        mOffset = 0;
        mRequest.searchQuestion(keywords, mOffset, COUNT);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (SEARCH_QUESTION.equals(apiName)) {
            dealSearchQuestionResp(response);
        }

        hideLoading();
    }

    private void dealSearchQuestionResp(JSONObject response) {
        MeasureSearchResp resp = GsonManager.getModel(response, MeasureSearchResp.class);
        if (resp == null || resp.getResponse_code() != 1) {
            resetOffset();
            return;
        }

        setKeywordsList(resp.getKeywords());

        if (mOffset == 0) {
            if (resp.getList() == null || resp.getList().size() == 0) {
                mView.showNone();
            } else {
                mView.showNotice(mCurKeywords, resp.getTotal());
            }
            mList = resp.getList();
            mView.showContent(mList);

            // Umeng
            UmengManager.onEvent(mContext, "Searchlist");
        } else {
            if (resp.getList() == null || resp.getList().size() == 0) {
                mView.showNoMoreToast();
                resetOffset();
            } else {
                mList.addAll(resp.getList());
                mView.showLoadMore(mList);
            }
        }

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
        resetOffset();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
        resetOffset();
    }

    private void hideLoading() {
        mView.hideLoading();
        mView.stopXListView();
    }

    public void loadMore() {
        mOffset = mOffset + COUNT;
        mRequest.searchQuestion(mCurKeywords, mOffset, COUNT);
    }

    private void resetOffset() {
        if (mOffset <= 0) {
            mOffset = 0;
        } else {
            mOffset = mOffset - COUNT;
        }
    }

    /**
     * 获取AnalysisBean（用于做题模块解析部分）
     * @param position 位置
     * @return String
     */
    public String getAnalysisBeanByPosition(int position) {
        if (mList == null || position < 0 || position >= mList.size()) return "";
        MeasureSearchResp.SearchItemBean searchItem = mList.get(position);
        if (searchItem == null) return "";

        MeasureAnalysisBean analysisBean = new MeasureAnalysisBean();
        List<MeasureQuestionBean> questionBeanList = new ArrayList<>();

        if ("material".equals(searchItem.getType())) {
            // 材料题
            List<MeasureSearchResp.SearchItemBean> questions = searchItem.getQuestions();
            if (questions == null) return "";

            for (MeasureSearchResp.SearchItemBean question : questions) {
                questionBeanList.add(transform(question));
            }
        } else {
            // 非材料题
            questionBeanList.add(transform(searchItem));
        }

        analysisBean.setQuestions(questionBeanList);

        return GsonManager.modelToString(analysisBean);
    }

    /**
     * QuestionBean转换成做题模块需要的Bean
     * @param searchItem SearchItemBean
     * @return MeasureQuestionBean
     */
    private MeasureQuestionBean transform(MeasureSearchResp.SearchItemBean searchItem) {
        MeasureQuestionBean question = new MeasureQuestionBean();
        if (searchItem == null) return question;

        question.setId(searchItem.getId());
        question.setMaterial(searchItem.getMaterial());
        question.setQuestion(searchItem.getQuestion());
        question.setOption_a(searchItem.getOption_a());
        question.setOption_b(searchItem.getOption_b());
        question.setOption_c(searchItem.getOption_c());
        question.setOption_d(searchItem.getOption_d());
        question.setAnswer(searchItem.getAnswer());
        question.setAnalysis(searchItem.getAnalysis());
        question.setNote_id(searchItem.getNote_id());
        question.setNote_ids(searchItem.getNote_ids());
        question.setNote_name(searchItem.getNote_name());
        question.setCategory_id(searchItem.getCategory_id());
        question.setCategory_name(searchItem.getCategory_name());
        question.setSource(searchItem.getSource());
        question.setAccuracy(searchItem.getAccuracy());
        question.setSummary_accuracy(searchItem.getSummary_accuracy());
        question.setSummary_count(searchItem.getSummary_count());
        question.setSummary_fallible(searchItem.getSummary_fallible());
        question.setMaterial_id(searchItem.getMaterial_id());

        return question;
    }

    public List<String> getKeywordsList() {
        return mKeywordsList;
    }

    public void setKeywordsList(List<String> mKeywordsList) {
        this.mKeywordsList = mKeywordsList;
    }
}
