package com.appublisher.quizbank.model.netdata.hierarchy;

import java.util.ArrayList;

/**
 * 知识点层级接口数据模型（第一层）
 */
public class HierarchyM {
    private int id;
    private String name;
    private int parent_id;
    private int total;
    private int done;
    private ArrayList<HierarchyM> childs;
    private int level;
    private int right;
    private float duration;
    private ArrayList<HierarchyM> notes;

    public ArrayList<HierarchyM> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<HierarchyM> notes) {
        this.notes = notes;
    }

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

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public ArrayList<HierarchyM> getChilds() {
        return childs;
    }

    public void setChilds(ArrayList<HierarchyM> childs) {
        this.childs = childs;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }
}
