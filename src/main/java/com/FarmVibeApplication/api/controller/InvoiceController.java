package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.OrderRepository;
import com.FarmVibeApplication.api.model.Order;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class InvoiceController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/download-delivered-invoice")
    public void downloadDeliveredInvoice(
            @RequestParam(value = "deliveryDate", required = true) String deliveryDateStr,
            @RequestParam(value = "userId", required = true) Long userId,
            HttpServletResponse response) {

        try {
            // Validate parameters
            if (deliveryDateStr == null || userId == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
                return;
            }

            LocalDate deliveryDate = LocalDate.parse(deliveryDateStr);
            List<Order> orders = orderRepository.findByDeliveryDateAndDeliveryStatus(deliveryDate, "Delivered")
                    .stream()
                    .filter(order -> order.getUser().getUserId().equals(userId))
                    .collect(Collectors.toList());

            if (orders.isEmpty()) {
                response.sendRedirect("/pageUrl?page=order-history-old&orderStatus=Delivered&userId=" + userId);
                return;
            }

            generateInvoicePDF(response, deliveryDate, orders, userId);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating invoice");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void generateInvoicePDF(HttpServletResponse response,
                                    LocalDate deliveryDate,
                                    List<Order> orders,
                                    Long userId) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=invoice_" + userId + "_" + deliveryDate + ".pdf");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        try {
            document.open();
            addInvoiceHeader(document, deliveryDate, userId);
            addOrderTable(document, orders);
            addInvoiceFooter(document, orders);
        } finally {
            document.close();
        }
    }

    private void addInvoiceHeader(Document document, LocalDate deliveryDate, Long userId) throws DocumentException {
        Paragraph header = new Paragraph();
        header.setAlignment(Element.ALIGN_CENTER);

        // Add logo if available
        // Image logo = Image.getInstance("path/to/logo.png");
        // logo.scaleToFit(100, 100);
        // header.add(logo);

        header.add(new Chunk("Farm Vibe - Order Invoice\n\n",
                new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD)));
        header.add(new Chunk("User ID: " + userId + "\n",
                new Font(Font.FontFamily.HELVETICA, 12)));
        header.add(new Chunk("Delivery Date: " + deliveryDate + "\n\n",
                new Font(Font.FontFamily.HELVETICA, 12)));

        document.add(header);
    }

    private void addOrderTable(Document document, List<Order> orders) throws DocumentException {
        PdfPTable table = new PdfPTable(10); // Number of columns
        table.setWidthPercentage(100);

        // Set column widths
        float[] columnWidths = {1f, 2f, 1f, 1.5f, 1.5f, 2f, 2f, 1.5f, 1.5f, 1f};
        table.setWidths(columnWidths);

        // Table headers
        String[] headers = {
                "Order ID", "Product", "Qty",
                "Price", "User", "Mobile",
                "Email", "Address", "Payment",
                "Status"
        };

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header,
                    new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
            cell.setBackgroundColor(new BaseColor(220, 220, 220));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Table content
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 9);
        for (Order order : orders) {
            addOrderRow(table, order, contentFont);
        }

        document.add(table);
    }

    private void addOrderRow(PdfPTable table, Order order, Font font) {
        table.addCell(new Phrase(String.valueOf(order.getOrderId()), font));
        table.addCell(new Phrase(order.getProduct().getName(), font));
        table.addCell(new Phrase(String.valueOf(order.getQuantity()), font));
        table.addCell(new Phrase("₹" + order.getTotalPrice(), font));
        table.addCell(new Phrase(order.getUser().getUser_firstName() + " " + order.getUser().getUser_lastName(), font));
        table.addCell(new Phrase(String.valueOf(order.getUser().getMobileNumber()), font));
        table.addCell(new Phrase(order.getUser().getUser_email(), font));

        String address = order.getAddress() != null ?
                order.getAddress().getHouse_number() + ", " +
                        order.getAddress().getStreet() + ", " +
                        order.getAddress().getCity() + "-" +
                        order.getAddress().getPincode() : "N/A";
        table.addCell(new Phrase(address, font));

        table.addCell(new Phrase(order.getPaymentMethod(), font));

        PdfPCell statusCell = new PdfPCell(new Phrase(order.getDeliveryStatus(), font));
        statusCell.setBackgroundColor(order.getDeliveryStatus().equals("Delivered") ?
                new BaseColor(200, 230, 200) : new BaseColor(230, 200, 200));
        table.addCell(statusCell);
    }

    private void addInvoiceFooter(Document document, List<Order> orders) throws DocumentException {
        double total = orders.stream().mapToDouble(Order::getTotalPrice).sum();

        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(50);
        footer.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell totalCell = new PdfPCell(new Phrase(
                "Total Amount: ₹" + String.format("%.2f", total),
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        totalCell.setBorder(Rectangle.TOP);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footer.addCell(totalCell);

        document.add(footer);

        Paragraph thanks = new Paragraph("\n\nThank you for your order!\n",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        thanks.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);
    }
}