package com.appublisher.quizbank.model.business;

import android.graphics.Color;
import android.view.View;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.fragment.WholePageFragment;
import com.appublisher.quizbank.model.netdata.wholepage.AreaM;
import com.appublisher.quizbank.utils.LocationManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.baidu.location.BDLocation;

/**
 * WholePageFragment Model
 */
public class WholePageModel {

    /**
     * 处理定位
     * @param bdLocation 定位信息
     */
    public static void dealLocation(BDLocation bdLocation) {
        // 省份
        final String province = bdLocation.getProvince();

        if (province == null || province.length() < 2) return;

        WholePageFragment.mRlLocation.setVisibility(View.VISIBLE);
        WholePageFragment.mTvLocation.setText(province.substring(0, 2));

        // 当前定位到的省份
        WholePageFragment.mTvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WholePageFragment.mTvLocation.setTextColor(Color.WHITE);
                WholePageFragment.mTvLocation.setBackgroundResource(
                        R.drawable.wholepage_item_all_selected);

                // 前一个view改变样式
                if (WholePageFragment.mTvLastProvince != null
                        && WholePageFragment.mTvLastProvince != WholePageFragment.mTvLocation) {
                    WholePageFragment.mTvLastProvince.setBackgroundResource(
                            R.drawable.wholepage_item_all);

                    WholePageFragment.mTvLastProvince.setTextColor(
                            WholePageFragment.mActivity.getResources().getColor(
                                    R.color.common_text));
                }

                WholePageFragment.mTvLastProvince = WholePageFragment.mTvLocation;

                // 记录当前的地区id
                if (WholePageFragment.mAreas != null) {
                    int size = WholePageFragment.mAreas.size();
                    for (int i = 0; i < size; i++) {
                        AreaM area = WholePageFragment.mAreas.get(i);

                        if (area == null) continue;

                        String name = area.getName();
                        if (!province.substring(0, 2).equals(name))
                            continue;

                        // 匹配成功
                        WholePageFragment.mCurAreaId = area.getArea_id();

                        // 更新菜单栏文字
                        WholePageFragment.mTvProvince.setText(area.getName());
                        break;
                    }
                }
            }
        });

        // 重新定位
        WholePageFragment.mTvReLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastManager.showToast(WholePageFragment.mActivity, "重新定位……");

                WholePageFragment.mHandler.sendEmptyMessage(WholePageFragment.GET_LOCATION);
            }
        });

        LocationManager.stopBaiduLocation();
    }

    /**
     * 显示空白图片
     * @param fragment WholePageFragment
     */
    public static void showNullImg(WholePageFragment fragment) {
        if (fragment.mEntirePapers == null || fragment.mEntirePapers.size() == 0) {
            fragment.mIvNull.setVisibility(View.VISIBLE);
        } else {
            fragment.mIvNull.setVisibility(View.GONE);
        }
    }
}
