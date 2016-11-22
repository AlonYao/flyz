package com.appublisher.quizbank.model.netdata;

import java.util.List;

/**
 * Created by jinbao on 2016/11/21.
 */

public class CarouselResp {


    private int response_code;
    private List<CarouselM> written;
    private List<CarouselM> interview;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<CarouselM> getWritten() {
        return written;
    }

    public void setWritten(List<CarouselM> written) {
        this.written = written;
    }

    public List<CarouselM> getInterview() {
        return interview;
    }

    public void setInterview(List<CarouselM> interview) {
        this.interview = interview;
    }
}
