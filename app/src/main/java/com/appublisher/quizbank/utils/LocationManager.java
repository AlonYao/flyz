package com.appublisher.quizbank.utils;

import android.app.Activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by huaxiao on 2015/5/7.
 */
public class LocationManager {

    private static LocationClient mLocationClient;

    public static void getBaiduLocation(Activity activity) {
        mLocationClient = new LocationClient(activity);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        //option.setPriority(LocationClientOption.NetWorkFirst);
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        mLocationClient.setLocOption(option);

        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation == null) {
                    return;
                }

                StringBuilder sb = new StringBuilder(256);
                sb.append("time : ");
                sb.append(bdLocation.getTime());
                sb.append("\nerror code : ");
                sb.append(bdLocation.getLocType());
                sb.append("\nlatitude : ");
                sb.append(bdLocation.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(bdLocation.getLongitude());
                sb.append("\nradius : ");
                sb.append(bdLocation.getRadius());
                sb.append("\ncity : ");
                sb.append(bdLocation.getCity());
                sb.append("\nprovince : ");
                sb.append(bdLocation.getProvince());

                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                    sb.append("\nspeed : ");
                    sb.append(bdLocation.getSpeed());
                    sb.append("\nsatellite : ");
                    sb.append(bdLocation.getSatelliteNumber());
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                    sb.append("\naddr : ");
                    sb.append(bdLocation.getAddrStr());
                }

                Logger.i(sb.toString());
            }
        });

        mLocationClient.start();
    }
}
