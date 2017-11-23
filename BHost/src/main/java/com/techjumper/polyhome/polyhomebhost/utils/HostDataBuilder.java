package com.techjumper.polyhome.polyhomebhost.utils;


import com.techjumper.lib2.utils.GsonUtils;
import com.techjumper.polyhome.polyhomebhost.entity.core.SaveInfoEntity;

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


    public static SaveInfoBuilder saveInfoBuilder() {
        return new SaveInfoBuilder();
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
