#-keep class com.wzy.yukalite.tools.**{*; }
#-keep class com.wzy.yukalite.UserManager{*; }
#-keep class com.wzy.yukalite.config.YukaException{*; }
#-keep class com.wzy.yukalite.YukaRequest.*{*; }
-keep class com.wzy.yukalite.config.Mode{*; }
-keep class com.wzy.yukalite.config.Model{*; }
-keep class com.wzy.yukalite.config.Translator{*; }
-keep class com.wzy.yukalite.config.YukaConfig{*; }
-keep class com.wzy.yukalite.YukaLite{*; }
#-keep class com.wzy.yukalite.**{*; }

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
#保持泛型
-keepattributes Signature

# 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {
    *;
}
#所有native的方法不能去混淆.
-keepclasseswithmembernames class * {
    native <methods>;
}
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

-keepattributes InnerClasses
-dontoptimize

# 反射
-keepattributes Signature
-keepattributes EnclosingMethod


-keep class com.lzf.easyfloat.** {*;}

#okhttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

#kotlin
-dontwarn kotlin.**