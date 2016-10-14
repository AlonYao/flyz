package com.appublisher.quizbank.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_course.coursecenter.CourseFragment;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.fragment.HomePageFragment;
import com.appublisher.quizbank.fragment.SettingFragment;

public class CommonFragmentActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private String mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_fragment);
        mFrom = getIntent().getStringExtra("from");

        setToolBar(this);

        mFragmentManager = getSupportFragmentManager();
        setValue();
    }

    public void setValue() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if ("homepage".equals(mFrom)) {
            fragmentTransaction.add(R.id.container_view, new HomePageFragment(), "Index");
        } else if ("setting".equals(mFrom)) {
            fragmentTransaction.add(R.id.container_view, new SettingFragment(), "Setting");
            setTitle("设置页");
        } else if ("course".equals(mFrom)) {
            final CourseFragment courseFragment = new CourseFragment();
            final Bundle bundle = new Bundle();
            bundle.putString("status", "purchased");
            courseFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.container_view, courseFragment, "Course");
            setTitle("课程中心");
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        if ("homepage".equals(mFrom)) {
            MenuItemCompat.setShowAsAction(menu.add("小班").setIcon(R.drawable.vip_entry),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ("小班".equals(item.getTitle())) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
