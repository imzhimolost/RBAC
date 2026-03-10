plugins {
    java
    application
}

group = "ru.university"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))

    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.mockito:mockito-core:5.22.0")

    testImplementation("org.mockito:mockito-junit-jupiter:5.22.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("ru.university.rbac.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    systemProperty("file.encoding", "utf-8")
}

tasks.test {
    useJUnitPlatform()
}