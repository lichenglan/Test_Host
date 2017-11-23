package com.techjumper.polyhome.droidpluginmanager;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.morgoo.droidplugin.pm.PluginManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PluginController implements ServiceConnection {
    private static PluginController INSTANCE;

    private List<WeakReference<IPluginController>> iPluginControllerList
            = Collections.synchronizedList(new ArrayList<>());

    private enum ControllerMethod {
        CONNECTED,
        DISCONNECTED
    }

    private PluginController() {
    }

    public static PluginController getInstance() {
        if (INSTANCE == null) {
            synchronized (PluginController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PluginController();
                }
            }
        }
        return INSTANCE;
    }

    public void start(IPluginController iPluginController) {
        addListener(iPluginController);
        if (PluginManager.getInstance().isConnected()) {
            notify(ControllerMethod.CONNECTED);
        } else {
            PluginManager.getInstance().addServiceConnection(this);
        }
    }

    public void addListener(IPluginController iPluginController) {
        boolean repeat = false;
        Iterator<WeakReference<IPluginController>> it = iPluginControllerList.iterator();
        while (it.hasNext()) {
            WeakReference<IPluginController> next = it.next();
            if (next == null || next.get() == null) {
                it.remove();
                continue;
            }
            if (next.get() == iPluginController) {
                repeat = true;
                break;
            }
        }
        if (!repeat) {
            iPluginControllerList.add(new WeakReference<>(iPluginController));
        }

    }

    private void notify(ControllerMethod controllerMethod) {
        Iterator<WeakReference<IPluginController>> it = iPluginControllerList.iterator();
        while (it.hasNext()) {
            WeakReference<IPluginController> next = it.next();
            if (next == null || next.get() == null) {
                it.remove();
                continue;
            }
            switch (controllerMethod) {
                case CONNECTED:
                    next.get().onPluginConnected();
                    break;
                default:
                    next.get().onPluginDisconnected();
                    break;
            }
        }

    }

    public void removeListener(IPluginController iPluginController) {
        Iterator<WeakReference<IPluginController>> it = iPluginControllerList.iterator();
        while (it.hasNext()) {
            WeakReference<IPluginController> next = it.next();
            if (next.get() == null) {
                it.remove();
                continue;
            }
            if (next.get() == iPluginController) {
                it.remove();
                break;
            }
        }
    }

    public void quit() {
        iPluginControllerList.clear();
        PluginManager.getInstance().removeServiceConnection(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        notify(ControllerMethod.CONNECTED);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        notify(ControllerMethod.DISCONNECTED);
    }

    public interface IPluginController {
        void onPluginConnected();

        void onPluginDisconnected();
    }

}
