package com.techjumper.plugintest2;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv = (TextView) findViewById(R.id.tv);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String text = "版本: " + versionName;
            mTv.setText(text);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
