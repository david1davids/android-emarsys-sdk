object Versions {
    const val kotlin_version = "1.3.41"
    const val kotlin_test_version = "3.1.11"
    const val mockito_version = "2.25.0"
    const val espresso_version = "3.1.1"
    const val firebase_core_version = "16.0.9"
    const val firebase_messaging_version = "18.0.0"
    const val hamcrest_version = "2.1"
    const val support_test_version = "1.1.1"
    const val support_library = "1.0.2"
    const val support_annotations = "1.0.2"
    const val buildToolsVersion = "28.0.3"
    const val multiDexVersion = "2.0.0"
    const val androidJunit4 = "1.1.0"
}

object Libs {
    val support_annotations = "androidx.annotation:annotation:${Versions.support_annotations}"
    val support_appcompat = "androidx.appcompat:appcompat:${Versions.support_library}"
    val firebase_core = "com.google.firebase:firebase-core:${Versions.firebase_core_version}"
    val firebase_messaging = "com.google.firebase:firebase-messaging:${Versions.firebase_messaging_version}"
    val espresso_idling_resources = "androidx.test.espresso:espresso-idling-resource:${Versions.espresso_version}"
}

object TestLibs {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin_version}"
    val kotlin_test = "io.kotlintest:kotlintest-runner-junit4:${Versions.kotlin_test_version}"
    val mockito_android = "org.mockito:mockito-android:${Versions.mockito_version}"
    val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso_version}"
    val support_test_runner = "androidx.test:runner:${Versions.support_test_version}"
    val support_test_rules = "androidx.test:rules:${Versions.support_test_version}"
    val hamcrest_core = "org.hamcrest:hamcrest-core:${Versions.hamcrest_version}"
    val hamcrest_library = "org.hamcrest:hamcrest-library:${Versions.hamcrest_version}"
    val multiDex = "androidx.multidex:multidex-instrumentation:${Versions.multiDexVersion}"
    val androidJunit4 = "androidx.test.ext:junit:${Versions.androidJunit4}"
}