import org.gradle.api.tasks.compile.JavaCompile

val vertxVersion = "3.6.2"
val junit5Version = "5.3.1"

group = "com.github.aesteve"
version = vertxVersion // align with Vert.x version

plugins {
    java
    jacoco
    maven
    id("org.sonarqube") version("2.6")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compile("io.vertx:vertx-core:$vertxVersion")
    compile("io.vertx:vertx-codegen:$vertxVersion")
    compile("io.vertx:vertx-web:$vertxVersion")

    testCompile("io.vertx:vertx-junit5:$vertxVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.2"
}

tasks.jacocoTestReport {
    dependsOn(":test")
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.destination = file("$buildDir/jacocoHtml")
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "5.1.1"
}

