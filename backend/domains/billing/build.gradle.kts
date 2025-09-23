dependencies {
    implementation(project(":backend:shared"))
    implementation(project(":backend:domains:contract"))
    implementation(project(":backend:domains:attendance"))
    implementation(libs.spring.autoconfigure)
    implementation("com.fasterxml.uuid:java-uuid-generator:4.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.assertj:assertj-core:3.21.0")
}

tasks.test {
    useJUnitPlatform()
}
