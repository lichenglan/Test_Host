// IMessage.aidl
package com.techjumper.polyhome.blauncher.aidl;

// Declare any non-default types here with import statements
import android.os.Bundle;

interface IMessageListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewMessageFromPluginEngine(int code, String message, in Bundle extras);
}
