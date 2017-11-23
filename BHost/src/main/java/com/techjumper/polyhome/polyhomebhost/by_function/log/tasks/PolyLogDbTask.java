package com.techjumper.polyhome.polyhomebhost.by_function.log.tasks;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/29
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public abstract class PolyLogDbTask {

    private boolean isFinished;
    private ILogDbTask iLogDbTask;

    public abstract void run();

    public void setLogDbTaskListener(ILogDbTask iLogDbTask) {
        this.iLogDbTask = iLogDbTask;
    }

    protected void onFinished() {
        isFinished = true;
        if (iLogDbTask != null)
            iLogDbTask.onDbTaskFinished();
        clearListener();
    }

    protected void onError(Throwable e) {
        isFinished = true;
        if (iLogDbTask != null)
            iLogDbTask.onError(e);
        clearListener();
    }

    public boolean isFinished() {
        return isFinished;
    }

    private void clearListener() {
        iLogDbTask = null;
    }

    public interface ILogDbTask {
        void onDbTaskFinished();

        void onError(Throwable e);
    }

}
