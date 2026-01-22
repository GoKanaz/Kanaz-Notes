-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature, Exceptions, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-dontwarn androidx.room.**

-dontwarn kotlinx.coroutines.**

-keepclassmembers class com.gokanaz.kanaznotes.data.model.** {
    *;
}

-keep class * extends androidx.lifecycle.ViewModel {
    *;
}

-keep class com.gokanaz.kanaznotes.workflow.** {
    *;
}

-keep class com.tencent.mmkv.** { *; }
-keep class com.tencent.mmkv.MMKV { *; }
-keepclassmembers class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**
