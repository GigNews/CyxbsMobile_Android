plugins {
  id("manager.library")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.libBase)
      implementation(projects.cyxbsComponents.config)
      implementation(projects.libUtils)
      implementation(projects.cyxbsPages.course.api)
    }
    androidMain.dependencies {
      implementation(libs.bundles.projectBase)
      implementation(libs.bundles.views)
      implementation(libs.bundles.network)

      api(libs.netLayout)
    }
  }
}
