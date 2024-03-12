-libraryjars <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.compiler.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.datatransfer.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.desktop.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.instrument.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.logging.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.management.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.management.rmi.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.naming.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.net.http.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.prefs.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.rmi.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.scripting.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.se.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.security.jgss.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.security.sasl.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.smartcardio.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.sql.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.sql.rowset.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.transaction.xa.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.xml.crypto.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.xml.jmod(!**.jar;!module-info.class)

-keepclasseswithmembers public class MainKt {
    public static void main(java.lang.String[]);
}

-dontwarn kotlinx.coroutines.debug.*
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


-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }

-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer,java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer,int,java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
}