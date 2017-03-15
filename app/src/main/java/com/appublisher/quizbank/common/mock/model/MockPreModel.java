package com.appublisher.quizbank.common.mock.model;

import android.content.Context;

import com.appublisher.quizbank.common.mock.dao.MockDAO;
import com.appublisher.quizbank.model.db.Mock;

/**
 * 模考说明页面
 */

public class MockPreModel extends MockModel{

    public MockPreModel(Context context) {
        super(context);
    }

    public void saveMockIdToLocal(int mockId) {
        try {
            MockDAO.save(mockId, 0);
        } catch (Exception e) {
            // Empty
        }
    }

    public static boolean isBookedInLocalCache(int mockId) {
        try {
            Mock mock = MockDAO.findById(mockId);
            return mock != null;
        } catch (Exception e) {
            // Empty
        }

        return false;
    }

    public static boolean isBookedInLocalCache(String mockId) {
        try {
            isBookedInLocalCache(Integer.parseInt(mockId));
        } catch (Exception e) {
            // Empty
        }

        return false;
    }
}
