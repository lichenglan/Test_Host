package com.techjumper.pluginappdemo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.techjumper.plugincommunicateengine.PluginEngine;
import com.techjumper.plugincommunicateengine.HostDataBuilder;
import com.techjumper.plugincommunicateengine.IPluginMessageReceiver;
import com.techjumper.plugincommunicateengine.entity.core.SaveInfoEntity;
import com.techjumper.plugincommunicateengine.utils.GsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/5/31
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class SecondActivity extends AppCompatActivity implements IPluginMessageReceiver {

    private TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mTvContent = (TextView) findViewById(R.id.tv_content);

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String text = "版本: " + versionName;
            mTvContent.setText(text);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        PluginEngine.getInstance().init(this);
        PluginEngine.getInstance().registerReceiver(this);
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                PluginEngine.getInstance().start(new PluginEngine.IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginEngine.PluginExecutor pluginExecutor) {
                        try {
                            pluginExecutor.send(PluginEngine.CODE_GET_PLUGIN_INFO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {
                    }
                });
                break;
            case R.id.btn_2:
//                String value1 = Math.random() * 100 + "";
//                String value2 = Math.random() * 100 + "";
//                String value3 = Math.random() * 100 + "";
//                String data = HostDataBuilder.saveInfoBuilder()
//                        .name("test")
//                        .put("key1", value1)
//                        .put("key2", value2)
//                        .put("key3", value3)
//                        .build();
//                JLog.d("data = \n" + data);
                PluginEngine.getInstance().start(new PluginEngine.IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginEngine.PluginExecutor pluginExecutor) {
                        try {
                            String value1 = Math.random() * 100 + "";
                            String value2 = Math.random() * 100 + "";
                            String value3 = Math.random() * 100 + "";
                            String data = HostDataBuilder.saveInfoBuilder()
                                    .name("test")
                                    .put("key1", value1)
                                    .put("key2", value2)
                                    .put("key3", value3)
                                    .build();
                            pluginExecutor.send(PluginEngine.CODE_SAVE_INFO, data);
                            String hint = "保存了以下值: [" + value1 + "," + value2 + "," + value3 + "]";
                            mTvContent.setText(hint);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {

                    }
                });
                break;
            case R.id.btn_3:
                PluginEngine.getInstance().start(new PluginEngine.IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginEngine.PluginExecutor pluginExecutor) {
                        try {
                            String data = HostDataBuilder.saveInfoBuilder()
                                    .name("test")
                                    .build();
                            pluginExecutor.send(PluginEngine.CODE_GET_SAVE_INFO, data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {

                    }
                });
                break;
            case R.id.btn_4:
                PluginEngine.getInstance().start(new PluginEngine.IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginEngine.PluginExecutor pluginExecutor) {
                        try {
                            pluginExecutor.send(PluginEngine.CODE_UPDATE_PLUGIN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {
                    }
                });
                break;
            case R.id.btn_5:
                PluginEngine.getInstance().start(new PluginEngine.IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginEngine.PluginExecutor pluginExecutor) {
                        try {
                            pluginExecutor.send(PluginEngine.CODE_CUSTOM, "{\"msg\":\"test\"}");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {

                    }
                });
                break;
            case R.id.btn_6:
                PluginEngine.getInstance().start(new PluginEngine.IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginEngine.PluginExecutor pluginExecutor) {
                        try {
                            pluginExecutor.send(PluginEngine.CODE_GET_PUSH_ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {

                    }
                });
                break;
            case R.id.btn_7:
                PluginEngine.getInstance().start(new PluginEngine.IPluginConnection() {
                    @Override
                    public void onEngineConnected(PluginEngine.PluginExecutor pluginExecutor) {
                        try {

                            String data = HostDataBuilder.startPluginBuilder()
                                    .packageName("com.techjumper.plugintest2") //指定插件的packageName
                                    .build();

                            pluginExecutor.send(PluginEngine.CODE_START_PLUGIN, data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEngineDisconnected() {

                    }
                });

                break;
        }
    }

    @Override
    protected void onDestroy() {
        PluginEngine.getInstance().unregisterReceiver(this);
        super.onDestroy();
    }

    @Override
    public void onPluginMessageReceive(int code, String message, Bundle extras) {

        Log.d("HIDETAG", "第二页收到核心层数据: code=" + code + ", message=" + message);
        StringBuilder sb = new StringBuilder();
        switch (code) {
            case PluginEngine.CODE_GET_PLUGIN_INFO:
                ArrayList<Parcelable> parcelableArrayList = extras.getParcelableArrayList(PluginEngine.KEY_EXTRA);
                if (parcelableArrayList == null) {
                    mTvContent.setText("插件信息为null");
                    return;
                }
                sb.append("插件个数:").append(parcelableArrayList.size()).append("\n");
                for (Parcelable parcelable : parcelableArrayList) {
                    if (parcelable instanceof PackageInfo) {
                        PackageInfo pluginInfo = (PackageInfo) parcelable;
                        sb.append("插件包名:").append(pluginInfo.packageName).append("\n");
                    }
                }
                mTvContent.setText(sb);
                break;
            case PluginEngine.CODE_SAVE_INFO:
                mTvContent.append("\n保存成功, 文件名:" + message);
                break;
            case PluginEngine.CODE_GET_SAVE_INFO:
                SaveInfoEntity saveInfoEntity = GsonUtils.fromJson(message, SaveInfoEntity.class);
                if (saveInfoEntity == null || saveInfoEntity.getData() == null)
                    return;
                String name = saveInfoEntity.getData().getName();
                sb.append("文件名:").append(name).append("\n");
                HashMap<String, String> values = saveInfoEntity.getData().getValues();
                if (values == null || values.size() == 0)
                    sb.append("无内容");
                else {
                    for (Map.Entry<String, String> next : values.entrySet()) {
                        sb.append("key:").append(next.getKey()).append(",value:").append(next.getValue()).append("\n");
                    }
                }
                mTvContent.setText(sb);
                break;
            case PluginEngine.CODE_CUSTOM:
                String msg = "SecondActivity 收到自定义消息: " + message;
                mTvContent.setText(msg);
                break;
            case PluginEngine.CODE_GET_PUSH_ID:
                String pushIdStr = "PUSH ID: " + extras.getString(PluginEngine.KEY_MESSAGE);
                mTvContent.setText(pushIdStr);
        }
    }
}
