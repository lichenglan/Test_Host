package com.techjumper.polyhome.droidpluginmanager;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/4/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * 专门用来处理assets下的插件
 */
public class PluginAssetsManager {

    private static PluginAssetsManager INSTANCE;

    private static String sAssetsPluginsPath = "plugins";
    private String mAssetsDir = "plugin_aseets";

    private PluginAssetsManager() {
    }

    public static PluginAssetsManager getInstance() {
        if (INSTANCE == null) {
            synchronized (PluginAssetsManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PluginAssetsManager();
                }
            }
        }
        return INSTANCE;
    }

    public Observable<Boolean> copyAssetsPluginToInstallDir() {
        return Observable
                .create((Observable.OnSubscribe<List<String>>) subscriber -> {
                    try {
                        PluginInstaller.getInstance().initPluginDir();
                        PluginInstaller.getInstance().initDir(
                                PluginInstaller.getInstance().getPluginBaseDir(), mAssetsDir);
                        String[] list = Utils.appContext.getAssets().list(sAssetsPluginsPath);
                        subscriber.onNext(Arrays.asList(list));

                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                    subscriber.onCompleted();
                })
                .flatMap(Observable::from)
                .map(name -> {
                    if (!Constants.POSTFIX_PACKAGE.equals(FileUtils.fetchFilePostfix(name)))
                        return false;
                    String targetAssetsPath;
                    try {
                        targetAssetsPath = PluginInstaller.getInstance().getPluginBaseDir()
                                + File.separator + mAssetsDir;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                    File targetFile = new File(targetAssetsPath + File.separator + name);

                    deletePreviousVersion(name, targetFile.getParent());
                    if (!targetFile.exists()) {
                        FileUtils.saveAssetsFileToPath(sAssetsPluginsPath, name, targetFile.getParent());
                    }

                    if (!targetFile.exists()) return false;
                    try {
                        if (!PluginUtils.isInstalled(targetFile.getAbsolutePath())
                                || PluginUtils.hasUpdate(targetFile.getAbsolutePath())) {
                            FileUtils.copyFileToOtherPath(targetFile.getParent(), name
                                    , PluginInstaller.getInstance().getPluginWaitInstallDir());
//                            JLog.d(targetFile.getName() + " 未安装过或者有版本升级,所以拷贝到安装目录里");
                        } else {
//                            JLog.d(targetFile.getName() + " 已经安装并且没有更新,所以不做操作");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                })
                .subscribeOn(Schedulers.io());
    }

    /**
     * 删除之前的版本
     */
    private void deletePreviousVersion(String apkName, String path) {
        int lastIndex = apkName.lastIndexOf("_");
        if (lastIndex == -1 || lastIndex == 0) return;
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] fileList = dir.listFiles();
        if (fileList == null) return;

        String preName = apkName.substring(0, lastIndex);
        for (File file : fileList) {
            if (file == null || !file.exists() || file.getName().equalsIgnoreCase(apkName))
                continue;
            if (file.getName().contains(preName)) {
                file.delete();
            }
        }

    }

}
