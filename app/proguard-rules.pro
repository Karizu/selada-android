# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/user/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v7.app.AppCompatActivity
-keep public class * extends smansa.BaseActivity

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# rxjava
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

#retrofit2
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn rx.**

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# Parceler
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-keep class org.parceler.Parceler$$Parcels

# Parcel library
-keep class **$$Parcelable { *; }

#okio
-dontwarn okio.**

-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final *** CREATOR;
}

-keep public class org.jsoup.** {
    public *;
}

-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <methods>;
}

# google analytic
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

# Request
-keep class smansa.component.network.response.** { *; }
-dontwarn smansa.component.network.response.**

-keep class smansa.component.network.gson.** { *; }
-dontwarn smansa.component.network.gson.**

# Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

#Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature
