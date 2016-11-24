package com.appublisher.quizbank.common.measure.bean;

/**
 * 做题模块：选项排除
 */

public class MeasureExcludeBean {

    private boolean exclude_a;
    private boolean exclude_b;
    private boolean exclude_c;
    private boolean exclude_d;

    public boolean isExclude_a() {
        return exclude_a;
    }

    public void setExclude_a(boolean exclude_a) {
        this.exclude_a = exclude_a;
    }

    public boolean isExclude_b() {
        return exclude_b;
    }

    public void setExclude_b(boolean exclude_b) {
        this.exclude_b = exclude_b;
    }

    public boolean isExclude_c() {
        return exclude_c;
    }

    public void setExclude_c(boolean exclude_c) {
        this.exclude_c = exclude_c;
    }

    public boolean isExclude_d() {
        return exclude_d;
    }

    public void setExclude_d(boolean exclude_d) {
        this.exclude_d = exclude_d;
    }
}
