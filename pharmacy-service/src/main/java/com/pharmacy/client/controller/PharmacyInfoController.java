package com.pharmacy.client.controller;

import com.pharmacy.client.entity.Medicine;
import com.pharmacy.client.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyInfoController {

    private final MedicineRepository medicineRepository;

    @Value("${app.branch-name:Unknown Branch}")
    private String branchName;

    @Value("${app.hotline:Unknown Hotline}")
    private String hotline;

    @Value("${spring.datasource.url:Unknown DB}")
    private String datasourceUrl;

    public PharmacyInfoController(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public record BranchInfoResponse(
            String branchName,
            String hotline,
            String datasourceUrl,
            long totalMedicinesInStock
    ) {}

    @GetMapping("/info")
    public BranchInfoResponse getBranchInfo() {
        return new BranchInfoResponse(
                branchName,
                hotline,
                datasourceUrl,
                medicineRepository.count()
        );
    }

    @GetMapping("/medicines")
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }
}

