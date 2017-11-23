package com.techjumper.corelib.utils.crash;

import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;


import com.techjumper.corelib.ui.activity.BaseActivity;
import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.system.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * app奔溃异常处理器
 * <p/>
 * 使用此类需要在AndroidManifest.xml配置以下权限
 * <p/>
 * <bold>android.permission.READ_EXTERNAL_STORAGE</bold>
 * <p/>
 * <bold>android.permission.WRITE_EXTERNAL_STORAGE</bold>
 * <p/>
 * <bold>android.permission.READ_PHONE_STATE</bold>
 * <p/>
 */
public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler {

    private volatile static CrashExceptionHandler INSTANCE;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
    /**
     * 默认存放闪退信息的文件夹名称
     */
    private static final String DEFAULT_CRASH_FOLDER_NAME = "Log";
    /**
     * appSD卡默认目录
     */
    private static final String DEFAULT_APP_FOLDER_NAME = "DefaultCrash";


    /**
     * app在SD卡上的主目录
     */
    private File mAppMainFolder;
    /**
     * 保存闪退日志的文件目录
     */
    private File mCrashInfoFolder;
    /**
     * 向远程服务器发送错误信息
     */
    private CrashExceptionRemoteReport mCrashExceptionRemoteReport;


    /**
     * @param appMainFolderName   app程序主目录名，配置后位于SD卡一级目录下
     * @param crashInfoFolderName 闪退日志保存目录名，配置后位于 appMainFolderName 配置的一级目录下
     */
    private CrashExceptionHandler(String appMainFolderName, String crashInfoFolderName) {
        if (!TextUtils.isEmpty(appMainFolderName)) {
            this.mAppMainFolder = new File(Environment.getExternalStorageDirectory(), appMainFolderName);
        } else {
            this.mAppMainFolder = new File(Environment.getExternalStorageDirectory(), DEFAULT_APP_FOLDER_NAME);
        }
        if (!TextUtils.isEmpty(crashInfoFolderName)) {
            this.mCrashInfoFolder = new File(mAppMainFolder, crashInfoFolderName);
        } else {
            this.mCrashInfoFolder = new File(mAppMainFolder, DEFAULT_CRASH_FOLDER_NAME);
        }
    }

    public static CrashExceptionHandler getInstance(String appMainFolderName, String crashInfoFolderName) {
        if (INSTANCE == null) {
            synchronized (CrashExceptionHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CrashExceptionHandler(appMainFolderName, crashInfoFolderName);
                }
            }
        }
        return INSTANCE;
    }

    public CrashExceptionHandler setDefaultHandler() {
        Thread.setDefaultUncaughtExceptionHandler(INSTANCE);
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        handleException(ex);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //退出所有Activity
        BaseActivity.finishAll();
        //杀死进程，退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 配置远程传回log到服务器的设置
     *
     * @param crashExceptionRemoteReport
     */
    public CrashExceptionHandler configRemoteReport(CrashExceptionRemoteReport crashExceptionRemoteReport) {
        this.mCrashExceptionRemoteReport = crashExceptionRemoteReport;
        return INSTANCE;
    }

    /**
     * 处理异常
     *
     * @param ex
     */
    private void handleException(Throwable ex) {
        if (ex != null) {
            sendCrashInfoToServer(ex);
            saveCrashInfoToFile(ex);

            //使用Toast来显示异常信息
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    try {
                        Toast.makeText(Utils.appContext, "程序出现异常 , 即将退出....", Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Looper.loop();
                }
            }.start();
        }
    }

    /**
     * 保存闪退信息到本地文件中
     *
     * @param ex
     */
    private void saveCrashInfoToFile(Throwable ex) {
        if (ex != null) {
            android.util.Log.e("HIDETAG", "------------ 崩溃啦~! ------------");

            StringBuilder builder = new StringBuilder();
            try {
                printStackTrace(builder, "", null, ex);
            } catch (IOException e) {
                e.printStackTrace();
            }
            android.util.Log.e("HIDETAG", builder.toString());
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                if (!mAppMainFolder.exists()) {//app目录不存在则先创建目录
                    mAppMainFolder.mkdirs();
                }
                if (!mCrashInfoFolder.exists()) {//闪退日志目录不存在则先创建闪退日志目录
                    mCrashInfoFolder.mkdirs();
                }
                String timeStampString = DATE_FORMAT.format(new Date());//当先的时间格式化
                String crashLogFileName = timeStampString + ".log";
                File crashLogFile = new File(mCrashInfoFolder, crashLogFileName);
                crashLogFile.createNewFile();

                //记录闪退环境的信息
                RandomAccessFile randomAccessFile = new RandomAccessFile(crashLogFile, "rw");
                randomAccessFile.write(strToBytes("------------Crash Environment Info------------" + "\n"));
                randomAccessFile.write(strToBytes("------------Manufacture: " + AppUtils.getDeviceManufacture() + "------------" + "\n"));
                randomAccessFile.write(strToBytes("------------DeviceName: " + AppUtils.getDeviceName() + "------------" + "\n"));
                randomAccessFile.write(strToBytes("------------SystemVersion: " + AppUtils.getSystemVersion() + "------------" + "\n"));
                randomAccessFile.write(strToBytes("------------DeviceIMEI: " + AppUtils.getDeviceIMEI() + "------------" + "\n"));
                randomAccessFile.write(strToBytes("------------AppVersion: " + AppUtils.getAppVersion() + "------------" + "\n"));
                randomAccessFile.write(strToBytes("------------Crash Environment Info------------" + "\n"));
                randomAccessFile.write(strToBytes("\n"));
                randomAccessFile.close();

                PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(crashLogFile.getAbsolutePath(), true)), true);
                ex.printStackTrace(pw);//写入崩溃的日志信息
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private byte[] strToBytes(String str) {
        byte[] b = null;
        try {
            b = str.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (b == null) b = new byte[1];
        return b;
    }

    /**
     * 发送发送闪退信息到远程服务器
     *
     * @param ex
     */
    private void sendCrashInfoToServer(Throwable ex) {
        if (mCrashExceptionRemoteReport != null) {
            mCrashExceptionRemoteReport.onCrash(ex);
        }
    }

    /**
     * 闪退日志远程奔溃接口，主要考虑不同app下，把log回传给服务器的方式不一样，所以此处留一个对外开放的接口
     */
    public interface CrashExceptionRemoteReport {
        /**
         * 当闪退发生时，回调此接口函数
         *
         * @param ex
         */
        void onCrash(Throwable ex);
    }

    public static void printStackTrace(StringBuilder err, String indent, StackTraceElement[] parentStack, Throwable ex)
            throws IOException {
        err.append(ex.toString());
        err.append("\n");

        StackTraceElement[] stack = ex.getStackTrace();
        if (stack != null) {
            int duplicates = parentStack != null ? countDuplicates(stack, parentStack) : 0;
            for (int i = 0; i < stack.length - duplicates; i++) {
                err.append(indent);
                err.append("\tat ");
                err.append(stack[i].toString());
                err.append("\n");
            }

            if (duplicates > 0) {
                err.append(indent);
                err.append("\t... ");
                err.append(Integer.toString(duplicates));
                err.append(" more\n");
            }
        }


        Throwable cause = ex.getCause();
        if (cause != null) {
            err.append(indent);
            err.append("Caused by: ");
            printStackTrace(err, indent, stack, cause);
        }
    }

    /**
     * Counts the number of duplicate stack frames, starting from the
     * end of the stack.
     */
    private static int countDuplicates(StackTraceElement[] currentStack,
                                       StackTraceElement[] parentStack) {
        int duplicates = 0;
        int parentIndex = parentStack.length;
        for (int i = currentStack.length; --i >= 0 && --parentIndex >= 0; ) {
            StackTraceElement parentFrame = parentStack[parentIndex];
            if (parentFrame.equals(currentStack[i])) {
                duplicates++;
            } else {
                break;
            }
        }
        return duplicates;
    }
}
