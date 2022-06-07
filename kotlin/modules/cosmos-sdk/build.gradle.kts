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
