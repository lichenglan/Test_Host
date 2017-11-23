// IPluginCommunicate.aidl
package com.techjumper.polyhome.blauncher.aidl;

// Declare any non-default types here with import statements
import com.techjumper.polyhome.blauncher.aidl.IMessageListener;
import android.os.Bundle;

interface IPluginCommunicate {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void registerListener(IMessageListener listener);
    void unregisterListener(IMessageListener listener);
    void sendMessage(int code, String message);
    void sendMessageWithExtras(int code, String message, in Bundle extras);
}
