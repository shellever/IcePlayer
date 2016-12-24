package com.shellever.iceplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Author: Shellever
 * Date:   12/14/2016
 * Email:  shellever@163.com
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;        //
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    private static CrashHandler ourInstance = new CrashHandler();


    public static CrashHandler getInstance() {
        return ourInstance;
    }

    private CrashHandler() {
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    // ===========================================================
    // Thread.UncaughtExceptionHandler
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (DEBUG) {
            Log.d(TAG, thread.getName() + ex.toString());
        }

        if (!handleException(ex) && mDefaultHandler != null) {
            // 若默认的异常处理方法中没有进行处理则使用系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);  //
        } else {
            try {
                Thread.sleep(3000); // 延时3秒中后退出引用程序，确保将异常信息进行有效保存
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!onAppExit()) {
                // 若应用程序退出前在onExit()方法中没有进行有效处理，则直接退出程序
                Process.killProcess(Process.myPid());
                System.exit(1);     // 0 - 成功退出，1 - 异常退出
            }
        }
    }
    // ===========================================================

    // CrashHandler中默认的异常处理方法
    // 处理：收集错误信息，发送错误报告等操作
    protected boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;   // 未处理
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "Sorry, application appears abnormal.", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

        // 保存异常信息到文件中
        storeCrashInfoToFile(ex);

        return true;    // 已经处理
    }

    protected boolean onAppExit() {
        return false;

        // 重新启动程序
//        Intent intent = new Intent();
//        intent.setClass(mContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
//        Process.killProcess(Process.myPid());
//        return true;
    }

    // 获取设备参数信息
    public Map<String, String> getDeviceInfo(Context context) {
        Map<String, String> infoMap = new LinkedHashMap<>();    // 默认保持插入的顺序

        // 手机设备名称 (m2 note)
        infoMap.put("devicemodel", Build.MODEL);

        // 手机操作系统版本 (android 5.1)
        infoMap.put("osversion", Build.VERSION.RELEASE);

        // 应用程序版本 (1.0)
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            infoMap.put("appversion", pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            infoMap.put("appversion", "0.0.0");
        }

        return infoMap;
    }

    // 保存异常信息到文件中
    public void storeCrashInfoToFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Map<String, String> infoMap = getDeviceInfo(mContext);  // 获取设备参数信息
        for (Map.Entry<String, String> entry : infoMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);             // 输出堆栈信息到PrintStream中
        Throwable cause = ex.getCause();    // 循环将所有的异常信息写入到PrintStream中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();

        sb.append("\r\n");                  // 分隔设备参数信息和异常信息
        sb.append(writer.toString());       //

        String date = mDateFormat.format(new Date());
        String fileName = "crash_" + date + ".log";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String root;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
            } else {
                root = Environment.getExternalStorageDirectory().getPath() + "/Documents";
            }
            File dir = new File(root + File.separator + "crash");
            if (!dir.exists()) {
                if (!dir.mkdir()) {         // 若目录创建不成功，则直接在Documents目录下保存log文件
                    dir = new File(root);
                }
            }
            try {
                // crash_20161214_224551.log
                FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
                fos.write(sb.toString().getBytes());
                fos.close();
                // log_list_com_shellever_exception.log
                String logList = "log_list_" + mContext.getPackageName().replaceAll("\\.", "_") + ".log";
                fos = new FileOutputStream(new File(dir, logList), true);   // true表示追加模式
                fileName += "\r\n";
                fos.write(fileName.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

// log_list_com_shellever_exception.log
/*
crash_20161214_224547.log
crash_20161214_224551.log

 */

//
// crash_20161214_224551.log
/*
devicemodel=m2 note
osversion=5.1
appversion=1.0

java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.lang.String.isEmpty()' on a null object reference
	at com.shellever.exception.MainActivity.testCrashHandler(MainActivity.java:29)
	at com.shellever.exception.MainActivity.onClick(MainActivity.java:22)
	at android.view.View.performClick(View.java:4908)
	at android.view.View$PerformClick.run(View.java:20378)
	at android.os.Handler.handleCallback(Handler.java:815)
	at android.os.Handler.dispatchMessage(Handler.java:104)
	at android.os.Looper.loop(Looper.java:194)
	at android.app.ActivityThread.main(ActivityThread.java:5691)
	at java.lang.reflect.Method.invoke(Native Method)
	at java.lang.reflect.Method.invoke(Method.java:372)
	at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:959)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:754)

 */
