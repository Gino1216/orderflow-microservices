package dev.portfolio.order.api;

import dev.portfolio.order.domain.OrderStatus;
import dev.portfolio.order.domain.PurchaseOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

public final class OrderDtos {
    private OrderDtos() {
    }

    public record CreateOrderRequest(
            @NotNull @Positive Long productId,
            @Positive int quantity,
            @NotBlank @Email String customerEmail) {
    }

    public record OrderResponse(
            String id,
            Long productId,
            String productSku,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalAmount,
            String customerEmail,
            OrderStatus status,
            Instant createdAt) {
        public static OrderResponse from(PurchaseOrder order) {
            return new OrderResponse(order.getId(), order.getProductId(), order.getProductSku(),
                    order.getProductName(), order.getQuantity(), order.getUnitPrice(), order.getTotalAmount(),
                    order.getCustomerEmail(), order.getStatus(), order.getCreatedAt());
        }
    }
}
