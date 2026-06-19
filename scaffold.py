import os
import re

modules = {
    "erp-central-service": 8083,
    "mes-service": 8084,
    "wms-service": 8085,
    "tms-service": 8086,
    "crm-service": 8087,
    "dealer-portal-service": 8088,
    "ecommerce-service": 8089,
    "hrms-service": 8090,
    "payroll-service": 8091,
    "accounting-service": 8092,
    "qms-service": 8093,
    "bi-service": 8094,
}

pom_template = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>furniture-erp</artifactId>
        <groupId>com.furniture.erp</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>{module_name}</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>common-domain</artifactId>
            <version>${{project.version}}</version>
        </dependency>
        <dependency>
            <groupId>com.furniture.erp</groupId>
            <artifactId>common-messaging</artifactId>
            <version>${{project.version}}</version>
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

app_template = """package com.furniture.erp.{pkg_name};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class {class_name} {{

    public static void main(String[] args) {{
        SpringApplication.run({class_name}.class, args);
    }}
}}
"""

yml_template = """server:
  port: {port}

spring:
  application:
    name: {module_name}
  datasource:
    url: jdbc:postgresql://localhost:5432/erp_db
    username: erp_user
    password: erp_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
  kafka:
    bootstrap-servers: localhost:9092
"""

for module_name, port in modules.items():
    # Package name: e.g. "erp-central-service" -> "erpcentral"
    pkg_name = module_name.replace("-service", "").replace("-", "")
    # Class name: e.g. "erp-central-service" -> "ErpCentralServiceApplication"
    parts = module_name.split("-")
    class_name = "".join([p.capitalize() for p in parts]) + "Application"
    
    # Create directories
    java_dir = f"{module_name}/src/main/java/com/furniture/erp/{pkg_name}"
    res_dir = f"{module_name}/src/main/resources"
    os.makedirs(java_dir, exist_ok=True)
    os.makedirs(res_dir, exist_ok=True)
    
    # Write pom.xml
    with open(f"{module_name}/pom.xml", "w") as f:
        f.write(pom_template.format(module_name=module_name))
        
    # Write Application.java
    with open(f"{java_dir}/{class_name}.java", "w") as f:
        f.write(app_template.format(pkg_name=pkg_name, class_name=class_name))
        
    # Write application.yml
    with open(f"{res_dir}/application.yml", "w") as f:
        f.write(yml_template.format(port=port, module_name=module_name))

print("Created directories and files for 12 modules.")

# Update root pom.xml
root_pom_path = "pom.xml"
with open(root_pom_path, "r") as f:
    pom_content = f.read()

# Generate module tags
module_tags = ""
for module_name in modules.keys():
    module_tags += f"        <module>{module_name}</module>\n"

# Regex to inject modules before </modules>
if "<module>procurement-service</module>" in pom_content:
    pom_content = pom_content.replace(
        "        <module>procurement-service</module>\n    </modules>",
        f"        <module>procurement-service</module>\n{module_tags}    </modules>"
    )
    with open(root_pom_path, "w") as f:
        f.write(pom_content)
    print("Updated root pom.xml")
else:
    print("Could not update root pom.xml automatically.")
