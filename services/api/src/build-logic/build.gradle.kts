plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    // libsが使えるようになり、ハードコードが不要になる
    // libsを使わず、バージョンを直接指定する
    val kotlinVersion = "2.1.20"
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}