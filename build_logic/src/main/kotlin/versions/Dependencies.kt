@file:SuppressWarnings("all")

package versions

import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude


//ARouter
const val `arouter-api` = "com.alibaba:arouter-api:1.5.2"
const val `arouter-compiler` = "com.alibaba:arouter-compiler:1.5.2"

//AndroidX
const val appcompat = "1.4.0"
const val `androidx-appcompat` = "androidx.appcompat:appcompat:$appcompat"

//Test
const val `testImpl-junit` = "junit:junit:4.13.2"
const val `androidTestImpl-junit` = "androidx.test.ext:junit:1.1.3"
const val `androidTestImpl-espresso` = "androidx.test.espresso:espresso-core:3.4.0"

//Bugly
const val `bugly-native-crash-report` = "com.tencent.bugly:nativecrashreport:3.9.2"
//bugly-crash-report-upgrade内已经集成bugly-crash-report，所以如果需要集成bugly的crash-report和upgrade时，crash-report需要注释掉。
//具体参考自官方文档 https://bugly.qq.com/docs/user-guide/instruction-manual-android-upgrade/?v=1.0.0   接入sdk时间2021-3-31
const val `bugly-crash-report` = "com.tencent.bugly:crashreport:4.0.0"
const val `bugly-crash-report-upgrade` = "com.tencent.bugly:crashreport_upgrade:1.5.23"

//美团多渠道打包
const val `walle-library` = "com.meituan.android.walle:library:1.1.7"
//腾讯多渠道打包
const val `vasDolly-library` = "com.tencent.vasdolly:helper:3.0.4"
//umeng
const val `umeng-common` = "com.umeng.umsdk:common:9.4.4"
const val `umeng-asms` = "com.umeng.umsdk:asms:1.4.1"
const val `umeng-push` = "com.umeng.umsdk:push:6.5.1"
const val  `umemg-union` = "com.umeng.umsdk:union:1.3.0"

//Sophics热修复
const val `sophics-android-hotfix` = "com.aliyun.ams:alicloud-android-hotfix:3.3.5"

//Rxjava3
const val `rxjava3-common` = "io.reactivex.rxjava3:rxjava:3.1.3"
const val `rxjava3-android` = "io.reactivex.rxjava3:rxandroid:3.0.0"
const val `rxjava3-kotlin` = "io.reactivex.rxjava3:rxkotlin:3.0.1"

//LphotoPicker
const val lPhotoPicker = "com.github.limuyang2:LPhotoPicker:2.6"

//android官方基础库
const val lifecycle = "2.4.1"
val android = listOf(
    "androidx.constraintlayout:constraintlayout:2.1.2",

    "com.google.android.material:material:1.4.0",

    "androidx.appcompat:appcompat:$appcompat",

    "androidx.core:core-ktx:1.7.0",
    "androidx.activity:activity-ktx:1.4.0",
    "androidx.fragment:fragment-ktx:1.4.1",

    "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle",
    "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle",
    "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle",
    //解决PendingIntent对android12兼容问题
    "androidx.work:work-runtime-ktx:2.7.1",
)

//,

//    "androidx.navigation:navigation-runtime-ktx:2.4.1",
//    "androidx.navigation:navigation-fragment-ktx:2.4.1",
//    "androidx.navigation:navigation-ui-ktx:2.4.1",

//"androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle",
//"androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle",

//    "androidx.room:room-ktx:2.4.2",




//eventbug事件总线
const val eventBus = "org.greenrobot:eventbus:3.3.1"

//glide
const val `glide-common` = "com.github.bumptech.glide:glide:4.13.0"
const val `glide-compiler` = "com.github.bumptech.glide:compiler:4.13.0"

//Retrogit
const val retrofit = "2.9.0"
const val `retrofit-common` = "com.squareup.retrofit2:retrofit:$retrofit"
const val `retrofit-converter` = "com.squareup.retrofit2:converter-gson:$retrofit"
const val `retrofit-rxjava3` = "com.squareup.retrofit2:adapter-rxjava3:$retrofit"

//okhttp
const val `okhttp-common` = "com.squareup.okhttp3:okhttp:4.9.3"
const val `okhttp-intercepter` = "com.squareup.okhttp3:logging-interceptor:4.9.3"

//gson
const val gson = "com.google.code.gson:gson:2.9.0"

//room
const val room = "2.4.1"
const val `room-runtime` = "androidx.room:room-runtime:$room"
const val `room-compiler` = "androidx.room:room-compiler:$room"
const val `room-rxjava3` = "androidx.room:room-rxjava3:$room"
const val `room-paging` = "androidx.room:room-paging:$room"

//paging
const val `paging-runtime` = "androidx.paging:paging-runtime:3.1.0"
const val `paging-rxjava3` = "androidx.paging:paging-rxjava3:3.1.0"

//rxPermission
const val rxPermission = "com.github.tbruyelle:rxpermissions:0.12"

//lottie
const val lottie = "com.airbnb.android:lottie:4.1.0"

//photoview
const val photoView = "com.github.chrisbanes:PhotoView:2.3.0"

//dialog
const val dialog = "com.afollestad.material-dialogs:core:3.3.0"

//coroutines
const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0-native-mt"

//workManger
const val workManager = "androidx.work:work-runtime-ktx:2.7.1"


fun DependencyHandlerScope.android() {
    android.forEach {
        "implementation"(it)
    }
}

fun DependencyHandlerScope.workManager(){
    "implementation"(workManager)
}


fun DependencyHandlerScope.threeParty() {
    defaultNet()
    glide()
}

fun DependencyHandlerScope.glide() {
    "implementation"(`glide-common`)
    "kapt"(`glide-compiler`)
}

fun DependencyHandlerScope.retrofit() {
    "implementation"(`retrofit-common`)
}

fun DependencyHandlerScope.okhttp() {
    "implementation"(`okhttp-common`)
    "implementation"(`okhttp-intercepter`)
}

fun DependencyHandlerScope.gson() {
    "implementation"(gson)
}

//Retrofit + Okhttp
fun DependencyHandlerScope.netBase() {
    retrofit()
    okhttp()
}

//Retrofit + Okhttp + Gson + Rxjava3
fun DependencyHandlerScope.defaultNet() {
    netBase()
    gson()
    rxjava3()
    "implementation"(`retrofit-rxjava3`)
    "implementation"(`retrofit-converter`)
}

//room基本依赖
fun DependencyHandlerScope.roomBase() {
    "implementation"(`room-runtime`)
    "kapt"(`room-compiler`)
}

//room + rxjava3 + paging
fun DependencyHandlerScope.defaultRoom() {
    roomBase()
    paging()
    rxjava3()
    "implementation"(`room-rxjava3`)
    "implementation"(`room-paging`)
}

//paging
fun DependencyHandlerScope.paging() {
    "implementation"(`paging-runtime`)
}

fun DependencyHandlerScope.rxPermission() {
    rxjava3()
    "implementation"(rxPermission)
}

//paging +  rxjava3
fun DependencyHandlerScope.`paging-rxjava3`() {
    paging()
    rxjava3()
    "implementation"(`paging-rxjava3`)
}

fun DependencyHandlerScope.eventBus() {
    "implementation"(eventBus)
}

fun DependencyHandlerScope.coroutines() {
    "implementation"(coroutines)
}

fun DependencyHandlerScope.lottie() {
    "implementation"(lottie)
}

fun DependencyHandlerScope.dialog() {
    "implementation"(dialog)
}

fun DependencyHandlerScope.photoView() {
    "implementation"(photoView)
}

// Bugly https://bugly.qq.com/docs/
// 其中 latest.release 指代最新 Bugly SDK 版本号
// Bugly 有如下功能：1、检测 bug；2、弹 dialog 强制用户升级
fun DependencyHandlerScope.bugly() {
    "implementation"(`bugly-crash-report-upgrade`)
    "implementation"(`bugly-native-crash-report`)
}

// 友盟 https://developer.umeng.com/docs/67966/detail/206987
// 注意：请不要随意升级，请查看官方文档后进行升级（官方文档有旧文档可以进行比较），因为很可能改一些东西
// 还有，目前掌邮只使用了友盟的推送服务，按照目前（22年）的官方命名叫 U-Push
fun DependencyHandlerScope.umeng() {
    common()
    push()

}

fun DependencyHandlerScope.common() {
    "implementation"(`umeng-asms`)// (必选)
    "implementation"(`umeng-common`)// asms包依赖必选
}

fun DependencyHandlerScope.push() {
    "implementation"(`umeng-push`)
    "implementation"(`umemg-union`)
}

// walle https://github.com/Meituan-Dianping/walle
fun DependencyHandlerScope.walle() {
    "implementation"(`walle-library`)
}
fun DependencyHandlerScope.vasDolly(){
    "implementation"(`vasDolly-library`)
}

// Sophix https://help.aliyun.com/document_detail/61082.html
// 注意：请不要随意升级，请查看官方文档后进行升级，因为很可能改一些东西
fun DependencyHandlerScope.hotFix() {
    "implementation"(`sophics-android-hotfix`){
        //与友盟push的utdid冲突，解决方案来源于官网
        //https://help.aliyun.com/knowledge_detail/59152.html?spm=a2c4g.11186623.0.0.3afd33beEHM2GM
        exclude(module="alicloud-android-utdid")
    }
}

fun DependencyHandlerScope.aRouter() {
    "implementation"(`arouter-api`)
    "kapt"(`arouter-compiler`)
}

fun DependencyHandlerScope.rxjava3() {
    "implementation"(`rxjava3-kotlin`)
    "implementation"(`rxjava3-common`)
    "implementation"(`rxjava3-android`)
}

fun DependencyHandlerScope.lPhotoPicker() {
    "implementation"(lPhotoPicker)
}

fun DependencyHandlerScope.test() {
    "testImplementation"(`testImpl-junit`)
    "androidTestImplementation"(`androidTestImpl-junit`)
    "androidTestImplementation"(`androidTestImpl-espresso`)
}

//google自动注册
fun DependencyHandlerScope.autoService() {
    "kapt"("com.google.auto.service:auto-service:1.0.1")
    "compileOnly"("com.google.auto.service:auto-service:1.0.1")
}