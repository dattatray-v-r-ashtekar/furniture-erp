import os

module_dir = "erp-monolith-runner"
os.makedirs(module_dir, exist_ok=True)
os.makedirs(f"{module_dir}/src/main/java/com/furniture/erp/monolith", exist_ok=True)
os.makedirs(f"{module_dir}/src/main/resources", exist_ok=True)

pom_xml = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.furniture.erp</groupId>
        <artifactId>furniture-erp</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>erp-monolith-runner</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Import all 14 Microservices -->
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>inventory-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>procurement-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>erp-central-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>mes-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>wms-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>tms-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>crm-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>dealer-portal-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>ecommerce-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>hrms-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>payroll-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>accounting-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>qms-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>bi-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
"""

app_java = """package com.furniture.erp.monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.furniture.erp"})
@EntityScan(basePackages = {"com.furniture.erp"})
@EnableJpaRepositories(basePackages = {"com.furniture.erp"})
public class MonolithApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonolithApplication.class, args);
    }
}
"""

application_yml = """server:
  port: 8080

spring:
  application:
    name: erp-monolith-runner
  datasource:
    url: jdbc:postgresql://localhost:5432/erp_db
    username: erp_user
    password: erp_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: erp-monolith-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
"""

with open(f"{module_dir}/pom.xml", "w") as f:
    f.write(pom_xml)

with open(f"{module_dir}/src/main/java/com/furniture/erp/monolith/MonolithApplication.java", "w") as f:
    f.write(app_java)

with open(f"{module_dir}/src/main/resources/application.yml", "w") as f:
    f.write(application_yml)

print("Generated erp-monolith-runner")
