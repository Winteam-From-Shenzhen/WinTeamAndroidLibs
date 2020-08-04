# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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


-optimizationpasses 5                   # 指定代码的压缩级别
-dontusemixedcaseclassnames             # 混淆时不会产生形形色色的类名
#-dontskipnonpubliclibraryclasses        # 指定不忽略非公共的库类
-dontpreverify                          # 不预校验
-dontoptimize
-verbose                                # 混淆时是否记录日志

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*    # 混淆时所采用的算法

#-keep public class com.ytzn.cmd.**
#-keep public class com.ytzn.impl.**
#-keeo public class com.ytzn.serialport.**
#-keeo public class com.ytzn.serialport.core.reader.**