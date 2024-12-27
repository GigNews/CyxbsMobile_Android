plugins {
  id("manager.library")
}

useARouter(false) // lib_base 模块不包含实现类，不需要处理注解
useDataBinding(false) // lib_base 模块只依赖 DataBinding 但不开启 DataBinding

dependencies {
  implementation(projects.cyxbsComponents.init)
  implementation(projects.libUtils)
  implementation(projects.cyxbsComponents.config)
  implementation(projects.cyxbsComponents.account.api)
  implementation(projects.cyxbsPages.login.api)

  implementation(libs.bundles.projectBase)
  implementation(libs.bundles.views)
  implementation(libs.bundles.network)
  implementation(libs.photoView)
  implementation(libs.slideShow)
  implementation(libs.glide)
}