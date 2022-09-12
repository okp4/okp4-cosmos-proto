import org.slf4j.LoggerFactory

val slf4jLogger = LoggerFactory.getLogger("some-logger")

plugins {
    signing
}

sourceSets {
    main {
        proto {
            srcDir("../../../proto/cosmos-sdk")
        }
    }
}

tasks {
    val reflectionConfiguration = register<GenerateReflectionConfig>("reflectionConfiguration") {
        dependsOn.addAll(listOf("compileJava"))

        output = "${sourceSets.main.get().output.resourcesDir!!.absolutePath}/reflection-config.json"
        sources = sourceSets.main.get().output.classesDirs.files.toList().map { it.path }
    }

    jar {
        dependsOn.add(reflectionConfiguration)
    }
}

publishing {
    publications {
        create<MavenPublication>("cosmos") {
            groupId = rootProject.group as String?
            artifactId = "cosmos-sdk"
            version = rootProject.version as String?

            artifact(tasks.jar)
        }
    }
}

signing {
    val keyId = project.property("signing.keyId")
    val password = project.property("signing.password")
    val secretKeyRingFile = project.property("signing.secretKeyRingFile")
    if ( keyId == "" || password == "" || secretKeyRingFile == "" ) {
        slf4jLogger.warn("Archives will not be signed. Reason is signing properties not set")
    } else {
        sign(publishing.publications)
    }
}
