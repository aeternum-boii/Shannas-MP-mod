plugins {
    id("java")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Source: https://mvnrepository.com/artifact/org.jspecify/jspecify
    //implementation("org.jspecify:jspecify:1.0.0")
    //implementation("org.jetbrains:annotations:26.1.0")

    // Source: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    //implementation("com.fasterxml.jackson.core:jackson-annotations:2.22")
    // Source: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    //implementation("com.fasterxml.jackson.core:jackson-core:2.22.3")
    // Source: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    //implementation("com.fasterxml.jackson.core:jackson-databind:2.22.3")
    implementation(files("libs/ironj-semantics-1.1.0.jar")) // contains all the other dependencies already
}

group = "net.aeternum"

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(27)
    }
}