-dontobfuscate

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
-dontwarn io.netty.**
-dontwarn org.jspecify.annotations.*
-dontwarn io.ktor.**

-keep class kotlin.Metadata

-keepclassmembers enum * {
    public *;
}

-keep class org.sqlite.** { *; }

-keepattributes *Annotation*, InnerClasses, Signature, RuntimeVisibleAnnotations, AnnotationDefault


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
-keep class io.ktor.serialization.kotlinx.KotlinxSerializationExtensionProvider
-keep class io.ktor.serialization.** implements io.ktor.serialization.kotlinx.KotlinxSerializationExtensionProvider

-keep class ** implements java.sql.Driver

-keep class org.apache.logging.** { *; }
-keep class org.slf4j.** { *; }

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueReferences

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class org.skynetsoftware.avnlauncher.**$$serializer { *; }
-keepclassmembers class org.skynetsoftware.avnlauncher.** {
    *** Companion;
}
-keepclasseswithmembers class org.skynetsoftware.avnlauncher.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class ** implements coil3.util.FetcherServiceLoaderTarget
