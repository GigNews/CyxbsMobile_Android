plugins {
  id("manager.lib")
}

useARouter()

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.rxjava)
    }
  }
}
