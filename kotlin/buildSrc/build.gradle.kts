plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
}

repositories {
    mavenCentral()
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    val grpcVersion = "1.45.0"
    api("io.grpc:grpc-stub:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")

    val protobufVersion = "3.19.4"
    api("com.google.protobuf:protobuf-java-util:$protobufVersion")
    api("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    val grpcKotlinVersion = "1.2.1"
    api("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

    api("com.google.code.gson:gson:2.9.0")
    api(gradleApi())
    api(kotlin("reflect"))
    api("org.reflections:reflections:0.10.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
