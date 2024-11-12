package com.example.app1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.net.SocketException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    String s ="检测开始\n";
    String sc_myappkey = "4D:DD:19:7F:A2:A2:59:77:0F:F1:3A:EB:FE:DD:26:A4:C1:8A:80:AA";//自建密钥库签名
    String sc_default = "5F:49:E9:F6:AC:16:31:F7:9A:77:7F:1A:15:06:EE:84:48:1D:4D:DF";//默认密钥库签名

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
                    checkSign();

                    startScheduledTask();
                    setDailyAlarm();

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
                checkSign();

                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("s",s);
                startActivity(intent);

                s = "检测开始";
            }
        });

        Button button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    checkFingrtPrint();
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                checkSign();

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

    //-----------------------------------------------ROOT检测------------------------------------------------------
    public void rootCheck() throws IOException {
        boolean flag = false;
        //检查SU命令
        rootcheck rc = new rootcheck();
        if (rc.checkSuCommand()){
            s += "\n检查SU命令:执行成功";
            flag = true;
        }else {
            s += "\n检查SU命令:执行失败";
        }
        //检查root文件
        if (rc.checkRootFiles()){
            s += "\n检查root文件:存在特征文件";
            flag = true;
        }else {
            s += "\n检查root文件:不存在特征文件";
        }
        //检查系统标签
        if (rc.checkSystemTags()){
            s += "\n检查系统标签:异常";
            flag = true;
        }else {
            s += "\n检查系统标签:正常";
        }
        //检查系统分区
        if (rc.checkMountInfo()){
            s += "\n检查分区读写模式:系统分区可写";
            flag = true;
        }else {
            s += "\n检查分区读写模式:系统分区只读";
        }
        //检查系统属性
        if (rc.checkSystemProperty()){
            s += "\n检查系统属性:异常";
            flag = true;
        }else {
            s += "\n检查系统属性:正常";
        }
        //检查SELinux
        if (rc.checkSELinuxStatus().equalsIgnoreCase("y")){
            s += "\n检查SELinux状态:异常";
            flag = true;
        } else if (rc.checkSELinuxStatus().equalsIgnoreCase("n")) {
            s += "\n检查SELinux状态:正常";
        } else {
            s += "\n检查SELinux状态:未知";
        }
        //检查bootloader
        if (rc.isBootloaderUnlocked()){
            s += "\n检查Bootloader状态:异常";
            flag = true;
        }else {
            s += "\n检查Bootloader状态:正常";
        }
        //检查TEE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (rc.isDeviceLocked()){
                s += "\n检查TEE状态:正常";

            }else {
                s += "\n检查TEE状态:异常";
                flag = true;
            }
        }


        if (flag){
            s += "\n\n可能已root";
        }else {
            s += "\n\n可能未root";
        }
/*
        test t = new test();
        String ts = t.tf();
        s += ts;
*/

    }

    //-----------------------------------------------模拟器检测------------------------------------------------------
    public void emulatorCheck(){
        boolean flag = false;
        Context context = this;

        emulatorcheck ec = new emulatorcheck();
        //检查设备型号和品牌
        if (ec.checkDeviceModel()){
            s += "\n检查设备型号和品牌:异常";
            flag = true;
        }else {
            s += "\n检查设备型号和品牌:正常";
        }
        //检查硬件特征
        if (ec.checkHardwareFeatures(context)){
            s += "\n检查硬件特征:异常";
            flag = true;
        }else {
            s += "\n检查硬件特征:正常";
        }
        //检查文件系统
        if (ec.checkFileSystem()){
            s += "\n检查文件系统:存在特征文件";
            flag = true;
        }else {
            s += "\n检查文件系统:不存在特征文件";
        }
        //检查运行程序
        if (ec.checkRunningApps()){
            s += "\n检查运行程序:存在运行中特征程序";
            flag = true;
        }else {
            s += "\n检查运行程序:不存在运行中特征程序";
        }
        //检查系统架构
        if (ec.checkArchitecture()){
            s += "\n检查系统架构:x86";
            flag = true;
        }else {
            s += "\n检查系统架构:arm";
        }
        //检查电池状态
        if (ec.checkBattery(context)){
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


    //-----------------------------------------------签名检测------------------------------------------------------
    public void checkSign(){
        if(signCheck()) {
            //TODO 签名正常
            s += "\n签名校验成功";
        }else{
            //TODO 签名不正确
            s += "\n签名校验失败";
        }
    }

    public boolean signCheck(){
        signcheck signCheck = new signcheck(this,sc_default);
        return signCheck.check();
    }


    private ScheduledExecutorService scheduler;
    public void startScheduledTask() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                signCheck();
            }
        }, 0, 15, TimeUnit.SECONDS);
        Log.d("ScheduledTask", "signcheck executed");
    }


    private static int executionCount = 0;

    public void setDailyAlarm() {

        AlarmManager aManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long triggerTime = calendar.getTimeInMillis();
        if (System.currentTimeMillis() > triggerTime) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            triggerTime = calendar.getTimeInMillis();
        }
        long intervalMillis = AlarmManager.INTERVAL_DAY; // 每天
        aManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalMillis, pendingIntent);

        filewr fl = new filewr();
        fl.bufferRead("sc.txt");
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 通过上下文调用 signCheck 方法
            if (context instanceof MainActivity) {
                ((MainActivity) context).signCheck();
                System.out.println("setDailyAlarm");
                executionCount += 1;

                filewr fl = new filewr();
                fl.bufferSave(String.valueOf(executionCount),"sc.txt");

            }
        }
    }

    //-----------------------------------------------设备指纹检测------------------------------------------------------
    public void checkFingrtPrint() throws Exception {
        fingerprint fp = new fingerprint();
        String dev =fp.getDeviceID(getContentResolver());
        String net = fp.getLocalMacAddress();
        String sys = fp.getSystemProperties();
        s += dev + net + sys;

        fp.getAccounts(this);

        filewr fl = new filewr();
        fl.bufferSave(s,"a.txt");
        fl.bufferRead("a.txt");
    }




}
