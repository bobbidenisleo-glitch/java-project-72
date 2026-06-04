plugins {
    id("java")
    id("jacoco")
    id("org.sonarqube") version "5.1.0.4882"
    id("application")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Javalin web framework
    implementation("io.javalin:javalin:6.1.3")
    
    // Logger
    implementation("org.slf4j:slf4j-simple:2.0.13")
    
    // Jackson for JSON handling (required by Javalin)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    
    // Testing
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

application {
    mainClass.set("hexlet.code.App")
}

sonar {
    properties {
        property("sonar.projectKey", "bobbidenisleo-glitch_java-project-72")
        property("sonar.organization", "bobbidenisleo-glitch")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

tasks.shadowJar {
    mergeServiceFiles()
    archiveFileName.set("app-all.jar")
    manifest {
        attributes["Main-Class"] = "hexlet.code.App"
    }
}
