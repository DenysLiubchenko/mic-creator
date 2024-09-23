package org.example.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "org.example")
@EntityScan(basePackages = {"org.example.dao.entity", "org.example.producer.entity"})
@EnableJpaRepositories(basePackages = {"org.example.dao.adapter", "org.example.producer.adapter"})
public class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }

}
