import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("io.realm.kotlin")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if(localPropertiesFile.exists()){
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.ladysparks.ttaenggrang"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ladysparks.ttaenggrang"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Server URL 추가
        buildConfigField("String", "SERVER_URL", "\"${localProperties.getProperty("SERVER_URL", "")}\"")

    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit + okhttp3
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0") // 최신 버전 사용 가능


    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutin 을 사용하기 위한 lib
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    // 뷰모델
    implementation ("androidx.fragment:fragment-ktx:1.5.7")

    // Realm Database
    implementation("io.realm:realm-gradle-plugin:10.15.1")

    // Realm (Kotlin SDK)
    implementation("io.realm.kotlin:library-base:1.11.0")

}