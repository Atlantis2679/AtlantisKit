plugins {
    `java-library`
}

group = "team2679.atlantiskit"
version = "4.1.1"

val wpilibVersion = "2027.0.0-alpha-7"
val advantageKitVersion = "27.0.0-alpha-6"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
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
    implementation("org.wpilib.wpiutil:wpiutil-java:$wpilibVersion")
    implementation("org.wpilib.wpilibj:wpilibj-java:$wpilibVersion")
    implementation("org.wpilib.wpiunits:wpiunits-java:$wpilibVersion")
    implementation("org.wpilib.ntcore:ntcore-java:$wpilibVersion")
    implementation("org.wpilib.wpimath:wpimath-java:$wpilibVersion")

    implementation("org.littletonrobotics.akit:akit-java:$advantageKitVersion")
    implementation("us.hebi.quickbuf:quickbuf-runtime:1.3.3")
}
