plugins {
    `java-library`
    `maven-publish`
}

group = "atlantis2679.lib"
version = "4.0.0"

val wpilibVersion = "2025.3.2"
val advantageKitVersion = "4.1.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://frcmaven.wpi.edu/artifactory/release")
    }
    maven {
        url = uri("https://frcmaven.wpi.edu/artifactory/littletonrobotics-mvn-release")
    }
}


dependencies {
    implementation("edu.wpi.first.wpiutil:wpiutil-java:$wpilibVersion")
    implementation("edu.wpi.first.wpilibj:wpilibj-java:$wpilibVersion")
    implementation("edu.wpi.first.wpiunits:wpiunits-java:$wpilibVersion")
    implementation("edu.wpi.first.wpilibNewCommands:wpilibNewCommands-java:$wpilibVersion")
    implementation("edu.wpi.first.ntcore:ntcore-java:$wpilibVersion")
    implementation("edu.wpi.first.wpimath:wpimath-java:$wpilibVersion")

    implementation("org.littletonrobotics.akit:akit-java:$advantageKitVersion")
    implementation("us.hebi.quickbuf:quickbuf-runtime:1.3.3")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                name.set("AtlantisLib")
                description.set("Team 2679 Atlantis FRC library")
                url.set("https://github.com/Atlantis2679/AtlantisLib")
            }
        }
    }
    
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/atlantis2679/AtlantisLib")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}