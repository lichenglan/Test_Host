package com.techjumper.polyhome.polyhomebhost.entity;

import java.util.List;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/6/13
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class UpdateAPKEntity extends BaseEntity<UpdateAPKEntity.DataEntity> {

    public static class DataEntity {

        private List<ResultEntity> result;

        public List<ResultEntity> getResult() {
            return result;
        }

        public void setResult(List<ResultEntity> result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "DataEntity{" +
                    "result=" + result +
                    '}';
        }

        public static class ResultEntity {
            private String version;
            private String url;
            private String package_name;

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getPackage_name() {
                return package_name;
            }

            public void setPackage_name(String package_name) {
                this.package_name = package_name;
            }

            @Override
            public String toString() {
                return "ResultEntity{" +
                        "version='" + version + '\'' +
                        ", url='" + url + '\'' +
                        ", package_name='" + package_name + '\'' +
                        '}';
            }
        }
    }

}
