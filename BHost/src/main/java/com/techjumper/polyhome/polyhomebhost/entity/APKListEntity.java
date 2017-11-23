package com.techjumper.polyhome.polyhomebhost.entity;

import java.util.List;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/9/22
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class APKListEntity extends BaseEntity<APKListEntity.DataEntity>{
    public static class DataEntity {

        private List<String> packages;

        public List<String> getPackages() {
            return packages;
        }

        public void setPackages(List<String> packages) {
            this.packages = packages;
        }
    }
}
