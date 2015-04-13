package com.appublisher.quizbank.model.netdata.homepage;

/**
 * 首页试卷数据模型
 */
public class PaperM {

    PaperTodayM today;
    PaperNoteM note;

    public PaperTodayM getToday() {
        return today;
    }

    public void setToday(PaperTodayM today) {
        this.today = today;
    }

    public PaperNoteM getNote() {
        return note;
    }

    public void setNote(PaperNoteM note) {
        this.note = note;
    }
}
