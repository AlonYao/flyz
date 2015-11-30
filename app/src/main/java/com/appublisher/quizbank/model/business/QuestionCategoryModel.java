package com.appublisher.quizbank.model.business;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by on 15/11/30.
 */
public class QuestionCategoryModel implements RequestCallback {
    public DataChange dataChange;
    public Context context;
    public QuestionCategoryModel(Context context) {
        this.context = context;
    }

    public interface DataChange {
        void onDataChange(String data);
    }


    public void getData(int question_id, DataChange setText) {
        dataChange = setText;
        new Request(context,this).getQuestionCategory(question_id);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) return;
        if ("question_category".equals(apiName)) {
            QuestionM questionM = GsonManager.getObejctFromJSON(response.toString(), QuestionM.class);
            String string = "全站作答" + questionM.getCount() + "次，正确率" + questionM.getAccuracy() + ",易错项为" + questionM.getFallible();
            dataChange.onDataChange(string);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }

}
