@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
    kotlin("multiplatform") version "2.2.21"
    id("org.jetbrains.dokka") version "2.1.0"

    `maven-publish`
    signing
}

val v = "1.0.0"
group = "xyz.calcugames"
version = "${if (project.hasProperty("snapshot")) "$v-SNAPSHOT" else v}${project.findProperty("suffix")?.toString()?.run { "-${this}" } ?: ""}"
description = "raylib Kotlin Multiplatform Bindings"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    configureSourceSets()
    applyDefaultHierarchyTemplate()
    withSourcesJar()

	val hostOs = System.getProperty("os.name").lowercase()
	val hostArch = System.getProperty("os.arch").lowercase()
	val isMac = hostOs.contains("mac") || hostOs.contains("darwin")
	val isArm64 = hostArch.contains("aarch64") || hostArch.contains("arm64")
	val isX64 = hostArch.contains("amd64") || hostArch.contains("x86_64")

	when {
		hostOs.contains("linux") && isX64 -> linuxX64()
		hostOs.contains("windows") && isX64 -> mingwX64()
		isMac && isX64 -> macosX64()
		isMac && isArm64 -> macosArm64()
		else -> throw GradleException("Host OS '$hostOs' with architecture '$hostArch' is not supported for native compilation.")
	}

    targets.filterIsInstance<KotlinNativeTarget>().forEach { target ->
		target.binaries {
			findByName("debugTest")?.apply {
				if (target.konanTarget.family.isAppleFamily) {
					val frameworks = listOf(
						"CoreFoundation",
						"CoreGraphics",
						"AppKit",
						"IOKit",
						"Metal",
						"QuartzCore",
						"Cocoa"
					)

					linkerOpts(frameworks.flatMap { listOf("-framework", it) })
				}

				if (target.konanTarget.family == Family.MINGW) {
					linkerOpts("-Llib/mingw", "-lraylib", "-Wl,--exclude-libs,user32")
				}
			}
		}

        target.compilations.all {
            cinterops {
                val raylib by creating {
                    defFile(project.file("lib/raylib.def"))

                    includeDirs {
                        allHeaders(project.file("lib/raylib/src"))
                    }
                }
            }
        }
    }

	sourceSets {
		commonMain.dependencies {
			implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
		}

		commonTest.dependencies {
			implementation(kotlin("test"))
		}
	}
}

fun KotlinMultiplatformExtension.configureSourceSets() {
    sourceSets
        .matching { it.name !in listOf("main", "test") }
        .all {
            val srcDir = if ("Test" in name) "test" else "main"
            val resourcesPrefix = if (name.endsWith("Test")) "test-" else ""
            val platform = when {
                (name.endsWith("Main") || name.endsWith("Test")) && "android" !in name -> name.dropLast(4)
                else -> name.substringBefore(name.first { it.isUpperCase() })
            }

            kotlin.srcDir("src/$platform/$srcDir")
            resources.srcDir("src/$platform/${resourcesPrefix}resources")

            languageSettings.apply {
                progressiveMode = true
            }
        }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project

    if (signingKey != null && signingPassword != null)
        useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications)
}

publishing {
    publications {
        filterIsInstance<MavenPublication>().forEach {
            it.apply {
                pom {
                    name = "raylibkt"

                    licenses {
                        license {
                            name = "MIT License"
                            url = "https://opensource.org/licenses/MIT"
                        }
                    }

                    developers {
                        developer {
                            id = "gmitch215"
                            name = "Gregory Mitchell"
                            email = "me@gmitch215.xyz"
                        }
                    }

                    scm {
                        connection = "scm:git:git://github.com/CalculusGames/raylibkt.git"
                        developerConnection = "scm:git:ssh://github.com/CalculusGames/raylibkt.git"
                        url = "https://github.com/CalculusGames/raylibkt"
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "CalculusGames"
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }

            val releases = "https://repo.calcugames.xyz/repository/maven-releases/"
            val snapshots = "https://repo.calcugames.xyz/repository/maven-snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshots else releases)
        }

        if (!version.toString().endsWith("SNAPSHOT")) {
            maven {
                name = "GithubPackages"
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }

                url = uri("https://maven.pkg.github.com/CalculusGames/raylibkt")
            }
        }
    }
}
