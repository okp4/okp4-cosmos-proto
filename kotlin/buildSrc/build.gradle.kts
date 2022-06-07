plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
}

repositories {
    mavenCentral()
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    val classgraphVersion = "4.8.146"
    api("io.github.classgraph:classgraph:$classgraphVersion")

    val protobufVersion = "3.19.4"
    api("com.google.protobuf:protobuf-java-util:$protobufVersion")

    val gsonVersion = "2.9.0"
    api("com.google.code.gson:gson:$gsonVersion")
    api(gradleApi())
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
