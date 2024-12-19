plugins {
  id("manager.composeApp")
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      // home 模块去依赖了其他模块，所以这里只依赖 home
      implementation(projects.cyxbsPages.home)

      implementation(libs.bundles.projectBase)
    }
  }
}