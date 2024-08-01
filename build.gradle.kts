plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("plugin.jpa") version "1.9.24"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("kapt") version "1.9.24"
}

group = "com.beanspace"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val jjwtVersion = "0.12.6"
val queryDslVersion = "5.0.0"
val kotestVersion = "5.8.1"
val mockkVersion = "1.13.8"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.741")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.33.0")


    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    implementation("com.querydsl:querydsl-jpa:$queryDslVersion:jakarta")
    kapt("com.querydsl:querydsl-apt:$queryDslVersion:jakarta")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.1")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    testImplementation("com.appmattus.fixture:fixture-kotest:1.2.0")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
