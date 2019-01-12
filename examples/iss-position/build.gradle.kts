group = "com.github.aesteve"
version = "3.6.2"

val mainVerticle = "com.github.aesteve.vertx.sse.examples.iss.ISSVerticle"

plugins {
    java
    application
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compile("com.github.aesteve:vertx-sse:${project.version}")
}

application {
    mainClassName = "io.vertx.core.Launcher"
}

tasks.withType<JavaExec> {
    args = listOf("run", mainVerticle)
}

tasks.wrapper {
    gradleVersion = "5.1.1"
}


