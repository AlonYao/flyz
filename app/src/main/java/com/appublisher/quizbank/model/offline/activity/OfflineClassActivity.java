package com.appublisher.quizbank.model.offline.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.offline.adapter.PurchasedClassesAdapter;
import com.appublisher.quizbank.model.offline.netdata.PurchasedClassM;

import java.util.ArrayList;

/**
 * 离线模块课程列表
 */
public class OfflineClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_course);

        // Toolbar
        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, "离线管理");

        // Init view
        ListView lv = (ListView) findViewById(R.id.offline_class_lv);

        // Data
        // noinspection unchecked
        ArrayList<PurchasedClassM> classes =
                (ArrayList<PurchasedClassM>) getIntent().getSerializableExtra("class_list");

        PurchasedClassesAdapter adapter = new PurchasedClassesAdapter(this, classes);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
