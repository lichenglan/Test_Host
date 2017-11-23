package com.techjumper.polyhome.blauncher.entity.core;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/30
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class StartPluginActivityEntity extends BaseMessageEntity<StartPluginActivityEntity.DataEntity> {

    /**
     * 无实际用处, 只是为了规范
     */
    public static final String ACTIVITY_LAUNCHER = "launcher";

    public static class DataEntity {
        private String packageName;
        private String activityName;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }
    }
}
