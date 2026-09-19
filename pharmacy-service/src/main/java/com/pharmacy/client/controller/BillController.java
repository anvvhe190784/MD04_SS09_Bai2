package com.pharmacy.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/api/v1/bill")
public class BillController {

    private static final Logger log = LoggerFactory.getLogger(BillController.class);

    @Value("${pharmacy.vat-rate:8.0}")
    private double vatRate;

    public record BillItem(String medicineName, Double price, Integer quantity) {}

    public record BillRequest(
            Double subtotal,
            List<BillItem> items
    ) {}

    public record BillResponse(
            double subtotal,
            double vatRate,
            double vatAmount,
            double finalTotal,
            String calculationDetails
    ) {}

    @GetMapping("/vat-rate")
    public double getCurrentVatRate() {
        return vatRate;
    }

    @PostMapping
    public BillResponse calculateBill(@RequestBody(required = false) BillRequest request) {
        double subtotal = 0.0;

        if (request != null) {
            if (request.subtotal() != null && request.subtotal() > 0) {
                subtotal = request.subtotal();
            } else if (request.items() != null && !request.items().isEmpty()) {
                for (BillItem item : request.items()) {
                    double price = item.price() != null ? item.price() : 0.0;
                    int quantity = item.quantity() != null ? item.quantity() : 1;
                    subtotal += price * quantity;
                }
            }
        }

        // Default test amount if 0 or empty request
        if (subtotal <= 0.0) {
            subtotal = 100000.0;
        }

        double vatAmount = subtotal * (vatRate / 100.0);
        double finalTotal = subtotal + vatAmount;

        String details = String.format("Subtotal: %.2f + VAT (%.1f%%): %.2f = Total: %.2f",
                subtotal, vatRate, vatAmount, finalTotal);

        log.info("Bill calculation executed: {}", details);

        return new BillResponse(
                subtotal,
                vatRate,
                vatAmount,
                finalTotal,
                details
        );
    }
}
