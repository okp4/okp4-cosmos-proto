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
