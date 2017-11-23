# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/zhaoyiding/Documents/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontskipnonpubliclibraryclassmembers
-dontshrink
#-dontoptimize

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,JavascriptInterface

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.view.View
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keepclassmembers class * extends android.app.Activity {
     public void *(android.view.View);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    **[] $VALUES;
    public *;
}

-keep public class * extends android.view.View {
     *** get*();
     void set*(***);
     public <init>(android.content.Context);
     public <init>(android.content.Context, android.util.AttributeSet);
     public <init>(android.content.Context, android.util.AttributeSet, int);
     public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class * implements java.io.Serializable {
     static final long serialVersionUID;
     private static final java.io.ObjectStreamField[] serialPersistentFields;
     private void writeObject(java.io.ObjectOutputStream);
     private void readObject(java.io.ObjectInputStream);
     java.lang.Object writeReplace();
     java.lang.Object readResolve();
}

-keepclassmembers class * {
    void *(**On*Event);
}

#-assumenosideeffects class android.util.Log { *; }
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** e(...);
#}

#-keepclassmembers class * {
#   public <init>(org.json.JSONObject);
#}

-keep class * extends java.lang.annotation.Annotation { *; }

-keepclassmembers @com.techjumper.corelib.mvp.factory.* class * {*;}

-keep public interface com.techjumper.corelib.mvp.interfaces.** {
    public protected *;
}

-keep public class com.techjumper.corelib.mvp.presenter.** {
    public protected *;
}

-keepclassmembers public class * {
   public <init>(...);
}

-dontwarn java.lang.invoke.*

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class com.google.**{*;}
-keep interface com.google.**{*;}

-dontwarn com.tbruyelle.**
-dontwarn android.support.v7.**

#-keepclassmembers class * extends android.webkit.WebViewClient {
#    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
#    public boolean *(android.webkit.WebView, java.lang.String);
#}

#-keepclassmembers class * extends android.webkit.WebViewClient {
#    public void *(android.webkit.WebView, java.lang.String);
#}

#-keepclassmembers class 包名.XXX$JSInterface1 {
#    <methods>;
#}

#-keepclasseswithmembernames class 包名.model.**{
#	private public protected *;
#}

#-------------------------     lib2     -------------------------

#Gson
-keep class com.google.gson.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#LiteORM
-keep public class com.litesuits.orm.LiteOrm { *; }
-keep public class com.litesuits.orm.db.* { *; }
-keep public class com.litesuits.orm.db.model.** { *; }
-keep public class com.litesuits.orm.db.annotation.** { *; }
-keep public class com.litesuits.orm.db.enums.** { *; }
-keep public class com.litesuits.orm.log.* { *; }
-keep public class com.litesuits.orm.db.assit.* { *; }

#retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

#okhttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}

#okio
-keep class okio.** {*;}
-dontwarn okio.**
-dontwarn sun.nio.**
-dontwarn java.beans.**

#-------------------------      SlidingMenu      -------------------------
-dontwarn com.techjumper.slidingmenulib.**
-keep class com.techjumper.slidingmenulib.** {*;}
-keep interface com.techjumper.slidingmenulib.** {*;}

#-------------------------      Kryonet      -------------------------
#-keep class com.esotericsoftware.** {*;}
#-keep interface com.esotericsoftware.** {*;}

#-------------------------      tcplib      -------------------------
#-keep class com.techjumper.tcplib.** {*;}
#-keep interface com.techjumper.tcplib.** {*;}

#-------------------------      KProgress      -------------------------
-keep class com.techjumper.progressdialog.**{*;}
-keep interface com.techjumper.progressdialog.**{*;}

#-------------------------      RemoteCamera      -------------------------
-keep class com.techjumper.remotecamera.**{*;}
-keep interface com.techjumper.remotecamera.**{*;}

#-------------------------      Zxing      -------------------------
-keep class com.techjumper.zxing.**{*;}
-keep interface com.techjumper.zxing.**{*;}

#-------------------------      Ptr      -------------------------
-keep class com.techjumper.ptr_lib.**{*;}
-keep interface com.techjumper.ptr_lib.**{*;}

#-------------------------      app      -------------------------
-keep class com.techjumper.polyhome.entity.** { *; }
-keep interface com.techjumper.polyhome.net.** { *; }
-keep class com.techjumper.polyhome.utils.HeaderGridExpandWraper$AnimExecutor {
    <methods>;
}

#umeng
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
