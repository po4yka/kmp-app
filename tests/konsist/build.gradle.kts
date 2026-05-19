// JVM-only architecture test module. Konsist analyzes source files of every
// other module via the root project; it does not contribute production code.
// Excluded from the KMP module-boundary rules in AGENTS.md (test-only fixture).
plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
