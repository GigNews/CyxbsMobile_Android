import com.android.build.gradle.BaseExtension

plugins {
  id("kmp.base")
  id("org.jetbrains.kotlin.plugin.compose")
  id("org.jetbrains.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(compose.materialIconsExtended)
      implementation(libsEx.`compose-navigation`)
      implementation(libsEx.`compose-lifecycle`)
      implementation(libsEx.`compose-constraintLayout`)
    }

    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libsEx.`compose-activity`)
    }

    if (Multiplatform.enableDesktop(project)) {
      val desktopMain by getting
      desktopMain.dependencies {
        implementation(compose.desktop.currentOs)
      }
    }
  }
}

plugins.withId("com.android.base") {
  configure<BaseExtension> {
    buildFeatures.compose = true
  }
  dependencies {
    add("debugImplementation", compose.uiTooling)
  }
  configurations.getByName("androidMainImplementation") {
    // 目前第三方的 constraintlayout 在安卓上的实现与 constraintlayout-core 存在依赖冲突
    // 所以这里 exclude 掉对应的 -android 依赖，然后下面在安卓上单独依赖官方的 constraintlayout-compose
    exclude(group = "tech.annexflow.compose", module = "constraintlayout-compose-multiplatform-android")
  }
  kotlin {
    sourceSets {
      androidMain.dependencies {
        implementation(libsEx.`compose-constraintLayout-android`)
      }
    }
  }
}

composeCompiler {
  // https://developer.android.com/jetpack/compose/performance/stability/diagnose#compose-compiler
  reportsDestination.set(
    layout.buildDirectory.get().asFile.resolve("compose_compiler")
  )

  // 对 Compose 配置外部类的稳定性
  // 只允许配置已有第三方库里面的类，如果是自己的类请打上 @Stable 注解
  // 配置规则可以查看 https://android-review.googlesource.com/c/platform/frameworks/support/+/2668595
  // 开启强跳过模式后可以不再设置外部类稳定性
//  stabilityConfigurationFile.set(
//    rootDir.resolve("config").resolve("compose-stability-config.txt")
//  )

  featureFlags.set(
    listOf(
      // 强跳过模式
      // https://developer.android.com/develop/ui/compose/performance/stability/strongskipping?hl=zh-cn
//      org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag.OptimizeNonSkippingGroups,
    )
  )
}
