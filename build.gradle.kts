import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.micronaut.application") version "4.3.2"
    id("io.micronaut.aot") version "4.3.2"
    kotlin("jvm") version "1.9.22"
    kotlin("kapt") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

version = "1.0.0"
group = "demo"

repositories {
    mavenCentral()
}

dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.serde:micronaut-serde-processor")

    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.slf4j:slf4j-api")
    implementation("ch.qos.logback:logback-classic")

    runtimeOnly("org.yaml:snakeyaml")

    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("demo.DemoApplication")
}

kotlin {
    jvmToolchain(21)
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental.set(true)
        annotations.set(setOf("demo.*"))
    }
    aot {
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
    }
}
