import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    id("org.jetbrains.kotlin.plugin.spring") version libs.versions.kotlin
    id("nu.studer.jooq") version "9.0"
}

val jooqVersion = rootProject.dependencyManagement.importedProperties["jooq.version"]

dependencies {
    implementation(libs.spring.autoconfigure)
    implementation(libs.spring.starter.jooq)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(project(":backend:domains"))

    testImplementation(libs.spring.starter.test) {
        exclude("org.junit.vintage:junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation(kotlin("test"))

    runtimeOnly("org.postgresql:postgresql:42.7.3")
    jooqGenerator("org.postgresql:postgresql:42.7.3")
    jooqGenerator("org.jooq:jooq-meta-extensions:$jooqVersion")
}

jooq {
    version.set(jooqVersion)
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {

            jooqConfiguration.apply {
                logging = Logging.WARN
                generator.apply {
                    name = org.jooq.codegen.KotlinGenerator::class.java.canonicalName
                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties = listOf(
                            Property().apply {
                                // @see https://www.jooq.org/doc/latest/manual/code-generation/codegen-ddl/
                                key = "scripts"
                                value = "../../docker/sqldef/volume/schema.sql"
                            }
                        )
                        forcedTypes = listOf(
                            ForcedType().apply {
                                name
                                userType = "java.time.OffsetDateTime"
                                converter =
                                    "com.example.infrastructure.jooq.converters.TimestampToOffsetDateTimeConverter"
                                includeExpression = ".*_DATETIME"
                            }
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isPojosAsKotlinDataClasses = true
                        isKotlinNotNullRecordAttributes = true
                    }
                    target.apply {
                        packageName = "com.example.infrastructure.db"
                        directory = "$projectDir/build/jooq/main"
                    }
                    strategy.name = org.jooq.codegen.DefaultGeneratorStrategy::class.java.canonicalName
                }
            }
        }
    }
}

tasks.bootJar {
    enabled = false
}
