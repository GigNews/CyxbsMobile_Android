plugins {
  id("manager.composeLib")
}

useARouter()

kotlin {
  sourceSets {
    commonMain.dependencies {
      subprojects.forEach { implementation(it) }
      implementation(projects.libBase)
      implementation(projects.libConfig)
      implementation(projects.libUtils)
      implementation(projects.cyxbsFunctions.update.api)
      implementation(projects.libAccount.apiAccount)
    }
    androidMain.dependencies {
      implementation(libs.bundles.projectBase)
      implementation(libs.bundles.views)
      implementation(libs.bundles.network)
      implementation(libs.lottie)
      implementation(libs.glide)
    }
  }
}