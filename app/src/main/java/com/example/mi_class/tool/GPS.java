package com.example.mi_class.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;




public class GPS {
    private static final String TAG = "MainActivity";
    //定位管理器
    private LocationManager locationManager;
    private Criteria criteria;


    // 进行GPS定位
    @SuppressLint("MissingPermission")
    public void getLocation(Activity activity) {
        // 获取位置管理服务
        locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        // 查找到服务信息
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        // 设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
        locationManager.requestLocationUpdates(provider,0, 0, mListener);
    }



    // 定位监听器
    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 定位到结果
            String tv1;
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tv1 = "维度： "+ latitude +" 经度" + longitude;
                Log.d("维度",latitude+"");
                Log.d("维度",longitude+"");
                System.out.println("GPS"+tv1);
            } else {
                tv1 ="(无法获取地理信息)";
                System.out.println(tv1);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 定位状态改变（可用/临时不可用）
            //Log.d(TAG, "onStatusChanged()");

        }

        @Override
        public void onProviderEnabled(String provider) {
            // 定位服务可用（设置-》位置服务-》打开定位）
            //Log.d(TAG, "onProviderEnabled()");
            //  String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        }

        @Override
        public void onProviderDisabled(String provider) {
            // 定位服务不可用（设置-》位置服务-》关闭定位）
            //Log.d(TAG, "onProviderDisabled()");
        }

    };

}
