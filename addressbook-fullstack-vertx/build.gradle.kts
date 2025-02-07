import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    val vertxPluginVersion: String by System.getProperties()
    id("io.vertx.vertx-plugin") version vertxPluginVersion
    val shadowVersion: String by System.getProperties()
    id("com.gradleup.shadow") version shadowVersion
    val kvisionVersion: String by System.getProperties()
    id("io.kvision") version kvisionVersion
}

version = "1.0.0-SNAPSHOT"
group = "com.example"

repositories {
    mavenCentral()
    mavenLocal()
}

// Versions
val kotlinVersion: String by System.getProperties()
val kvisionVersion: String by System.getProperties()
val logbackVersion: String by project
val vertxVersion: String by project
val exposedVersion: String by project
val hikariVersion: String by project
val h2Version: String by project
val pgsqlVersion: String by project
val kweryVersion: String by project
val commonsCodecVersion: String by project
val jdbcNamedParametersVersion: String by project


kotlin {
    jvmToolchain(21)
    jvm {
        withJava()
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "main.bundle.js"
                sourceMaps = false
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.kvision:kvision-server-vertx:$kvisionVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("io.vertx:vertx-auth-common:$vertxVersion")
                implementation("com.h2database:h2:$h2Version")
                implementation("org.jetbrains.exposed:exposed:$exposedVersion")
                implementation("org.postgresql:postgresql:$pgsqlVersion")
                implementation("com.zaxxer:HikariCP:$hikariVersion")
                implementation("commons-codec:commons-codec:$commonsCodecVersion")
                implementation("com.axiomalaska:jdbc-named-parameters:$jdbcNamedParametersVersion")
                implementation("com.github.andrewoma.kwery:core:$kweryVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("io.kvision:kvision:$kvisionVersion")
                implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
                implementation("io.kvision:kvision-state:$kvisionVersion")
                implementation("io.kvision:kvision-fontawesome:$kvisionVersion")
                implementation("io.kvision:kvision-i18n:$kvisionVersion")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("io.kvision:kvision-testutils:$kvisionVersion")
            }
        }
    }
}

vertx {
    mainVerticle = "com.example.MainVerticle"
    jvmArgs = listOf("-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory")
}
