import com.google.protobuf.gradle.*

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("gradle.plugin.com.google.protobuf:protobuf-gradle-plugin:0.8.18")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    }
}

group = "com.okp4.grpc"

fun prepareVersion(): String {
    val digits = (project.property("project.version") as String).split(".")
    if (digits.size != 3) {
        throw GradleException("Wrong 'project.version' specified in properties, expects format 'x.y.z'")
    }

    return digits.map { it.toInt() }
        .let {
            it.takeIf { it[2] == 0 }?.subList(0, 2) ?: it
        }.let {
            it.takeIf { !project.hasProperty("release") }?.mapIndexed { i, d ->
                if(i == 1) d + 1 else d
            } ?: it
        }.joinToString(".") + project.hasProperty("release").let { if (it) "" else "-SNAPSHOT" }
}

afterEvaluate {
    project.version = prepareVersion()
}

subprojects {
    apply {
        plugin("com.google.protobuf")
        plugin("org.jetbrains.kotlin.jvm")
    }

    repositories {
        mavenCentral()
    }

    val grpcVersion = "1.45.0"
    val grpcKotlinVersion = "1.2.1"
    val protobufVersion = "3.19.4"
    val coroutinesVersion = "1.6.0"

    val implementation by configurations
    val api by configurations
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        api("io.grpc:grpc-stub:$grpcVersion")
        api("io.grpc:grpc-protobuf:$grpcVersion")
        api("com.google.protobuf:protobuf-java-util:$protobufVersion")
        api("com.google.protobuf:protobuf-kotlin:$protobufVersion")
        api("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
        }
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:$protobufVersion"
        }
        plugins {
            id("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
            }
            id("grpckt") {
                artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
            }
        }
        generateProtoTasks {
            all().forEach {
                it.plugins {
                    id("grpc")
                    id("grpckt")
                }
                it.builtins {
                    id("kotlin")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            val repositoryUrl = project.findProperty("repositoryUrl")
            repositoryUrl?.let{
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri(repositoryUrl))
            }
        }
    }
}
