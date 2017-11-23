package com.techjumper.polyhome.polyhomebhost.by_function.log.tasks;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;

import com.techjumper.corelib.utils.common.DateUtils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.file.FileUtils;
import com.techjumper.polyhome.polyhomebhost.by_function.log.LogUtils;
import com.techjumper.polyhome.polyhomebhost.by_function.log.PolyLogDbExecutor;
import com.techjumper.polyhome.polyhomebhost.entity.sql.PolyLog;
import com.techjumper.polyhome.polyhomebhost.net.NetExecutor;
import com.techjumper.polyhome.polyhomebhost.net.NetHelper;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/29
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class UpdateAndCleanTask extends PolyLogDbTask {

    private String mContent;

    @Override
    public void run() {
        PolyLogDbExecutor.BriteDatabaseHelper dbHelper = PolyLogDbExecutor.getHelper();
        JLog.d("<log> 准备上传日志");
        Observable.just("")
                .flatMap(s -> dbHelper.queryAll())
                .map(polyLogs -> {
                    if (polyLogs == null) {
                        JLog.d("<log> 没有任何日志信息");
                        return "";
                    }
                    JLog.d("<log> 日志数量：" + polyLogs.size() + "条");
                    StringBuilder sb = new StringBuilder();
                    for (PolyLog polyLog : polyLogs) {
                        if (polyLog == null)
                            continue;
                        sb.append(polyLogToString(polyLog)).append("\r\n");
                    }
                    return sb.toString();
                })
                .flatMap(s -> {
                    if (TextUtils.isEmpty(s))
                        return Observable.error(new NullPointerException("没有任何日志信息"));
                    mContent = s;
                    return NetExecutor.getUserInfo();
                })
                .flatMap(userEntity -> {
                    if (!NetHelper.processNetworkResult(userEntity)) {
                        String log = userEntity == null ? "请求服务器失败，数据为空"
                                : "请求服务器失败，code=" + userEntity.getError_code() + " msg=" + userEntity.getError_msg();
                        return Observable.error(new NetworkErrorException(log));
                    }

                    if (userEntity.getData() == null)
                        return Observable.error(new NullPointerException("用户信息为空"));

                    return Observable.just(userEntity.getData().getId() + "");
                })
                .flatMap(familyId -> {
                    if (TextUtils.isEmpty(familyId))
                        return Observable.error(new NullPointerException("no family id"));
                    return NetExecutor.uploadLogs(
                            familyId
                            , DateUtils.formatCurrentTime("yyyyMMddHHmmss") + ".log"
                            , FileUtils.base64Encode(mContent));
                })
                .flatMap(trueEntity -> {
                    if (!NetHelper.isSuccess(trueEntity)) {
                        String reason = trueEntity == null ? "<log> 日志上传服务器失败：无返回信息"
                                : "<log> 日志上传服务器失败：code=" + trueEntity.getError_code() + ", msg=" + trueEntity.getError_msg();
                        return Observable.error(new NetworkErrorException(reason));
                    }
                    JLog.d("<log> 日志上传到服务器成功");
                    boolean b = dbHelper.deleteAll();
                    return Observable.just(b);
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        dbHelper.close();
                        onFinished();
                    }

                    @Override
                    public void onError(Throwable e) {
                        String error = e.toString();
                        if (error.contains("large")
                                || error.contains("Large")) {
                            Observable.just("")
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Subscriber<String>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            LogUtils.insertLog("上传日志失败（large）：" + e);
                                        }

                                        @Override
                                        public void onNext(String s) {
                                            dbHelper.deleteAll();
                                            dbHelper.close();
                                            UpdateAndCleanTask.this.onError(e);
                                        }
                                    });
                            return;
                        }
                        LogUtils.insertLog("上传日志失败：" + e);
                        dbHelper.close();
                        UpdateAndCleanTask.this.onError(e);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                    }
                });

    }

    private String polyLogToString(PolyLog polyLog) {
        String time = DateUtils.formatTime("yyyy-MM-dd HH:mm:ss", polyLog.time() * 1000);
        return time + ": " + polyLog.content();
    }
}
