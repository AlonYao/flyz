package com.appublisher.quizbank.dao;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.quizbank.model.db.Mock;


public class MockDAO {
    /**
     * 查询数据
     * @return 模考本地数据
     */
    public static Mock findById(int paper_id) {
        try {
            return new Select().from(Mock.class)
                    .where("paper_id = ?", paper_id)
                    .executeSingle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 插入数据
     * @param paper_id 试卷id
     * @param date 是否预约
     */
    public static void insert(int paper_id, int date) {
        Mock item = new Mock();
        item.paper_id = paper_id;
        item.date = date;
        item.save();
    }


    /**
     * 保存数据
     * @param paper_id 试卷id
     */
    public static void save(int paper_id, int date) {
        Mock item = findById(paper_id);

        if (item != null) {
            update(paper_id, date);
        } else {
            insert(paper_id, date);
        }
    }
    /**
     * 更新数据
     * @param date 是否预约
     */
    public static void update(int paper_id, int date) {
        try {
            new Update(Mock.class)
                    .set("date = ?", date)
                    .where("paper_id = ?", paper_id)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取是否预约
     * @param paper_id 试卷id
     * @return 0表示没有预约，1表示预约
     */
    public static int getIsDateById(int paper_id) {
        Mock paper = findById(paper_id);

        if (paper == null) return 0;

        return paper.date;
    }
}
