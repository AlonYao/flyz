package com.appublisher.quizbank.dao;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.quizbank.model.db.Paper;

/**
 * 试卷信息DAO层
 */
public class PaperDAO {

    /**
     * 查询数据
     * @return 试卷本地数据
     */
    public static Paper findById(int paper_id) {
        try {
            return new Select().from(Paper.class)
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
     * @param last_position 上一次的位置
     */
    public static void insert(int paper_id, int last_position) {
        Paper item = new Paper();
        item.paper_id = paper_id;
        item.last_position = last_position;
        item.save();
    }

    /**
     * 更新数据
     * @param last_position 上一次的位置
     */
    public static void update(int paper_id, int last_position) {
        try {
            new Update(Paper.class)
                    .set("last_position = ?", last_position)
                    .where("paper_id = ?", paper_id)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存数据
     * @param paper_id 试卷id
     */
    public static void save(int paper_id, int last_position) {
        Paper item = findById(paper_id);

        if (item != null) {
            update(paper_id, last_position);
        } else {
            insert(paper_id, last_position);
        }
    }

    /**
     * 获取上次记录的位置
     * @param paper_id 试卷id
     * @return 位置
     */
    public static int getLastPosition(int paper_id) {
        Paper paper = findById(paper_id);

        if (paper == null) return 0;

        return paper.last_position;
    }
}
