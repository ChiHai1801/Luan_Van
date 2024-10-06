plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.luanvan2324"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.luanvan2324"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        viewBinding = true
        mlModelBinding = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.3.0")
    implementation ("androidx.recyclerview:recyclerview:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.cardview:cardview:1.0.0")


    implementation ("com.google.android.material:material:1.4.0")


    // TFLite GPU delegate 2.3.0 or above is required.
    implementation ("org.tensorflow:tensorflow-lite-gpu:2.3.0")

    implementation ("org.tensorflow:tensorflow-lite-metadata:0.1.0")

    // Import tflite dependencies
//    implementation ("org.tensorflow:tensorflow-lite:0.0.0-nightly-SNAPSHOT")
//    // The GPU delegate library is optional. Depend on it as needed.
//    implementation ("org.tensorflow:tensorflow-lite-gpu:0.0.0-nightly-SNAPSHOT")
//    implementation ("org.tensorflow:tensorflow-lite-support:0.0.0-nightly-SNAPSHOT")
}