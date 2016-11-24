package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_course.coursecenter.CourseFragment;
import com.appublisher.lib_course.offline.activity.OfflineActivity;
import com.appublisher.lib_course.opencourse.model.OpenCourseModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.fragment.FavoriteFragment;
import com.appublisher.quizbank.fragment.SettingFragment;
import com.appublisher.quizbank.fragment.WholePageFragment;
import com.appublisher.quizbank.fragment.WrongQuestionsFragment;
import com.appublisher.quizbank.model.business.CommonModel;

public class CommonFragmentActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private String mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_fragment);
        mFrom = getIntent().getStringExtra("from");

        CommonModel.setToolBar(this);

        mFragmentManager = getSupportFragmentManager();
        setValue();
    }

    public void setValue() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if ("setting".equals(mFrom)) {
            fragmentTransaction.add(R.id.container_view, new SettingFragment(), "Setting");
            setTitle("设置页");
        } else if (mFrom != null && mFrom.contains("course")) {
            final CourseFragment courseFragment = new CourseFragment();
            if (mFrom.contains("purchased")) {
                final Bundle bundle = new Bundle();
                bundle.putString("status", "purchased");
                courseFragment.setArguments(bundle);
            }
            fragmentTransaction.add(R.id.container_view, courseFragment, "Activity_Course");
            setTitle("课程中心");
        } else if ("wholepage".equals(mFrom)) {
            fragmentTransaction.add(R.id.container_view, new WholePageFragment(), "Wholepage");
            setTitle("真题演练");
        } else if ("collect".equals(mFrom)) {
            fragmentTransaction.add(R.id.container_view, new FavoriteFragment(), "Favorite");
            setTitle("收藏夹");
        } else if ("wrong".equals(mFrom)) {
            fragmentTransaction.add(R.id.container_view, new WrongQuestionsFragment(), "Wrong");
            setTitle("错题本");
        }

        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if ("homepage".equals(mFrom)) {
            MenuItemCompat.setShowAsAction(menu.add("小班").setIcon(R.drawable.vip_entry),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            setDisplayHomeAsUpEnabled(this, false);
        } else if (mFrom != null && mFrom.contains("course")) {
            MenuItemCompat.setShowAsAction(menu.add("下载").setIcon(R.drawable.actionbar_download),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setShowAsAction(menu.add("评分").setIcon(R.drawable.actionbar_rate), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setShowAsAction(menu.add("下载"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setShowAsAction(menu.add("评分"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ("小班".equals(item.getTitle())) {
            finish();
        } else if ("下载".equals(item.getTitle())) {
            Intent intent = new Intent(this, OfflineActivity.class);
            startActivity(intent);
        } else if ("评分".equals(item.getTitle())) {
            OpenCourseModel.skipToMyGrade(this, CourseFragment.mUnRateClasses, "false");
        }

        return super.onOptionsItemSelected(item);
    }
}
