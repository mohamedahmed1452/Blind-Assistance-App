plugins {
    id("com.android.application")
    id("com.chaquo.python")
}

android {
    namespace = "com.example.blind_assistance_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.blind_assistance_project"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

    }

    chaquopy {
        defaultConfig {
            buildPython("C:\\Users\\NanoChip\\AppData\\Local\\Programs\\Python\\Python38\\python.exe")
        }
        defaultConfig {
            version = "3.8"
        }
        sourceSets {
            getByName("main") {
                srcDir("src/main/python")
            }
        }
        defaultConfig {
            pip {

//                  install("torch")
//                  install("torchvision")
//                  install("pandas")
//                  install("pathlib")
//                  install("nltk")


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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        mlModelBinding = true
    }

}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.loopj.android:android-async-http:1.4.10")
    implementation ("com.google.android.gms:play-services-tasks:17.2.0")
    implementation ("org.apache.commons:commons-lang3:3.12.0")



    //rounded image view
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.28")

}