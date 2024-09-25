package com.example.app1;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    String s ="检测开始";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    rootCheck();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }



                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("s",s);
                startActivity(intent);

                s = "检测开始";
            }
        });

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emulatorCheck();

                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("s",s);
                startActivity(intent);

                s = "检测开始";

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void rootCheck() throws IOException {
        boolean flag = false;
        //检查SU命令
        if (checkSuCommand()){
            s += "\n检查SU命令:执行成功";
            flag = true;
        }else {
            s += "\n检查SU命令:执行失败";
        }
        //检查root文件
        if (checkRootFiles()){
            s += "\n检查root文件:存在特征文件";
            flag = true;
        }else {
            s += "\n检查root文件:不存在特征文件";
        }
        //检查系统标签
        if (checkSystemTags()){
            s += "\n检查系统标签:异常";
            flag = true;
        }else {
            s += "\n检查系统标签:正常";
        }
        //检查系统分区
        if (checkMountInfo()){
            s += "\n检查系统分区:系统分区可写";
            flag = true;
        }else {
            s += "\n检查系统分区:系统分区只读";
        }
        //检查系统属性
        if (checkSystemProperty()){
            s += "\n检查系统属性:异常";
            flag = true;
        }else {
            s += "\n检查系统属性:正常";
        }
        if (checkSELinuxStatus()){
            s += "\n检查SELinux状态:异常";
            flag = true;
        }else {
            s += "\n检查SELinux状态:正常";
        }

        if (flag){
            s += "\n\n可能已root";
        }else {
            s += "\n\n可能未root";
        }

    }

    public void emulatorCheck(){
        boolean flag = false;
        Context context = this;
        //检查设备型号和品牌
        if (checkDeviceModel()){
            s += "\n检查设备型号和品牌:异常";
            flag = true;
        }else {
            s += "\n检查设备型号和品牌:正常";
        }
        //检查硬件特征
        if (checkHardwareFeatures(context)){
            s += "\n检查硬件特征:异常";
            flag = true;
        }else {
            s += "\n检查硬件特征:正常";
        }
        //检查文件系统
        if (checkFileSystem()){
            s += "\n检查文件系统:存在特征文件";
            flag = true;
        }else {
            s += "\n检查文件系统:不存在特征文件";
        }
        //检查运行程序
        if (checkRunningApps()){
            s += "\n检查运行程序:存在运行中特征程序";
            flag = true;
        }else {
            s += "\n检查运行程序:不存在运行中特征程序";
        }
        //检查系统架构
        if (checkArchitecture()){
            s += "\n检查系统架构:x86";
            flag = true;
        }else {
            s += "\n检查系统架构:arm";
        }
        //检查电池状态
        if (checkBattery(context)){
            s += "\n检查电池状态:异常";
            flag = true;
        }else {
            s += "\n检查电池状态:正常";
        }

        if (flag){
            s += "\n\n可能是模拟器";
        }else {
            s += "\n\n可能是真机";
        }

    }


    //root检测
    //检查SU命令能否执行
    public boolean checkSuCommand() {
        Process process = null;
        Process process1 = null;
        try {
            // 尝试运行su命令
            process = Runtime.getRuntime().exec("su" );
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = in.readLine();
            System.out.println(line);
            if (in.readLine() != null) {
                return true; // su命令执行成功，可能被Root
            }
            return false;
        } catch (Exception e) {
            System.out.println(e);
            return false; // 执行su命令失败，可能未Root
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    //检查root文件是否存在
    public boolean checkRootFiles() {
        String[] rootFilesPaths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/bin/su",
                "/magisk",
                "/data/adb/magisk",
        };
        for (String path : rootFilesPaths) {
            if (new File(path).exists()) {
                System.out.println(path);
                return true; // 发现Root相关文件
            }
        }
        return false;
    }
    //检查系统属性
    public boolean checkSystemTags() {
        try {
            // 读取ro.build.tags系统属性
            String buildTags = Build.TAGS;
            System.out.println(buildTags);
            if (buildTags != null && !buildTags.equals("release-keys")) {
                return true; // 设备使用了带有test-keys签名的固件，通常是Root设备
            }
        } catch (Exception e) {
            // 读取系统属性失败
        }
        return false;
    }
    //检查系统分区读写模式
    public boolean checkMountInfo() {
        try {
            Process process = Runtime.getRuntime().exec("mount");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine())!= null) {
                System.out.println(line);
                if (line.contains("/system") && line.contains("rw")) {
                    System.out.println("rw");
                    return true; // /system分区被挂载为读写模式，可能是Root设备
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    //检查系统属性
    private boolean checkSystemProperty() {
        String debuggable = getSystemProperty("ro.debuggable");
        String secure = getSystemProperty("ro.secure");
        System.out.println("ro.debuggable:"+debuggable);
        System.out.println("ro.secure:"+secure);
        if(debuggable.equals("1") || secure.equals("0")){
            return true;
        }
        return false;
    }
    //获取系统属性值。
    private String getSystemProperty(String key) {
        String value = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop " + key);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            value = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    //检测SELinux状态
    public boolean checkSELinuxStatus() {
        String selinuxStatus = getSELinuxStatus();
        System.out.println(selinuxStatus);
        if (selinuxStatus.equalsIgnoreCase("disabled") || selinuxStatus.equalsIgnoreCase("permissive")) {
            return true; // SELinux处于禁用或宽松模式，可能是Root设备
        }
        return false;
    }
    //获取SELinux状态
    private String getSELinuxStatus() {
        try {
            Process process = Runtime.getRuntime().exec("getenforce");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine().trim(); // 返回SELinux的状态
        } catch (Exception e) {
            return "unknown"; // 检测失败
        }
    }




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
        System.out.println(hasSensor);
        System.out.println(hasTelephony);
        return !hasTelephony || !hasSensor; // 模拟器可能缺少这些特征
    }
    //检查文件系统
    public boolean checkFileSystem() {
        String[] emulatorFiles = {
                "/dev/socket/qemud",
                "/dev/qemu_pipe",
                "/proc/cpuinfo",
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
        if (Build.CPU_ABI.contains("x86")) {
            return true;
        }
        return false;
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
            // 判断电池级别和充电状态，通常模拟器电池满电且不在充电
            if (batteryPct == 100.0 && !isCharging) {
                return true; // 可能在模拟器中
            }
        }
        return false; // 不在模拟器中
    }
}