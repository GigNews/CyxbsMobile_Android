import versions.threeParty

plugins {
    id("com.redrock.cyxbs")
}

dependencies {
    implementation(project(":lib_account:api_account"))
    threeParty()
    implementation("de.hdodenhof:circleimageview:3.1.0")
}