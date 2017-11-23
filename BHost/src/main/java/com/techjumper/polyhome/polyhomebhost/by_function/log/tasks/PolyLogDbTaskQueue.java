package com.techjumper.polyhome.polyhomebhost.by_function.log.tasks;

import com.techjumper.corelib.utils.common.JLog;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PolyLogDbTaskQueue implements PolyLogDbTask.ILogDbTask {

    private LinkedBlockingQueue<PolyLogDbTask> mQueue = new LinkedBlockingQueue<>();
    private AtomicBoolean mIsRuning = new AtomicBoolean(false);

    private PolyLogDbTaskQueue() {
    }

    private static class SingletonInstance {
        private static final PolyLogDbTaskQueue INSTANCE = new PolyLogDbTaskQueue();
    }

    public static PolyLogDbTaskQueue getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void addToQueue(PolyLogDbTask task) {
        if (task == null)
            return;
        try {
            mQueue.put(task);
            task.setLogDbTaskListener(this);
        } catch (InterruptedException e) {
            try {
                mQueue.put(task);
                task.setLogDbTaskListener(this);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                JLog.d("<log> push task 失败");
            }
        }
        start();
    }

    private void start() {
        if (mIsRuning.get())
            return;
        forceStart();
    }

    private void forceStart() {
        mIsRuning.set(true);
        try {
            PolyLogDbTask take = mQueue.take();
            if (take == null) {
                onDbTaskFinished();
                return;
            }
            take.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
            onDbTaskFinished();
            JLog.d("<log> 获取Task失败");
        }
    }

    @Override
    public void onDbTaskFinished() {
        pollingOrStop();
    }

    @Override
    public void onError(Throwable e) {
        pollingOrStop();
    }

    private void pollingOrStop() {
        if (mQueue.isEmpty()) {
            mIsRuning.set(false);
            return;
        }
        forceStart();
    }
}