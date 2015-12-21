package com.appublisher.quizbank.model.netdata.hierarchy;

import java.util.ArrayList;

/**
 * 知识点组数据模型
 */
public class NoteGroupM {
    int group_id;
    String name;
    ArrayList<NoteItemM> notes;
    int done;
    int total;
    int level;
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
    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<NoteItemM> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<NoteItemM> notes) {
        this.notes = notes;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
