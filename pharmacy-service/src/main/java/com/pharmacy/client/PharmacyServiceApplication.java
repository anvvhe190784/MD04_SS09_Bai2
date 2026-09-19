package com.pharmacy.client;

import com.pharmacy.client.entity.Medicine;
import com.pharmacy.client.repository.MedicineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PharmacyServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(PharmacyServiceApplication.class);

    @Value("${app.branch-name:Unknown Branch}")
    private String branchName;

    @Value("${app.hotline:Unknown Hotline}")
    private String hotline;

    @Value("${spring.datasource.url:Unknown DB}")
    private String datasourceUrl;

    public static void main(String[] args) {
        SpringApplication.run(PharmacyServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(MedicineRepository medicineRepository) {
        return args -> {
            log.info("==================================================================");
            log.info("       PHARMACY SERVICE - CENTRALIZED CONFIGURATION READY         ");
            log.info("==================================================================");
            log.info(" Branch Name     : {}", branchName);
            log.info(" Hotline         : {}", hotline);
            log.info(" Database URL    : {}", datasourceUrl);
            log.info("==================================================================");

            // Seed initial sample medicines to demonstrate active database connectivity
            medicineRepository.save(new Medicine("Paracetamol 500mg", 25000.0, 150));
            medicineRepository.save(new Medicine("Amoxicillin 500mg", 45000.0, 80));
            medicineRepository.save(new Medicine("Vitamin C 1000mg", 60000.0, 200));

            log.info("Database initialized successfully with {} sample medicines.", medicineRepository.count());
            log.info("==================================================================");
        };
    }
}

