package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.OrderRepository;
import com.FarmVibeApplication.api.model.Order;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
public class InvoiceController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/order-download-invoice")
    public void downloadInvoice(@RequestParam("deliveryDate") String deliveryDateStr, HttpServletResponse response) throws IOException {
        LocalDate deliveryDate = LocalDate.parse(deliveryDateStr);
        List<Order> orders = orderRepository.findByDeliveryDateAndDeliveryStatus(deliveryDate, "Not Delivered");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=invoice_" + deliveryDate + ".pdf");

        try (OutputStream out = response.getOutputStream()) {
            // Placeholder PDF content
            out.write(("Invoice for Delivery Date: " + deliveryDate + "\nTotal Orders: " + orders.size()).getBytes());
        }
    }
}