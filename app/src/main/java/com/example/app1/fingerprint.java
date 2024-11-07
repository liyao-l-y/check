package com.example.app1;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;


public class fingerprint {
    public  String getDeviceID(ContentResolver contentResolver) {
        String s = "设备指纹：\n";
        try {
            // 获取小米健康ID
            String miHealthId = Settings.Global.getString(contentResolver, "mi_health_id");
            Log.d("DeviceFingerprint", "Mi Health ID: " + miHealthId);
            if (miHealthId != null)
                s += "Mi Health ID: " + miHealthId + "\n";

            // 获取GC Booster UUID
            String gcboosterUUID = Settings.Global.getString(contentResolver, "gcbooster_uuid");
            Log.d("DeviceFingerprint", "GC Booster UUID: " + gcboosterUUID);
            if (gcboosterUUID != null)
                s += "GC Booster UUID: " + gcboosterUUID + "\n";

            // 获取MQS UUID
            String keyMqsUUID = Settings.Global.getString(contentResolver, "key_mqs_uuid");
            Log.d("DeviceFingerprint", "Key MQS UUID: " + keyMqsUUID);
            if (keyMqsUUID != null)
                s += "Key MQS UUID: " + keyMqsUUID + "\n";

            // 获取广告ID
            String adAaid = Settings.Global.getString(contentResolver, "ad_aaid");
            Log.d("DeviceFingerprint", "Ad AAID: " + adAaid);
            if (adAaid != null)
                s += "Ad AAID: " + adAaid + "\n";

            // 获取ANDROID_ID
            @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
            Log.d("DeviceFingerprint", "Android ID: " + androidId);
            if (androidId != null)
                s += "Android ID: " + androidId + "\n";

        } catch (Exception e) {
            Log.e("DeviceFingerprint", "Error retrieving device fingerprint: " + e.getMessage());
        }


        return s;
    }

    public String getNetId(Context context){

        StringBuilder netId = new StringBuilder();
        netId.append("\n网络:\n");

        try {
            //wifi mac地址
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            @SuppressLint("HardwareIds") String wifiMac = info.getMacAddress();

            if(wifiMac != null){
                netId.append("wifi：").append(wifiMac).append("\n");
                Log.i("getwifiMac", wifiMac);
            }

            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("HardwareIds") String imei = tm.getDeviceId();
            if(imei != null){
                netId.append("imei:").append(imei).append("\n");
                Log.i("getimei", imei);
            }

            //序列号（sn）
            @SuppressLint("HardwareIds") String sn = tm.getSimSerialNumber();
            if(sn != null){
                netId.append("sn:").append(sn).append("\n");
                Log.i("getsn", sn);
            }

            //如果上面都没有， 则生成一个id：随机码
//		 	String uuid = getUUID(context);
//			if(!isEmpty(uuid)){
//				deviceId.append("id");
//				deviceId.append(uuid);
//				Log.e("getDeviceId : ", deviceId.toString());
//				return deviceId.toString();
//			}

        } catch (Exception e) {
            e.printStackTrace();
            //deviceId.append("id").append(getUUID(context));
        }

        Log.i("getDeviceId : ", netId.toString());

        return netId.toString();
    }


    public String getSystemProperties() {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            StringBuilder s = new StringBuilder("\n系统:\n");

            String[] properties = {
                    "ro.build.fingerprint",
                    "ro.build.build.fingerprint",
                    "ro.bootimage.build.fingerprint",
                    "ro.odm.build.fingerprint",
                    "ro.product.build.fingerprint",
                    "ro.system_ext.build.fingerprint",
                    "ro.system.build.fingerprint",
                    "ro.vendor.build.fingerprint",
                    "ro.build.description"
            };

            for (String property : properties) {
                String value = (String) get.invoke(c, property);
                Log.d("DeviceProperties", property + ": " + value);
                s.append(property).append(":").append(value).append("\n");
            }
            return s.toString();
        } catch (Exception e) {
            Log.e("DeviceProperties", "Error retrieving system properties: " + e.getMessage());
        }
        return "系统属性获取失败";

    }


    public void getAccounts(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccounts();
        for (Account account : accounts) {
            Log.d("AccountManager", "Account: " + account.name + " Type: " + account.type);
        }
    }


}
