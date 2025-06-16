package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.OrderRepository;
import com.FarmVibeApplication.api.model.Order;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderDetailsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("pageUrl?page=/orders")
    public String showOrderDetails(Model model) {
        List<Order> nonDeliveredOrders = orderRepository.findByDeliveryStatus("Not Delivered");

        Map<LocalDate, List<Order>> groupedOrders = nonDeliveredOrders.stream()
                .collect(Collectors.groupingBy(Order::getDeliveryDate));

        model.addAttribute("groupedOrders", groupedOrders);
        return "order-detail";
    }

    @GetMapping("//download-invoice-order")
    public void downloadInvoice(@RequestParam("deliveryDate") String deliveryDateStr, HttpServletResponse response) {
        try {
            LocalDate deliveryDate = LocalDate.parse(deliveryDateStr);
            List<Order> orders = orderRepository.findByDeliveryStatus("Not Delivered").stream()
                    .filter(order -> deliveryDate.equals(order.getDeliveryDate()))
                    .collect(Collectors.toList());

            if (orders.isEmpty()) {
                response.sendRedirect("/pageUrl?page=/orders");
                return;
            }

            // PDF response setup
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=invoice_" + deliveryDate + ".pdf");

            OutputStream out = response.getOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Invoice for Delivery Date: " + deliveryDate, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Table header
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            String[] headers = {"Order ID", "Product", "Qty", "Price", "User", "Address", "Payment"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            double totalAmount = 0;

            for (Order order : orders) {
                table.addCell(String.valueOf(order.getOrderId()));
                table.addCell(order.getProduct().getName());
                table.addCell(String.valueOf(order.getQuantity()));
                table.addCell(String.valueOf(order.getTotalPrice()));
                table.addCell(order.getUser().getUser_firstName() + " " + order.getUser().getUser_lastName());

                String fullAddress = order.getAddress().getStreet() + ", "
                        + order.getAddress().getCity() + ", "
                        + order.getAddress().getPincode();
                table.addCell(fullAddress);

                table.addCell(order.getPaymentMethod() + " (" + order.getPaymentStatus() + ")");
                totalAmount += order.getTotalPrice();
            }

            document.add(table);

            // Total amount
            Paragraph total = new Paragraph("Total Amount: ₹" + String.format("%.2f", totalAmount),
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
            total.setSpacingBefore(20f);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
