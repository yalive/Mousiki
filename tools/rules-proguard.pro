-repackageclasses
-dontwarn com.sothree.slidinguppanel.SlidingUpPanelLayout
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keep class com.cas.musicplayer.data.local.models.** { *; }



# Kotlinx serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Change here com.yourcompany.yourpackage
-keep,includedescriptorclasses class com.mousiki.shared.data.*.**$$serializer { *; }
-keepclassmembers class com.mousiki.shared.data.*.** {
    *** Companion;
}
-keepclasseswithmembers class com.mousiki.shared.data.*.** {
    kotlinx.serialization.KSerializer serializer(...);
}