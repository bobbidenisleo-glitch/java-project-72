plugins {
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

// --- Resource setup ---
sourceSets {
    main {
        resources {
            // JTE templates
            srcDir("src/main/resources/templates")
            // Gradle уже включает src/main/resources по умолчанию
        }
    }
}

// --- Handle duplicate resources ---
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation("io.javalin:javalin:5.6.3")
    implementation("io.javalin:javalin-rendering:5.6.3")
    implementation("gg.jte:jte:2.3.2")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.konghq:unirest-java:3.14.0")
    runtimeOnly("com.h2database:h2:2.2.224")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("io.javalin:javalin-testtools:5.6.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
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
