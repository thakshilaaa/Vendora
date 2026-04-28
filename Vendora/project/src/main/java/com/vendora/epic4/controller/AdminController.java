package com.vendora.epic4.controller;

import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.epic4.model.Order;
import com.vendora.epic4.model.Payment;
import com.vendora.epic4.repository.OrderRepository;
import com.vendora.epic4.repository.PaymentRepository;
import com.vendora.epic4.service.OrderDeliverySyncService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController("epic4AdminController")
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderDeliverySyncService orderDeliverySyncService;

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        Double totalRevenue = orderRepository.getTotalRevenue();
        double revenue = (totalRevenue != null) ? totalRevenue : 0.0;
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus("PENDING");
        Double averageValue = orderRepository.getAverageOrderValue();
        double avgValue = (averageValue != null) ? averageValue : 0.0;

        stats.put("totalRevenue", String.format("%.2f", revenue));
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("avgOrderValue", String.format("%.2f", avgValue));
        return stats;
    }

    @GetMapping("/download-receipt/{orderId}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);


            Table labelContainer = new Table(UnitValue.createPercentArray(new float[]{100}))
                    .setWidth(UnitValue.createPointValue(350f))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(2));

            // Header - Company Name with Background
            Cell header = new Cell().add(new Paragraph("VENDORA BEAUTY STORE")
                            .setBold().setFontSize(16).setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE))
                    .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.CENTER).setPadding(5);
            labelContainer.addCell(header);

            // Shipping Details Section
            String customerPhone = (order.getPhone() != null) ? order.getPhone() : "N/A";
            String shippingDetails = "SHIP TO:\n" +
                    order.getFirstName().toUpperCase() + " " + order.getLastName().toUpperCase() + "\n" +
                    "Anuradhapura / Colombo, Sri Lanka\n" +
                    "Contact: " + customerPhone;

            labelContainer.addCell(new Cell().add(new Paragraph(shippingDetails)
                    .setPadding(10).setFontSize(11).setMultipliedLeading(1.2f)));

            // Order Summary Section
            String orderInfo = "ORDER ID: #" + order.getId() + " | PRODUCT: " + order.getProduct();
            labelContainer.addCell(new Cell().add(new Paragraph(orderInfo).setFontSize(10).setItalic().setPaddingLeft(10)));

            // Payment Highlight (Very Professional look)
            String method = order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A";
            String paymentInfo = "PAYMENT: " + method.toUpperCase() +
                    "\nAMOUNT: RS. " + String.format("%.2f", order.getAmount());

            Cell paymentCell = new Cell().add(new Paragraph(paymentInfo).setBold().setFontSize(14).setPadding(10));


            if ("COD".equalsIgnoreCase(method)) {
                paymentCell.setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED);
                paymentCell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
            }
            labelContainer.addCell(paymentCell);

            document.add(labelContainer);

            // Footer (Small Text)
            document.add(new Paragraph("\nScan for track: #VNDR-" + order.getId())
                    .setFontSize(8).setTextAlignment(TextAlignment.CENTER));

            document.close();

            byte[] pdfBytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("Shipping_Label_" + orderId + ".pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/{id}/pay-confirm")
    @Transactional
    public ResponseEntity<?> confirmPayment(@PathVariable Long id) {
        return orderRepository.findById(id).map(order -> {
            order.setPaymentStatus("PAID");
            orderRepository.save(order);
            List<Payment> payments = paymentRepository.findByOrderId(id);
            if (payments != null) {
                for (Payment p : payments) {
                    p.setStatus("PAID");
                    paymentRepository.save(p);
                }
            }
            orderDeliverySyncService.tryCreateDeliveryForOrder(order);
            return ResponseEntity.ok("Payment Verified Successfully!");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/refund-order/{orderId}")
    public ResponseEntity<?> refundOrder(@PathVariable Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            // ඕඩර් එකේ සාමාන්‍ය Status එක Refunded කරයි
            order.setStatus("Refunded");

            // පේමන්ට් එකේ Status එකත් REFUNDED කරයි (මෙන්න මේකයි වැදගත්)
            order.setPaymentStatus("REFUNDED");

            orderRepository.save(order);
            return ResponseEntity.ok("Order and Payment Refunded Successfully!");
        }).orElse(ResponseEntity.notFound().build());
    }

}