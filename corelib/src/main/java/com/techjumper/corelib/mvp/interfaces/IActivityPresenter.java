package com.techjumper.corelib.mvp.interfaces;

import android.content.Intent;
import android.os.Bundle;


/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/12
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public interface IActivityPresenter<View> extends IPresenter<View> {

    void onCreate(Bundle saveInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onRestart();

    void onDestroy();

    void onSavedInstanceState(Bundle saveInstanceState);

    void onRestoreInstanceState(Bundle saveInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

}
