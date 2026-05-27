plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.sonarqube)
    java
    jacoco
}

group = "com.traceability"
version = "0.1.0-SNAPSHOT"
description = "Microservicio de Solicitudes — TraceAbility"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencyManagement {
    imports {
        mavenBom("software.amazon.awssdk:bom:${libs.versions.aws.sdk.get()}")
    }
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.integration)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.integration.mail)
    implementation(libs.aws.s3)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.caffeine)
    implementation(libs.springdoc.openapi)
    implementation(libs.resilience4j)
    implementation(libs.bundles.flyway)
    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            element = "PACKAGE"
            includes = listOf(
                "com.traceability.solicitudes.domain*",
                "com.traceability.solicitudes.application*",
            )
            limit {
                counter = "LINE"
                minimum = "1.00".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

sonar {
    properties {
        property("sonar.projectKey", "ServiPlus-S-A_api-solicitudes")
        property("sonar.organization", "serviplus-s-a")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.scanner.skipJreProvisioning", "true")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.qualitygate.wait", "true")
        property(
            "sonar.exclusions",
            "**/build/**,**/docker/**,**/scripts/**,**/target/**",
        )
    }
}

tasks.named("sonar") {
    dependsOn(tasks.check)
}
