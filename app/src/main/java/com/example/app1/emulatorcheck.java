package com.example.app1;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class emulatorcheck {
    //模拟器检测
    //检查设备型号和品牌
    public boolean checkDeviceModel() {
        String model = android.os.Build.MODEL;
        String brand = android.os.Build.BRAND;
        System.out.println(model);
        System.out.println(brand);
        return model.contains("Genymotion") || model.contains("x86") || brand.equalsIgnoreCase("generic");
    }
    //检查硬件特征
    public boolean checkHardwareFeatures(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean hasTelephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        boolean hasSensor = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        return !hasTelephony || !hasSensor; // 模拟器可能缺少这些特征
    }
    //检查文件系统
    public boolean checkFileSystem() {
        String[] emulatorFiles = {
                "/dev/socket/qemud",
                "/dev/qemu_pipe",
        };
        for (String filePath : emulatorFiles) {
            if (new File(filePath).exists()) {
                System.out.println(filePath);
                return true; // 找到模拟器特有文件
            }
        }
        return false; // 没有找到模拟器特有文件
    }
    //检查运行程序
    public boolean checkRunningApps() {
        try {
            Process process = Runtime.getRuntime().exec("ps");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (line.contains("emulator")) {
                    return true; // 找到模拟器相关进程
                }
            }
        } catch (IOException e) {
            System.out.println("f");
            return false; // 读取进程信息失败
        }
        return false;
    }
    //检查系统架构
    public  boolean checkArchitecture(){
        return Build.CPU_ABI.contains("x86");
    }
    //检查电池状态
    public boolean checkBattery(Context context) {
        // 获取电池状态
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        if (batteryStatus != null) {
            int batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = batteryLevel * 100 / (float)batteryScale;
            System.out.println(batteryPct);
            boolean isCharging = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING;
            // 判断电池电量和充电状态，通常模拟器电池满电且不在充电
            return batteryPct == 80.0 && !isCharging; // 可能在模拟器中
        }
        return false; // 不在模拟器中
    }
}
