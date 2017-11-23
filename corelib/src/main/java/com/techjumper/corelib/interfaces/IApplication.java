package com.techjumper.corelib.interfaces;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/8
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IApplication {

    /**
     * 返回一个String[2]的数组,下标0为应用的英文名,下标1为crash文件名字
     */
    String[] fetchCrashFolderName();
}
