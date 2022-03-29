sourceSets {
    main {
        proto {
            srcDir("../../proto/okp4")
        }
    }
}

dependencies {
    implementation(project(":cosmos-sdk"))
}

publishing {
    publications {
        create<MavenPublication>("okp4") {
            groupId = rootProject.group as String?
            artifactId = "okp4"
            version = rootProject.version as String?

            artifact(tasks.jar)
        }
    }
}
