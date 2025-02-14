plugins {
  id("manager.lib")
  id("kmp.compose")
}

useKtProvider(false) // utils 模块不包含实现类，不需要处理注解

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.cyxbsComponents.init)
      implementation(projects.cyxbsComponents.config)
      implementation(projects.cyxbsComponents.account.api)
      implementation(libs.kmp.ktProvider.manager) // utils 私有，其他模块通过 ::class.impl() 获取
    }
    androidMain.dependencies {
      implementation(libs.bundles.projectBase)
      implementation(libs.bundles.views)
      implementation(libs.bundles.network)
      implementation(libs.glide)
      implementation(libs.rxpermissions)
      implementation(libs.okhttp.logging.interceptor)
      implementation(libs.retrofit.converter.gson)
      implementation(libs.retrofit.converter.kotlinxSerialization)
      implementation(libs.retrofit.adapter.rxjava)

      // 阿里云 dns 解析工具
      // https://help.aliyun.com/document_detail/434554.html?spm=a2c4g.435252.0.0.1da95979yyEzm3
      implementation(libs.alicloud.httpdns)
    }
  }
}

android {
  buildFeatures {
    buildConfig = true
  }
  defaultConfig {
    // 写入版本信息到 BuildConfig，其他模块可以通过调用 getAppVersionCode() 和 getAppVersionName() 方法获得
    buildConfigField("long", "VERSION_CODE", Config.versionCode.toString())
    buildConfigField("String", "VERSION_NAME", "\"${Config.versionName}\"")
  }
}