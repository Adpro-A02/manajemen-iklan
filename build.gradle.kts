plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
    id("org.sonarqube") version "5.1.0.4882"
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.ehcache:ehcache:3.10.8")
	runtimeOnly("com.h2database:h2")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.mockito:mockito-core")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

}

// SonarQube configuration
sonar {
    properties {
        property("sonar.projectKey", "manajemen-iklan")
        property("sonar.projectName", "Manajemen Iklan")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.token", "your-sonar-token-here")  // Will be overridden by environment
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.exclusions", "**/ManajemenIklanApplication.java,**/config/**,**/dto/**,**/enums/**")
        property("sonar.cpd.exclusions", "**/dto/**,**/model/**")
    }
}
tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    
    // Set test profile explicitly
    systemProperty("spring.profiles.active", "test")
    
    // JVM arguments for tests
    jvmArgs = listOf(
        "-XX:+EnableDynamicAgentLoading",
        "-Dspring.profiles.active=test"
    )
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
    
    // Exclude certain classes from coverage if needed
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/ManajemenIklanApplication.class")
                exclude("**/config/**")
                exclude("**/dto/**")
                exclude("**/enums/**")
            }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal() // 80% coverage requirement
            }
        }
        rule {
            enabled = false
            element = "CLASS"
            includes = listOf("id.ac.ui.cs.advprog.manajemen_iklan.*")
            
            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
            }
        }
    }
}

// Make check task depend on coverage verification
tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

// SonarQube task dependencies
tasks.named("sonar") {
    dependsOn(tasks.test, tasks.jacocoTestReport)
}