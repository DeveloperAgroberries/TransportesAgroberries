plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.AgroberriesMX.transportesagroberries"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.AgroberriesMX.transportesagroberries.AB"
        minSdk = 21
        targetSdk = 35
        versionCode = 4 //Ultima vesion subida a produccion 1 29/10/2025
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release"){
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "AgroberriesMX","Transportes Agroberries")

            buildConfigField("String", "BASE_URL","\"http://54.165.41.23:5053/api/TransportApp/\"")
            //buildConfigField("String", "BASE_URL", "\"http://192.168.1.184:5011/api/TransportApp/\"")
        }

        getByName("debug"){
            isDebuggable = true
            resValue("string", "AgroberriesMX", "[DEBUG] Transportes Agroberries")
            buildConfigField("String", "BASE_URL", "\"http://54.165.41.23:5053/api/TransportApp/\"")
            //buildConfigField("String", "BASE_URL", "\"http://192.168.1.184:5011/api/TransportApp/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    val navVersion = "2.7.7"
    val daggerHiltVersion = "2.51.1"
    val retrofitVersion = "2.9.0"
    val okHttpVersion = "4.12.0"

    //NavComponent
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    //DaggerHilt
    implementation("com.google.dagger:hilt-android:$daggerHiltVersion")
    kapt("com.google.dagger:hilt-compiler:$daggerHiltVersion")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //OkHttp
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

    //SQLite
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Para el escaneo de QR (Zxing)
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0") // O la versión más reciente

    // Para habilitar las funciones de extensión de Fragmentos (como activityViewModels)
    implementation("androidx.fragment:fragment-ktx:1.8.1")

    // Para la integración de Hilt con ViewModels
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")

    // Para ViewModel y LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")

    //GPS
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // O la versión más reciente

    implementation("androidx.camera:camera-core:1.3.3")
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
}