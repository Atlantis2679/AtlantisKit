plugins {
    `java-library`
}

group = "team2679.atlantiskit"
version = "4.1.0"

val wpilibVersion = "2026.1.1"
val advantageKitVersion = "26.0.0"

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
