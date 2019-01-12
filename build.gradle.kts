import org.gradle.api.tasks.compile.JavaCompile

val vertxVersion = "3.6.2"

group = "com.github.aesteve"
version = vertxVersion // align with Vert.x version

plugins {
    java
    jacoco
    maven
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

    testCompile("io.vertx:vertx-unit:$vertxVersion")
    testCompile("junit:junit:4.12")
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

