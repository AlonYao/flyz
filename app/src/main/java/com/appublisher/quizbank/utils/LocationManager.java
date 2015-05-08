package com.appublisher.quizbank.utils;

import android.app.Activity;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by huaxiao on 2015/5/7.
 */
public class LocationManager {

    private static LocationClient mLocationClient;

    /**
     * 获取地理位置
     * @param activity Activity
     * @param bdLocationListener 回调监听
     */
    public static void getBaiduLocation(Activity activity, BDLocationListener bdLocationListener) {
        mLocationClient = new LocationClient(activity);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        //option.setPriority(LocationClientOption.NetWorkFirst);
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        mLocationClient.setLocOption(option);

        mLocationClient.registerLocationListener(bdLocationListener);

        mLocationClient.start();
    }

    /**
     * 停止百度定位
     */
    public static void stopBaiduLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

}
