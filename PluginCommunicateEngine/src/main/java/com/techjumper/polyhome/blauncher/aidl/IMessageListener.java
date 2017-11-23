/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhaoyiding/Documents/workspace/_work/polyhome_b_host/polyhome_b_host_android/BLauncher/src/main/aidl/com/techjumper/polyhome/blauncher/aidl/IMessageListener.aidl
 */
package com.techjumper.polyhome.blauncher.aidl;
public interface IMessageListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements IMessageListener
{
private static final String DESCRIPTOR = "com.techjumper.polyhome.blauncher.aidl.IMessageListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.techjumper.polyhome.blauncher.aidl.IMessageListener interface,
 * generating a proxy if needed.
 */
public static IMessageListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof IMessageListener))) {
return ((IMessageListener)iin);
}
return new Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onNewMessageFromPluginEngine:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
String _arg1;
_arg1 = data.readString();
android.os.Bundle _arg2;
if ((0!=data.readInt())) {
_arg2 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
this.onNewMessageFromPluginEngine(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements IMessageListener
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
@Override public void onNewMessageFromPluginEngine(int code, String message, android.os.Bundle extras) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(code);
_data.writeString(message);
if ((extras!=null)) {
_data.writeInt(1);
extras.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onNewMessageFromPluginEngine, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onNewMessageFromPluginEngine = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
public void onNewMessageFromPluginEngine(int code, String message, android.os.Bundle extras) throws android.os.RemoteException;
}
