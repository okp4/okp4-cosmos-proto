import org.slf4j.LoggerFactory

val slf4jLogger = LoggerFactory.getLogger("some-logger")

plugins {
    signing
    `maven-publish`
}

sourceSets {
    main {
        proto {
            srcDir("../../../proto/cosmos-sdk")
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
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
        create<MavenPublication>("mavenJavaCosmos") {
            from(components["java"])

            groupId = rootProject.group as String?
            artifactId = "cosmos-sdk"
            version = rootProject.version as String?

            pom {
                name.set("cosmos-sdk")
                description.set("Provides Kotlin gRPC clients for ØKP4 and by extension, CØSMOS based blockchains generated from their Protobuf definitions.")
                url.set("https://github.com/okp4/okp4-cosmos-proto")
                licenses {
                    license {
                        name.set("BSD 3-Clause License")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }
                developers {
                    developer {
                        id.set("bot-anik")
                        name.set("Bot Anik")
                        email.set("ops@okp4.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:okp4/okp4-cosmos-proto.git")
                    developerConnection.set("scm:git:ssh://github.com:okp4/okp4-cosmos-proto.git")
                    url.set("https://github.com/okp4/okp4-cosmos-proto.git")
                }
            }
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
        sign(publishing.publications["mavenJavaCosmos"])
    }
}
