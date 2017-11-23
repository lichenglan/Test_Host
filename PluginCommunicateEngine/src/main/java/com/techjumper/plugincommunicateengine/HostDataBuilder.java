package com.techjumper.plugincommunicateengine;

import com.techjumper.plugincommunicateengine.entity.core.SaveInfoEntity;
import com.techjumper.plugincommunicateengine.entity.core.StartPluginActivityEntity;
import com.techjumper.plugincommunicateengine.utils.GsonUtils;

import java.util.HashMap;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/30
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class HostDataBuilder {

    private HostDataBuilder() {
    }

    public static StartPluginBuilder startPluginBuilder() {
        return new StartPluginBuilder();
    }

    public static SaveInfoBuilder saveInfoBuilder() {
        return new SaveInfoBuilder();
    }

    public static final class StartPluginBuilder {
        private String packageName;
        private String activityName;

        private StartPluginBuilder() {
        }

        public StartPluginBuilder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public StartPluginBuilder activityName(String activityName) {
            this.activityName = activityName;
            return this;
        }

        public String build() {
            StartPluginActivityEntity startPluginActivityEntity = new StartPluginActivityEntity();
            StartPluginActivityEntity.DataEntity dataEntity = new StartPluginActivityEntity.DataEntity();
            dataEntity.setPackageName(packageName);
            dataEntity.setActivityName(activityName);
            startPluginActivityEntity.setData(dataEntity);
            return GsonUtils.toJson(startPluginActivityEntity);
        }
    }

    public static final class SaveInfoBuilder {
        private String name;
        private HashMap<String, String> values = new HashMap<>();

        private SaveInfoBuilder() {
        }

        public SaveInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SaveInfoBuilder put(String key, String value) {
            values.put(key, value);
            return this;
        }

        public String build() {
            SaveInfoEntity saveInfoEntity = new SaveInfoEntity();
            SaveInfoEntity.DataEntity dataEntity = new SaveInfoEntity.DataEntity();
            dataEntity.setName(name);
            dataEntity.setValues(values);
            saveInfoEntity.setData(dataEntity);
            return GsonUtils.toJson(saveInfoEntity);
        }

    }
}
