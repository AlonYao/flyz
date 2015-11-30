package com.appublisher.quizbank.model.netdata.hierarchy;

/**
 * 知识点数据模型
 */
public class NoteItemM {
    int note_id;
    String name;
    int done;
    int total;
    public  int getDone(){
        return  done;
    }
    public  int getTotal(){
        return  total;
    }
    public void setDone(int done){
        this.done = done;
    }
    public  void setTotal(int total){
        this.total= total;
    }
    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
