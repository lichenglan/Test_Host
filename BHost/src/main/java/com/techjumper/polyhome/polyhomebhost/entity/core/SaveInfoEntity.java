package com.techjumper.polyhome.polyhomebhost.entity.core;

import java.util.HashMap;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/8
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class SaveInfoEntity extends BaseMessageEntity<SaveInfoEntity.DataEntity> {
    public static class DataEntity {
        private String name;
        private HashMap<String, String> values;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public HashMap<String, String> getValues() {
            return values;
        }

        public void setValues(HashMap<String, String> values) {
            this.values = values;
        }
    }

}
