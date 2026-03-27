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


# --------------------------------------------------------
# 1. JNA (Java Native Access) & Native 연결 관련
# --------------------------------------------------------
# JNA 핵심 클래스 보존
-keep class com.sun.jna.** { *; }
-keepclassmembers class com.sun.jna.** { *; }

# JNA Structure (구조체) 보존 - "unknown or zero size" 에러 해결
-keepclasseswithmembers class * extends com.sun.jna.Structure {
    <fields>;
    <init>(...);
}
-keep class * extends com.sun.jna.Structure { *; }

# JNA Callback (콜백) 보존 - "Callback must implement a single public method" 에러 해결
-keep class * extends com.sun.jna.Callback { *; }
-keepclassmembers class * extends com.sun.jna.Callback { *; }

# JNA Library 인터페이스 보존
-keep class * implements com.sun.jna.Library { *; }

# 네이티브 메서드 이름 유지
-keepclasseswithmembernames class * {
    native <methods>;
}

# --------------------------------------------------------
# 2. Rusaint & FFI (Rust 연동 라이브러리) 관련
# --------------------------------------------------------
# dev.eatsteak.rusaint 패키지 전체 보존
-keep class dev.eatsteak.rusaint.** { *; }
-keepclassmembers class dev.eatsteak.rusaint.** { *; }

# rustls-platform-verifier Android 초기화/연결 클래스 보존
-keep, includedescriptorclasses class org.rustls.platformverifier.** { *; }

# --------------------------------------------------------
# 3. Protobuf & DataStore 관련 (H2.f, isLoggedIn_ 에러 해결)
# --------------------------------------------------------
# Protobuf 생성된 메시지 클래스 보존
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
    <methods>;
}

# Protobuf 빌더 보존
-keep class * extends com.google.protobuf.GeneratedMessageLite$Builder { *; }

# 리플렉션 시 필요한 속성들 유지
-keepattributes Signature, EnclosingMethod, InnerClasses, AnnotationDefault, *Annotation*

# --------------------------------------------------------
# 4. 기타 경고 무시
# --------------------------------------------------------
-dontwarn com.sun.jna.**
-dontwarn com.google.protobuf.**
