plugins {
  id("manager.composeLib")
}

useARouter()
useDataBinding()

kotlin {
  sourceSets {
    commonMain.dependencies {
      subprojects.forEach { implementation(it) }
      implementation(projects.libBase)
      implementation(projects.cyxbsComponents.config)
      implementation(projects.cyxbsComponents.utils)
      implementation(projects.cyxbsComponents.account.api)
    }
    androidMain.dependencies {
      implementation(libs.bundles.projectBase)
      implementation(libs.bundles.views)
      implementation(libs.bundles.network)
      implementation(libs.glide)
      implementation(libs.photoView)
      implementation(libs.slideShow)
    }
  }
}

