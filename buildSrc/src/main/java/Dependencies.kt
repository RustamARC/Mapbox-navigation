object Dependencies {

    const val mapbox_android_sdk = "com.mapbox.mapboxsdk:mapbox-android-sdk:${Versions.mapbox}"
    const val mapbox_android_navigation_ui =
        "com.mapbox.mapboxsdk:mapbox-android-navigation-ui:${Versions.mapbox_navigation}"
    const val mapbox_navigation_ui = "com.mapbox.navigation:ui:${Versions.mapbox_navigation_ui}"

    // Activity KTX for viewModels()
    const val activity_ktx = "androidx.activity:activity-ktx:${Versions.activity_ktx}"

    // Navigation Components
    const val fragment_ktx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation_fragment_ktx}"
    const val ui_ktx =
        "androidx.navigation:navigation-ui-ktx:{Versions.navigation_fragment_ktx}"

    // Dagger - Hilt
    const val hilt_android = "com.google.dagger:hilt-android:${Versions.hilt_android}"
    const val hilt_android_compiler =
        "com.google.dagger:hilt-android-compiler:${Versions.hilt_android}"

    const val hilt_lifecycle_viewmodel =
        "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.hilt_lifecycle_viewmodel}"
    const val hilt_compiler = "androidx.hilt:hilt-compiler:${Versions.hilt_compiler}"

    /*implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0'
    implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-building-v9:0.7.0'
    implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-localization-v9:0.12.0'
    implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-locationlayer:0.11.0'
    implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-markerview-v9:0.4.0'
    implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-offline-v9:0.7.0'
    implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-places-v9:0.12.0'
    implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-scalebar-v9:0.5.0'
    implementation 'com.mapbox.mapboxsdk:mapbox-android-plugin-traffic-v9:0.10.0'
*/
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val room_runtime = "androidx.room:room-runtime:${Versions.room}"
    const val room_compiler = "androidx.room:room-compiler:${Versions.room}"

    // optional - Kotlin Extensions and Coroutines support for Room
    const val room_ktx = "androidx.room:room-ktx:${Versions.room}"


    //LifeCycle
    const val lifecycle_common = "androidx.lifecycle:lifecycle-common:${Versions.lifecycle}"
    const val lifecycle_runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
    const val lifecycle_extensions = "android.arch.lifecycle:extensions:${Versions.lifecycle}"
    const val livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"

    // Coroutine Lifecycle Scopes
    const val lifecyle_runtime_ktx =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"

    //Retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.converter_gson}"
    const val loggin_interceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.logging_interceptor}"

    //Coroutines
    const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    // optional - Test helpers
    const val room_testing = "androidx.room:room-testing:${Versions.room}"
    const val core_ktx = "androidx.core:core-ktx:${Versions.core}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintlayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    const val navigation_fragment = "androidx.navigation:navigation-fragment:${Versions.navigation}"
    const val navigation_ui = "androidx.navigation:navigation-ui:${Versions.navigation}"

    const val navigation_fragment_ktx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigation_ui_ktx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val junit = "junit:junit:${Versions.junit}"
    const val test_ext = "androidx.test.ext:junit:${Versions.test_ext}"
    const val espreso_core = "androidx.test.espresso:espresso-core:${Versions.espreso_core}"
    const val easypermission = "pub.devrel:easypermissions:${Versions.easypermission}"
}