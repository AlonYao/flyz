package com.appublisher.quizbank.model.netdata.hierarchy;

import java.util.ArrayList;

/**
 * 知识点层级接口数据模型（第一层）
 */
public class HierarchyM {
    int category_id;
    String name;
    int level;
    ArrayList<NoteGroupM> note_group;
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
    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<NoteGroupM> getNote_group() {
        return note_group;
    }

    public void setNote_group(ArrayList<NoteGroupM> note_group) {
        this.note_group = note_group;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
