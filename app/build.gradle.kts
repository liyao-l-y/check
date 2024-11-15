plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.app1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app1"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ndkVersion = "28.0.12433566"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }


}

task("testClasses")


dependencies {



    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("org.bouncycastle:bcprov-jdk18on:1.76")
    implementation (libs.guava)
    implementation (libs.cbor)


    implementation (libs.dev.core.ktx)
    //noinspection GradleDependency
    //implementation ("dev.rikka.rikkax.material:material:1.6.6")
    implementation ("dev.rikka.rikkax.html:html-ktx:1.1.2")
    implementation ("dev.rikka.rikkax.recyclerview:recyclerview-adapter:1.3.0")
    implementation ("dev.rikka.rikkax.widget:borderview:1.1.0")

    implementation ("com.google.android.material:material:1.10.0")

    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")




}