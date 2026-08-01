package com.furniture.erp.monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
@ComponentScan(basePackages = {"com.furniture.erp"})
@EntityScan(basePackages = {"com.furniture.erp"})
@EnableJpaRepositories(basePackages = {"com.furniture.erp"})
public class MonolithApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonolithApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner commandLineRunner(org.springframework.context.ApplicationContext ctx) {
        return args -> {
            System.out.println("===== START BEAN INSPECTION =====");
            String[] beanNames = ctx.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                if (beanName.toLowerCase().contains("ecommerce") || beanName.toLowerCase().contains("order") || beanName.toLowerCase().contains("controller")) {
                    System.out.println("FOUND BEAN: " + beanName);
                }
            }
            try {
                Class<?> clazz = Class.forName("com.furniture.erp.ecommerce.infrastructure.rest.OnlineOrderController");
                System.out.println("CLASS LOADED SUCCESSFULLY: " + clazz.getName());
                System.out.println("Annotations: " + java.util.Arrays.toString(clazz.getAnnotations()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("===== END BEAN INSPECTION =====");
        };
    }

    @Bean
    @org.springframework.context.annotation.Primary
    public com.furniture.erp.domain.event.publisher.DomainEventPublisher<com.furniture.erp.domain.event.DomainEvent<?>> primaryDomainEventPublisher(org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate) {
        return new com.furniture.erp.domain.event.publisher.DomainEventPublisher<com.furniture.erp.domain.event.DomainEvent<?>>() {
            private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger("PrimaryDomainEventPublisher");
            @Override
            public void publish(com.furniture.erp.domain.event.DomainEvent<?> domainEvent) {
                String topic = domainEvent.getClass().getSimpleName();
                log.info("Publishing domain event: {} to topic: {}", domainEvent, topic);
                kafkaTemplate.send(topic, domainEvent);
            }
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
