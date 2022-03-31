sourceSets {
    main {
        proto {
            srcDir("../../../proto/cosmos-sdk")
        }
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
