package com.techjumper.polyhome.polyhomebhost.by_function.log.tasks;

import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.polyhome.polyhomebhost.by_function.log.PolyLogDbExecutor;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/29
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class InsertTask extends PolyLogDbTask {

    private String mContent;

    public InsertTask(String content) {
        if (mContent == null)
            mContent = "";
        mContent = content;
    }

    @Override
    public void run() {
        Observable.just("")
                .map(s -> {
                    PolyLogDbExecutor.BriteDatabaseHelper dbHelper = null;
                    try {
                        dbHelper = PolyLogDbExecutor.getHelper();
                        dbHelper.insert("1611111445 " + mContent);
                        JLog.d("<log> 插入了一条日志：" + mContent);
                    } finally {
                        if (dbHelper != null)
                            dbHelper.close();
                    }
                    return "";
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        onFinished();
                    }

                    @Override
                    public void onError(Throwable e) {
                        InsertTask.this.onError(e);
                    }

                    @Override
                    public void onNext(String s) {

                    }
                });
    }

}
