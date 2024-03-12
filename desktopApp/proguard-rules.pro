-keepclasseswithmembers public class MainKt {
    public static void main(java.lang.String[]);
}

-dontwarn javax.annotation.**
-dontwarn org.apache.**
-dontwarn org.bouncycastle.**
-dontwarn android.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn org.w3c.**
-dontwarn org.xml.sax.**
-dontwarn javax.xml.**
-dontwarn sun.misc.**
-dontwarn org.codehaus.mojo.**
-dontwarn com.ibm.icu.**

-keep class kotlin.Metadata

-keepclassmembers enum * {
    public *;
}

-keep class org.sqlite.** { *; }

-keepattributes *Annotation*, InnerClasses, Signature


-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }
-keep class androidx.compose.** { *; }

-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer,java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer,int,java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
    boolean isTraceInProgress();
    void traceEventStart(int, java.lang.String);
    void traceEventEnd();
}

-keepclassmembers class io.ktor.** {
    volatile <fields>;
}

-keepclassmembernames class io.ktor.** {
    volatile <fields>;
}

-keep class io.ktor.client.HttpClientEngineContainer
-keep class io.ktor.client.engine.** implements io.ktor.client.HttpClientEngineContainer

-keep class ** implements java.sql.Driver

-keep class org.apache.logging.** { *; }
-keep class org.slf4j.** { *; }