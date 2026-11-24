package dev.portfolio.catalog.config;

import dev.portfolio.catalog.product.Product;
import dev.portfolio.catalog.product.ProductRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDataConfig {
    @Bean
    CommandLineRunner sampleProducts(ProductRepository products) {
        return args -> {
            if (products.count() == 0) {
                products.save(new Product("KEY-JAVA-01", "Java Mechanical Keyboard",
                        new BigDecimal("1290000.00"), 20));
                products.save(new Product("BOOK-SPRING-01", "Spring Boot in Practice",
                        new BigDecimal("450000.00"), 35));
            }
        };
    }
}
