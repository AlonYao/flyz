package com.appublisher.quizbank.model.netdata.mock;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jinbao on 2016/11/8.
 */

public class MockGufenResp{

    private int response_code;
    private MockBean mock;

    private GufenM gufen;

    protected MockGufenResp(Parcel in) {
        response_code = in.readInt();
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public MockBean getMock() {
        return mock;
    }

    public void setMock(MockBean mock) {
        this.mock = mock;
    }

    public GufenM getGufen() {
        return gufen;
    }

    public void setGufen(GufenM gufen) {
        this.gufen = gufen;
    }


    public static class MockBean {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
