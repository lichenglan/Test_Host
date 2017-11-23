package com.techjumper.polyhome.blauncher.entity.event;

import android.content.pm.PackageInfo;

import java.util.ArrayList;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/14
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PluginInfoReceiveEvent {
    private ArrayList<PackageInfo> pluginInfoList;

    public PluginInfoReceiveEvent(ArrayList<PackageInfo> pluginInfoList) {
        this.pluginInfoList = pluginInfoList;
    }

    public ArrayList<PackageInfo> getPluginInfoList() {
        return pluginInfoList;
    }

    public void setPluginInfoList(ArrayList<PackageInfo> pluginInfoList) {
        this.pluginInfoList = pluginInfoList;
    }
}
