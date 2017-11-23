package com.techjumper.corelib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/15
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

/**
 * 用来恢复Fragment的实体
 */
public class BaseFragmentActivitySaveEntity implements Parcelable {
    public static final String ENTITY_KEY = "BaseFragmentActivitySaveEntity";

    private String[] allFragmentTags;
    private ArrayList<IntKeyStringValue> keyValue;
    private int containerId;
    private String noStackTag;

    public String[] getAllFragmentTags() {
        return allFragmentTags;
    }

    public void setAllFragmentTags(String[] allFragmentTags) {
        this.allFragmentTags = allFragmentTags;
    }

    public ArrayList<IntKeyStringValue> getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(ArrayList<IntKeyStringValue> keyValue) {
        this.keyValue = keyValue;
    }

    public int getContainerId() {
        return containerId;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    public String getNoStackTag() {
        return noStackTag;
    }

    public void setNoStackTag(String noStackTag) {
        this.noStackTag = noStackTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.allFragmentTags);
        dest.writeTypedList(keyValue);
        dest.writeInt(this.containerId);
        dest.writeString(this.noStackTag);
    }

    public BaseFragmentActivitySaveEntity() {
    }

    protected BaseFragmentActivitySaveEntity(Parcel in) {
        this.allFragmentTags = in.createStringArray();
        this.keyValue = in.createTypedArrayList(IntKeyStringValue.CREATOR);
        this.containerId = in.readInt();
        this.noStackTag = in.readString();
    }

    public static final Parcelable.Creator<BaseFragmentActivitySaveEntity> CREATOR = new Parcelable.Creator<BaseFragmentActivitySaveEntity>() {
        public BaseFragmentActivitySaveEntity createFromParcel(Parcel source) {
            return new BaseFragmentActivitySaveEntity(source);
        }

        public BaseFragmentActivitySaveEntity[] newArray(int size) {
            return new BaseFragmentActivitySaveEntity[size];
        }
    };


    public static class IntKeyStringValue implements Parcelable {
        private int key;
        private String tag;

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.key);
            dest.writeString(this.tag);
        }

        public IntKeyStringValue() {
        }

        protected IntKeyStringValue(Parcel in) {
            this.key = in.readInt();
            this.tag = in.readString();
        }

        public static final Parcelable.Creator<IntKeyStringValue> CREATOR = new Parcelable.Creator<IntKeyStringValue>() {
            public IntKeyStringValue createFromParcel(Parcel source) {
                return new IntKeyStringValue(source);
            }

            public IntKeyStringValue[] newArray(int size) {
                return new IntKeyStringValue[size];
            }
        };
    }

}
